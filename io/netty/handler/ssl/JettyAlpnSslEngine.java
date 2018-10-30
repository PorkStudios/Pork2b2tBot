/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.alpn.ALPN
 *  org.eclipse.jetty.alpn.ALPN$ClientProvider
 *  org.eclipse.jetty.alpn.ALPN$Provider
 *  org.eclipse.jetty.alpn.ALPN$ServerProvider
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslEngine;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.eclipse.jetty.alpn.ALPN;

abstract class JettyAlpnSslEngine
extends JdkSslEngine {
    private static final boolean available = JettyAlpnSslEngine.initAvailable();

    static boolean isAvailable() {
        return available;
    }

    private static boolean initAvailable() {
        if (PlatformDependent.javaVersion() <= 8) {
            try {
                Class.forName("sun.security.ssl.ALPNExtension", true, null);
                return true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return false;
    }

    static JettyAlpnSslEngine newClientEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ClientEngine(engine, applicationNegotiator);
    }

    static JettyAlpnSslEngine newServerEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ServerEngine(engine, applicationNegotiator);
    }

    private JettyAlpnSslEngine(SSLEngine engine) {
        super(engine);
    }

    private static final class ServerEngine
    extends JettyAlpnSslEngine {
        ServerEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator) {
            super(engine);
            ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
            final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector = ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory().newSelector(this, new LinkedHashSet<String>(applicationNegotiator.protocols())), "protocolSelector");
            ALPN.put((SSLEngine)engine, (ALPN.Provider)new ALPN.ServerProvider(){

                public String select(List<String> protocols) throws SSLException {
                    try {
                        return protocolSelector.select(protocols);
                    }
                    catch (Throwable t) {
                        throw SslUtils.toSSLHandshakeException(t);
                    }
                }

                public void unsupported() {
                    protocolSelector.unsupported();
                }
            });
        }

        @Override
        public void closeInbound() throws SSLException {
            try {
                ALPN.remove((SSLEngine)this.getWrappedEngine());
            }
            finally {
                super.closeInbound();
            }
        }

        @Override
        public void closeOutbound() {
            try {
                ALPN.remove((SSLEngine)this.getWrappedEngine());
            }
            finally {
                super.closeOutbound();
            }
        }

    }

    private static final class ClientEngine
    extends JettyAlpnSslEngine {
        ClientEngine(SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator) {
            super(engine);
            ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
            final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener = ObjectUtil.checkNotNull(applicationNegotiator.protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener");
            ALPN.put((SSLEngine)engine, (ALPN.Provider)new ALPN.ClientProvider(){

                public List<String> protocols() {
                    return applicationNegotiator.protocols();
                }

                public void selected(String protocol) throws SSLException {
                    try {
                        protocolListener.selected(protocol);
                    }
                    catch (Throwable t) {
                        throw SslUtils.toSSLHandshakeException(t);
                    }
                }

                public void unsupported() {
                    protocolListener.unsupported();
                }
            });
        }

        @Override
        public void closeInbound() throws SSLException {
            try {
                ALPN.remove((SSLEngine)this.getWrappedEngine());
            }
            finally {
                super.closeInbound();
            }
        }

        @Override
        public void closeOutbound() {
            try {
                ALPN.remove((SSLEngine)this.getWrappedEngine());
            }
            finally {
                super.closeOutbound();
            }
        }

    }

}


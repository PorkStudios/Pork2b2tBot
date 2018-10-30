/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.npn.NextProtoNego
 *  org.eclipse.jetty.npn.NextProtoNego$ClientProvider
 *  org.eclipse.jetty.npn.NextProtoNego$Provider
 *  org.eclipse.jetty.npn.NextProtoNego$ServerProvider
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslEngine;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import org.eclipse.jetty.npn.NextProtoNego;

final class JettyNpnSslEngine
extends JdkSslEngine {
    private static boolean available;

    static boolean isAvailable() {
        JettyNpnSslEngine.updateAvailability();
        return available;
    }

    private static void updateAvailability() {
        if (available) {
            return;
        }
        try {
            Class.forName("sun.security.ssl.NextProtoNegoExtension", true, null);
            available = true;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    JettyNpnSslEngine(SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator, boolean server) {
        super(engine);
        ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
        if (server) {
            final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener = ObjectUtil.checkNotNull(applicationNegotiator.protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener");
            NextProtoNego.put((SSLEngine)engine, (NextProtoNego.Provider)new NextProtoNego.ServerProvider(){

                public void unsupported() {
                    protocolListener.unsupported();
                }

                public List<String> protocols() {
                    return applicationNegotiator.protocols();
                }

                public void protocolSelected(String protocol) {
                    try {
                        protocolListener.selected(protocol);
                    }
                    catch (Throwable t) {
                        PlatformDependent.throwException(t);
                    }
                }
            });
        } else {
            final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector = ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory().newSelector(this, new LinkedHashSet<String>(applicationNegotiator.protocols())), "protocolSelector");
            NextProtoNego.put((SSLEngine)engine, (NextProtoNego.Provider)new NextProtoNego.ClientProvider(){

                public boolean supports() {
                    return true;
                }

                public void unsupported() {
                    protocolSelector.unsupported();
                }

                public String selectProtocol(List<String> protocols) {
                    try {
                        return protocolSelector.select(protocols);
                    }
                    catch (Throwable t) {
                        PlatformDependent.throwException(t);
                        return null;
                    }
                }
            });
        }
    }

    @Override
    public void closeInbound() throws SSLException {
        NextProtoNego.remove((SSLEngine)this.getWrappedEngine());
        super.closeInbound();
    }

    @Override
    public void closeOutbound() {
        NextProtoNego.remove((SSLEngine)this.getWrappedEngine());
        super.closeOutbound();
    }

}


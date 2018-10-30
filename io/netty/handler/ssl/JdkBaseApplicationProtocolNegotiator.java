/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolUtil;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslEngine;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;

class JdkBaseApplicationProtocolNegotiator
implements JdkApplicationProtocolNegotiator {
    private final List<String> protocols;
    private final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory;
    private final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory;
    private final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory;
    static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory(){

        @Override
        public JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine engine, Set<String> supportedProtocols) {
            return new FailProtocolSelector((JdkSslEngine)engine, supportedProtocols);
        }
    };
    static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory NO_FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory(){

        @Override
        public JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine engine, Set<String> supportedProtocols) {
            return new NoFailProtocolSelector((JdkSslEngine)engine, supportedProtocols);
        }
    };
    static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory(){

        @Override
        public JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine engine, List<String> supportedProtocols) {
            return new FailProtocolSelectionListener((JdkSslEngine)engine, supportedProtocols);
        }
    };
    static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory NO_FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory(){

        @Override
        public JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine engine, List<String> supportedProtocols) {
            return new NoFailProtocolSelectionListener((JdkSslEngine)engine, supportedProtocols);
        }
    };

    JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, Iterable<String> protocols) {
        this(wrapperFactory, selectorFactory, listenerFactory, ApplicationProtocolUtil.toList(protocols));
    }

    /* varargs */ JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, String ... protocols) {
        this(wrapperFactory, selectorFactory, listenerFactory, ApplicationProtocolUtil.toList(protocols));
    }

    private JdkBaseApplicationProtocolNegotiator(JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory, JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, List<String> protocols) {
        this.wrapperFactory = ObjectUtil.checkNotNull(wrapperFactory, "wrapperFactory");
        this.selectorFactory = ObjectUtil.checkNotNull(selectorFactory, "selectorFactory");
        this.listenerFactory = ObjectUtil.checkNotNull(listenerFactory, "listenerFactory");
        this.protocols = Collections.unmodifiableList(ObjectUtil.checkNotNull(protocols, "protocols"));
    }

    @Override
    public List<String> protocols() {
        return this.protocols;
    }

    @Override
    public JdkApplicationProtocolNegotiator.ProtocolSelectorFactory protocolSelectorFactory() {
        return this.selectorFactory;
    }

    @Override
    public JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory protocolListenerFactory() {
        return this.listenerFactory;
    }

    @Override
    public JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory() {
        return this.wrapperFactory;
    }

    private static final class FailProtocolSelectionListener
    extends NoFailProtocolSelectionListener {
        FailProtocolSelectionListener(JdkSslEngine engineWrapper, List<String> supportedProtocols) {
            super(engineWrapper, supportedProtocols);
        }

        @Override
        protected void noSelectedMatchFound(String protocol) throws Exception {
            throw new SSLHandshakeException("No compatible protocols found");
        }
    }

    private static class NoFailProtocolSelectionListener
    implements JdkApplicationProtocolNegotiator.ProtocolSelectionListener {
        private final JdkSslEngine engineWrapper;
        private final List<String> supportedProtocols;

        NoFailProtocolSelectionListener(JdkSslEngine engineWrapper, List<String> supportedProtocols) {
            this.engineWrapper = engineWrapper;
            this.supportedProtocols = supportedProtocols;
        }

        @Override
        public void unsupported() {
            this.engineWrapper.setNegotiatedApplicationProtocol(null);
        }

        @Override
        public void selected(String protocol) throws Exception {
            if (this.supportedProtocols.contains(protocol)) {
                this.engineWrapper.setNegotiatedApplicationProtocol(protocol);
            } else {
                this.noSelectedMatchFound(protocol);
            }
        }

        protected void noSelectedMatchFound(String protocol) throws Exception {
        }
    }

    private static final class FailProtocolSelector
    extends NoFailProtocolSelector {
        FailProtocolSelector(JdkSslEngine engineWrapper, Set<String> supportedProtocols) {
            super(engineWrapper, supportedProtocols);
        }

        @Override
        public String noSelectMatchFound() throws Exception {
            throw new SSLHandshakeException("Selected protocol is not supported");
        }
    }

    static class NoFailProtocolSelector
    implements JdkApplicationProtocolNegotiator.ProtocolSelector {
        private final JdkSslEngine engineWrapper;
        private final Set<String> supportedProtocols;

        NoFailProtocolSelector(JdkSslEngine engineWrapper, Set<String> supportedProtocols) {
            this.engineWrapper = engineWrapper;
            this.supportedProtocols = supportedProtocols;
        }

        @Override
        public void unsupported() {
            this.engineWrapper.setNegotiatedApplicationProtocol(null);
        }

        @Override
        public String select(List<String> protocols) throws Exception {
            for (String p : this.supportedProtocols) {
                if (!protocols.contains(p)) continue;
                this.engineWrapper.setNegotiatedApplicationProtocol(p);
                return p;
            }
            return this.noSelectMatchFound();
        }

        public String noSelectMatchFound() throws Exception {
            this.engineWrapper.setNegotiatedApplicationProtocol(null);
            return null;
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLEngine;

final class JdkDefaultApplicationProtocolNegotiator
implements JdkApplicationProtocolNegotiator {
    public static final JdkDefaultApplicationProtocolNegotiator INSTANCE = new JdkDefaultApplicationProtocolNegotiator();
    private static final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory DEFAULT_SSL_ENGINE_WRAPPER_FACTORY = new JdkApplicationProtocolNegotiator.SslEngineWrapperFactory(){

        @Override
        public SSLEngine wrapSslEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
            return engine;
        }
    };

    private JdkDefaultApplicationProtocolNegotiator() {
    }

    @Override
    public JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory() {
        return DEFAULT_SSL_ENGINE_WRAPPER_FACTORY;
    }

    @Override
    public JdkApplicationProtocolNegotiator.ProtocolSelectorFactory protocolSelectorFactory() {
        throw new UnsupportedOperationException("Application protocol negotiation unsupported");
    }

    @Override
    public JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory protocolListenerFactory() {
        throw new UnsupportedOperationException("Application protocol negotiation unsupported");
    }

    @Override
    public List<String> protocols() {
        return Collections.emptyList();
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;

@Deprecated
public interface JdkApplicationProtocolNegotiator
extends ApplicationProtocolNegotiator {
    public SslEngineWrapperFactory wrapperFactory();

    public ProtocolSelectorFactory protocolSelectorFactory();

    public ProtocolSelectionListenerFactory protocolListenerFactory();

    public static interface ProtocolSelectionListenerFactory {
        public ProtocolSelectionListener newListener(SSLEngine var1, List<String> var2);
    }

    public static interface ProtocolSelectorFactory {
        public ProtocolSelector newSelector(SSLEngine var1, Set<String> var2);
    }

    public static interface ProtocolSelectionListener {
        public void unsupported();

        public void selected(String var1) throws Exception;
    }

    public static interface ProtocolSelector {
        public void unsupported();

        public String select(List<String> var1) throws Exception;
    }

    public static abstract class AllocatorAwareSslEngineWrapperFactory
    implements SslEngineWrapperFactory {
        @Override
        public final SSLEngine wrapSslEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
            return this.wrapSslEngine(engine, ByteBufAllocator.DEFAULT, applicationNegotiator, isServer);
        }

        abstract SSLEngine wrapSslEngine(SSLEngine var1, ByteBufAllocator var2, JdkApplicationProtocolNegotiator var3, boolean var4);
    }

    public static interface SslEngineWrapperFactory {
        public SSLEngine wrapSslEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2, boolean var3);
    }

}


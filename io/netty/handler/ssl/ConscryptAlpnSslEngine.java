/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.conscrypt.AllocatedBuffer
 *  org.conscrypt.BufferAllocator
 *  org.conscrypt.Conscrypt
 *  org.conscrypt.HandshakeListener
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslEngine;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.conscrypt.AllocatedBuffer;
import org.conscrypt.BufferAllocator;
import org.conscrypt.Conscrypt;
import org.conscrypt.HandshakeListener;

abstract class ConscryptAlpnSslEngine
extends JdkSslEngine {
    private static final boolean USE_BUFFER_ALLOCATOR = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.conscrypt.useBufferAllocator", true);

    static ConscryptAlpnSslEngine newClientEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ClientEngine(engine, alloc, applicationNegotiator);
    }

    static ConscryptAlpnSslEngine newServerEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ServerEngine(engine, alloc, applicationNegotiator);
    }

    private ConscryptAlpnSslEngine(SSLEngine engine, ByteBufAllocator alloc, List<String> protocols) {
        super(engine);
        if (USE_BUFFER_ALLOCATOR) {
            Conscrypt.setBufferAllocator((SSLEngine)engine, (BufferAllocator)new BufferAllocatorAdapter(alloc));
        }
        Conscrypt.setApplicationProtocols((SSLEngine)engine, (String[])protocols.toArray(new String[protocols.size()]));
    }

    final int calculateOutNetBufSize(int plaintextBytes, int numBuffers) {
        long maxOverhead = (long)Conscrypt.maxSealOverhead((SSLEngine)this.getWrappedEngine()) * (long)numBuffers;
        return (int)Math.min(Integer.MAX_VALUE, (long)plaintextBytes + maxOverhead);
    }

    final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dests) throws SSLException {
        return Conscrypt.unwrap((SSLEngine)this.getWrappedEngine(), (ByteBuffer[])srcs, (ByteBuffer[])dests);
    }

    private static final class BufferAdapter
    extends AllocatedBuffer {
        private final ByteBuf nettyBuffer;
        private final ByteBuffer buffer;

        BufferAdapter(ByteBuf nettyBuffer) {
            this.nettyBuffer = nettyBuffer;
            this.buffer = nettyBuffer.nioBuffer(0, nettyBuffer.capacity());
        }

        public ByteBuffer nioBuffer() {
            return this.buffer;
        }

        public AllocatedBuffer retain() {
            this.nettyBuffer.retain();
            return this;
        }

        public AllocatedBuffer release() {
            this.nettyBuffer.release();
            return this;
        }
    }

    private static final class BufferAllocatorAdapter
    extends BufferAllocator {
        private final ByteBufAllocator alloc;

        BufferAllocatorAdapter(ByteBufAllocator alloc) {
            this.alloc = alloc;
        }

        public AllocatedBuffer allocateDirectBuffer(int capacity) {
            return new BufferAdapter(this.alloc.directBuffer(capacity));
        }
    }

    private static final class ServerEngine
    extends ConscryptAlpnSslEngine {
        private final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector;

        ServerEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
            super(engine, alloc, applicationNegotiator.protocols());
            Conscrypt.setHandshakeListener((SSLEngine)engine, (HandshakeListener)new HandshakeListener(){

                public void onHandshakeFinished() throws SSLException {
                    ServerEngine.this.selectProtocol();
                }
            });
            this.protocolSelector = ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory().newSelector(this, new LinkedHashSet<String>(applicationNegotiator.protocols())), "protocolSelector");
        }

        private void selectProtocol() throws SSLException {
            try {
                String protocol = Conscrypt.getApplicationProtocol((SSLEngine)this.getWrappedEngine());
                this.protocolSelector.select(protocol != null ? Collections.singletonList(protocol) : Collections.emptyList());
            }
            catch (Throwable e) {
                throw SslUtils.toSSLHandshakeException(e);
            }
        }

    }

    private static final class ClientEngine
    extends ConscryptAlpnSslEngine {
        private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener;

        ClientEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
            super(engine, alloc, applicationNegotiator.protocols());
            Conscrypt.setHandshakeListener((SSLEngine)engine, (HandshakeListener)new HandshakeListener(){

                public void onHandshakeFinished() throws SSLException {
                    ClientEngine.this.selectProtocol();
                }
            });
            this.protocolListener = ObjectUtil.checkNotNull(applicationNegotiator.protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener");
        }

        private void selectProtocol() throws SSLException {
            String protocol = Conscrypt.getApplicationProtocol((SSLEngine)this.getWrappedEngine());
            try {
                this.protocolListener.selected(protocol);
            }
            catch (Throwable e) {
                throw SslUtils.toSSLHandshakeException(e);
            }
        }

    }

}


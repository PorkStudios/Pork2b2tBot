/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.Buffer
 *  io.netty.internal.tcnative.SSL
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolAccessor;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.CipherSuiteConverter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.Java7SslParametersUtils;
import io.netty.handler.ssl.Java8SslUtils;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslJavaxX509Certificate;
import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.OpenSslX509Certificate;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.SslUtils;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.SSL;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.AlgorithmConstraints;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import javax.security.auth.x500.X500Principal;
import javax.security.cert.X509Certificate;

public class ReferenceCountedOpenSslEngine
extends SSLEngine
implements ReferenceCounted,
ApplicationProtocolAccessor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslEngine.class);
    private static final SSLException BEGIN_HANDSHAKE_ENGINE_CLOSED = ThrowableUtil.unknownStackTrace(new SSLException("engine closed"), ReferenceCountedOpenSslEngine.class, "beginHandshake()");
    private static final SSLException HANDSHAKE_ENGINE_CLOSED = ThrowableUtil.unknownStackTrace(new SSLException("engine closed"), ReferenceCountedOpenSslEngine.class, "handshake()");
    private static final SSLException RENEGOTIATION_UNSUPPORTED = ThrowableUtil.unknownStackTrace(new SSLException("renegotiation unsupported"), ReferenceCountedOpenSslEngine.class, "beginHandshake()");
    private static final ResourceLeakDetector<ReferenceCountedOpenSslEngine> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslEngine.class);
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV2 = 0;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV3 = 1;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1 = 2;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_1 = 3;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_2 = 4;
    private static final int[] OPENSSL_OP_NO_PROTOCOLS = new int[]{SSL.SSL_OP_NO_SSLv2, SSL.SSL_OP_NO_SSLv3, SSL.SSL_OP_NO_TLSv1, SSL.SSL_OP_NO_TLSv1_1, SSL.SSL_OP_NO_TLSv1_2};
    private static final int DEFAULT_HOSTNAME_VALIDATION_FLAGS = 0;
    static final int MAX_PLAINTEXT_LENGTH = SSL.SSL_MAX_PLAINTEXT_LENGTH;
    private static final int MAX_RECORD_SIZE = SSL.SSL_MAX_RECORD_LENGTH;
    private static final AtomicIntegerFieldUpdater<ReferenceCountedOpenSslEngine> DESTROYED_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ReferenceCountedOpenSslEngine.class, "destroyed");
    private static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
    private static final SSLEngineResult NEED_UNWRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
    private static final SSLEngineResult NEED_UNWRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
    private static final SSLEngineResult NEED_WRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
    private static final SSLEngineResult NEED_WRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
    private static final SSLEngineResult CLOSED_NOT_HANDSHAKING = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
    private long ssl;
    private long networkBIO;
    private boolean certificateSet;
    private HandshakeState handshakeState;
    private boolean renegotiationPending;
    private boolean receivedShutdown;
    private volatile int destroyed;
    private volatile String applicationProtocol;
    private final ResourceLeakTracker<ReferenceCountedOpenSslEngine> leak;
    private final AbstractReferenceCounted refCnt;
    private volatile ClientAuth clientAuth;
    private volatile long lastAccessed;
    private String endPointIdentificationAlgorithm;
    private Object algorithmConstraints;
    private List<String> sniHostNames;
    private volatile Collection<?> matchers;
    private boolean isInboundDone;
    private boolean outboundClosed;
    final boolean jdkCompatibilityMode;
    private final boolean clientMode;
    private final ByteBufAllocator alloc;
    private final OpenSslEngineMap engineMap;
    private final OpenSslApplicationProtocolNegotiator apn;
    private final boolean rejectRemoteInitiatedRenegotiation;
    private final OpenSslSession session;
    private final Certificate[] localCerts;
    private final ByteBuffer[] singleSrcBuffer;
    private final ByteBuffer[] singleDstBuffer;
    private final OpenSslKeyMaterialManager keyMaterialManager;
    private final boolean enableOcsp;
    private int maxWrapOverhead;
    private int maxWrapBufferSize;
    SSLHandshakeException handshakeException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ReferenceCountedOpenSslEngine(ReferenceCountedOpenSslContext context, ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode, boolean leakDetection) {
        long finalSsl;
        super(peerHost, peerPort);
        this.handshakeState = HandshakeState.NOT_STARTED;
        this.refCnt = new AbstractReferenceCounted(){

            @Override
            public ReferenceCounted touch(Object hint) {
                if (ReferenceCountedOpenSslEngine.this.leak != null) {
                    ReferenceCountedOpenSslEngine.this.leak.record(hint);
                }
                return ReferenceCountedOpenSslEngine.this;
            }

            @Override
            protected void deallocate() {
                ReferenceCountedOpenSslEngine.this.shutdown();
                if (ReferenceCountedOpenSslEngine.this.leak != null) {
                    boolean closed = ReferenceCountedOpenSslEngine.this.leak.close(ReferenceCountedOpenSslEngine.this);
                    assert (closed);
                }
            }
        };
        this.clientAuth = ClientAuth.NONE;
        this.lastAccessed = -1L;
        this.singleSrcBuffer = new ByteBuffer[1];
        this.singleDstBuffer = new ByteBuffer[1];
        OpenSsl.ensureAvailability();
        this.alloc = ObjectUtil.checkNotNull(alloc, "alloc");
        this.apn = (OpenSslApplicationProtocolNegotiator)context.applicationProtocolNegotiator();
        this.session = new OpenSslSession(context.sessionContext());
        this.clientMode = context.isClient();
        this.engineMap = context.engineMap;
        this.rejectRemoteInitiatedRenegotiation = context.getRejectRemoteInitiatedRenegotiation();
        this.localCerts = context.keyCertChain;
        this.keyMaterialManager = context.keyMaterialManager();
        this.enableOcsp = context.enableOcsp;
        this.jdkCompatibilityMode = jdkCompatibilityMode;
        Lock readerLock = context.ctxLock.readLock();
        readerLock.lock();
        try {
            finalSsl = SSL.newSSL((long)context.ctx, (boolean)(!context.isClient()));
        }
        finally {
            readerLock.unlock();
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            this.ssl = finalSsl;
            try {
                this.networkBIO = SSL.bioNewByteBuffer((long)this.ssl, (int)context.getBioNonApplicationBufferSize());
                this.setClientAuth(this.clientMode ? ClientAuth.NONE : context.clientAuth);
                if (context.protocols != null) {
                    this.setEnabledProtocols(context.protocols);
                }
                if (this.clientMode && peerHost != null) {
                    SSL.setTlsExtHostName((long)this.ssl, (String)peerHost);
                }
                if (this.enableOcsp) {
                    SSL.enableOcsp((long)this.ssl);
                }
                if (!jdkCompatibilityMode) {
                    SSL.setMode((long)this.ssl, (int)(SSL.getMode((long)this.ssl) | SSL.SSL_MODE_ENABLE_PARTIAL_WRITE));
                }
                this.calculateMaxWrapOverhead();
            }
            catch (Throwable cause) {
                SSL.freeSSL((long)this.ssl);
                PlatformDependent.throwException(cause);
            }
        }
        this.leak = leakDetection ? leakDetector.track(this) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOcspResponse(byte[] response) {
        if (!this.enableOcsp) {
            throw new IllegalStateException("OCSP stapling is not enabled");
        }
        if (this.clientMode) {
            throw new IllegalStateException("Not a server SSLEngine");
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            SSL.setOcspResponse((long)this.ssl, (byte[])response);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getOcspResponse() {
        if (!this.enableOcsp) {
            throw new IllegalStateException("OCSP stapling is not enabled");
        }
        if (!this.clientMode) {
            throw new IllegalStateException("Not a client SSLEngine");
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            return SSL.getOcspResponse((long)this.ssl);
        }
    }

    @Override
    public final int refCnt() {
        return this.refCnt.refCnt();
    }

    @Override
    public final ReferenceCounted retain() {
        this.refCnt.retain();
        return this;
    }

    @Override
    public final ReferenceCounted retain(int increment) {
        this.refCnt.retain(increment);
        return this;
    }

    @Override
    public final ReferenceCounted touch() {
        this.refCnt.touch();
        return this;
    }

    @Override
    public final ReferenceCounted touch(Object hint) {
        this.refCnt.touch(hint);
        return this;
    }

    @Override
    public final boolean release() {
        return this.refCnt.release();
    }

    @Override
    public final boolean release(int decrement) {
        return this.refCnt.release(decrement);
    }

    @Override
    public final synchronized SSLSession getHandshakeSession() {
        switch (this.handshakeState) {
            case NOT_STARTED: 
            case FINISHED: {
                return null;
            }
        }
        return this.session;
    }

    public final synchronized long sslPointer() {
        return this.ssl;
    }

    public final synchronized void shutdown() {
        if (DESTROYED_UPDATER.compareAndSet(this, 0, 1)) {
            this.engineMap.remove(this.ssl);
            SSL.freeSSL((long)this.ssl);
            this.networkBIO = 0L;
            this.ssl = 0L;
            this.outboundClosed = true;
            this.isInboundDone = true;
        }
        SSL.clearError();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int writePlaintextData(ByteBuffer src, int len) {
        int sslWrote;
        int pos = src.position();
        int limit = src.limit();
        if (src.isDirect()) {
            sslWrote = SSL.writeToSSL((long)this.ssl, (long)(Buffer.address((ByteBuffer)src) + (long)pos), (int)len);
            if (sslWrote > 0) {
                src.position(pos + sslWrote);
            }
        } else {
            ByteBuf buf = this.alloc.directBuffer(len);
            try {
                src.limit(pos + len);
                buf.setBytes(0, src);
                src.limit(limit);
                sslWrote = SSL.writeToSSL((long)this.ssl, (long)OpenSsl.memoryAddress(buf), (int)len);
                if (sslWrote > 0) {
                    src.position(pos + sslWrote);
                } else {
                    src.position(pos);
                }
            }
            finally {
                buf.release();
            }
        }
        return sslWrote;
    }

    private ByteBuf writeEncryptedData(ByteBuffer src, int len) {
        int pos = src.position();
        if (src.isDirect()) {
            SSL.bioSetByteBuffer((long)this.networkBIO, (long)(Buffer.address((ByteBuffer)src) + (long)pos), (int)len, (boolean)false);
        } else {
            ByteBuf buf = this.alloc.directBuffer(len);
            try {
                int limit = src.limit();
                src.limit(pos + len);
                buf.writeBytes(src);
                src.position(pos);
                src.limit(limit);
                SSL.bioSetByteBuffer((long)this.networkBIO, (long)OpenSsl.memoryAddress(buf), (int)len, (boolean)false);
                return buf;
            }
            catch (Throwable cause) {
                buf.release();
                PlatformDependent.throwException(cause);
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int readPlaintextData(ByteBuffer dst) {
        int sslRead;
        int pos = dst.position();
        if (dst.isDirect()) {
            sslRead = SSL.readFromSSL((long)this.ssl, (long)(Buffer.address((ByteBuffer)dst) + (long)pos), (int)(dst.limit() - pos));
            if (sslRead > 0) {
                dst.position(pos + sslRead);
            }
        } else {
            int limit = dst.limit();
            int len = Math.min(this.maxEncryptedPacketLength0(), limit - pos);
            ByteBuf buf = this.alloc.directBuffer(len);
            try {
                sslRead = SSL.readFromSSL((long)this.ssl, (long)OpenSsl.memoryAddress(buf), (int)len);
                if (sslRead > 0) {
                    dst.limit(pos + sslRead);
                    buf.getBytes(buf.readerIndex(), dst);
                    dst.limit(limit);
                }
            }
            finally {
                buf.release();
            }
        }
        return sslRead;
    }

    final synchronized int maxWrapOverhead() {
        return this.maxWrapOverhead;
    }

    final synchronized int maxEncryptedPacketLength() {
        return this.maxEncryptedPacketLength0();
    }

    final int maxEncryptedPacketLength0() {
        return this.maxWrapOverhead + MAX_PLAINTEXT_LENGTH;
    }

    final int calculateMaxLengthForWrap(int plaintextLength, int numComponents) {
        return (int)Math.min((long)this.maxWrapBufferSize, (long)plaintextLength + (long)this.maxWrapOverhead * (long)numComponents);
    }

    final synchronized int sslPending() {
        return this.sslPending0();
    }

    private void calculateMaxWrapOverhead() {
        this.maxWrapOverhead = SSL.getMaxWrapOverhead((long)this.ssl);
        this.maxWrapBufferSize = this.jdkCompatibilityMode ? this.maxEncryptedPacketLength0() : this.maxEncryptedPacketLength0() << 4;
    }

    private int sslPending0() {
        return this.handshakeState != HandshakeState.FINISHED ? 0 : SSL.sslPending((long)this.ssl);
    }

    private boolean isBytesAvailableEnoughForWrap(int bytesAvailable, int plaintextLength, int numComponents) {
        return (long)bytesAvailable - (long)this.maxWrapOverhead * (long)numComponents >= (long)plaintextLength;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public final SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
        if (srcs == null) {
            throw new IllegalArgumentException("srcs is null");
        }
        if (dst == null) {
            throw new IllegalArgumentException("dst is null");
        }
        if (offset >= srcs.length || offset + length > srcs.length) {
            throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
        }
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            if (this.isOutboundDone()) {
                return this.isInboundDone() || this.isDestroyed() ? CLOSED_NOT_HANDSHAKING : NEED_UNWRAP_CLOSED;
            }
            int bytesProduced = 0;
            ByteBuf bioReadCopyBuf = null;
            try {
                Object src;
                if (dst.isDirect()) {
                    SSL.bioSetByteBuffer((long)this.networkBIO, (long)(Buffer.address((ByteBuffer)dst) + (long)dst.position()), (int)dst.remaining(), (boolean)true);
                } else {
                    bioReadCopyBuf = this.alloc.directBuffer(dst.remaining());
                    SSL.bioSetByteBuffer((long)this.networkBIO, (long)OpenSsl.memoryAddress(bioReadCopyBuf), (int)bioReadCopyBuf.writableBytes(), (boolean)true);
                }
                int bioLengthBefore = SSL.bioLengthByteBuffer((long)this.networkBIO);
                if (this.outboundClosed) {
                    bytesProduced = SSL.bioFlushByteBuffer((long)this.networkBIO);
                    if (bytesProduced <= 0) {
                        SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
                        return sSLEngineResult;
                    }
                    if (!this.doSSLShutdown()) {
                        SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, bytesProduced);
                        return sSLEngineResult;
                    }
                    bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer((long)this.networkBIO);
                    SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, bytesProduced);
                    return sSLEngineResult;
                }
                SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
                if (this.handshakeState != HandshakeState.FINISHED) {
                    if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY) {
                        this.handshakeState = HandshakeState.STARTED_IMPLICITLY;
                    }
                    if ((bytesProduced = SSL.bioFlushByteBuffer((long)this.networkBIO)) > 0 && this.handshakeException != null) {
                        SSLEngineResult sSLEngineResult = this.newResult(SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, bytesProduced);
                        return sSLEngineResult;
                    }
                    status = this.handshake();
                    if (this.renegotiationPending && status == SSLEngineResult.HandshakeStatus.FINISHED) {
                        this.renegotiationPending = false;
                        SSL.setState((long)this.ssl, (int)SSL.SSL_ST_ACCEPT);
                        this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
                        status = this.handshake();
                    }
                    if ((bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer((long)this.networkBIO)) > 0) {
                        SSLEngineResult sSLEngineResult = this.newResult(this.mayFinishHandshake(status != SSLEngineResult.HandshakeStatus.FINISHED ? (bytesProduced == bioLengthBefore ? SSLEngineResult.HandshakeStatus.NEED_WRAP : this.getHandshakeStatus(SSL.bioLengthNonApplication((long)this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED), 0, bytesProduced);
                        return sSLEngineResult;
                    }
                    if (status == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                        SSLEngineResult sSLEngineResult = this.isOutboundDone() ? NEED_UNWRAP_CLOSED : NEED_UNWRAP_OK;
                        return sSLEngineResult;
                    }
                    if (this.outboundClosed) {
                        bytesProduced = SSL.bioFlushByteBuffer((long)this.networkBIO);
                        SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake(status, 0, bytesProduced);
                        return sSLEngineResult;
                    }
                }
                int endOffset = offset + length;
                if (this.jdkCompatibilityMode) {
                    int srcsLen = 0;
                    for (int i = offset; i < endOffset; ++i) {
                        ByteBuffer src2 = srcs[i];
                        if (src2 == null) {
                            throw new IllegalArgumentException("srcs[" + i + "] is null");
                        }
                        if (srcsLen == MAX_PLAINTEXT_LENGTH || (srcsLen += src2.remaining()) <= MAX_PLAINTEXT_LENGTH && srcsLen >= 0) continue;
                        srcsLen = MAX_PLAINTEXT_LENGTH;
                    }
                    if (!this.isBytesAvailableEnoughForWrap(dst.remaining(), srcsLen, 1)) {
                        SSLEngineResult i = new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), 0, 0);
                        return i;
                    }
                }
                int bytesConsumed = 0;
                bytesProduced = SSL.bioFlushByteBuffer((long)this.networkBIO);
                while (offset < endOffset) {
                    src = srcs[offset];
                    int remaining = src.remaining();
                    if (remaining != 0) {
                        int bytesWritten;
                        if (this.jdkCompatibilityMode) {
                            bytesWritten = this.writePlaintextData((ByteBuffer)src, Math.min(remaining, MAX_PLAINTEXT_LENGTH - bytesConsumed));
                        } else {
                            int availableCapacityForWrap = dst.remaining() - bytesProduced - this.maxWrapOverhead;
                            if (availableCapacityForWrap <= 0) {
                                SSLEngineResult sSLEngineResult = new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), bytesConsumed, bytesProduced);
                                return sSLEngineResult;
                            }
                            bytesWritten = this.writePlaintextData((ByteBuffer)src, Math.min(remaining, availableCapacityForWrap));
                        }
                        if (bytesWritten > 0) {
                            bytesConsumed += bytesWritten;
                            int pendingNow = SSL.bioLengthByteBuffer((long)this.networkBIO);
                            bioLengthBefore = pendingNow;
                            if (this.jdkCompatibilityMode || (bytesProduced += bioLengthBefore - pendingNow) == dst.remaining()) {
                                SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake(status, bytesConsumed, bytesProduced);
                                return sSLEngineResult;
                            }
                        } else {
                            int sslError = SSL.getError((long)this.ssl, (int)bytesWritten);
                            if (sslError == SSL.SSL_ERROR_ZERO_RETURN) {
                                if (!this.receivedShutdown) {
                                    this.closeAll();
                                    SSLEngineResult.HandshakeStatus hs = this.mayFinishHandshake(status != SSLEngineResult.HandshakeStatus.FINISHED ? ((bytesProduced += bioLengthBefore - SSL.bioLengthByteBuffer((long)this.networkBIO)) == dst.remaining() ? SSLEngineResult.HandshakeStatus.NEED_WRAP : this.getHandshakeStatus(SSL.bioLengthNonApplication((long)this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED);
                                    SSLEngineResult sSLEngineResult = this.newResult(hs, bytesConsumed, bytesProduced);
                                    return sSLEngineResult;
                                }
                                SSLEngineResult hs = this.newResult(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, bytesConsumed, bytesProduced);
                                return hs;
                            }
                            if (sslError == SSL.SSL_ERROR_WANT_READ) {
                                SSLEngineResult hs = this.newResult(SSLEngineResult.HandshakeStatus.NEED_UNWRAP, bytesConsumed, bytesProduced);
                                return hs;
                            }
                            if (sslError != SSL.SSL_ERROR_WANT_WRITE) {
                                throw this.shutdownWithError("SSL_write");
                            }
                            SSLEngineResult hs = this.newResult(SSLEngineResult.Status.BUFFER_OVERFLOW, status, bytesConsumed, bytesProduced);
                            return hs;
                        }
                    }
                    ++offset;
                }
                src = this.newResultMayFinishHandshake(status, bytesConsumed, bytesProduced);
                return src;
            }
            finally {
                SSL.bioClearByteBuffer((long)this.networkBIO);
                if (bioReadCopyBuf == null) {
                    dst.position(dst.position() + bytesProduced);
                } else {
                    assert (bioReadCopyBuf.readableBytes() <= dst.remaining());
                    dst.put(bioReadCopyBuf.internalNioBuffer(bioReadCopyBuf.readerIndex(), bytesProduced));
                    bioReadCopyBuf.release();
                }
            }
        }
    }

    private SSLEngineResult newResult(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) {
        return this.newResult(SSLEngineResult.Status.OK, hs, bytesConsumed, bytesProduced);
    }

    private SSLEngineResult newResult(SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) {
        if (this.isOutboundDone()) {
            if (this.isInboundDone()) {
                hs = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
                this.shutdown();
            }
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, hs, bytesConsumed, bytesProduced);
        }
        return new SSLEngineResult(status, hs, bytesConsumed, bytesProduced);
    }

    private SSLEngineResult newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
        return this.newResult(this.mayFinishHandshake(hs != SSLEngineResult.HandshakeStatus.FINISHED ? this.getHandshakeStatus() : SSLEngineResult.HandshakeStatus.FINISHED), bytesConsumed, bytesProduced);
    }

    private SSLEngineResult newResultMayFinishHandshake(SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
        return this.newResult(status, this.mayFinishHandshake(hs != SSLEngineResult.HandshakeStatus.FINISHED ? this.getHandshakeStatus() : SSLEngineResult.HandshakeStatus.FINISHED), bytesConsumed, bytesProduced);
    }

    private SSLException shutdownWithError(String operations) {
        String err = SSL.getLastError();
        return this.shutdownWithError(operations, err);
    }

    private SSLException shutdownWithError(String operation, String err) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} failed: OpenSSL error: {}", (Object)operation, (Object)err);
        }
        this.shutdown();
        if (this.handshakeState == HandshakeState.FINISHED) {
            return new SSLException(err);
        }
        return new SSLHandshakeException(err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public final SSLEngineResult unwrap(ByteBuffer[] srcs, int srcsOffset, int srcsLength, ByteBuffer[] dsts, int dstsOffset, int dstsLength) throws SSLException {
        block50 : {
            if (srcs == null) {
                throw new NullPointerException("srcs");
            }
            if (srcsOffset >= srcs.length) throw new IndexOutOfBoundsException("offset: " + srcsOffset + ", length: " + srcsLength + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
            if (srcsOffset + srcsLength > srcs.length) {
                throw new IndexOutOfBoundsException("offset: " + srcsOffset + ", length: " + srcsLength + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
            }
            if (dsts == null) {
                throw new IllegalArgumentException("dsts is null");
            }
            if (dstsOffset >= dsts.length) throw new IndexOutOfBoundsException("offset: " + dstsOffset + ", length: " + dstsLength + " (expected: offset <= offset + length <= dsts.length (" + dsts.length + "))");
            if (dstsOffset + dstsLength > dsts.length) {
                throw new IndexOutOfBoundsException("offset: " + dstsOffset + ", length: " + dstsLength + " (expected: offset <= offset + length <= dsts.length (" + dsts.length + "))");
            }
            capacity = 0L;
            dstsEndOffset = dstsOffset + dstsLength;
            for (i = dstsOffset; i < dstsEndOffset; capacity += (long)dst.remaining(), ++i) {
                dst = dsts[i];
                if (dst == null) {
                    throw new IllegalArgumentException("dsts[" + i + "] is null");
                }
                if (!dst.isReadOnly()) continue;
                throw new ReadOnlyBufferException();
            }
            srcsEndOffset = srcsOffset + srcsLength;
            len = 0L;
            for (i = srcsOffset; i < srcsEndOffset; len += (long)src.remaining(), ++i) {
                src = srcs[i];
                if (src != null) continue;
                throw new IllegalArgumentException("srcs[" + i + "] is null");
            }
            i = this;
            // MONITORENTER : i
            if (this.isInboundDone()) {
                if (!this.isOutboundDone() && !this.isDestroyed()) {
                    v0 = ReferenceCountedOpenSslEngine.NEED_WRAP_CLOSED;
                    // MONITOREXIT : i
                    return v0;
                }
                v0 = ReferenceCountedOpenSslEngine.CLOSED_NOT_HANDSHAKING;
                return v0;
            }
            status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
            if (this.handshakeState != HandshakeState.FINISHED) {
                if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY) {
                    this.handshakeState = HandshakeState.STARTED_IMPLICITLY;
                }
                if ((status = this.handshake()) == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                    // MONITOREXIT : i
                    return ReferenceCountedOpenSslEngine.NEED_WRAP_OK;
                }
                if (this.isInboundDone) {
                    // MONITOREXIT : i
                    return ReferenceCountedOpenSslEngine.NEED_WRAP_CLOSED;
                }
            }
            sslPending = this.sslPending0();
            if (this.jdkCompatibilityMode) {
                if (len < 5L) {
                    // MONITOREXIT : i
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0);
                }
                packetLength = SslUtils.getEncryptedPacketLength(srcs, srcsOffset);
                if (packetLength == -2) {
                    throw new NotSslRecordException("not an SSL/TLS record");
                }
                packetLengthDataOnly = packetLength - 5;
                if ((long)packetLengthDataOnly > capacity) {
                    if (packetLengthDataOnly > ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE) {
                        throw new SSLException("Illegal packet length: " + packetLengthDataOnly + " > " + this.session.getApplicationBufferSize());
                    }
                    this.session.tryExpandApplicationBufferSize(packetLengthDataOnly);
                    // MONITOREXIT : i
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_OVERFLOW, status, 0, 0);
                }
                if (len < (long)packetLength) {
                    // MONITOREXIT : i
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0);
                }
            } else {
                if (len == 0L && sslPending <= 0) {
                    // MONITOREXIT : i
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0);
                }
                if (capacity == 0L) {
                    // MONITOREXIT : i
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_OVERFLOW, status, 0, 0);
                }
                packetLength = (int)Math.min(Integer.MAX_VALUE, len);
            }
            if (!ReferenceCountedOpenSslEngine.$assertionsDisabled && srcsOffset >= srcsEndOffset) {
                throw new AssertionError();
            }
            if (!ReferenceCountedOpenSslEngine.$assertionsDisabled && capacity <= 0L) {
                throw new AssertionError();
            }
            bytesProduced = 0;
            bytesConsumed = 0;
            try {
                do {
                    if ((remaining = (src = srcs[srcsOffset]).remaining()) == 0) {
                        if (sslPending <= 0) {
                            if (++srcsOffset < srcsEndOffset) continue;
                            break;
                        }
                        bioWriteCopyBuf = null;
                        pendingEncryptedBytes = SSL.bioLengthByteBuffer((long)this.networkBIO);
                    } else {
                        pendingEncryptedBytes = Math.min(packetLength, remaining);
                        bioWriteCopyBuf = this.writeEncryptedData(src, pendingEncryptedBytes);
                    }
                    try {
                        block51 : {
                            do lbl-1000: // 4 sources:
                            {
                                block52 : {
                                    if ((dst = dsts[dstsOffset]).hasRemaining()) break block52;
                                    if (++dstsOffset < dstsEndOffset) ** GOTO lbl-1000
                                    break block50;
                                }
                                bytesRead = this.readPlaintextData(dst);
                                localBytesConsumed = pendingEncryptedBytes - SSL.bioLengthByteBuffer((long)this.networkBIO);
                                bytesConsumed += localBytesConsumed;
                                packetLength -= localBytesConsumed;
                                pendingEncryptedBytes -= localBytesConsumed;
                                src.position(src.position() + localBytesConsumed);
                                if (bytesRead <= 0) break block51;
                                bytesProduced += bytesRead;
                                if (dst.hasRemaining()) continue;
                                sslPending = this.sslPending0();
                                if (++dstsOffset < dstsEndOffset) ** GOTO lbl-1000
                                var26_27 = sslPending > 0 ? this.newResult(SSLEngineResult.Status.BUFFER_OVERFLOW, status, bytesConsumed, bytesProduced) : this.newResultMayFinishHandshake(this.isInboundDone() != false ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
                                return var26_27;
                            } while (packetLength != 0 && !this.jdkCompatibilityMode);
                            break;
                        }
                        sslError = SSL.getError((long)this.ssl, (int)bytesRead);
                        if (sslError != SSL.SSL_ERROR_WANT_READ && sslError != SSL.SSL_ERROR_WANT_WRITE) {
                            if (sslError != SSL.SSL_ERROR_ZERO_RETURN) {
                                var27_29 = this.sslReadErrorResult(SSL.getLastErrorNumber(), bytesConsumed, bytesProduced);
                                return var27_29;
                            }
                            if (!this.receivedShutdown) {
                                this.closeAll();
                            }
                            var27_28 = this.newResultMayFinishHandshake(this.isInboundDone() != false ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
                            return var27_28;
                        }
                        if (++srcsOffset < srcsEndOffset) continue;
                    }
                    finally {
                        if (bioWriteCopyBuf == null) continue;
                        bioWriteCopyBuf.release();
                        continue;
                    }
                    break;
                } while (true);
            }
            finally {
                SSL.bioClearByteBuffer((long)this.networkBIO);
                this.rejectRemoteInitiatedRenegotiation();
            }
        }
        if (!this.receivedShutdown && (SSL.getShutdown((long)this.ssl) & SSL.SSL_RECEIVED_SHUTDOWN) == SSL.SSL_RECEIVED_SHUTDOWN) {
            this.closeAll();
        }
        if (this.isInboundDone()) {
            v1 = SSLEngineResult.Status.CLOSED;
            return this.newResultMayFinishHandshake(v1, status, bytesConsumed, bytesProduced);
        }
        v1 = SSLEngineResult.Status.OK;
        // MONITOREXIT : i
        return this.newResultMayFinishHandshake(v1, status, bytesConsumed, bytesProduced);
    }

    private SSLEngineResult sslReadErrorResult(int err, int bytesConsumed, int bytesProduced) throws SSLException {
        String errStr = SSL.getErrorString((long)err);
        if (SSL.bioLengthNonApplication((long)this.networkBIO) > 0) {
            if (this.handshakeException == null && this.handshakeState != HandshakeState.FINISHED) {
                this.handshakeException = new SSLHandshakeException(errStr);
            }
            return new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, bytesConsumed, bytesProduced);
        }
        throw this.shutdownWithError("SSL_read", errStr);
    }

    private void closeAll() throws SSLException {
        this.receivedShutdown = true;
        this.closeOutbound();
        this.closeInbound();
    }

    private void rejectRemoteInitiatedRenegotiation() throws SSLHandshakeException {
        if (this.rejectRemoteInitiatedRenegotiation && !this.isDestroyed() && SSL.getHandshakeCount((long)this.ssl) > 1) {
            this.shutdown();
            throw new SSLHandshakeException("remote-initiated renegotiation not allowed");
        }
    }

    public final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dsts) throws SSLException {
        return this.unwrap(srcs, 0, srcs.length, dsts, 0, dsts.length);
    }

    private ByteBuffer[] singleSrcBuffer(ByteBuffer src) {
        this.singleSrcBuffer[0] = src;
        return this.singleSrcBuffer;
    }

    private void resetSingleSrcBuffer() {
        this.singleSrcBuffer[0] = null;
    }

    private ByteBuffer[] singleDstBuffer(ByteBuffer src) {
        this.singleDstBuffer[0] = src;
        return this.singleDstBuffer;
    }

    private void resetSingleDstBuffer() {
        this.singleDstBuffer[0] = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
        try {
            SSLEngineResult sSLEngineResult = this.unwrap(this.singleSrcBuffer(src), 0, 1, dsts, offset, length);
            return sSLEngineResult;
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        try {
            SSLEngineResult sSLEngineResult = this.wrap(this.singleSrcBuffer(src), dst);
            return sSLEngineResult;
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        try {
            SSLEngineResult sSLEngineResult = this.unwrap(this.singleSrcBuffer(src), this.singleDstBuffer(dst));
            return sSLEngineResult;
        }
        finally {
            this.resetSingleSrcBuffer();
            this.resetSingleDstBuffer();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
        try {
            SSLEngineResult sSLEngineResult = this.unwrap(this.singleSrcBuffer(src), dsts);
            return sSLEngineResult;
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }

    @Override
    public final Runnable getDelegatedTask() {
        return null;
    }

    @Override
    public final synchronized void closeInbound() throws SSLException {
        if (this.isInboundDone) {
            return;
        }
        this.isInboundDone = true;
        if (this.isOutboundDone()) {
            this.shutdown();
        }
        if (this.handshakeState != HandshakeState.NOT_STARTED && !this.receivedShutdown) {
            throw new SSLException("Inbound closed before receiving peer's close_notify: possible truncation attack?");
        }
    }

    @Override
    public final synchronized boolean isInboundDone() {
        return this.isInboundDone;
    }

    @Override
    public final synchronized void closeOutbound() {
        if (this.outboundClosed) {
            return;
        }
        this.outboundClosed = true;
        if (this.handshakeState != HandshakeState.NOT_STARTED && !this.isDestroyed()) {
            int mode = SSL.getShutdown((long)this.ssl);
            if ((mode & SSL.SSL_SENT_SHUTDOWN) != SSL.SSL_SENT_SHUTDOWN) {
                this.doSSLShutdown();
            }
        } else {
            this.shutdown();
        }
    }

    private boolean doSSLShutdown() {
        if (SSL.isInInit((long)this.ssl) != 0) {
            return false;
        }
        int err = SSL.shutdownSSL((long)this.ssl);
        if (err < 0) {
            int sslErr = SSL.getError((long)this.ssl, (int)err);
            if (sslErr == SSL.SSL_ERROR_SYSCALL || sslErr == SSL.SSL_ERROR_SSL) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SSL_shutdown failed: OpenSSL error: {}", (Object)SSL.getLastError());
                }
                this.shutdown();
                return false;
            }
            SSL.clearError();
        }
        return true;
    }

    @Override
    public final synchronized boolean isOutboundDone() {
        return this.outboundClosed && (this.networkBIO == 0L || SSL.bioLengthNonApplication((long)this.networkBIO) == 0);
    }

    @Override
    public final String[] getSupportedCipherSuites() {
        return OpenSsl.AVAILABLE_CIPHER_SUITES.toArray(new String[OpenSsl.AVAILABLE_CIPHER_SUITES.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final String[] getEnabledCipherSuites() {
        String[] enabled;
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            if (this.isDestroyed()) {
                return EmptyArrays.EMPTY_STRINGS;
            }
            enabled = SSL.getCiphers((long)this.ssl);
        }
        if (enabled == null) {
            return EmptyArrays.EMPTY_STRINGS;
        }
        referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            for (int i = 0; i < enabled.length; ++i) {
                String mapped = this.toJavaCipherSuite(enabled[i]);
                if (mapped == null) continue;
                enabled[i] = mapped;
            }
        }
        return enabled;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void setEnabledCipherSuites(String[] cipherSuites) {
        ObjectUtil.checkNotNull(cipherSuites, "cipherSuites");
        StringBuilder buf = new StringBuilder();
        for (String c : cipherSuites) {
            if (c == null) break;
            String converted = CipherSuiteConverter.toOpenSsl(c);
            if (converted == null) {
                converted = c;
            }
            if (!OpenSsl.isCipherSuiteAvailable(converted)) {
                throw new IllegalArgumentException("unsupported cipher suite: " + c + '(' + converted + ')');
            }
            buf.append(converted);
            buf.append(':');
        }
        if (buf.length() == 0) {
            throw new IllegalArgumentException("empty cipher suites");
        }
        buf.setLength(buf.length() - 1);
        String cipherSuiteSpec = buf.toString();
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            if (!this.isDestroyed()) {
                try {
                    SSL.setCipherSuites((long)this.ssl, (String)cipherSuiteSpec);
                }
                catch (Exception e) {
                    throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec, e);
                }
            } else {
                throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec);
            }
        }
    }

    @Override
    public final String[] getSupportedProtocols() {
        return OpenSsl.SUPPORTED_PROTOCOLS_SET.toArray(new String[OpenSsl.SUPPORTED_PROTOCOLS_SET.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final String[] getEnabledProtocols() {
        int opts;
        ArrayList<String> enabled = new ArrayList<String>(6);
        enabled.add("SSLv2Hello");
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            if (this.isDestroyed()) {
                return enabled.toArray(new String[1]);
            }
            opts = SSL.getOptions((long)this.ssl);
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1, "TLSv1")) {
            enabled.add("TLSv1");
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_1, "TLSv1.1")) {
            enabled.add("TLSv1.1");
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_2, "TLSv1.2")) {
            enabled.add("TLSv1.2");
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled(opts, SSL.SSL_OP_NO_SSLv2, "SSLv2")) {
            enabled.add("SSLv2");
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled(opts, SSL.SSL_OP_NO_SSLv3, "SSLv3")) {
            enabled.add("SSLv3");
        }
        return enabled.toArray(new String[enabled.size()]);
    }

    private static boolean isProtocolEnabled(int opts, int disableMask, String protocolString) {
        return (opts & disableMask) == 0 && OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(protocolString);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void setEnabledProtocols(String[] protocols) {
        if (protocols == null) {
            throw new IllegalArgumentException();
        }
        int minProtocolIndex = OPENSSL_OP_NO_PROTOCOLS.length;
        int maxProtocolIndex = 0;
        for (String p : protocols) {
            if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(p)) {
                throw new IllegalArgumentException("Protocol " + p + " is not supported.");
            }
            if (p.equals("SSLv2")) {
                if (minProtocolIndex > 0) {
                    minProtocolIndex = 0;
                }
                if (maxProtocolIndex >= 0) continue;
                maxProtocolIndex = 0;
                continue;
            }
            if (p.equals("SSLv3")) {
                if (minProtocolIndex > 1) {
                    minProtocolIndex = 1;
                }
                if (maxProtocolIndex >= 1) continue;
                maxProtocolIndex = 1;
                continue;
            }
            if (p.equals("TLSv1")) {
                if (minProtocolIndex > 2) {
                    minProtocolIndex = 2;
                }
                if (maxProtocolIndex >= 2) continue;
                maxProtocolIndex = 2;
                continue;
            }
            if (p.equals("TLSv1.1")) {
                if (minProtocolIndex > 3) {
                    minProtocolIndex = 3;
                }
                if (maxProtocolIndex >= 3) continue;
                maxProtocolIndex = 3;
                continue;
            }
            if (!p.equals("TLSv1.2")) continue;
            if (minProtocolIndex > 4) {
                minProtocolIndex = 4;
            }
            if (maxProtocolIndex >= 4) continue;
            maxProtocolIndex = 4;
        }
        Object object = this;
        synchronized (object) {
            int opts;
            if (!this.isDestroyed()) {
                int i;
                SSL.clearOptions((long)this.ssl, (int)(SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2));
                opts = 0;
                for (i = 0; i < minProtocolIndex; ++i) {
                    opts |= OPENSSL_OP_NO_PROTOCOLS[i];
                }
                assert (maxProtocolIndex != Integer.MAX_VALUE);
                for (i = maxProtocolIndex + 1; i < OPENSSL_OP_NO_PROTOCOLS.length; ++i) {
                    opts |= OPENSSL_OP_NO_PROTOCOLS[i];
                }
            } else {
                throw new IllegalStateException("failed to enable protocols: " + Arrays.asList(protocols));
            }
            SSL.setOptions((long)this.ssl, (int)opts);
        }
    }

    @Override
    public final SSLSession getSession() {
        return this.session;
    }

    @Override
    public final synchronized void beginHandshake() throws SSLException {
        switch (this.handshakeState) {
            case STARTED_IMPLICITLY: {
                this.checkEngineClosed(BEGIN_HANDSHAKE_ENGINE_CLOSED);
                this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
                this.calculateMaxWrapOverhead();
                break;
            }
            case STARTED_EXPLICITLY: {
                break;
            }
            case FINISHED: {
                if (this.clientMode) {
                    throw RENEGOTIATION_UNSUPPORTED;
                }
                int status = SSL.renegotiate((long)this.ssl);
                if (status != 1 || (status = SSL.doHandshake((long)this.ssl)) != 1) {
                    int err = SSL.getError((long)this.ssl, (int)status);
                    if (err == SSL.SSL_ERROR_WANT_READ || err == SSL.SSL_ERROR_WANT_WRITE) {
                        this.renegotiationPending = true;
                        this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
                        this.lastAccessed = System.currentTimeMillis();
                        return;
                    }
                    throw this.shutdownWithError("renegotiation failed");
                }
                SSL.setState((long)this.ssl, (int)SSL.SSL_ST_ACCEPT);
                this.lastAccessed = System.currentTimeMillis();
            }
            case NOT_STARTED: {
                this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
                this.handshake();
                this.calculateMaxWrapOverhead();
                break;
            }
            default: {
                throw new Error();
            }
        }
    }

    private void checkEngineClosed(SSLException cause) throws SSLException {
        if (this.isDestroyed()) {
            throw cause;
        }
    }

    private static SSLEngineResult.HandshakeStatus pendingStatus(int pendingStatus) {
        return pendingStatus > 0 ? SSLEngineResult.HandshakeStatus.NEED_WRAP : SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
    }

    private static boolean isEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    private static boolean isEmpty(byte[] cert) {
        return cert == null || cert.length == 0;
    }

    private SSLEngineResult.HandshakeStatus handshake() throws SSLException {
        int code;
        if (this.handshakeState == HandshakeState.FINISHED) {
            return SSLEngineResult.HandshakeStatus.FINISHED;
        }
        this.checkEngineClosed(HANDSHAKE_ENGINE_CLOSED);
        SSLHandshakeException exception = this.handshakeException;
        if (exception != null) {
            if (SSL.bioLengthNonApplication((long)this.networkBIO) > 0) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            this.handshakeException = null;
            this.shutdown();
            throw exception;
        }
        this.engineMap.add(this);
        if (this.lastAccessed == -1L) {
            this.lastAccessed = System.currentTimeMillis();
        }
        if (!this.certificateSet && this.keyMaterialManager != null) {
            this.certificateSet = true;
            this.keyMaterialManager.setKeyMaterial(this);
        }
        if ((code = SSL.doHandshake((long)this.ssl)) <= 0) {
            if (this.handshakeException != null) {
                exception = this.handshakeException;
                this.handshakeException = null;
                this.shutdown();
                throw exception;
            }
            int sslError = SSL.getError((long)this.ssl, (int)code);
            if (sslError == SSL.SSL_ERROR_WANT_READ || sslError == SSL.SSL_ERROR_WANT_WRITE) {
                return ReferenceCountedOpenSslEngine.pendingStatus(SSL.bioLengthNonApplication((long)this.networkBIO));
            }
            throw this.shutdownWithError("SSL_do_handshake");
        }
        this.session.handshakeFinished();
        this.engineMap.remove(this.ssl);
        return SSLEngineResult.HandshakeStatus.FINISHED;
    }

    private SSLEngineResult.HandshakeStatus mayFinishHandshake(SSLEngineResult.HandshakeStatus status) throws SSLException {
        if (status == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING && this.handshakeState != HandshakeState.FINISHED) {
            return this.handshake();
        }
        return status;
    }

    @Override
    public final synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        return this.needPendingStatus() ? ReferenceCountedOpenSslEngine.pendingStatus(SSL.bioLengthNonApplication((long)this.networkBIO)) : SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }

    private SSLEngineResult.HandshakeStatus getHandshakeStatus(int pending) {
        return this.needPendingStatus() ? ReferenceCountedOpenSslEngine.pendingStatus(pending) : SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }

    private boolean needPendingStatus() {
        return this.handshakeState != HandshakeState.NOT_STARTED && !this.isDestroyed() && (this.handshakeState != HandshakeState.FINISHED || this.isInboundDone() || this.isOutboundDone());
    }

    private String toJavaCipherSuite(String openSslCipherSuite) {
        if (openSslCipherSuite == null) {
            return null;
        }
        String prefix = ReferenceCountedOpenSslEngine.toJavaCipherSuitePrefix(SSL.getVersion((long)this.ssl));
        return CipherSuiteConverter.toJava(openSslCipherSuite, prefix);
    }

    private static String toJavaCipherSuitePrefix(String protocolVersion) {
        int c = protocolVersion == null || protocolVersion.isEmpty() ? 0 : (int)protocolVersion.charAt(0);
        switch (c) {
            case 84: {
                return "TLS";
            }
            case 83: {
                return "SSL";
            }
        }
        return "UNKNOWN";
    }

    @Override
    public final void setUseClientMode(boolean clientMode) {
        if (clientMode != this.clientMode) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public final boolean getUseClientMode() {
        return this.clientMode;
    }

    @Override
    public final void setNeedClientAuth(boolean b) {
        this.setClientAuth(b ? ClientAuth.REQUIRE : ClientAuth.NONE);
    }

    @Override
    public final boolean getNeedClientAuth() {
        return this.clientAuth == ClientAuth.REQUIRE;
    }

    @Override
    public final void setWantClientAuth(boolean b) {
        this.setClientAuth(b ? ClientAuth.OPTIONAL : ClientAuth.NONE);
    }

    @Override
    public final boolean getWantClientAuth() {
        return this.clientAuth == ClientAuth.OPTIONAL;
    }

    public final synchronized void setVerify(int verifyMode, int depth) {
        SSL.setVerify((long)this.ssl, (int)verifyMode, (int)depth);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setClientAuth(ClientAuth mode) {
        if (this.clientMode) {
            return;
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        synchronized (referenceCountedOpenSslEngine) {
            if (this.clientAuth == mode) {
                return;
            }
            switch (mode) {
                case NONE: {
                    SSL.setVerify((long)this.ssl, (int)0, (int)10);
                    break;
                }
                case REQUIRE: {
                    SSL.setVerify((long)this.ssl, (int)2, (int)10);
                    break;
                }
                case OPTIONAL: {
                    SSL.setVerify((long)this.ssl, (int)1, (int)10);
                    break;
                }
                default: {
                    throw new Error(mode.toString());
                }
            }
            this.clientAuth = mode;
        }
    }

    @Override
    public final void setEnableSessionCreation(boolean b) {
        if (b) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public final boolean getEnableSessionCreation() {
        return false;
    }

    @Override
    public final synchronized SSLParameters getSSLParameters() {
        SSLParameters sslParameters = super.getSSLParameters();
        int version = PlatformDependent.javaVersion();
        if (version >= 7) {
            sslParameters.setEndpointIdentificationAlgorithm(this.endPointIdentificationAlgorithm);
            Java7SslParametersUtils.setAlgorithmConstraints(sslParameters, this.algorithmConstraints);
            if (version >= 8) {
                if (this.sniHostNames != null) {
                    Java8SslUtils.setSniHostNames(sslParameters, this.sniHostNames);
                }
                if (!this.isDestroyed()) {
                    Java8SslUtils.setUseCipherSuitesOrder(sslParameters, (SSL.getOptions((long)this.ssl) & SSL.SSL_OP_CIPHER_SERVER_PREFERENCE) != 0);
                }
                Java8SslUtils.setSNIMatchers(sslParameters, this.matchers);
            }
        }
        return sslParameters;
    }

    @Override
    public final synchronized void setSSLParameters(SSLParameters sslParameters) {
        int version = PlatformDependent.javaVersion();
        if (version >= 7) {
            String endPointIdentificationAlgorithm;
            if (sslParameters.getAlgorithmConstraints() != null) {
                throw new IllegalArgumentException("AlgorithmConstraints are not supported.");
            }
            if (version >= 8) {
                if (!this.isDestroyed()) {
                    if (this.clientMode) {
                        List<String> sniHostNames = Java8SslUtils.getSniHostNames(sslParameters);
                        for (String name : sniHostNames) {
                            SSL.setTlsExtHostName((long)this.ssl, (String)name);
                        }
                        this.sniHostNames = sniHostNames;
                    }
                    if (Java8SslUtils.getUseCipherSuitesOrder(sslParameters)) {
                        SSL.setOptions((long)this.ssl, (int)SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
                    } else {
                        SSL.clearOptions((long)this.ssl, (int)SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
                    }
                }
                this.matchers = sslParameters.getSNIMatchers();
            }
            boolean endPointVerificationEnabled = (endPointIdentificationAlgorithm = sslParameters.getEndpointIdentificationAlgorithm()) != null && !endPointIdentificationAlgorithm.isEmpty();
            SSL.setHostNameValidation((long)this.ssl, (int)0, (String)(endPointVerificationEnabled ? this.getPeerHost() : null));
            if (this.clientMode && endPointVerificationEnabled) {
                SSL.setVerify((long)this.ssl, (int)2, (int)-1);
            }
            this.endPointIdentificationAlgorithm = endPointIdentificationAlgorithm;
            this.algorithmConstraints = sslParameters.getAlgorithmConstraints();
        }
        super.setSSLParameters(sslParameters);
    }

    private boolean isDestroyed() {
        return this.destroyed != 0;
    }

    final boolean checkSniHostnameMatch(String hostname) {
        return Java8SslUtils.checkSniHostnameMatch(this.matchers, hostname);
    }

    @Override
    public String getNegotiatedApplicationProtocol() {
        return this.applicationProtocol;
    }

    private final class OpenSslSession
    implements SSLSession {
        private final OpenSslSessionContext sessionContext;
        private X509Certificate[] x509PeerCerts;
        private Certificate[] peerCerts;
        private String protocol;
        private String cipher;
        private byte[] id;
        private long creationTime;
        private volatile int applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
        private Map<String, Object> values;

        OpenSslSession(OpenSslSessionContext sessionContext) {
            this.sessionContext = sessionContext;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte[] getId() {
            ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
            synchronized (referenceCountedOpenSslEngine) {
                if (this.id == null) {
                    return EmptyArrays.EMPTY_BYTES;
                }
                return (byte[])this.id.clone();
            }
        }

        @Override
        public SSLSessionContext getSessionContext() {
            return this.sessionContext;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long getCreationTime() {
            ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
            synchronized (referenceCountedOpenSslEngine) {
                if (this.creationTime == 0L && !ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                    this.creationTime = SSL.getTime((long)ReferenceCountedOpenSslEngine.this.ssl) * 1000L;
                }
            }
            return this.creationTime;
        }

        @Override
        public long getLastAccessedTime() {
            long lastAccessed = ReferenceCountedOpenSslEngine.this.lastAccessed;
            return lastAccessed == -1L ? this.getCreationTime() : lastAccessed;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void invalidate() {
            ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
            synchronized (referenceCountedOpenSslEngine) {
                if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                    SSL.setTimeout((long)ReferenceCountedOpenSslEngine.this.ssl, (long)0L);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isValid() {
            ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
            synchronized (referenceCountedOpenSslEngine) {
                if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                    return System.currentTimeMillis() - SSL.getTimeout((long)ReferenceCountedOpenSslEngine.this.ssl) * 1000L < SSL.getTime((long)ReferenceCountedOpenSslEngine.this.ssl) * 1000L;
                }
            }
            return false;
        }

        @Override
        public void putValue(String name, Object value) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            if (value == null) {
                throw new NullPointerException("value");
            }
            Map<String, Object> values = this.values;
            if (values == null) {
                values = this.values = new HashMap<String, Object>(2);
            }
            Object old = values.put(name, value);
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueBound(new SSLSessionBindingEvent(this, name));
            }
            this.notifyUnbound(old, name);
        }

        @Override
        public Object getValue(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            if (this.values == null) {
                return null;
            }
            return this.values.get(name);
        }

        @Override
        public void removeValue(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            Map<String, Object> values = this.values;
            if (values == null) {
                return;
            }
            Object old = values.remove(name);
            this.notifyUnbound(old, name);
        }

        @Override
        public String[] getValueNames() {
            Map<String, Object> values = this.values;
            if (values == null || values.isEmpty()) {
                return EmptyArrays.EMPTY_STRINGS;
            }
            return values.keySet().toArray(new String[values.size()]);
        }

        private void notifyUnbound(Object value, String name) {
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueUnbound(new SSLSessionBindingEvent(this, name));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void handshakeFinished() throws SSLException {
            ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
            synchronized (referenceCountedOpenSslEngine) {
                if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                    throw new SSLException("Already closed");
                }
                this.id = SSL.getSessionId((long)ReferenceCountedOpenSslEngine.this.ssl);
                this.cipher = ReferenceCountedOpenSslEngine.this.toJavaCipherSuite(SSL.getCipherForSSL((long)ReferenceCountedOpenSslEngine.this.ssl));
                this.protocol = SSL.getVersion((long)ReferenceCountedOpenSslEngine.this.ssl);
                this.initPeerCerts();
                this.selectApplicationProtocol();
                ReferenceCountedOpenSslEngine.this.calculateMaxWrapOverhead();
                ReferenceCountedOpenSslEngine.this.handshakeState = HandshakeState.FINISHED;
            }
        }

        private void initPeerCerts() {
            byte[][] chain = SSL.getPeerCertChain((long)ReferenceCountedOpenSslEngine.this.ssl);
            if (ReferenceCountedOpenSslEngine.this.clientMode) {
                if (ReferenceCountedOpenSslEngine.isEmpty((Object[])chain)) {
                    this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
                    this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
                } else {
                    this.peerCerts = new Certificate[chain.length];
                    this.x509PeerCerts = new X509Certificate[chain.length];
                    this.initCerts(chain, 0);
                }
            } else {
                byte[] clientCert = SSL.getPeerCertificate((long)ReferenceCountedOpenSslEngine.this.ssl);
                if (ReferenceCountedOpenSslEngine.isEmpty(clientCert)) {
                    this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
                    this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
                } else if (ReferenceCountedOpenSslEngine.isEmpty((Object[])chain)) {
                    this.peerCerts = new Certificate[]{new OpenSslX509Certificate(clientCert)};
                    this.x509PeerCerts = new X509Certificate[]{new OpenSslJavaxX509Certificate(clientCert)};
                } else {
                    this.peerCerts = new Certificate[chain.length + 1];
                    this.x509PeerCerts = new X509Certificate[chain.length + 1];
                    this.peerCerts[0] = new OpenSslX509Certificate(clientCert);
                    this.x509PeerCerts[0] = new OpenSslJavaxX509Certificate(clientCert);
                    this.initCerts(chain, 1);
                }
            }
        }

        private void initCerts(byte[][] chain, int startPos) {
            for (int i = 0; i < chain.length; ++i) {
                int certPos = startPos + i;
                this.peerCerts[certPos] = new OpenSslX509Certificate(chain[i]);
                this.x509PeerCerts[certPos] = new OpenSslJavaxX509Certificate(chain[i]);
            }
        }

        private void selectApplicationProtocol() throws SSLException {
            ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior = ReferenceCountedOpenSslEngine.this.apn.selectedListenerFailureBehavior();
            List<String> protocols = ReferenceCountedOpenSslEngine.this.apn.protocols();
            switch (ReferenceCountedOpenSslEngine.this.apn.protocol()) {
                case NONE: {
                    break;
                }
                case ALPN: {
                    String applicationProtocol = SSL.getAlpnSelected((long)ReferenceCountedOpenSslEngine.this.ssl);
                    if (applicationProtocol == null) break;
                    ReferenceCountedOpenSslEngine.this.applicationProtocol = this.selectApplicationProtocol(protocols, behavior, applicationProtocol);
                    break;
                }
                case NPN: {
                    String applicationProtocol = SSL.getNextProtoNegotiated((long)ReferenceCountedOpenSslEngine.this.ssl);
                    if (applicationProtocol == null) break;
                    ReferenceCountedOpenSslEngine.this.applicationProtocol = this.selectApplicationProtocol(protocols, behavior, applicationProtocol);
                    break;
                }
                case NPN_AND_ALPN: {
                    String applicationProtocol = SSL.getAlpnSelected((long)ReferenceCountedOpenSslEngine.this.ssl);
                    if (applicationProtocol == null) {
                        applicationProtocol = SSL.getNextProtoNegotiated((long)ReferenceCountedOpenSslEngine.this.ssl);
                    }
                    if (applicationProtocol == null) break;
                    ReferenceCountedOpenSslEngine.this.applicationProtocol = this.selectApplicationProtocol(protocols, behavior, applicationProtocol);
                    break;
                }
                default: {
                    throw new Error();
                }
            }
        }

        private String selectApplicationProtocol(List<String> protocols, ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior, String applicationProtocol) throws SSLException {
            if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT) {
                return applicationProtocol;
            }
            int size = protocols.size();
            assert (size > 0);
            if (protocols.contains(applicationProtocol)) {
                return applicationProtocol;
            }
            if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.CHOOSE_MY_LAST_PROTOCOL) {
                return protocols.get(size - 1);
            }
            throw new SSLException("unknown protocol " + applicationProtocol);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
            ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
            synchronized (referenceCountedOpenSslEngine) {
                if (ReferenceCountedOpenSslEngine.isEmpty(this.peerCerts)) {
                    throw new SSLPeerUnverifiedException("peer not verified");
                }
                return (Certificate[])this.peerCerts.clone();
            }
        }

        @Override
        public Certificate[] getLocalCertificates() {
            if (ReferenceCountedOpenSslEngine.this.localCerts == null) {
                return null;
            }
            return (Certificate[])ReferenceCountedOpenSslEngine.this.localCerts.clone();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
            ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
            synchronized (referenceCountedOpenSslEngine) {
                if (ReferenceCountedOpenSslEngine.isEmpty(this.x509PeerCerts)) {
                    throw new SSLPeerUnverifiedException("peer not verified");
                }
                return (X509Certificate[])this.x509PeerCerts.clone();
            }
        }

        @Override
        public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
            Certificate[] peer = this.getPeerCertificates();
            return ((java.security.cert.X509Certificate)peer[0]).getSubjectX500Principal();
        }

        @Override
        public Principal getLocalPrincipal() {
            Certificate[] local = ReferenceCountedOpenSslEngine.this.localCerts;
            if (local == null || local.length == 0) {
                return null;
            }
            return ((java.security.cert.X509Certificate)local[0]).getIssuerX500Principal();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getCipherSuite() {
            ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
            synchronized (referenceCountedOpenSslEngine) {
                if (this.cipher == null) {
                    return ReferenceCountedOpenSslEngine.INVALID_CIPHER;
                }
                return this.cipher;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public String getProtocol() {
            String protocol = this.protocol;
            if (protocol == null) {
                ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = ReferenceCountedOpenSslEngine.this;
                synchronized (referenceCountedOpenSslEngine) {
                    protocol = !ReferenceCountedOpenSslEngine.this.isDestroyed() ? SSL.getVersion((long)ReferenceCountedOpenSslEngine.this.ssl) : "";
                }
            }
            return protocol;
        }

        @Override
        public String getPeerHost() {
            return ReferenceCountedOpenSslEngine.this.getPeerHost();
        }

        @Override
        public int getPeerPort() {
            return ReferenceCountedOpenSslEngine.this.getPeerPort();
        }

        @Override
        public int getPacketBufferSize() {
            return ReferenceCountedOpenSslEngine.this.maxEncryptedPacketLength();
        }

        @Override
        public int getApplicationBufferSize() {
            return this.applicationBufferSize;
        }

        void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
            if (packetLengthDataOnly > ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH && this.applicationBufferSize != MAX_RECORD_SIZE) {
                this.applicationBufferSize = MAX_RECORD_SIZE;
            }
        }
    }

    private static enum HandshakeState {
        NOT_STARTED,
        STARTED_IMPLICITLY,
        STARTED_EXPLICITLY,
        FINISHED;
        

        private HandshakeState() {
        }
    }

}


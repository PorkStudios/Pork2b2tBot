/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.ssl.SniCompletionEvent;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

public abstract class AbstractSniHandler<T>
extends ByteToMessageDecoder
implements ChannelOutboundHandler {
    private static final int MAX_SSL_RECORDS = 4;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractSniHandler.class);
    private boolean handshakeFailed;
    private boolean suppressRead;
    private boolean readPending;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        block17 : {
            if (this.suppressRead) return;
            if (this.handshakeFailed) return;
            int writerIndex = in.writerIndex();
            try {
                block9 : for (int i = 0; i < 4; ++i) {
                    int readerIndex = in.readerIndex();
                    int readableBytes = writerIndex - readerIndex;
                    if (readableBytes < 5) {
                        return;
                    }
                    short command = in.getUnsignedByte(readerIndex);
                    block2 : switch (command) {
                        case 20: 
                        case 21: {
                            int len = SslUtils.getEncryptedPacketLength(in, readerIndex);
                            if (len == -2) {
                                this.handshakeFailed = true;
                                NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
                                in.skipBytes(in.readableBytes());
                                ctx.fireUserEventTriggered(new SniCompletionEvent(e));
                                SslUtils.notifyHandshakeFailure(ctx, e, true);
                                throw e;
                            }
                            if (len == -1) return;
                            if (writerIndex - readerIndex - 5 < len) {
                                return;
                            }
                            in.skipBytes(len);
                            continue block9;
                        }
                        case 22: {
                            short majorVersion = in.getUnsignedByte(readerIndex + 1);
                            if (majorVersion == 3) {
                                int extensionsLength;
                                int extensionsLimit;
                                int packetLength = in.getUnsignedShort(readerIndex + 3) + 5;
                                if (readableBytes < packetLength) {
                                    return;
                                }
                                int endOffset = readerIndex + packetLength;
                                int offset = readerIndex + 43;
                                if (endOffset - offset < 6) break;
                                short sessionIdLength = in.getUnsignedByte(offset);
                                int cipherSuitesLength = in.getUnsignedShort(offset += sessionIdLength + 1);
                                short compressionMethodLength = in.getUnsignedByte(offset += cipherSuitesLength + 2);
                                offset += compressionMethodLength + 1;
                                if ((extensionsLimit = (offset += 2) + (extensionsLength = in.getUnsignedShort(offset))) > endOffset) break;
                                while (extensionsLimit - offset >= 4) {
                                    int extensionLength;
                                    int extensionType = in.getUnsignedShort(offset);
                                    offset += 2;
                                    if (extensionsLimit - (offset += 2) < (extensionLength = in.getUnsignedShort(offset))) break block2;
                                    if (extensionType == 0) {
                                        int serverNameLength;
                                        if (extensionsLimit - (offset += 2) < 3) break block2;
                                        short serverNameType = in.getUnsignedByte(offset);
                                        ++offset;
                                        if (serverNameType != 0 || extensionsLimit - (offset += 2) < (serverNameLength = in.getUnsignedShort(offset))) break block2;
                                        String hostname = in.toString(offset, serverNameLength, CharsetUtil.US_ASCII);
                                        try {
                                            this.select(ctx, hostname.toLowerCase(Locale.US));
                                            return;
                                        }
                                        catch (Throwable t) {
                                            PlatformDependent.throwException(t);
                                        }
                                        return;
                                    }
                                    offset += extensionLength;
                                }
                                break;
                            }
                        }
                        default: {
                            break;
                        }
                    }
                    break;
                }
            }
            catch (NotSslRecordException e) {
                throw e;
            }
            catch (Exception e) {
                if (!logger.isDebugEnabled()) break block17;
                logger.debug("Unexpected client hello packet: " + ByteBufUtil.hexDump(in), e);
            }
        }
        this.select(ctx, null);
    }

    private void select(final ChannelHandlerContext ctx, final String hostname) throws Exception {
        Future<T> future = this.lookup(ctx, hostname);
        if (future.isDone()) {
            this.fireSniCompletionEvent(ctx, hostname, future);
            this.onLookupComplete(ctx, hostname, future);
        } else {
            this.suppressRead = true;
            future.addListener(new FutureListener<T>(){

                @Override
                public void operationComplete(Future<T> future) throws Exception {
                    try {
                        AbstractSniHandler.this.suppressRead = false;
                        try {
                            AbstractSniHandler.this.fireSniCompletionEvent(ctx, hostname, future);
                            AbstractSniHandler.this.onLookupComplete(ctx, hostname, future);
                        }
                        catch (DecoderException err) {
                            ctx.fireExceptionCaught(err);
                        }
                        catch (Exception cause) {
                            ctx.fireExceptionCaught(new DecoderException(cause));
                        }
                        catch (Throwable cause) {
                            ctx.fireExceptionCaught(cause);
                        }
                    }
                    finally {
                        if (AbstractSniHandler.this.readPending) {
                            AbstractSniHandler.this.readPending = false;
                            ctx.read();
                        }
                    }
                }
            });
        }
    }

    private void fireSniCompletionEvent(ChannelHandlerContext ctx, String hostname, Future<T> future) {
        Throwable cause = future.cause();
        if (cause == null) {
            ctx.fireUserEventTriggered(new SniCompletionEvent(hostname));
        } else {
            ctx.fireUserEventTriggered(new SniCompletionEvent(hostname, cause));
        }
    }

    protected abstract Future<T> lookup(ChannelHandlerContext var1, String var2) throws Exception;

    protected abstract void onLookupComplete(ChannelHandlerContext var1, String var2, Future<T> var3) throws Exception;

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (this.suppressRead) {
            this.readPending = true;
        } else {
            ctx.read();
        }
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionAdapter;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2FrameListenerDecorator;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2LocalFlowController;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;

public class DelegatingDecompressorFrameListener
extends Http2FrameListenerDecorator {
    private final Http2Connection connection;
    private final boolean strict;
    private boolean flowControllerInitialized;
    private final Http2Connection.PropertyKey propertyKey;

    public DelegatingDecompressorFrameListener(Http2Connection connection, Http2FrameListener listener) {
        this(connection, listener, true);
    }

    public DelegatingDecompressorFrameListener(Http2Connection connection, Http2FrameListener listener, boolean strict) {
        super(listener);
        this.connection = connection;
        this.strict = strict;
        this.propertyKey = connection.newKey();
        connection.addListener(new Http2ConnectionAdapter(){

            @Override
            public void onStreamRemoved(Http2Stream stream) {
                Http2Decompressor decompressor = DelegatingDecompressorFrameListener.this.decompressor(stream);
                if (decompressor != null) {
                    DelegatingDecompressorFrameListener.cleanup(decompressor);
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
        int nextBuf2;
        Http2Stream stream = this.connection.stream(streamId);
        Http2Decompressor decompressor = this.decompressor(stream);
        if (decompressor == null) {
            return this.listener.onDataRead(ctx, streamId, data, padding, endOfStream);
        }
        EmbeddedChannel channel = decompressor.decompressor();
        int compressedBytes = data.readableBytes() + padding;
        decompressor.incrementCompressedBytes(compressedBytes);
        channel.writeInbound(data.retain());
        ByteBuf buf = DelegatingDecompressorFrameListener.nextReadableBuf(channel);
        if (buf == null && endOfStream && channel.finish()) {
            buf = DelegatingDecompressorFrameListener.nextReadableBuf(channel);
        }
        if (buf == null) {
            if (endOfStream) {
                this.listener.onDataRead(ctx, streamId, Unpooled.EMPTY_BUFFER, padding, true);
            }
            decompressor.incrementDecompressedBytes(compressedBytes);
            return compressedBytes;
        }
        try {
            Http2LocalFlowController flowController = this.connection.local().flowController();
            decompressor.incrementDecompressedBytes(padding);
            do {
                boolean decompressedEndOfStream;
                ByteBuf nextBuf2;
                boolean bl = decompressedEndOfStream = (nextBuf2 = DelegatingDecompressorFrameListener.nextReadableBuf(channel)) == null && endOfStream;
                if (decompressedEndOfStream && channel.finish()) {
                    nextBuf2 = DelegatingDecompressorFrameListener.nextReadableBuf(channel);
                    decompressedEndOfStream = nextBuf2 == null;
                }
                decompressor.incrementDecompressedBytes(buf.readableBytes());
                flowController.consumeBytes(stream, this.listener.onDataRead(ctx, streamId, buf, padding, decompressedEndOfStream));
                if (nextBuf2 == null) break;
                padding = 0;
                buf.release();
                buf = nextBuf2;
            } while (true);
            nextBuf2 = 0;
        }
        catch (Throwable throwable) {
            try {
                buf.release();
                throw throwable;
            }
            catch (Http2Exception e) {
                throw e;
            }
            catch (Throwable t) {
                throw Http2Exception.streamError(stream.id(), Http2Error.INTERNAL_ERROR, t, "Decompressor error detected while delegating data read on streamId %d", stream.id());
            }
        }
        buf.release();
        return nextBuf2;
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream) throws Http2Exception {
        this.initDecompressor(ctx, streamId, headers, endStream);
        this.listener.onHeadersRead(ctx, streamId, headers, padding, endStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream) throws Http2Exception {
        this.initDecompressor(ctx, streamId, headers, endStream);
        this.listener.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
    }

    protected EmbeddedChannel newContentDecompressor(ChannelHandlerContext ctx, CharSequence contentEncoding) throws Http2Exception {
        if (HttpHeaderValues.GZIP.contentEqualsIgnoreCase(contentEncoding) || HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase(contentEncoding)) {
            return new EmbeddedChannel(ctx.channel().id(), ctx.channel().metadata().hasDisconnect(), ctx.channel().config(), ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        }
        if (HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase(contentEncoding) || HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase(contentEncoding)) {
            ZlibWrapper wrapper = this.strict ? ZlibWrapper.ZLIB : ZlibWrapper.ZLIB_OR_NONE;
            return new EmbeddedChannel(ctx.channel().id(), ctx.channel().metadata().hasDisconnect(), ctx.channel().config(), ZlibCodecFactory.newZlibDecoder(wrapper));
        }
        return null;
    }

    protected CharSequence getTargetContentEncoding(CharSequence contentEncoding) throws Http2Exception {
        return HttpHeaderValues.IDENTITY;
    }

    private void initDecompressor(ChannelHandlerContext ctx, int streamId, Http2Headers headers, boolean endOfStream) throws Http2Exception {
        Http2Stream stream = this.connection.stream(streamId);
        if (stream == null) {
            return;
        }
        Http2Decompressor decompressor = this.decompressor(stream);
        if (decompressor == null && !endOfStream) {
            EmbeddedChannel channel;
            CharSequence contentEncoding = (CharSequence)headers.get(HttpHeaderNames.CONTENT_ENCODING);
            if (contentEncoding == null) {
                contentEncoding = HttpHeaderValues.IDENTITY;
            }
            if ((channel = this.newContentDecompressor(ctx, contentEncoding)) != null) {
                decompressor = new Http2Decompressor(channel);
                stream.setProperty(this.propertyKey, decompressor);
                CharSequence targetContentEncoding = this.getTargetContentEncoding(contentEncoding);
                if (HttpHeaderValues.IDENTITY.contentEqualsIgnoreCase(targetContentEncoding)) {
                    headers.remove(HttpHeaderNames.CONTENT_ENCODING);
                } else {
                    headers.set(HttpHeaderNames.CONTENT_ENCODING, targetContentEncoding);
                }
            }
        }
        if (decompressor != null) {
            headers.remove(HttpHeaderNames.CONTENT_LENGTH);
            if (!this.flowControllerInitialized) {
                this.flowControllerInitialized = true;
                this.connection.local().flowController(new ConsumedBytesConverter(this.connection.local().flowController()));
            }
        }
    }

    Http2Decompressor decompressor(Http2Stream stream) {
        return stream == null ? null : (Http2Decompressor)stream.getProperty(this.propertyKey);
    }

    private static void cleanup(Http2Decompressor decompressor) {
        decompressor.decompressor().finishAndReleaseAll();
    }

    private static ByteBuf nextReadableBuf(EmbeddedChannel decompressor) {
        ByteBuf buf;
        do {
            if ((buf = (ByteBuf)decompressor.readInbound()) == null) {
                return null;
            }
            if (buf.isReadable()) break;
            buf.release();
        } while (true);
        return buf;
    }

    private static final class Http2Decompressor {
        private final EmbeddedChannel decompressor;
        private int compressed;
        private int decompressed;

        Http2Decompressor(EmbeddedChannel decompressor) {
            this.decompressor = decompressor;
        }

        EmbeddedChannel decompressor() {
            return this.decompressor;
        }

        void incrementCompressedBytes(int delta) {
            assert (delta >= 0);
            this.compressed += delta;
        }

        void incrementDecompressedBytes(int delta) {
            assert (delta >= 0);
            this.decompressed += delta;
        }

        int consumeBytes(int streamId, int decompressedBytes) throws Http2Exception {
            if (decompressedBytes < 0) {
                throw new IllegalArgumentException("decompressedBytes must not be negative: " + decompressedBytes);
            }
            if (this.decompressed - decompressedBytes < 0) {
                throw Http2Exception.streamError(streamId, Http2Error.INTERNAL_ERROR, "Attempting to return too many bytes for stream %d. decompressed: %d decompressedBytes: %d", streamId, this.decompressed, decompressedBytes);
            }
            double consumedRatio = (double)decompressedBytes / (double)this.decompressed;
            int consumedCompressed = Math.min(this.compressed, (int)Math.ceil((double)this.compressed * consumedRatio));
            if (this.compressed - consumedCompressed < 0) {
                throw Http2Exception.streamError(streamId, Http2Error.INTERNAL_ERROR, "overflow when converting decompressed bytes to compressed bytes for stream %d.decompressedBytes: %d decompressed: %d compressed: %d consumedCompressed: %d", streamId, decompressedBytes, this.decompressed, this.compressed, consumedCompressed);
            }
            this.decompressed -= decompressedBytes;
            this.compressed -= consumedCompressed;
            return consumedCompressed;
        }
    }

    private final class ConsumedBytesConverter
    implements Http2LocalFlowController {
        private final Http2LocalFlowController flowController;

        ConsumedBytesConverter(Http2LocalFlowController flowController) {
            this.flowController = ObjectUtil.checkNotNull(flowController, "flowController");
        }

        @Override
        public Http2LocalFlowController frameWriter(Http2FrameWriter frameWriter) {
            return this.flowController.frameWriter(frameWriter);
        }

        @Override
        public void channelHandlerContext(ChannelHandlerContext ctx) throws Http2Exception {
            this.flowController.channelHandlerContext(ctx);
        }

        @Override
        public void initialWindowSize(int newWindowSize) throws Http2Exception {
            this.flowController.initialWindowSize(newWindowSize);
        }

        @Override
        public int initialWindowSize() {
            return this.flowController.initialWindowSize();
        }

        @Override
        public int windowSize(Http2Stream stream) {
            return this.flowController.windowSize(stream);
        }

        @Override
        public void incrementWindowSize(Http2Stream stream, int delta) throws Http2Exception {
            this.flowController.incrementWindowSize(stream, delta);
        }

        @Override
        public void receiveFlowControlledFrame(Http2Stream stream, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
            this.flowController.receiveFlowControlledFrame(stream, data, padding, endOfStream);
        }

        @Override
        public boolean consumeBytes(Http2Stream stream, int numBytes) throws Http2Exception {
            Http2Decompressor decompressor = DelegatingDecompressorFrameListener.this.decompressor(stream);
            if (decompressor != null) {
                numBytes = decompressor.consumeBytes(stream.id(), numBytes);
            }
            try {
                return this.flowController.consumeBytes(stream, numBytes);
            }
            catch (Http2Exception e) {
                throw e;
            }
            catch (Throwable t) {
                throw Http2Exception.streamError(stream.id(), Http2Error.INTERNAL_ERROR, t, "Error while returning bytes to flow control window", new Object[0]);
            }
        }

        @Override
        public int unconsumedBytes(Http2Stream stream) {
            return this.flowController.unconsumedBytes(stream);
        }

        @Override
        public int initialWindowSize(Http2Stream stream) {
            return this.flowController.initialWindowSize(stream);
        }
    }

}


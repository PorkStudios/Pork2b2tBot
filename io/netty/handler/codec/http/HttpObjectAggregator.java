/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpExpectationFailedEvent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMessageUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class HttpObjectAggregator
extends MessageAggregator<HttpObject, HttpMessage, HttpContent, FullHttpMessage> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpObjectAggregator.class);
    private static final FullHttpResponse CONTINUE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse EXPECTATION_FAILED = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse TOO_LARGE_CLOSE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse TOO_LARGE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);
    private final boolean closeOnExpectationFailed;

    public HttpObjectAggregator(int maxContentLength) {
        this(maxContentLength, false);
    }

    public HttpObjectAggregator(int maxContentLength, boolean closeOnExpectationFailed) {
        super(maxContentLength);
        this.closeOnExpectationFailed = closeOnExpectationFailed;
    }

    @Override
    protected boolean isStartMessage(HttpObject msg) throws Exception {
        return msg instanceof HttpMessage;
    }

    @Override
    protected boolean isContentMessage(HttpObject msg) throws Exception {
        return msg instanceof HttpContent;
    }

    @Override
    protected boolean isLastContentMessage(HttpContent msg) throws Exception {
        return msg instanceof LastHttpContent;
    }

    @Override
    protected boolean isAggregated(HttpObject msg) throws Exception {
        return msg instanceof FullHttpMessage;
    }

    @Override
    protected boolean isContentLengthInvalid(HttpMessage start, int maxContentLength) {
        try {
            return HttpUtil.getContentLength(start, -1L) > (long)maxContentLength;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static Object continueResponse(HttpMessage start, int maxContentLength, ChannelPipeline pipeline) {
        if (HttpUtil.isUnsupportedExpectation(start)) {
            pipeline.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
            return EXPECTATION_FAILED.retainedDuplicate();
        }
        if (HttpUtil.is100ContinueExpected(start)) {
            if (HttpUtil.getContentLength(start, -1L) <= (long)maxContentLength) {
                return CONTINUE.retainedDuplicate();
            }
            pipeline.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
            return TOO_LARGE.retainedDuplicate();
        }
        return null;
    }

    @Override
    protected Object newContinueResponse(HttpMessage start, int maxContentLength, ChannelPipeline pipeline) {
        Object response = HttpObjectAggregator.continueResponse(start, maxContentLength, pipeline);
        if (response != null) {
            start.headers().remove(HttpHeaderNames.EXPECT);
        }
        return response;
    }

    @Override
    protected boolean closeAfterContinueResponse(Object msg) {
        return this.closeOnExpectationFailed && this.ignoreContentAfterContinueResponse(msg);
    }

    @Override
    protected boolean ignoreContentAfterContinueResponse(Object msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse httpResponse = (HttpResponse)msg;
            return httpResponse.status().codeClass().equals((Object)HttpStatusClass.CLIENT_ERROR);
        }
        return false;
    }

    @Override
    protected FullHttpMessage beginAggregation(HttpMessage start, ByteBuf content) throws Exception {
        AggregatedFullHttpMessage ret;
        assert (!(start instanceof FullHttpMessage));
        HttpUtil.setTransferEncodingChunked(start, false);
        if (start instanceof HttpRequest) {
            ret = new AggregatedFullHttpRequest((HttpRequest)start, content, null);
        } else if (start instanceof HttpResponse) {
            ret = new AggregatedFullHttpResponse((HttpResponse)start, content, null);
        } else {
            throw new Error();
        }
        return ret;
    }

    @Override
    protected void aggregate(FullHttpMessage aggregated, HttpContent content) throws Exception {
        if (content instanceof LastHttpContent) {
            ((AggregatedFullHttpMessage)aggregated).setTrailingHeaders(((LastHttpContent)content).trailingHeaders());
        }
    }

    @Override
    protected void finishAggregation(FullHttpMessage aggregated) throws Exception {
        if (!HttpUtil.isContentLengthSet(aggregated)) {
            aggregated.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)String.valueOf(aggregated.content().readableBytes()));
        }
    }

    @Override
    protected void handleOversizedMessage(final ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
        if (oversized instanceof HttpRequest) {
            if (oversized instanceof FullHttpMessage || !HttpUtil.is100ContinueExpected(oversized) && !HttpUtil.isKeepAlive(oversized)) {
                ChannelFuture future = ctx.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate());
                future.addListener(new ChannelFutureListener(){

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
                        }
                        ctx.close();
                    }
                });
            } else {
                ctx.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener(new ChannelFutureListener(){

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            logger.debug("Failed to send a 413 Request Entity Too Large.", future.cause());
                            ctx.close();
                        }
                    }
                });
            }
            HttpObjectDecoder decoder = ctx.pipeline().get(HttpObjectDecoder.class);
            if (decoder != null) {
                decoder.reset();
            }
        } else {
            if (oversized instanceof HttpResponse) {
                ctx.close();
                throw new TooLongFrameException("Response entity too large: " + oversized);
            }
            throw new IllegalStateException();
        }
    }

    static {
        EXPECTATION_FAILED.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)0);
        TOO_LARGE.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)0);
        TOO_LARGE_CLOSE.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)0);
        TOO_LARGE_CLOSE.headers().set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.CLOSE);
    }

    private static final class AggregatedFullHttpResponse
    extends AggregatedFullHttpMessage
    implements FullHttpResponse {
        AggregatedFullHttpResponse(HttpResponse message, ByteBuf content, HttpHeaders trailingHeaders) {
            super(message, content, trailingHeaders);
        }

        @Override
        public FullHttpResponse copy() {
            return this.replace(this.content().copy());
        }

        @Override
        public FullHttpResponse duplicate() {
            return this.replace(this.content().duplicate());
        }

        @Override
        public FullHttpResponse retainedDuplicate() {
            return this.replace(this.content().retainedDuplicate());
        }

        @Override
        public FullHttpResponse replace(ByteBuf content) {
            DefaultFullHttpResponse dup = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), content);
            dup.headers().set(this.headers());
            dup.trailingHeaders().set(this.trailingHeaders());
            dup.setDecoderResult(this.decoderResult());
            return dup;
        }

        @Override
        public FullHttpResponse setStatus(HttpResponseStatus status) {
            ((HttpResponse)this.message).setStatus(status);
            return this;
        }

        @Override
        public HttpResponseStatus getStatus() {
            return ((HttpResponse)this.message).status();
        }

        @Override
        public HttpResponseStatus status() {
            return this.getStatus();
        }

        @Override
        public FullHttpResponse setProtocolVersion(HttpVersion version) {
            super.setProtocolVersion(version);
            return this;
        }

        @Override
        public FullHttpResponse retain(int increment) {
            super.retain(increment);
            return this;
        }

        @Override
        public FullHttpResponse retain() {
            super.retain();
            return this;
        }

        @Override
        public FullHttpResponse touch(Object hint) {
            super.touch(hint);
            return this;
        }

        @Override
        public FullHttpResponse touch() {
            super.touch();
            return this;
        }

        public String toString() {
            return HttpMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
        }
    }

    private static final class AggregatedFullHttpRequest
    extends AggregatedFullHttpMessage
    implements FullHttpRequest {
        AggregatedFullHttpRequest(HttpRequest request, ByteBuf content, HttpHeaders trailingHeaders) {
            super(request, content, trailingHeaders);
        }

        @Override
        public FullHttpRequest copy() {
            return this.replace(this.content().copy());
        }

        @Override
        public FullHttpRequest duplicate() {
            return this.replace(this.content().duplicate());
        }

        @Override
        public FullHttpRequest retainedDuplicate() {
            return this.replace(this.content().retainedDuplicate());
        }

        @Override
        public FullHttpRequest replace(ByteBuf content) {
            DefaultFullHttpRequest dup = new DefaultFullHttpRequest(this.protocolVersion(), this.method(), this.uri(), content);
            dup.headers().set(this.headers());
            dup.trailingHeaders().set(this.trailingHeaders());
            dup.setDecoderResult(this.decoderResult());
            return dup;
        }

        @Override
        public FullHttpRequest retain(int increment) {
            super.retain(increment);
            return this;
        }

        @Override
        public FullHttpRequest retain() {
            super.retain();
            return this;
        }

        @Override
        public FullHttpRequest touch() {
            super.touch();
            return this;
        }

        @Override
        public FullHttpRequest touch(Object hint) {
            super.touch(hint);
            return this;
        }

        @Override
        public FullHttpRequest setMethod(HttpMethod method) {
            ((HttpRequest)this.message).setMethod(method);
            return this;
        }

        @Override
        public FullHttpRequest setUri(String uri) {
            ((HttpRequest)this.message).setUri(uri);
            return this;
        }

        @Override
        public HttpMethod getMethod() {
            return ((HttpRequest)this.message).method();
        }

        @Override
        public String getUri() {
            return ((HttpRequest)this.message).uri();
        }

        @Override
        public HttpMethod method() {
            return this.getMethod();
        }

        @Override
        public String uri() {
            return this.getUri();
        }

        @Override
        public FullHttpRequest setProtocolVersion(HttpVersion version) {
            super.setProtocolVersion(version);
            return this;
        }

        public String toString() {
            return HttpMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
        }
    }

    private static abstract class AggregatedFullHttpMessage
    implements FullHttpMessage {
        protected final HttpMessage message;
        private final ByteBuf content;
        private HttpHeaders trailingHeaders;

        AggregatedFullHttpMessage(HttpMessage message, ByteBuf content, HttpHeaders trailingHeaders) {
            this.message = message;
            this.content = content;
            this.trailingHeaders = trailingHeaders;
        }

        @Override
        public HttpHeaders trailingHeaders() {
            HttpHeaders trailingHeaders = this.trailingHeaders;
            if (trailingHeaders == null) {
                return EmptyHttpHeaders.INSTANCE;
            }
            return trailingHeaders;
        }

        void setTrailingHeaders(HttpHeaders trailingHeaders) {
            this.trailingHeaders = trailingHeaders;
        }

        @Override
        public HttpVersion getProtocolVersion() {
            return this.message.protocolVersion();
        }

        @Override
        public HttpVersion protocolVersion() {
            return this.message.protocolVersion();
        }

        @Override
        public FullHttpMessage setProtocolVersion(HttpVersion version) {
            this.message.setProtocolVersion(version);
            return this;
        }

        @Override
        public HttpHeaders headers() {
            return this.message.headers();
        }

        @Override
        public DecoderResult decoderResult() {
            return this.message.decoderResult();
        }

        @Override
        public DecoderResult getDecoderResult() {
            return this.message.decoderResult();
        }

        @Override
        public void setDecoderResult(DecoderResult result) {
            this.message.setDecoderResult(result);
        }

        @Override
        public ByteBuf content() {
            return this.content;
        }

        @Override
        public int refCnt() {
            return this.content.refCnt();
        }

        @Override
        public FullHttpMessage retain() {
            this.content.retain();
            return this;
        }

        @Override
        public FullHttpMessage retain(int increment) {
            this.content.retain(increment);
            return this;
        }

        @Override
        public FullHttpMessage touch(Object hint) {
            this.content.touch(hint);
            return this;
        }

        @Override
        public FullHttpMessage touch() {
            this.content.touch();
            return this;
        }

        @Override
        public boolean release() {
            return this.content.release();
        }

        @Override
        public boolean release(int decrement) {
            return this.content.release(decrement);
        }

        @Override
        public abstract FullHttpMessage copy();

        @Override
        public abstract FullHttpMessage duplicate();

        @Override
        public abstract FullHttpMessage retainedDuplicate();
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.ComposedLastHttpContent;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public abstract class HttpContentEncoder
extends MessageToMessageCodec<HttpRequest, HttpObject> {
    private static final CharSequence ZERO_LENGTH_HEAD = "HEAD";
    private static final CharSequence ZERO_LENGTH_CONNECT = "CONNECT";
    private static final int CONTINUE_CODE = HttpResponseStatus.CONTINUE.code();
    private final Queue<CharSequence> acceptEncodingQueue = new ArrayDeque<CharSequence>();
    private EmbeddedChannel encoder;
    private State state = State.AWAIT_HEADERS;

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg instanceof HttpContent || msg instanceof HttpResponse;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpRequest msg, List<Object> out) throws Exception {
        HttpMethod meth;
        CharSequence acceptedEncoding = msg.headers().get(HttpHeaderNames.ACCEPT_ENCODING);
        if (acceptedEncoding == null) {
            acceptedEncoding = HttpContentDecoder.IDENTITY;
        }
        if ((meth = msg.method()) == HttpMethod.HEAD) {
            acceptedEncoding = ZERO_LENGTH_HEAD;
        } else if (meth == HttpMethod.CONNECT) {
            acceptedEncoding = ZERO_LENGTH_CONNECT;
        }
        this.acceptEncodingQueue.add(acceptedEncoding);
        out.add(ReferenceCountUtil.retain(msg));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        boolean isFull = msg instanceof HttpResponse && msg instanceof LastHttpContent;
        switch (this.state) {
            case AWAIT_HEADERS: {
                CharSequence acceptEncoding;
                HttpContentEncoder.ensureHeaders(msg);
                assert (this.encoder == null);
                HttpResponse res = (HttpResponse)msg;
                int code = res.status().code();
                if (code == CONTINUE_CODE) {
                    acceptEncoding = null;
                } else {
                    acceptEncoding = this.acceptEncodingQueue.poll();
                    if (acceptEncoding == null) {
                        throw new IllegalStateException("cannot send more responses than requests");
                    }
                }
                if (HttpContentEncoder.isPassthru(res.protocolVersion(), code, acceptEncoding)) {
                    if (isFull) {
                        out.add(ReferenceCountUtil.retain(res));
                        break;
                    }
                    out.add(res);
                    this.state = State.PASS_THROUGH;
                    break;
                }
                if (isFull && !((ByteBufHolder)((Object)res)).content().isReadable()) {
                    out.add(ReferenceCountUtil.retain(res));
                    break;
                }
                Result result = this.beginEncode(res, acceptEncoding.toString());
                if (result == null) {
                    if (isFull) {
                        out.add(ReferenceCountUtil.retain(res));
                        break;
                    }
                    out.add(res);
                    this.state = State.PASS_THROUGH;
                    break;
                }
                this.encoder = result.contentEncoder();
                res.headers().set((CharSequence)HttpHeaderNames.CONTENT_ENCODING, (Object)result.targetContentEncoding());
                if (isFull) {
                    DefaultHttpResponse newRes = new DefaultHttpResponse(res.protocolVersion(), res.status());
                    newRes.headers().set(res.headers());
                    out.add(newRes);
                    HttpContentEncoder.ensureContent(res);
                    this.encodeFullResponse(newRes, (HttpContent)((Object)res), out);
                    break;
                }
                res.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
                res.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
                out.add(res);
                this.state = State.AWAIT_CONTENT;
                if (!(msg instanceof HttpContent)) break;
            }
            case AWAIT_CONTENT: {
                HttpContentEncoder.ensureContent(msg);
                if (!this.encodeContent((HttpContent)msg, out)) break;
                this.state = State.AWAIT_HEADERS;
                break;
            }
            case PASS_THROUGH: {
                HttpContentEncoder.ensureContent(msg);
                out.add(ReferenceCountUtil.retain(msg));
                if (!(msg instanceof LastHttpContent)) break;
                this.state = State.AWAIT_HEADERS;
            }
        }
    }

    private void encodeFullResponse(HttpResponse newRes, HttpContent content, List<Object> out) {
        int existingMessages = out.size();
        this.encodeContent(content, out);
        if (HttpUtil.isContentLengthSet(newRes)) {
            int messageSize = 0;
            for (int i = existingMessages; i < out.size(); ++i) {
                Object item = out.get(i);
                if (!(item instanceof HttpContent)) continue;
                messageSize += ((HttpContent)item).content().readableBytes();
            }
            HttpUtil.setContentLength(newRes, messageSize);
        } else {
            newRes.headers().set((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)HttpHeaderValues.CHUNKED);
        }
    }

    private static boolean isPassthru(HttpVersion version, int code, CharSequence httpMethod) {
        return code < 200 || code == 204 || code == 304 || httpMethod == ZERO_LENGTH_HEAD || httpMethod == ZERO_LENGTH_CONNECT && code == 200 || version == HttpVersion.HTTP_1_0;
    }

    private static void ensureHeaders(HttpObject msg) {
        if (!(msg instanceof HttpResponse)) {
            throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
        }
    }

    private static void ensureContent(HttpObject msg) {
        if (!(msg instanceof HttpContent)) {
            throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
        }
    }

    private boolean encodeContent(HttpContent c, List<Object> out) {
        ByteBuf content = c.content();
        this.encode(content, out);
        if (c instanceof LastHttpContent) {
            this.finishEncode(out);
            LastHttpContent last = (LastHttpContent)c;
            HttpHeaders headers = last.trailingHeaders();
            if (headers.isEmpty()) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                out.add(new ComposedLastHttpContent(headers));
            }
            return true;
        }
        return false;
    }

    protected abstract Result beginEncode(HttpResponse var1, String var2) throws Exception;

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        this.cleanupSafely(ctx);
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.cleanupSafely(ctx);
        super.channelInactive(ctx);
    }

    private void cleanup() {
        if (this.encoder != null) {
            this.encoder.finishAndReleaseAll();
            this.encoder = null;
        }
    }

    private void cleanupSafely(ChannelHandlerContext ctx) {
        try {
            this.cleanup();
        }
        catch (Throwable cause) {
            ctx.fireExceptionCaught(cause);
        }
    }

    private void encode(ByteBuf in, List<Object> out) {
        this.encoder.writeOutbound(in.retain());
        this.fetchEncoderOutput(out);
    }

    private void finishEncode(List<Object> out) {
        if (this.encoder.finish()) {
            this.fetchEncoderOutput(out);
        }
        this.encoder = null;
    }

    private void fetchEncoderOutput(List<Object> out) {
        ByteBuf buf;
        while ((buf = (ByteBuf)this.encoder.readOutbound()) != null) {
            if (!buf.isReadable()) {
                buf.release();
                continue;
            }
            out.add(new DefaultHttpContent(buf));
        }
    }

    public static final class Result {
        private final String targetContentEncoding;
        private final EmbeddedChannel contentEncoder;

        public Result(String targetContentEncoding, EmbeddedChannel contentEncoder) {
            if (targetContentEncoding == null) {
                throw new NullPointerException("targetContentEncoding");
            }
            if (contentEncoder == null) {
                throw new NullPointerException("contentEncoder");
            }
            this.targetContentEncoding = targetContentEncoding;
            this.contentEncoder = contentEncoder;
        }

        public String targetContentEncoding() {
            return this.targetContentEncoding;
        }

        public EmbeddedChannel contentEncoder() {
            return this.contentEncoder;
        }
    }

    private static enum State {
        PASS_THROUGH,
        AWAIT_HEADERS,
        AWAIT_CONTENT;
        

        private State() {
        }
    }

}


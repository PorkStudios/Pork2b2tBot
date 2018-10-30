/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.spdy.DefaultSpdyDataFrame;
import io.netty.handler.codec.spdy.DefaultSpdyHeadersFrame;
import io.netty.handler.codec.spdy.DefaultSpdySynReplyFrame;
import io.netty.handler.codec.spdy.DefaultSpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyHttpHeaders;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpdyHttpEncoder
extends MessageToMessageEncoder<HttpObject> {
    private int currentStreamId;
    private final boolean validateHeaders;
    private final boolean headersToLowerCase;

    public SpdyHttpEncoder(SpdyVersion version) {
        this(version, true, true);
    }

    public SpdyHttpEncoder(SpdyVersion version, boolean headersToLowerCase, boolean validateHeaders) {
        if (version == null) {
            throw new NullPointerException("version");
        }
        this.headersToLowerCase = headersToLowerCase;
        this.validateHeaders = validateHeaders;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        boolean valid = false;
        boolean last = false;
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest)msg;
            SpdySynStreamFrame spdySynStreamFrame = this.createSynStreamFrame(httpRequest);
            out.add(spdySynStreamFrame);
            last = spdySynStreamFrame.isLast() || spdySynStreamFrame.isUnidirectional();
            valid = true;
        }
        if (msg instanceof HttpResponse) {
            HttpResponse httpResponse = (HttpResponse)msg;
            SpdyHeadersFrame spdyHeadersFrame = this.createHeadersFrame(httpResponse);
            out.add(spdyHeadersFrame);
            last = spdyHeadersFrame.isLast();
            valid = true;
        }
        if (msg instanceof HttpContent && !last) {
            HttpContent chunk = (HttpContent)msg;
            chunk.content().retain();
            DefaultSpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame(this.currentStreamId, chunk.content());
            if (chunk instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent)chunk;
                HttpHeaders trailers = trailer.trailingHeaders();
                if (trailers.isEmpty()) {
                    spdyDataFrame.setLast(true);
                    out.add(spdyDataFrame);
                } else {
                    DefaultSpdyHeadersFrame spdyHeadersFrame = new DefaultSpdyHeadersFrame(this.currentStreamId, this.validateHeaders);
                    spdyHeadersFrame.setLast(true);
                    Iterator<Map.Entry<CharSequence, CharSequence>> itr = trailers.iteratorCharSequence();
                    while (itr.hasNext()) {
                        Map.Entry<CharSequence, CharSequence> entry = itr.next();
                        CharSequence headerName = this.headersToLowerCase ? AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey();
                        spdyHeadersFrame.headers().add(headerName, entry.getValue());
                    }
                    out.add(spdyDataFrame);
                    out.add(spdyHeadersFrame);
                }
            } else {
                out.add(spdyDataFrame);
            }
            valid = true;
        }
        if (!valid) {
            throw new UnsupportedMessageTypeException(msg, new Class[0]);
        }
    }

    private SpdySynStreamFrame createSynStreamFrame(HttpRequest httpRequest) throws Exception {
        HttpHeaders httpHeaders = httpRequest.headers();
        int streamId = httpHeaders.getInt(SpdyHttpHeaders.Names.STREAM_ID);
        int associatedToStreamId = httpHeaders.getInt(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, 0);
        byte priority = (byte)httpHeaders.getInt(SpdyHttpHeaders.Names.PRIORITY, 0);
        String scheme = httpHeaders.get(SpdyHttpHeaders.Names.SCHEME);
        httpHeaders.remove(SpdyHttpHeaders.Names.STREAM_ID);
        httpHeaders.remove(SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID);
        httpHeaders.remove(SpdyHttpHeaders.Names.PRIORITY);
        httpHeaders.remove(SpdyHttpHeaders.Names.SCHEME);
        httpHeaders.remove(HttpHeaderNames.CONNECTION);
        httpHeaders.remove("Keep-Alive");
        httpHeaders.remove("Proxy-Connection");
        httpHeaders.remove(HttpHeaderNames.TRANSFER_ENCODING);
        DefaultSpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, associatedToStreamId, priority, this.validateHeaders);
        SpdyHeaders frameHeaders = spdySynStreamFrame.headers();
        frameHeaders.set(SpdyHeaders.HttpNames.METHOD, httpRequest.method().name());
        frameHeaders.set(SpdyHeaders.HttpNames.PATH, httpRequest.uri());
        frameHeaders.set(SpdyHeaders.HttpNames.VERSION, httpRequest.protocolVersion().text());
        String host = httpHeaders.get(HttpHeaderNames.HOST);
        httpHeaders.remove(HttpHeaderNames.HOST);
        frameHeaders.set(SpdyHeaders.HttpNames.HOST, host);
        if (scheme == null) {
            scheme = "https";
        }
        frameHeaders.set(SpdyHeaders.HttpNames.SCHEME, scheme);
        Iterator<Map.Entry<CharSequence, CharSequence>> itr = httpHeaders.iteratorCharSequence();
        while (itr.hasNext()) {
            Map.Entry<CharSequence, CharSequence> entry = itr.next();
            CharSequence headerName = this.headersToLowerCase ? AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey();
            frameHeaders.add(headerName, entry.getValue());
        }
        this.currentStreamId = spdySynStreamFrame.streamId();
        if (associatedToStreamId == 0) {
            spdySynStreamFrame.setLast(SpdyHttpEncoder.isLast(httpRequest));
        } else {
            spdySynStreamFrame.setUnidirectional(true);
        }
        return spdySynStreamFrame;
    }

    private SpdyHeadersFrame createHeadersFrame(HttpResponse httpResponse) throws Exception {
        HttpHeaders httpHeaders = httpResponse.headers();
        int streamId = httpHeaders.getInt(SpdyHttpHeaders.Names.STREAM_ID);
        httpHeaders.remove(SpdyHttpHeaders.Names.STREAM_ID);
        httpHeaders.remove(HttpHeaderNames.CONNECTION);
        httpHeaders.remove("Keep-Alive");
        httpHeaders.remove("Proxy-Connection");
        httpHeaders.remove(HttpHeaderNames.TRANSFER_ENCODING);
        DefaultSpdyHeadersFrame spdyHeadersFrame = SpdyCodecUtil.isServerId(streamId) ? new DefaultSpdyHeadersFrame(streamId, this.validateHeaders) : new DefaultSpdySynReplyFrame(streamId, this.validateHeaders);
        SpdyHeaders frameHeaders = spdyHeadersFrame.headers();
        frameHeaders.set(SpdyHeaders.HttpNames.STATUS, httpResponse.status().codeAsText());
        frameHeaders.set(SpdyHeaders.HttpNames.VERSION, httpResponse.protocolVersion().text());
        Iterator<Map.Entry<CharSequence, CharSequence>> itr = httpHeaders.iteratorCharSequence();
        while (itr.hasNext()) {
            Map.Entry<CharSequence, CharSequence> entry = itr.next();
            CharSequence headerName = this.headersToLowerCase ? AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey();
            spdyHeadersFrame.headers().add(headerName, entry.getValue());
        }
        this.currentStreamId = streamId;
        spdyHeadersFrame.setLast(SpdyHttpEncoder.isLast(httpResponse));
        return spdyHeadersFrame;
    }

    private static boolean isLast(HttpMessage httpMessage) {
        FullHttpMessage fullMessage;
        if (httpMessage instanceof FullHttpMessage && (fullMessage = (FullHttpMessage)httpMessage).trailingHeaders().isEmpty() && !fullMessage.content().isReadable()) {
            return true;
        }
        return false;
    }
}


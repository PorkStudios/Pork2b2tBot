/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;

public class HttpResponseEncoder
extends HttpObjectEncoder<HttpResponse> {
    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) && !(msg instanceof HttpRequest);
    }

    @Override
    protected void encodeInitialLine(ByteBuf buf, HttpResponse response) throws Exception {
        response.protocolVersion().encode(buf);
        buf.writeByte(32);
        response.status().encode(buf);
        ByteBufUtil.writeShortBE(buf, 3338);
    }

    @Override
    protected void sanitizeHeadersBeforeEncode(HttpResponse msg, boolean isAlwaysEmpty) {
        HttpResponseStatus status;
        if (isAlwaysEmpty && ((status = msg.status()).codeClass() == HttpStatusClass.INFORMATIONAL || status.code() == HttpResponseStatus.NO_CONTENT.code())) {
            msg.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
            msg.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
        }
    }

    @Override
    protected boolean isContentAlwaysEmpty(HttpResponse msg) {
        HttpResponseStatus status = msg.status();
        if (status.codeClass() == HttpStatusClass.INFORMATIONAL) {
            if (status.code() == HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
                return msg.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_VERSION);
            }
            return true;
        }
        return status.code() == HttpResponseStatus.NO_CONTENT.code() || status.code() == HttpResponseStatus.NOT_MODIFIED.code();
    }
}


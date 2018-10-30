/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.util.AsciiString;

@Deprecated
public abstract class RtspObjectDecoder
extends HttpObjectDecoder {
    protected RtspObjectDecoder() {
        this(4096, 8192, 8192);
    }

    protected RtspObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
        super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false);
    }

    protected RtspObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
        super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false, validateHeaders);
    }

    @Override
    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        boolean empty = super.isContentAlwaysEmpty(msg);
        if (empty) {
            return true;
        }
        if (!msg.headers().contains(RtspHeaderNames.CONTENT_LENGTH)) {
            return true;
        }
        return empty;
    }
}


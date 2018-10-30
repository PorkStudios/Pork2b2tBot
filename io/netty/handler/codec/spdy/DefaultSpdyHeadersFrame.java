/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeaders;
import io.netty.handler.codec.spdy.DefaultSpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.util.internal.StringUtil;
import java.util.Map;

public class DefaultSpdyHeadersFrame
extends DefaultSpdyStreamFrame
implements SpdyHeadersFrame {
    private boolean invalid;
    private boolean truncated;
    private final SpdyHeaders headers;

    public DefaultSpdyHeadersFrame(int streamId) {
        this(streamId, true);
    }

    public DefaultSpdyHeadersFrame(int streamId, boolean validate) {
        super(streamId);
        this.headers = new DefaultSpdyHeaders(validate);
    }

    @Override
    public SpdyHeadersFrame setStreamId(int streamId) {
        super.setStreamId(streamId);
        return this;
    }

    @Override
    public SpdyHeadersFrame setLast(boolean last) {
        super.setLast(last);
        return this;
    }

    @Override
    public boolean isInvalid() {
        return this.invalid;
    }

    @Override
    public SpdyHeadersFrame setInvalid() {
        this.invalid = true;
        return this;
    }

    @Override
    public boolean isTruncated() {
        return this.truncated;
    }

    @Override
    public SpdyHeadersFrame setTruncated() {
        this.truncated = true;
        return this;
    }

    @Override
    public SpdyHeaders headers() {
        return this.headers;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append("(last: ").append(this.isLast()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(this.streamId()).append(StringUtil.NEWLINE).append("--> Headers:").append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    protected void appendHeaders(StringBuilder buf) {
        for (Map.Entry e : this.headers()) {
            buf.append("    ");
            buf.append((CharSequence)e.getKey());
            buf.append(": ");
            buf.append((CharSequence)e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }
}


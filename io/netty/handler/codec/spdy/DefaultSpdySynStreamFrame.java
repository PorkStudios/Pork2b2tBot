/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.util.internal.StringUtil;

public class DefaultSpdySynStreamFrame
extends DefaultSpdyHeadersFrame
implements SpdySynStreamFrame {
    private int associatedStreamId;
    private byte priority;
    private boolean unidirectional;

    public DefaultSpdySynStreamFrame(int streamId, int associatedStreamId, byte priority) {
        this(streamId, associatedStreamId, priority, true);
    }

    public DefaultSpdySynStreamFrame(int streamId, int associatedStreamId, byte priority, boolean validateHeaders) {
        super(streamId, validateHeaders);
        this.setAssociatedStreamId(associatedStreamId);
        this.setPriority(priority);
    }

    @Override
    public SpdySynStreamFrame setStreamId(int streamId) {
        super.setStreamId(streamId);
        return this;
    }

    @Override
    public SpdySynStreamFrame setLast(boolean last) {
        super.setLast(last);
        return this;
    }

    @Override
    public SpdySynStreamFrame setInvalid() {
        super.setInvalid();
        return this;
    }

    @Override
    public int associatedStreamId() {
        return this.associatedStreamId;
    }

    @Override
    public SpdySynStreamFrame setAssociatedStreamId(int associatedStreamId) {
        if (associatedStreamId < 0) {
            throw new IllegalArgumentException("Associated-To-Stream-ID cannot be negative: " + associatedStreamId);
        }
        this.associatedStreamId = associatedStreamId;
        return this;
    }

    @Override
    public byte priority() {
        return this.priority;
    }

    @Override
    public SpdySynStreamFrame setPriority(byte priority) {
        if (priority < 0 || priority > 7) {
            throw new IllegalArgumentException("Priority must be between 0 and 7 inclusive: " + priority);
        }
        this.priority = priority;
        return this;
    }

    @Override
    public boolean isUnidirectional() {
        return this.unidirectional;
    }

    @Override
    public SpdySynStreamFrame setUnidirectional(boolean unidirectional) {
        this.unidirectional = unidirectional;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append("(last: ").append(this.isLast()).append("; unidirectional: ").append(this.isUnidirectional()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(this.streamId()).append(StringUtil.NEWLINE);
        if (this.associatedStreamId != 0) {
            buf.append("--> Associated-To-Stream-ID = ").append(this.associatedStreamId()).append(StringUtil.NEWLINE);
        }
        buf.append("--> Priority = ").append(this.priority()).append(StringUtil.NEWLINE).append("--> Headers:").append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
}


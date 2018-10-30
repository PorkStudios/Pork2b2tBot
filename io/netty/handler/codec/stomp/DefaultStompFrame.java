/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.stomp.DefaultStompHeadersSubframe;
import io.netty.handler.codec.stomp.LastStompContentSubframe;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompContentSubframe;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCounted;
import java.nio.charset.Charset;

public class DefaultStompFrame
extends DefaultStompHeadersSubframe
implements StompFrame {
    private final ByteBuf content;

    public DefaultStompFrame(StompCommand command) {
        this(command, Unpooled.buffer(0));
    }

    public DefaultStompFrame(StompCommand command, ByteBuf content) {
        super(command);
        if (content == null) {
            throw new NullPointerException("content");
        }
        this.content = content;
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public StompFrame copy() {
        return this.replace(this.content.copy());
    }

    @Override
    public StompFrame duplicate() {
        return this.replace(this.content.duplicate());
    }

    @Override
    public StompFrame retainedDuplicate() {
        return this.replace(this.content.retainedDuplicate());
    }

    @Override
    public StompFrame replace(ByteBuf content) {
        return new DefaultStompFrame(this.command, content);
    }

    @Override
    public int refCnt() {
        return this.content.refCnt();
    }

    @Override
    public StompFrame retain() {
        this.content.retain();
        return this;
    }

    @Override
    public StompFrame retain(int increment) {
        this.content.retain(increment);
        return this;
    }

    @Override
    public StompFrame touch() {
        this.content.touch();
        return this;
    }

    @Override
    public StompFrame touch(Object hint) {
        this.content.touch(hint);
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
    public String toString() {
        return "DefaultStompFrame{command=" + (Object)((Object)this.command) + ", headers=" + this.headers + ", content=" + this.content.toString(CharsetUtil.UTF_8) + '}';
    }
}


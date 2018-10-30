/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.memcache.FullMemcacheMessage;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheContent;
import io.netty.handler.codec.memcache.MemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequest;
import io.netty.handler.codec.memcache.binary.DefaultBinaryMemcacheRequest;
import io.netty.handler.codec.memcache.binary.FullBinaryMemcacheRequest;
import io.netty.util.ReferenceCounted;

public class DefaultFullBinaryMemcacheRequest
extends DefaultBinaryMemcacheRequest
implements FullBinaryMemcacheRequest {
    private final ByteBuf content;

    public DefaultFullBinaryMemcacheRequest(ByteBuf key, ByteBuf extras) {
        this(key, extras, Unpooled.buffer(0));
    }

    public DefaultFullBinaryMemcacheRequest(ByteBuf key, ByteBuf extras, ByteBuf content) {
        super(key, extras);
        if (content == null) {
            throw new NullPointerException("Supplied content is null.");
        }
        this.content = content;
        this.setTotalBodyLength(this.keyLength() + this.extrasLength() + content.readableBytes());
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public FullBinaryMemcacheRequest retain() {
        super.retain();
        return this;
    }

    @Override
    public FullBinaryMemcacheRequest retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public FullBinaryMemcacheRequest touch() {
        super.touch();
        return this;
    }

    @Override
    public FullBinaryMemcacheRequest touch(Object hint) {
        super.touch(hint);
        this.content.touch(hint);
        return this;
    }

    @Override
    protected void deallocate() {
        super.deallocate();
        this.content.release();
    }

    @Override
    public FullBinaryMemcacheRequest copy() {
        ByteBuf extras;
        ByteBuf key = this.key();
        if (key != null) {
            key = key.copy();
        }
        if ((extras = this.extras()) != null) {
            extras = extras.copy();
        }
        return new DefaultFullBinaryMemcacheRequest(key, extras, this.content().copy());
    }

    @Override
    public FullBinaryMemcacheRequest duplicate() {
        ByteBuf extras;
        ByteBuf key = this.key();
        if (key != null) {
            key = key.duplicate();
        }
        if ((extras = this.extras()) != null) {
            extras = extras.duplicate();
        }
        return new DefaultFullBinaryMemcacheRequest(key, extras, this.content().duplicate());
    }

    @Override
    public FullBinaryMemcacheRequest retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public FullBinaryMemcacheRequest replace(ByteBuf content) {
        ByteBuf extras;
        ByteBuf key = this.key();
        if (key != null) {
            key = key.retainedDuplicate();
        }
        if ((extras = this.extras()) != null) {
            extras = extras.retainedDuplicate();
        }
        return new DefaultFullBinaryMemcacheRequest(key, extras, content);
    }
}


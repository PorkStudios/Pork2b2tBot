/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.memcache.DefaultMemcacheContent;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheContent;
import io.netty.util.ReferenceCounted;

public class DefaultLastMemcacheContent
extends DefaultMemcacheContent
implements LastMemcacheContent {
    public DefaultLastMemcacheContent() {
        super(Unpooled.buffer());
    }

    public DefaultLastMemcacheContent(ByteBuf content) {
        super(content);
    }

    @Override
    public LastMemcacheContent retain() {
        super.retain();
        return this;
    }

    @Override
    public LastMemcacheContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public LastMemcacheContent touch() {
        super.touch();
        return this;
    }

    @Override
    public LastMemcacheContent touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public LastMemcacheContent copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public LastMemcacheContent duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public LastMemcacheContent retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public LastMemcacheContent replace(ByteBuf content) {
        return new DefaultLastMemcacheContent(content);
    }
}


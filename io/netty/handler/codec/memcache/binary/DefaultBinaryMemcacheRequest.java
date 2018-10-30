/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.MemcacheMessage;
import io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequest;
import io.netty.util.ReferenceCounted;

public class DefaultBinaryMemcacheRequest
extends AbstractBinaryMemcacheMessage
implements BinaryMemcacheRequest {
    public static final byte REQUEST_MAGIC_BYTE = -128;
    private short reserved;

    public DefaultBinaryMemcacheRequest() {
        this(null, null);
    }

    public DefaultBinaryMemcacheRequest(ByteBuf key) {
        this(key, null);
    }

    public DefaultBinaryMemcacheRequest(ByteBuf key, ByteBuf extras) {
        super(key, extras);
        this.setMagic((byte)-128);
    }

    @Override
    public short reserved() {
        return this.reserved;
    }

    @Override
    public BinaryMemcacheRequest setReserved(short reserved) {
        this.reserved = reserved;
        return this;
    }

    @Override
    public BinaryMemcacheRequest retain() {
        super.retain();
        return this;
    }

    @Override
    public BinaryMemcacheRequest retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public BinaryMemcacheRequest touch() {
        super.touch();
        return this;
    }

    @Override
    public BinaryMemcacheRequest touch(Object hint) {
        super.touch(hint);
        return this;
    }
}


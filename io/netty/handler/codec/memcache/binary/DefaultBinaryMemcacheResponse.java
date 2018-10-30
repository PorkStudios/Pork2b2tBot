/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.MemcacheMessage;
import io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponse;
import io.netty.util.ReferenceCounted;

public class DefaultBinaryMemcacheResponse
extends AbstractBinaryMemcacheMessage
implements BinaryMemcacheResponse {
    public static final byte RESPONSE_MAGIC_BYTE = -127;
    private short status;

    public DefaultBinaryMemcacheResponse() {
        this(null, null);
    }

    public DefaultBinaryMemcacheResponse(ByteBuf key) {
        this(key, null);
    }

    public DefaultBinaryMemcacheResponse(ByteBuf key, ByteBuf extras) {
        super(key, extras);
        this.setMagic((byte)-127);
    }

    @Override
    public short status() {
        return this.status;
    }

    @Override
    public BinaryMemcacheResponse setStatus(short status) {
        this.status = status;
        return this;
    }

    @Override
    public BinaryMemcacheResponse retain() {
        super.retain();
        return this;
    }

    @Override
    public BinaryMemcacheResponse retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public BinaryMemcacheResponse touch() {
        super.touch();
        return this;
    }

    @Override
    public BinaryMemcacheResponse touch(Object hint) {
        super.touch(hint);
        return this;
    }
}


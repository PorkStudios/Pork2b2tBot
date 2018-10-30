/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.AbstractMemcacheObject;
import io.netty.handler.codec.memcache.MemcacheMessage;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheMessage;
import io.netty.util.ReferenceCounted;

public abstract class AbstractBinaryMemcacheMessage
extends AbstractMemcacheObject
implements BinaryMemcacheMessage {
    private ByteBuf key;
    private ByteBuf extras;
    private byte magic;
    private byte opcode;
    private short keyLength;
    private byte extrasLength;
    private byte dataType;
    private int totalBodyLength;
    private int opaque;
    private long cas;

    protected AbstractBinaryMemcacheMessage(ByteBuf key, ByteBuf extras) {
        this.key = key;
        this.keyLength = key == null ? (short)0 : (short)key.readableBytes();
        this.extras = extras;
        this.extrasLength = extras == null ? (byte)0 : (byte)extras.readableBytes();
        this.totalBodyLength = this.keyLength + this.extrasLength;
    }

    @Override
    public ByteBuf key() {
        return this.key;
    }

    @Override
    public ByteBuf extras() {
        return this.extras;
    }

    @Override
    public BinaryMemcacheMessage setKey(ByteBuf key) {
        if (this.key != null) {
            this.key.release();
        }
        this.key = key;
        short oldKeyLength = this.keyLength;
        this.keyLength = key == null ? (short)0 : (short)key.readableBytes();
        this.totalBodyLength = this.totalBodyLength + this.keyLength - oldKeyLength;
        return this;
    }

    @Override
    public BinaryMemcacheMessage setExtras(ByteBuf extras) {
        if (this.extras != null) {
            this.extras.release();
        }
        this.extras = extras;
        short oldExtrasLength = this.extrasLength;
        this.extrasLength = extras == null ? (byte)0 : (byte)extras.readableBytes();
        this.totalBodyLength = this.totalBodyLength + this.extrasLength - oldExtrasLength;
        return this;
    }

    @Override
    public byte magic() {
        return this.magic;
    }

    @Override
    public BinaryMemcacheMessage setMagic(byte magic) {
        this.magic = magic;
        return this;
    }

    @Override
    public long cas() {
        return this.cas;
    }

    @Override
    public BinaryMemcacheMessage setCas(long cas) {
        this.cas = cas;
        return this;
    }

    @Override
    public int opaque() {
        return this.opaque;
    }

    @Override
    public BinaryMemcacheMessage setOpaque(int opaque) {
        this.opaque = opaque;
        return this;
    }

    @Override
    public int totalBodyLength() {
        return this.totalBodyLength;
    }

    @Override
    public BinaryMemcacheMessage setTotalBodyLength(int totalBodyLength) {
        this.totalBodyLength = totalBodyLength;
        return this;
    }

    @Override
    public byte dataType() {
        return this.dataType;
    }

    @Override
    public BinaryMemcacheMessage setDataType(byte dataType) {
        this.dataType = dataType;
        return this;
    }

    @Override
    public byte extrasLength() {
        return this.extrasLength;
    }

    BinaryMemcacheMessage setExtrasLength(byte extrasLength) {
        this.extrasLength = extrasLength;
        return this;
    }

    @Override
    public short keyLength() {
        return this.keyLength;
    }

    BinaryMemcacheMessage setKeyLength(short keyLength) {
        this.keyLength = keyLength;
        return this;
    }

    @Override
    public byte opcode() {
        return this.opcode;
    }

    @Override
    public BinaryMemcacheMessage setOpcode(byte opcode) {
        this.opcode = opcode;
        return this;
    }

    @Override
    public BinaryMemcacheMessage retain() {
        super.retain();
        return this;
    }

    @Override
    public BinaryMemcacheMessage retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    protected void deallocate() {
        if (this.key != null) {
            this.key.release();
        }
        if (this.extras != null) {
            this.extras.release();
        }
    }

    @Override
    public BinaryMemcacheMessage touch() {
        super.touch();
        return this;
    }

    @Override
    public BinaryMemcacheMessage touch(Object hint) {
        if (this.key != null) {
            this.key.touch(hint);
        }
        if (this.extras != null) {
            this.extras.touch(hint);
        }
        return this;
    }
}


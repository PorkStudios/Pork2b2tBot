/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;

public class HAProxyTLV
extends DefaultByteBufHolder {
    private final Type type;
    private final byte typeByteValue;

    HAProxyTLV(Type type, byte typeByteValue, ByteBuf content) {
        super(content);
        ObjectUtil.checkNotNull(type, "type");
        this.type = type;
        this.typeByteValue = typeByteValue;
    }

    public Type type() {
        return this.type;
    }

    public byte typeByteValue() {
        return this.typeByteValue;
    }

    @Override
    public HAProxyTLV copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public HAProxyTLV duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public HAProxyTLV retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public HAProxyTLV replace(ByteBuf content) {
        return new HAProxyTLV(this.type, this.typeByteValue, content);
    }

    @Override
    public HAProxyTLV retain() {
        super.retain();
        return this;
    }

    @Override
    public HAProxyTLV retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public HAProxyTLV touch() {
        super.touch();
        return this;
    }

    @Override
    public HAProxyTLV touch(Object hint) {
        super.touch(hint);
        return this;
    }

    public static enum Type {
        PP2_TYPE_ALPN,
        PP2_TYPE_AUTHORITY,
        PP2_TYPE_SSL,
        PP2_TYPE_SSL_VERSION,
        PP2_TYPE_SSL_CN,
        PP2_TYPE_NETNS,
        OTHER;
        

        private Type() {
        }

        public static Type typeForByteValue(byte byteValue) {
            switch (byteValue) {
                case 1: {
                    return PP2_TYPE_ALPN;
                }
                case 2: {
                    return PP2_TYPE_AUTHORITY;
                }
                case 32: {
                    return PP2_TYPE_SSL;
                }
                case 33: {
                    return PP2_TYPE_SSL_VERSION;
                }
                case 34: {
                    return PP2_TYPE_SSL_CN;
                }
                case 48: {
                    return PP2_TYPE_NETNS;
                }
            }
            return OTHER;
        }
    }

}


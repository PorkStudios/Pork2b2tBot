/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.ssl.PemEncoded;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;

class PemValue
extends AbstractReferenceCounted
implements PemEncoded {
    private final ByteBuf content;
    private final boolean sensitive;

    public PemValue(ByteBuf content, boolean sensitive) {
        this.content = ObjectUtil.checkNotNull(content, "content");
        this.sensitive = sensitive;
    }

    @Override
    public boolean isSensitive() {
        return this.sensitive;
    }

    @Override
    public ByteBuf content() {
        int count = this.refCnt();
        if (count <= 0) {
            throw new IllegalReferenceCountException(count);
        }
        return this.content;
    }

    @Override
    public PemValue copy() {
        return this.replace(this.content.copy());
    }

    @Override
    public PemValue duplicate() {
        return this.replace(this.content.duplicate());
    }

    @Override
    public PemValue retainedDuplicate() {
        return this.replace(this.content.retainedDuplicate());
    }

    @Override
    public PemValue replace(ByteBuf content) {
        return new PemValue(content, this.sensitive);
    }

    @Override
    public PemValue touch() {
        return (PemValue)super.touch();
    }

    @Override
    public PemValue touch(Object hint) {
        this.content.touch(hint);
        return this;
    }

    @Override
    public PemValue retain() {
        return (PemValue)super.retain();
    }

    @Override
    public PemValue retain(int increment) {
        return (PemValue)super.retain(increment);
    }

    @Override
    protected void deallocate() {
        if (this.sensitive) {
            SslUtils.zeroout(this.content);
        }
        this.content.release();
    }
}


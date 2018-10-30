/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

interface PemEncoded
extends ByteBufHolder {
    public boolean isSensitive();

    @Override
    public PemEncoded copy();

    @Override
    public PemEncoded duplicate();

    @Override
    public PemEncoded retainedDuplicate();

    @Override
    public PemEncoded replace(ByteBuf var1);

    @Override
    public PemEncoded retain();

    @Override
    public PemEncoded retain(int var1);

    @Override
    public PemEncoded touch();

    @Override
    public PemEncoded touch(Object var1);
}


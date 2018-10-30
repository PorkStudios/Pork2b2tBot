/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

public interface ReferenceCounted {
    public int refCnt();

    public ReferenceCounted retain();

    public ReferenceCounted retain(int var1);

    public ReferenceCounted touch();

    public ReferenceCounted touch(Object var1);

    public boolean release();

    public boolean release(int var1);
}


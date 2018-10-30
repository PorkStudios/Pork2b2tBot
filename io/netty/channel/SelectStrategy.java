/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.util.IntSupplier;

public interface SelectStrategy {
    public static final int SELECT = -1;
    public static final int CONTINUE = -2;

    public int calculateStrategy(IntSupplier var1, boolean var2) throws Exception;
}


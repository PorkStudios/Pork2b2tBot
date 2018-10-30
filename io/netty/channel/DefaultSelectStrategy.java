/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.SelectStrategy;
import io.netty.util.IntSupplier;

final class DefaultSelectStrategy
implements SelectStrategy {
    static final SelectStrategy INSTANCE = new DefaultSelectStrategy();

    private DefaultSelectStrategy() {
    }

    @Override
    public int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception {
        return hasTasks ? selectSupplier.get() : -1;
    }
}


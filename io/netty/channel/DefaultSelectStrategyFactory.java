/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.DefaultSelectStrategy;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SelectStrategyFactory;

public final class DefaultSelectStrategyFactory
implements SelectStrategyFactory {
    public static final SelectStrategyFactory INSTANCE = new DefaultSelectStrategyFactory();

    private DefaultSelectStrategyFactory() {
    }

    @Override
    public SelectStrategy newSelectStrategy() {
        return DefaultSelectStrategy.INSTANCE;
    }
}


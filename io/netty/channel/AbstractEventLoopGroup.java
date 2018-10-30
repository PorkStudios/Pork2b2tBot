/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;

public abstract class AbstractEventLoopGroup
extends AbstractEventExecutorGroup
implements EventLoopGroup {
    @Override
    public abstract EventLoop next();
}


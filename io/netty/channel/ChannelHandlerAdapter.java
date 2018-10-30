/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.InternalThreadLocalMap;
import java.lang.annotation.Annotation;
import java.util.Map;

public abstract class ChannelHandlerAdapter
implements ChannelHandler {
    boolean added;

    protected void ensureNotSharable() {
        if (this.isSharable()) {
            throw new IllegalStateException("ChannelHandler " + this.getClass().getName() + " is not allowed to be shared");
        }
    }

    public boolean isSharable() {
        Class<?> clazz = this.getClass();
        Map<Class<?>, Boolean> cache = InternalThreadLocalMap.get().handlerSharableCache();
        Boolean sharable = cache.get(clazz);
        if (sharable == null) {
            sharable = clazz.isAnnotationPresent(ChannelHandler.Sharable.class);
            cache.put(clazz, sharable);
        }
        return sharable;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}


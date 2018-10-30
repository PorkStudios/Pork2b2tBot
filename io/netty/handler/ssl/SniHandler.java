/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.ssl.AbstractSniHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AsyncMapping;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import javax.net.ssl.SSLEngine;

public class SniHandler
extends AbstractSniHandler<SslContext> {
    private static final Selection EMPTY_SELECTION = new Selection(null, null);
    protected final AsyncMapping<String, SslContext> mapping;
    private volatile Selection selection = EMPTY_SELECTION;

    public SniHandler(Mapping<? super String, ? extends SslContext> mapping) {
        this(new AsyncMappingAdapter(mapping));
    }

    public SniHandler(DomainNameMapping<? extends SslContext> mapping) {
        this((Mapping<? super String, ? extends SslContext>)mapping);
    }

    public SniHandler(AsyncMapping<? super String, ? extends SslContext> mapping) {
        this.mapping = ObjectUtil.checkNotNull(mapping, "mapping");
    }

    public String hostname() {
        return this.selection.hostname;
    }

    public SslContext sslContext() {
        return this.selection.context;
    }

    @Override
    protected Future<SslContext> lookup(ChannelHandlerContext ctx, String hostname) throws Exception {
        return this.mapping.map(hostname, ctx.executor().newPromise());
    }

    @Override
    protected final void onLookupComplete(ChannelHandlerContext ctx, String hostname, Future<SslContext> future) throws Exception {
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new DecoderException("failed to get the SslContext for " + hostname, cause);
        }
        SslContext sslContext = future.getNow();
        this.selection = new Selection(sslContext, hostname);
        try {
            this.replaceHandler(ctx, hostname, sslContext);
        }
        catch (Throwable cause) {
            this.selection = EMPTY_SELECTION;
            PlatformDependent.throwException(cause);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void replaceHandler(ChannelHandlerContext ctx, String hostname, SslContext sslContext) throws Exception {
        SslHandler sslHandler = null;
        try {
            sslHandler = sslContext.newHandler(ctx.alloc());
            ctx.pipeline().replace(this, SslHandler.class.getName(), (ChannelHandler)sslHandler);
            sslHandler = null;
        }
        finally {
            if (sslHandler != null) {
                ReferenceCountUtil.safeRelease(sslHandler.engine());
            }
        }
    }

    private static final class Selection {
        final SslContext context;
        final String hostname;

        Selection(SslContext context, String hostname) {
            this.context = context;
            this.hostname = hostname;
        }
    }

    private static final class AsyncMappingAdapter
    implements AsyncMapping<String, SslContext> {
        private final Mapping<? super String, ? extends SslContext> mapping;

        private AsyncMappingAdapter(Mapping<? super String, ? extends SslContext> mapping) {
            this.mapping = ObjectUtil.checkNotNull(mapping, "mapping");
        }

        @Override
        public Future<SslContext> map(String input, Promise<SslContext> promise) {
            SslContext context;
            try {
                context = this.mapping.map(input);
            }
            catch (Throwable cause) {
                return promise.setFailure(cause);
            }
            return promise.setSuccess(context);
        }
    }

}


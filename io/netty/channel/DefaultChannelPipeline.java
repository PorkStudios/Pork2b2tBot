/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.channel.AbstractChannelHandlerContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundInvoker;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineException;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelHandlerContext;
import io.netty.channel.DefaultChannelProgressivePromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.FailedChannelFuture;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.SucceededChannelFuture;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultChannelPipeline
implements ChannelPipeline {
    static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
    private static final String HEAD_NAME = DefaultChannelPipeline.generateName0(HeadContext.class);
    private static final String TAIL_NAME = DefaultChannelPipeline.generateName0(TailContext.class);
    private static final FastThreadLocal<Map<Class<?>, String>> nameCaches = new FastThreadLocal<Map<Class<?>, String>>(){

        @Override
        protected Map<Class<?>, String> initialValue() throws Exception {
            return new WeakHashMap();
        }
    };
    private static final AtomicReferenceFieldUpdater<DefaultChannelPipeline, MessageSizeEstimator.Handle> ESTIMATOR = AtomicReferenceFieldUpdater.newUpdater(DefaultChannelPipeline.class, MessageSizeEstimator.Handle.class, "estimatorHandle");
    final AbstractChannelHandlerContext head;
    final AbstractChannelHandlerContext tail;
    private final Channel channel;
    private final ChannelFuture succeededFuture;
    private final VoidChannelPromise voidPromise;
    private final boolean touch = ResourceLeakDetector.isEnabled();
    private Map<EventExecutorGroup, EventExecutor> childExecutors;
    private volatile MessageSizeEstimator.Handle estimatorHandle;
    private boolean firstRegistration = true;
    private PendingHandlerCallback pendingHandlerCallbackHead;
    private boolean registered;

    protected DefaultChannelPipeline(Channel channel) {
        this.channel = ObjectUtil.checkNotNull(channel, "channel");
        this.succeededFuture = new SucceededChannelFuture(channel, null);
        this.voidPromise = new VoidChannelPromise(channel, true);
        this.tail = new TailContext(this);
        this.head = new HeadContext(this);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    final MessageSizeEstimator.Handle estimatorHandle() {
        MessageSizeEstimator.Handle handle = this.estimatorHandle;
        if (handle == null && !ESTIMATOR.compareAndSet(this, null, handle = this.channel.config().getMessageSizeEstimator().newHandle())) {
            handle = this.estimatorHandle;
        }
        return handle;
    }

    final Object touch(Object msg, AbstractChannelHandlerContext next) {
        return this.touch ? ReferenceCountUtil.touch(msg, next) : msg;
    }

    private AbstractChannelHandlerContext newContext(EventExecutorGroup group, String name, ChannelHandler handler) {
        return new DefaultChannelHandlerContext(this, this.childExecutor(group), name, handler);
    }

    private EventExecutor childExecutor(EventExecutorGroup group) {
        EventExecutor childExecutor;
        if (group == null) {
            return null;
        }
        Boolean pinEventExecutor = this.channel.config().getOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
        if (pinEventExecutor != null && !pinEventExecutor.booleanValue()) {
            return group.next();
        }
        Map<EventExecutorGroup, EventExecutor> childExecutors = this.childExecutors;
        if (childExecutors == null) {
            childExecutors = this.childExecutors = new IdentityHashMap<EventExecutorGroup, EventExecutor>(4);
        }
        if ((childExecutor = childExecutors.get(group)) == null) {
            childExecutor = group.next();
            childExecutors.put(group, childExecutor);
        }
        return childExecutor;
    }

    @Override
    public final Channel channel() {
        return this.channel;
    }

    @Override
    public final ChannelPipeline addFirst(String name, ChannelHandler handler) {
        return this.addFirst(null, name, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
        AbstractChannelHandlerContext newCtx;
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            DefaultChannelPipeline.checkMultiplicity(handler);
            name = this.filterName(name, handler);
            newCtx = this.newContext(group, name, handler);
            this.addFirst0(newCtx);
            if (!this.registered) {
                newCtx.setAddPending();
                this.callHandlerCallbackLater(newCtx, true);
                return this;
            }
            EventExecutor executor = newCtx.executor();
            if (!executor.inEventLoop()) {
                newCtx.setAddPending();
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
                    }
                });
                return this;
            }
        }
        this.callHandlerAdded0(newCtx);
        return this;
    }

    private void addFirst0(AbstractChannelHandlerContext newCtx) {
        AbstractChannelHandlerContext nextCtx = this.head.next;
        newCtx.prev = this.head;
        newCtx.next = nextCtx;
        this.head.next = newCtx;
        nextCtx.prev = newCtx;
    }

    @Override
    public final ChannelPipeline addLast(String name, ChannelHandler handler) {
        return this.addLast(null, name, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
        AbstractChannelHandlerContext newCtx;
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            DefaultChannelPipeline.checkMultiplicity(handler);
            newCtx = this.newContext(group, this.filterName(name, handler), handler);
            this.addLast0(newCtx);
            if (!this.registered) {
                newCtx.setAddPending();
                this.callHandlerCallbackLater(newCtx, true);
                return this;
            }
            EventExecutor executor = newCtx.executor();
            if (!executor.inEventLoop()) {
                newCtx.setAddPending();
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
                    }
                });
                return this;
            }
        }
        this.callHandlerAdded0(newCtx);
        return this;
    }

    private void addLast0(AbstractChannelHandlerContext newCtx) {
        AbstractChannelHandlerContext prev;
        newCtx.prev = prev = this.tail.prev;
        newCtx.next = this.tail;
        prev.next = newCtx;
        this.tail.prev = newCtx;
    }

    @Override
    public final ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
        return this.addBefore(null, baseName, name, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        AbstractChannelHandlerContext newCtx;
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            DefaultChannelPipeline.checkMultiplicity(handler);
            name = this.filterName(name, handler);
            AbstractChannelHandlerContext ctx = this.getContextOrDie(baseName);
            newCtx = this.newContext(group, name, handler);
            DefaultChannelPipeline.addBefore0(ctx, newCtx);
            if (!this.registered) {
                newCtx.setAddPending();
                this.callHandlerCallbackLater(newCtx, true);
                return this;
            }
            EventExecutor executor = newCtx.executor();
            if (!executor.inEventLoop()) {
                newCtx.setAddPending();
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
                    }
                });
                return this;
            }
        }
        this.callHandlerAdded0(newCtx);
        return this;
    }

    private static void addBefore0(AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
        newCtx.prev = ctx.prev;
        newCtx.next = ctx;
        ctx.prev.next = newCtx;
        ctx.prev = newCtx;
    }

    private String filterName(String name, ChannelHandler handler) {
        if (name == null) {
            return this.generateName(handler);
        }
        this.checkDuplicateName(name);
        return name;
    }

    @Override
    public final ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
        return this.addAfter(null, baseName, name, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        AbstractChannelHandlerContext newCtx;
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            DefaultChannelPipeline.checkMultiplicity(handler);
            name = this.filterName(name, handler);
            AbstractChannelHandlerContext ctx = this.getContextOrDie(baseName);
            newCtx = this.newContext(group, name, handler);
            DefaultChannelPipeline.addAfter0(ctx, newCtx);
            if (!this.registered) {
                newCtx.setAddPending();
                this.callHandlerCallbackLater(newCtx, true);
                return this;
            }
            EventExecutor executor = newCtx.executor();
            if (!executor.inEventLoop()) {
                newCtx.setAddPending();
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
                    }
                });
                return this;
            }
        }
        this.callHandlerAdded0(newCtx);
        return this;
    }

    private static void addAfter0(AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
        newCtx.prev = ctx;
        newCtx.next = ctx.next;
        ctx.next.prev = newCtx;
        ctx.next = newCtx;
    }

    @Override
    public final /* varargs */ ChannelPipeline addFirst(ChannelHandler ... handlers) {
        return this.addFirst((EventExecutorGroup)null, handlers);
    }

    @Override
    public final /* varargs */ ChannelPipeline addFirst(EventExecutorGroup executor, ChannelHandler ... handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers");
        }
        if (handlers.length == 0 || handlers[0] == null) {
            return this;
        }
        for (int size = 1; size < handlers.length && handlers[size] != null; ++size) {
        }
        for (int i = size - 1; i >= 0; --i) {
            ChannelHandler h = handlers[i];
            this.addFirst(executor, (String)null, h);
        }
        return this;
    }

    @Override
    public final /* varargs */ ChannelPipeline addLast(ChannelHandler ... handlers) {
        return this.addLast((EventExecutorGroup)null, handlers);
    }

    @Override
    public final /* varargs */ ChannelPipeline addLast(EventExecutorGroup executor, ChannelHandler ... handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers");
        }
        for (ChannelHandler h : handlers) {
            if (h == null) break;
            this.addLast(executor, (String)null, h);
        }
        return this;
    }

    private String generateName(ChannelHandler handler) {
        Class<?> handlerType;
        Map<Class<?>, String> cache = nameCaches.get();
        String name = cache.get(handlerType = handler.getClass());
        if (name == null) {
            name = DefaultChannelPipeline.generateName0(handlerType);
            cache.put(handlerType, name);
        }
        if (this.context0(name) != null) {
            String baseName = name.substring(0, name.length() - 1);
            int i = 1;
            do {
                String newName;
                if (this.context0(newName = baseName + i) == null) {
                    name = newName;
                    break;
                }
                ++i;
            } while (true);
        }
        return name;
    }

    private static String generateName0(Class<?> handlerType) {
        return StringUtil.simpleClassName(handlerType) + "#0";
    }

    @Override
    public final ChannelPipeline remove(ChannelHandler handler) {
        this.remove(this.getContextOrDie(handler));
        return this;
    }

    @Override
    public final ChannelHandler remove(String name) {
        return this.remove(this.getContextOrDie(name)).handler();
    }

    @Override
    public final <T extends ChannelHandler> T remove(Class<T> handlerType) {
        return (T)this.remove(this.getContextOrDie(handlerType)).handler();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private AbstractChannelHandlerContext remove(final AbstractChannelHandlerContext ctx) {
        assert (ctx != this.head && ctx != this.tail);
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            DefaultChannelPipeline.remove0(ctx);
            if (!this.registered) {
                this.callHandlerCallbackLater(ctx, false);
                return ctx;
            }
            EventExecutor executor = ctx.executor();
            if (!executor.inEventLoop()) {
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DefaultChannelPipeline.this.callHandlerRemoved0(ctx);
                    }
                });
                return ctx;
            }
        }
        this.callHandlerRemoved0(ctx);
        return ctx;
    }

    private static void remove0(AbstractChannelHandlerContext ctx) {
        AbstractChannelHandlerContext next;
        AbstractChannelHandlerContext prev = ctx.prev;
        prev.next = next = ctx.next;
        next.prev = prev;
    }

    @Override
    public final ChannelHandler removeFirst() {
        if (this.head.next == this.tail) {
            throw new NoSuchElementException();
        }
        return this.remove(this.head.next).handler();
    }

    @Override
    public final ChannelHandler removeLast() {
        if (this.head.next == this.tail) {
            throw new NoSuchElementException();
        }
        return this.remove(this.tail.prev).handler();
    }

    @Override
    public final ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
        this.replace(this.getContextOrDie(oldHandler), newName, newHandler);
        return this;
    }

    @Override
    public final ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
        return this.replace(this.getContextOrDie(oldName), newName, newHandler);
    }

    @Override
    public final <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
        return (T)this.replace(this.getContextOrDie(oldHandlerType), newName, newHandler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ChannelHandler replace(final AbstractChannelHandlerContext ctx, String newName, ChannelHandler newHandler) {
        AbstractChannelHandlerContext newCtx;
        assert (ctx != this.head && ctx != this.tail);
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            DefaultChannelPipeline.checkMultiplicity(newHandler);
            if (newName == null) {
                newName = this.generateName(newHandler);
            } else {
                boolean sameName = ctx.name().equals(newName);
                if (!sameName) {
                    this.checkDuplicateName(newName);
                }
            }
            newCtx = this.newContext(ctx.executor, newName, newHandler);
            DefaultChannelPipeline.replace0(ctx, newCtx);
            if (!this.registered) {
                this.callHandlerCallbackLater(newCtx, true);
                this.callHandlerCallbackLater(ctx, false);
                return ctx.handler();
            }
            EventExecutor executor = ctx.executor();
            if (!executor.inEventLoop()) {
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
                        DefaultChannelPipeline.this.callHandlerRemoved0(ctx);
                    }
                });
                return ctx.handler();
            }
        }
        this.callHandlerAdded0(newCtx);
        this.callHandlerRemoved0(ctx);
        return ctx.handler();
    }

    private static void replace0(AbstractChannelHandlerContext oldCtx, AbstractChannelHandlerContext newCtx) {
        AbstractChannelHandlerContext prev = oldCtx.prev;
        AbstractChannelHandlerContext next = oldCtx.next;
        newCtx.prev = prev;
        newCtx.next = next;
        prev.next = newCtx;
        next.prev = newCtx;
        oldCtx.prev = newCtx;
        oldCtx.next = newCtx;
    }

    private static void checkMultiplicity(ChannelHandler handler) {
        if (handler instanceof ChannelHandlerAdapter) {
            ChannelHandlerAdapter h = (ChannelHandlerAdapter)handler;
            if (!h.isSharable() && h.added) {
                throw new ChannelPipelineException(h.getClass().getName() + " is not a @Sharable handler, so can't be added or removed multiple times.");
            }
            h.added = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void callHandlerAdded0(AbstractChannelHandlerContext ctx) {
        try {
            ctx.handler().handlerAdded(ctx);
            ctx.setAddComplete();
        }
        catch (Throwable t) {
            boolean removed;
            block9 : {
                removed = false;
                try {
                    DefaultChannelPipeline.remove0(ctx);
                    try {
                        ctx.handler().handlerRemoved(ctx);
                    }
                    finally {
                        ctx.setRemoved();
                    }
                    removed = true;
                }
                catch (Throwable t2) {
                    if (!logger.isWarnEnabled()) break block9;
                    logger.warn("Failed to remove a handler: " + ctx.name(), t2);
                }
            }
            if (removed) {
                this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; removed.", t));
            }
            this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; also failed to remove.", t));
        }
    }

    private void callHandlerRemoved0(AbstractChannelHandlerContext ctx) {
        try {
            try {
                ctx.handler().handlerRemoved(ctx);
            }
            finally {
                ctx.setRemoved();
            }
        }
        catch (Throwable t) {
            this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerRemoved() has thrown an exception.", t));
        }
    }

    final void invokeHandlerAddedIfNeeded() {
        assert (this.channel.eventLoop().inEventLoop());
        if (this.firstRegistration) {
            this.firstRegistration = false;
            this.callHandlerAddedForAllHandlers();
        }
    }

    @Override
    public final ChannelHandler first() {
        ChannelHandlerContext first = this.firstContext();
        if (first == null) {
            return null;
        }
        return first.handler();
    }

    @Override
    public final ChannelHandlerContext firstContext() {
        AbstractChannelHandlerContext first = this.head.next;
        if (first == this.tail) {
            return null;
        }
        return this.head.next;
    }

    @Override
    public final ChannelHandler last() {
        AbstractChannelHandlerContext last = this.tail.prev;
        if (last == this.head) {
            return null;
        }
        return last.handler();
    }

    @Override
    public final ChannelHandlerContext lastContext() {
        AbstractChannelHandlerContext last = this.tail.prev;
        if (last == this.head) {
            return null;
        }
        return last;
    }

    @Override
    public final ChannelHandler get(String name) {
        ChannelHandlerContext ctx = this.context(name);
        if (ctx == null) {
            return null;
        }
        return ctx.handler();
    }

    @Override
    public final <T extends ChannelHandler> T get(Class<T> handlerType) {
        ChannelHandlerContext ctx = this.context(handlerType);
        if (ctx == null) {
            return null;
        }
        return (T)ctx.handler();
    }

    @Override
    public final ChannelHandlerContext context(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return this.context0(name);
    }

    @Override
    public final ChannelHandlerContext context(ChannelHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            if (ctx.handler() == handler) {
                return ctx;
            }
            ctx = ctx.next;
        }
        return null;
    }

    @Override
    public final ChannelHandlerContext context(Class<? extends ChannelHandler> handlerType) {
        if (handlerType == null) {
            throw new NullPointerException("handlerType");
        }
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            if (handlerType.isAssignableFrom(ctx.handler().getClass())) {
                return ctx;
            }
            ctx = ctx.next;
        }
        return null;
    }

    @Override
    public final List<String> names() {
        ArrayList<String> list = new ArrayList<String>();
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            list.add(ctx.name());
            ctx = ctx.next;
        }
        return list;
    }

    @Override
    public final Map<String, ChannelHandler> toMap() {
        LinkedHashMap<String, ChannelHandler> map = new LinkedHashMap<String, ChannelHandler>();
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != this.tail) {
            map.put(ctx.name(), ctx.handler());
            ctx = ctx.next;
        }
        return map;
    }

    @Override
    public final Iterator<Map.Entry<String, ChannelHandler>> iterator() {
        return this.toMap().entrySet().iterator();
    }

    public final String toString() {
        StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append('{');
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != this.tail) {
            buf.append('(').append(ctx.name()).append(" = ").append(ctx.handler().getClass().getName()).append(')');
            ctx = ctx.next;
            if (ctx == this.tail) break;
            buf.append(", ");
        }
        buf.append('}');
        return buf.toString();
    }

    @Override
    public final ChannelPipeline fireChannelRegistered() {
        AbstractChannelHandlerContext.invokeChannelRegistered(this.head);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelUnregistered() {
        AbstractChannelHandlerContext.invokeChannelUnregistered(this.head);
        return this;
    }

    private synchronized void destroy() {
        this.destroyUp(this.head.next, false);
    }

    private void destroyUp(AbstractChannelHandlerContext ctx, boolean inEventLoop) {
        Thread currentThread = Thread.currentThread();
        AbstractChannelHandlerContext tail = this.tail;
        do {
            if (ctx == tail) {
                this.destroyDown(currentThread, tail.prev, inEventLoop);
                break;
            }
            EventExecutor executor = ctx.executor();
            if (!inEventLoop && !executor.inEventLoop(currentThread)) {
                final AbstractChannelHandlerContext finalCtx = ctx;
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DefaultChannelPipeline.this.destroyUp(finalCtx, true);
                    }
                });
                break;
            }
            ctx = ctx.next;
            inEventLoop = false;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void destroyDown(Thread currentThread, AbstractChannelHandlerContext ctx, boolean inEventLoop) {
        AbstractChannelHandlerContext head = this.head;
        while (ctx != head) {
            EventExecutor executor = ctx.executor();
            if (inEventLoop || executor.inEventLoop(currentThread)) {
                DefaultChannelPipeline defaultChannelPipeline = this;
                synchronized (defaultChannelPipeline) {
                    DefaultChannelPipeline.remove0(ctx);
                }
            } else {
                final AbstractChannelHandlerContext finalCtx = ctx;
                executor.execute(new Runnable(){

                    @Override
                    public void run() {
                        DefaultChannelPipeline.this.destroyDown(Thread.currentThread(), finalCtx, true);
                    }
                });
                break;
            }
            this.callHandlerRemoved0(ctx);
            ctx = ctx.prev;
            inEventLoop = false;
        }
    }

    @Override
    public final ChannelPipeline fireChannelActive() {
        AbstractChannelHandlerContext.invokeChannelActive(this.head);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelInactive() {
        AbstractChannelHandlerContext.invokeChannelInactive(this.head);
        return this;
    }

    @Override
    public final ChannelPipeline fireExceptionCaught(Throwable cause) {
        AbstractChannelHandlerContext.invokeExceptionCaught(this.head, cause);
        return this;
    }

    @Override
    public final ChannelPipeline fireUserEventTriggered(Object event) {
        AbstractChannelHandlerContext.invokeUserEventTriggered(this.head, event);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelRead(Object msg) {
        AbstractChannelHandlerContext.invokeChannelRead(this.head, msg);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelReadComplete() {
        AbstractChannelHandlerContext.invokeChannelReadComplete(this.head);
        return this;
    }

    @Override
    public final ChannelPipeline fireChannelWritabilityChanged() {
        AbstractChannelHandlerContext.invokeChannelWritabilityChanged(this.head);
        return this;
    }

    @Override
    public final ChannelFuture bind(SocketAddress localAddress) {
        return this.tail.bind(localAddress);
    }

    @Override
    public final ChannelFuture connect(SocketAddress remoteAddress) {
        return this.tail.connect(remoteAddress);
    }

    @Override
    public final ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.tail.connect(remoteAddress, localAddress);
    }

    @Override
    public final ChannelFuture disconnect() {
        return this.tail.disconnect();
    }

    @Override
    public final ChannelFuture close() {
        return this.tail.close();
    }

    @Override
    public final ChannelFuture deregister() {
        return this.tail.deregister();
    }

    @Override
    public final ChannelPipeline flush() {
        this.tail.flush();
        return this;
    }

    @Override
    public final ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.tail.bind(localAddress, promise);
    }

    @Override
    public final ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.tail.connect(remoteAddress, promise);
    }

    @Override
    public final ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.tail.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public final ChannelFuture disconnect(ChannelPromise promise) {
        return this.tail.disconnect(promise);
    }

    @Override
    public final ChannelFuture close(ChannelPromise promise) {
        return this.tail.close(promise);
    }

    @Override
    public final ChannelFuture deregister(ChannelPromise promise) {
        return this.tail.deregister(promise);
    }

    @Override
    public final ChannelPipeline read() {
        this.tail.read();
        return this;
    }

    @Override
    public final ChannelFuture write(Object msg) {
        return this.tail.write(msg);
    }

    @Override
    public final ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.tail.write(msg, promise);
    }

    @Override
    public final ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.tail.writeAndFlush(msg, promise);
    }

    @Override
    public final ChannelFuture writeAndFlush(Object msg) {
        return this.tail.writeAndFlush(msg);
    }

    @Override
    public final ChannelPromise newPromise() {
        return new DefaultChannelPromise(this.channel);
    }

    @Override
    public final ChannelProgressivePromise newProgressivePromise() {
        return new DefaultChannelProgressivePromise(this.channel);
    }

    @Override
    public final ChannelFuture newSucceededFuture() {
        return this.succeededFuture;
    }

    @Override
    public final ChannelFuture newFailedFuture(Throwable cause) {
        return new FailedChannelFuture(this.channel, null, cause);
    }

    @Override
    public final ChannelPromise voidPromise() {
        return this.voidPromise;
    }

    private void checkDuplicateName(String name) {
        if (this.context0(name) != null) {
            throw new IllegalArgumentException("Duplicate handler name: " + name);
        }
    }

    private AbstractChannelHandlerContext context0(String name) {
        AbstractChannelHandlerContext context = this.head.next;
        while (context != this.tail) {
            if (context.name().equals(name)) {
                return context;
            }
            context = context.next;
        }
        return null;
    }

    private AbstractChannelHandlerContext getContextOrDie(String name) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(name);
        if (ctx == null) {
            throw new NoSuchElementException(name);
        }
        return ctx;
    }

    private AbstractChannelHandlerContext getContextOrDie(ChannelHandler handler) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handler);
        if (ctx == null) {
            throw new NoSuchElementException(handler.getClass().getName());
        }
        return ctx;
    }

    private AbstractChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> handlerType) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handlerType);
        if (ctx == null) {
            throw new NoSuchElementException(handlerType.getName());
        }
        return ctx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void callHandlerAddedForAllHandlers() {
        PendingHandlerCallback pendingHandlerCallbackHead;
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            assert (!this.registered);
            this.registered = true;
            pendingHandlerCallbackHead = this.pendingHandlerCallbackHead;
            this.pendingHandlerCallbackHead = null;
        }
        PendingHandlerCallback task = pendingHandlerCallbackHead;
        while (task != null) {
            task.execute();
            task = task.next;
        }
    }

    private void callHandlerCallbackLater(AbstractChannelHandlerContext ctx, boolean added) {
        assert (!this.registered);
        PendingHandlerCallback task = added ? new PendingHandlerAddedTask(ctx) : new PendingHandlerRemovedTask(ctx);
        PendingHandlerCallback pending = this.pendingHandlerCallbackHead;
        if (pending == null) {
            this.pendingHandlerCallbackHead = task;
        } else {
            while (pending.next != null) {
                pending = pending.next;
            }
            pending.next = task;
        }
    }

    protected void onUnhandledInboundException(Throwable cause) {
        try {
            logger.warn("An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.", cause);
        }
        finally {
            ReferenceCountUtil.release(cause);
        }
    }

    protected void onUnhandledInboundMessage(Object msg) {
        try {
            logger.debug("Discarded inbound message {} that reached at the tail of the pipeline. Please check your pipeline configuration.", msg);
        }
        finally {
            ReferenceCountUtil.release(msg);
        }
    }

    protected void incrementPendingOutboundBytes(long size) {
        ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
        if (buffer != null) {
            buffer.incrementPendingOutboundBytes(size);
        }
    }

    protected void decrementPendingOutboundBytes(long size) {
        ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
        if (buffer != null) {
            buffer.decrementPendingOutboundBytes(size);
        }
    }

    private final class PendingHandlerRemovedTask
    extends PendingHandlerCallback {
        PendingHandlerRemovedTask(AbstractChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        public void run() {
            DefaultChannelPipeline.this.callHandlerRemoved0(this.ctx);
        }

        @Override
        void execute() {
            EventExecutor executor = this.ctx.executor();
            if (executor.inEventLoop()) {
                DefaultChannelPipeline.this.callHandlerRemoved0(this.ctx);
            } else {
                try {
                    executor.execute(this);
                }
                catch (RejectedExecutionException e) {
                    if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                        DefaultChannelPipeline.logger.warn("Can't invoke handlerRemoved() as the EventExecutor {} rejected it, removing handler {}.", executor, this.ctx.name(), e);
                    }
                    this.ctx.setRemoved();
                }
            }
        }
    }

    private final class PendingHandlerAddedTask
    extends PendingHandlerCallback {
        PendingHandlerAddedTask(AbstractChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        public void run() {
            DefaultChannelPipeline.this.callHandlerAdded0(this.ctx);
        }

        @Override
        void execute() {
            EventExecutor executor = this.ctx.executor();
            if (executor.inEventLoop()) {
                DefaultChannelPipeline.this.callHandlerAdded0(this.ctx);
            } else {
                try {
                    executor.execute(this);
                }
                catch (RejectedExecutionException e) {
                    if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                        DefaultChannelPipeline.logger.warn("Can't invoke handlerAdded() as the EventExecutor {} rejected it, removing handler {}.", executor, this.ctx.name(), e);
                    }
                    DefaultChannelPipeline.remove0(this.ctx);
                    this.ctx.setRemoved();
                }
            }
        }
    }

    private static abstract class PendingHandlerCallback
    implements Runnable {
        final AbstractChannelHandlerContext ctx;
        PendingHandlerCallback next;

        PendingHandlerCallback(AbstractChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        abstract void execute();
    }

    final class HeadContext
    extends AbstractChannelHandlerContext
    implements ChannelOutboundHandler,
    ChannelInboundHandler {
        private final Channel.Unsafe unsafe;

        HeadContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, HEAD_NAME, false, true);
            this.unsafe = pipeline.channel().unsafe();
            this.setAddComplete();
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            this.unsafe.bind(localAddress, promise);
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            this.unsafe.connect(remoteAddress, localAddress, promise);
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            this.unsafe.disconnect(promise);
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            this.unsafe.close(promise);
        }

        @Override
        public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            this.unsafe.deregister(promise);
        }

        @Override
        public void read(ChannelHandlerContext ctx) {
            this.unsafe.beginRead();
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            this.unsafe.write(msg, promise);
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            this.unsafe.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.fireExceptionCaught(cause);
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            DefaultChannelPipeline.this.invokeHandlerAddedIfNeeded();
            ctx.fireChannelRegistered();
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelUnregistered();
            if (!DefaultChannelPipeline.this.channel.isOpen()) {
                DefaultChannelPipeline.this.destroy();
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelActive();
            this.readIfIsAutoRead();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelInactive();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.fireChannelRead(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelReadComplete();
            this.readIfIsAutoRead();
        }

        private void readIfIsAutoRead() {
            if (DefaultChannelPipeline.this.channel.config().isAutoRead()) {
                DefaultChannelPipeline.this.channel.read();
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
            ctx.fireChannelWritabilityChanged();
        }
    }

    final class TailContext
    extends AbstractChannelHandlerContext
    implements ChannelInboundHandler {
        TailContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, TAIL_NAME, true, false);
            this.setAddComplete();
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            ReferenceCountUtil.release(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            DefaultChannelPipeline.this.onUnhandledInboundException(cause);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            DefaultChannelPipeline.this.onUnhandledInboundMessage(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        }
    }

}


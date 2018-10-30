/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DelegatingChannelPromiseNotifier;
import io.netty.channel.PendingBytesTracker;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.Collection;

public abstract class AbstractCoalescingBufferQueue {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractCoalescingBufferQueue.class);
    private final ArrayDeque<Object> bufAndListenerPairs;
    private final PendingBytesTracker tracker;
    private int readableBytes;

    protected AbstractCoalescingBufferQueue(Channel channel, int initSize) {
        this.bufAndListenerPairs = new ArrayDeque(initSize);
        this.tracker = channel == null ? null : PendingBytesTracker.newTracker(channel);
    }

    public final void addFirst(ByteBuf buf, ChannelPromise promise) {
        this.addFirst(buf, AbstractCoalescingBufferQueue.toChannelFutureListener(promise));
    }

    private void addFirst(ByteBuf buf, ChannelFutureListener listener) {
        if (listener != null) {
            this.bufAndListenerPairs.addFirst(listener);
        }
        this.bufAndListenerPairs.addFirst(buf);
        this.incrementReadableBytes(buf.readableBytes());
    }

    public final void add(ByteBuf buf) {
        this.add(buf, (ChannelFutureListener)null);
    }

    public final void add(ByteBuf buf, ChannelPromise promise) {
        this.add(buf, AbstractCoalescingBufferQueue.toChannelFutureListener(promise));
    }

    public final void add(ByteBuf buf, ChannelFutureListener listener) {
        this.bufAndListenerPairs.add(buf);
        if (listener != null) {
            this.bufAndListenerPairs.add(listener);
        }
        this.incrementReadableBytes(buf.readableBytes());
    }

    public final ByteBuf removeFirst(ChannelPromise aggregatePromise) {
        Object entry = this.bufAndListenerPairs.poll();
        if (entry == null) {
            return null;
        }
        assert (entry instanceof ByteBuf);
        ByteBuf result = (ByteBuf)entry;
        this.decrementReadableBytes(result.readableBytes());
        entry = this.bufAndListenerPairs.peek();
        if (entry instanceof ChannelFutureListener) {
            aggregatePromise.addListener((ChannelFutureListener)entry);
            this.bufAndListenerPairs.poll();
        }
        return result;
    }

    public final ByteBuf remove(ByteBufAllocator alloc, int bytes, ChannelPromise aggregatePromise) {
        int originalBytes;
        ByteBuf toReturn;
        ObjectUtil.checkPositiveOrZero(bytes, "bytes");
        ObjectUtil.checkNotNull(aggregatePromise, "aggregatePromise");
        if (this.bufAndListenerPairs.isEmpty()) {
            return this.removeEmptyValue();
        }
        bytes = Math.min(bytes, this.readableBytes);
        toReturn = null;
        ByteBuf entryBuffer = null;
        originalBytes = bytes;
        try {
            Object entry;
            while ((entry = this.bufAndListenerPairs.poll()) != null) {
                if (entry instanceof ChannelFutureListener) {
                    aggregatePromise.addListener((ChannelFutureListener)entry);
                    continue;
                }
                entryBuffer = (ByteBuf)entry;
                if (entryBuffer.readableBytes() > bytes) {
                    this.bufAndListenerPairs.addFirst(entryBuffer);
                    if (bytes > 0) {
                        entryBuffer = entryBuffer.readRetainedSlice(bytes);
                        toReturn = toReturn == null ? this.composeFirst(alloc, entryBuffer) : this.compose(alloc, toReturn, entryBuffer);
                        bytes = 0;
                    }
                    break;
                }
                bytes -= entryBuffer.readableBytes();
                toReturn = toReturn == null ? this.composeFirst(alloc, entryBuffer) : this.compose(alloc, toReturn, entryBuffer);
                entryBuffer = null;
            }
        }
        catch (Throwable cause) {
            ReferenceCountUtil.safeRelease(entryBuffer);
            ReferenceCountUtil.safeRelease(toReturn);
            aggregatePromise.setFailure(cause);
            PlatformDependent.throwException(cause);
        }
        this.decrementReadableBytes(originalBytes - bytes);
        return toReturn;
    }

    public final int readableBytes() {
        return this.readableBytes;
    }

    public final boolean isEmpty() {
        return this.bufAndListenerPairs.isEmpty();
    }

    public final void releaseAndFailAll(ChannelOutboundInvoker invoker, Throwable cause) {
        this.releaseAndCompleteAll(invoker.newFailedFuture(cause));
    }

    public final void copyTo(AbstractCoalescingBufferQueue dest) {
        dest.bufAndListenerPairs.addAll(this.bufAndListenerPairs);
        dest.incrementReadableBytes(this.readableBytes);
    }

    public final void writeAndRemoveAll(ChannelHandlerContext ctx) {
        this.decrementReadableBytes(this.readableBytes);
        Throwable pending = null;
        ByteBuf previousBuf = null;
        do {
            Object entry = this.bufAndListenerPairs.poll();
            try {
                if (entry == null) {
                    if (previousBuf == null) break;
                    ctx.write(previousBuf, ctx.voidPromise());
                    break;
                }
                if (entry instanceof ByteBuf) {
                    if (previousBuf != null) {
                        ctx.write(previousBuf, ctx.voidPromise());
                    }
                    previousBuf = (ByteBuf)entry;
                    continue;
                }
                if (entry instanceof ChannelPromise) {
                    ctx.write(previousBuf, (ChannelPromise)entry);
                    previousBuf = null;
                    continue;
                }
                ctx.write(previousBuf).addListener((ChannelFutureListener)entry);
                previousBuf = null;
            }
            catch (Throwable t) {
                if (pending == null) {
                    pending = t;
                    continue;
                }
                logger.info("Throwable being suppressed because Throwable {} is already pending", (Object)pending, (Object)t);
            }
        } while (true);
        if (pending != null) {
            throw new IllegalStateException(pending);
        }
    }

    protected abstract ByteBuf compose(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3);

    protected final ByteBuf composeIntoComposite(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
        CompositeByteBuf composite = alloc.compositeBuffer(this.size() + 2);
        try {
            composite.addComponent(true, cumulation);
            composite.addComponent(true, next);
        }
        catch (Throwable cause) {
            composite.release();
            ReferenceCountUtil.safeRelease(next);
            PlatformDependent.throwException(cause);
        }
        return composite;
    }

    protected final ByteBuf copyAndCompose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
        ByteBuf newCumulation = alloc.ioBuffer(cumulation.readableBytes() + next.readableBytes());
        try {
            newCumulation.writeBytes(cumulation).writeBytes(next);
        }
        catch (Throwable cause) {
            newCumulation.release();
            ReferenceCountUtil.safeRelease(next);
            PlatformDependent.throwException(cause);
        }
        cumulation.release();
        next.release();
        return newCumulation;
    }

    protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first) {
        return first;
    }

    protected abstract ByteBuf removeEmptyValue();

    protected final int size() {
        return this.bufAndListenerPairs.size();
    }

    private void releaseAndCompleteAll(ChannelFuture future) {
        Object entry;
        this.decrementReadableBytes(this.readableBytes);
        Throwable pending = null;
        while ((entry = this.bufAndListenerPairs.poll()) != null) {
            try {
                if (entry instanceof ByteBuf) {
                    ReferenceCountUtil.safeRelease(entry);
                    continue;
                }
                ((ChannelFutureListener)entry).operationComplete(future);
            }
            catch (Throwable t) {
                if (pending == null) {
                    pending = t;
                    continue;
                }
                logger.info("Throwable being suppressed because Throwable {} is already pending", (Object)pending, (Object)t);
            }
        }
        if (pending != null) {
            throw new IllegalStateException(pending);
        }
    }

    private void incrementReadableBytes(int increment) {
        int nextReadableBytes = this.readableBytes + increment;
        if (nextReadableBytes < this.readableBytes) {
            throw new IllegalStateException("buffer queue length overflow: " + this.readableBytes + " + " + increment);
        }
        this.readableBytes = nextReadableBytes;
        if (this.tracker != null) {
            this.tracker.incrementPendingOutboundBytes(increment);
        }
    }

    private void decrementReadableBytes(int decrement) {
        this.readableBytes -= decrement;
        assert (this.readableBytes >= 0);
        if (this.tracker != null) {
            this.tracker.decrementPendingOutboundBytes(decrement);
        }
    }

    private static ChannelFutureListener toChannelFutureListener(ChannelPromise promise) {
        return promise.isVoid() ? null : new DelegatingChannelPromiseNotifier(promise);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import sun.misc.Unsafe;

@DoNotMock(value="Use Futures.immediate*Future or SettableFuture")
@GwtCompatible(emulated=true)
public abstract class AbstractFuture<V>
extends FluentFuture<V> {
    private static final boolean GENERATE_CANCELLATION_CAUSES;
    private static final Logger log;
    private static final long SPIN_THRESHOLD_NANOS = 1000L;
    private static final AtomicHelper ATOMIC_HELPER;
    private static final Object NULL;
    private volatile Object value;
    private volatile Listener listeners;
    private volatile Waiter waiters;

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private void removeWaiter(Waiter node) {
        node.thread = null;
        block0 : do {
            pred = null;
            curr = this.waiters;
            if (curr == Waiter.TOMBSTONE) {
                return;
            }
            while (curr != null) {
                succ = curr.next;
                if (curr.thread != null) {
                    pred = curr;
                } else if (pred != null) {
                    pred.next = succ;
                    if (pred.thread == null) {
                        continue block0;
                    }
                } else {
                    if (AbstractFuture.ATOMIC_HELPER.casWaiters(this, curr, succ)) ** break;
                    continue block0;
                }
                curr = succ;
            }
            return;
            break;
        } while (true);
    }

    protected AbstractFuture() {
    }

    @CanIgnoreReturnValue
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        long remainingNanos;
        long endNanos;
        Object localValue;
        block11 : {
            remainingNanos = unit.toNanos(timeout);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            localValue = this.value;
            if (localValue != null & !(localValue instanceof SetFuture)) {
                return this.getDoneValue(localValue);
            }
            long l = endNanos = remainingNanos > 0L ? System.nanoTime() + remainingNanos : 0L;
            if (remainingNanos >= 1000L) {
                Waiter oldHead = this.waiters;
                if (oldHead != Waiter.TOMBSTONE) {
                    Waiter node = new Waiter();
                    do {
                        node.setNext(oldHead);
                        if (!ATOMIC_HELPER.casWaiters(this, oldHead, node)) continue;
                        do {
                            LockSupport.parkNanos(this, remainingNanos);
                            if (Thread.interrupted()) {
                                this.removeWaiter(node);
                                throw new InterruptedException();
                            }
                            localValue = this.value;
                            if (!(localValue != null & !(localValue instanceof SetFuture))) continue;
                            return this.getDoneValue(localValue);
                        } while ((remainingNanos = endNanos - System.nanoTime()) >= 1000L);
                        this.removeWaiter(node);
                        break block11;
                    } while ((oldHead = this.waiters) != Waiter.TOMBSTONE);
                }
                return this.getDoneValue(this.value);
            }
        }
        while (remainingNanos > 0L) {
            localValue = this.value;
            if (localValue != null & !(localValue instanceof SetFuture)) {
                return this.getDoneValue(localValue);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            remainingNanos = endNanos - System.nanoTime();
        }
        String futureToString = this.toString();
        if (this.isDone()) {
            throw new TimeoutException("Waited " + timeout + " " + Ascii.toLowerCase(unit.toString()) + " but future completed as timeout expired");
        }
        throw new TimeoutException("Waited " + timeout + " " + Ascii.toLowerCase(unit.toString()) + " for " + futureToString);
    }

    @CanIgnoreReturnValue
    @Override
    public V get() throws InterruptedException, ExecutionException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Object localValue = this.value;
        if (localValue != null & !(localValue instanceof SetFuture)) {
            return this.getDoneValue(localValue);
        }
        Waiter oldHead = this.waiters;
        if (oldHead != Waiter.TOMBSTONE) {
            Waiter node = new Waiter();
            do {
                node.setNext(oldHead);
                if (!ATOMIC_HELPER.casWaiters(this, oldHead, node)) continue;
                do {
                    LockSupport.park(this);
                    if (!Thread.interrupted()) continue;
                    this.removeWaiter(node);
                    throw new InterruptedException();
                } while (!((localValue = this.value) != null & !(localValue instanceof SetFuture)));
                return this.getDoneValue(localValue);
            } while ((oldHead = this.waiters) != Waiter.TOMBSTONE);
        }
        return this.getDoneValue(this.value);
    }

    private V getDoneValue(Object obj) throws ExecutionException {
        if (obj instanceof Cancellation) {
            throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", ((Cancellation)obj).cause);
        }
        if (obj instanceof Failure) {
            throw new ExecutionException(((Failure)obj).exception);
        }
        if (obj == NULL) {
            return null;
        }
        Object asV = obj;
        return (V)asV;
    }

    @Override
    public boolean isDone() {
        Object localValue = this.value;
        return localValue != null & !(localValue instanceof SetFuture);
    }

    @Override
    public boolean isCancelled() {
        Object localValue = this.value;
        return localValue instanceof Cancellation;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @CanIgnoreReturnValue
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Object localValue = this.value;
        boolean rValue = false;
        if (!(localValue == null | localValue instanceof SetFuture)) return rValue;
        CancellationException cause = GENERATE_CANCELLATION_CAUSES ? new CancellationException("Future.cancel() was called.") : null;
        Cancellation valueToSet = new Cancellation(mayInterruptIfRunning, cause);
        AbstractFuture abstractFuture = this;
        do {
            if (ATOMIC_HELPER.casValue(abstractFuture, localValue, valueToSet)) {
                rValue = true;
                if (mayInterruptIfRunning) {
                    abstractFuture.interruptTask();
                }
                AbstractFuture.complete(abstractFuture);
                if (!(localValue instanceof SetFuture)) return rValue;
                ListenableFuture futureToPropagateTo = ((SetFuture)localValue).future;
                if (futureToPropagateTo instanceof TrustedFuture) {
                    AbstractFuture trusted = (AbstractFuture)futureToPropagateTo;
                    localValue = trusted.value;
                    if (!(localValue == null | localValue instanceof SetFuture)) return rValue;
                    abstractFuture = trusted;
                    continue;
                }
                futureToPropagateTo.cancel(mayInterruptIfRunning);
                return rValue;
            }
            localValue = abstractFuture.value;
            if (!(localValue instanceof SetFuture)) return rValue;
        } while (true);
    }

    protected void interruptTask() {
    }

    protected final boolean wasInterrupted() {
        Object localValue = this.value;
        return localValue instanceof Cancellation && ((Cancellation)localValue).wasInterrupted;
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {
        Preconditions.checkNotNull(listener, "Runnable was null.");
        Preconditions.checkNotNull(executor, "Executor was null.");
        Listener oldHead = this.listeners;
        if (oldHead != Listener.TOMBSTONE) {
            Listener newNode = new Listener(listener, executor);
            do {
                newNode.next = oldHead;
                if (!ATOMIC_HELPER.casListeners(this, oldHead, newNode)) continue;
                return;
            } while ((oldHead = this.listeners) != Listener.TOMBSTONE);
        }
        AbstractFuture.executeListener(listener, executor);
    }

    @CanIgnoreReturnValue
    protected boolean set(@Nullable V value) {
        Object valueToSet;
        Object object = valueToSet = value == null ? NULL : value;
        if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
            AbstractFuture.complete(this);
            return true;
        }
        return false;
    }

    @CanIgnoreReturnValue
    protected boolean setException(Throwable throwable) {
        Failure valueToSet = new Failure(Preconditions.checkNotNull(throwable));
        if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
            AbstractFuture.complete(this);
            return true;
        }
        return false;
    }

    @Beta
    @CanIgnoreReturnValue
    protected boolean setFuture(ListenableFuture<? extends V> future) {
        Preconditions.checkNotNull(future);
        Object localValue = this.value;
        if (localValue == null) {
            if (future.isDone()) {
                Object value = AbstractFuture.getFutureValue(future);
                if (ATOMIC_HELPER.casValue(this, null, value)) {
                    AbstractFuture.complete(this);
                    return true;
                }
                return false;
            }
            SetFuture<? extends V> valueToSet = new SetFuture<V>(this, future);
            if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
                try {
                    future.addListener(valueToSet, MoreExecutors.directExecutor());
                }
                catch (Throwable t) {
                    Failure failure;
                    try {
                        failure = new Failure(t);
                    }
                    catch (Throwable oomMostLikely2) {
                        failure = Failure.FALLBACK_INSTANCE;
                    }
                    boolean oomMostLikely2 = ATOMIC_HELPER.casValue(this, valueToSet, failure);
                }
                return true;
            }
            localValue = this.value;
        }
        if (localValue instanceof Cancellation) {
            future.cancel(((Cancellation)localValue).wasInterrupted);
        }
        return false;
    }

    private static Object getFutureValue(ListenableFuture<?> future) {
        Object valueToSet;
        if (future instanceof TrustedFuture) {
            return ((AbstractFuture)future).value;
        }
        try {
            Object v = Futures.getDone(future);
            valueToSet = v == null ? NULL : v;
        }
        catch (ExecutionException exception) {
            valueToSet = new Failure(exception.getCause());
        }
        catch (CancellationException cancellation) {
            valueToSet = new Cancellation(false, cancellation);
        }
        catch (Throwable t) {
            valueToSet = new Failure(t);
        }
        return valueToSet;
    }

    private static void complete(AbstractFuture<?> future) {
        Listener next = null;
        block0 : do {
            AbstractFuture.super.releaseWaiters();
            future.afterDone();
            next = AbstractFuture.super.clearListeners(next);
            future = null;
            while (next != null) {
                Listener curr = next;
                next = next.next;
                Runnable task = curr.task;
                if (task instanceof SetFuture) {
                    Object valueToSet;
                    SetFuture setFuture = (SetFuture)task;
                    future = setFuture.owner;
                    if (future.value != setFuture || !ATOMIC_HELPER.casValue(future, setFuture, valueToSet = AbstractFuture.getFutureValue(setFuture.future))) continue;
                    continue block0;
                }
                AbstractFuture.executeListener(task, curr.executor);
            }
            break;
        } while (true);
    }

    @Beta
    protected void afterDone() {
    }

    final Throwable trustedGetException() {
        return ((Failure)this.value).exception;
    }

    final void maybePropagateCancellation(@Nullable Future<?> related) {
        if (related != null & this.isCancelled()) {
            related.cancel(this.wasInterrupted());
        }
    }

    private void releaseWaiters() {
        Waiter head;
        while (!ATOMIC_HELPER.casWaiters(this, head = this.waiters, Waiter.TOMBSTONE)) {
        }
        Waiter currentWaiter = head;
        while (currentWaiter != null) {
            currentWaiter.unpark();
            currentWaiter = currentWaiter.next;
        }
    }

    private Listener clearListeners(Listener onto) {
        Listener head;
        while (!ATOMIC_HELPER.casListeners(this, head = this.listeners, Listener.TOMBSTONE)) {
        }
        Listener reversedList = onto;
        while (head != null) {
            Listener tmp = head;
            head = head.next;
            tmp.next = reversedList;
            reversedList = tmp;
        }
        return reversedList;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder().append(Object.super.toString()).append("[status=");
        if (this.isCancelled()) {
            builder.append("CANCELLED");
        } else if (this.isDone()) {
            this.addDoneString(builder);
        } else {
            String pendingDescription;
            try {
                pendingDescription = this.pendingToString();
            }
            catch (RuntimeException e) {
                pendingDescription = "Exception thrown from implementation: " + e.getClass();
            }
            if (!Strings.isNullOrEmpty(pendingDescription)) {
                builder.append("PENDING, info=[").append(pendingDescription).append("]");
            } else if (this.isDone()) {
                this.addDoneString(builder);
            } else {
                builder.append("PENDING");
            }
        }
        return builder.append("]").toString();
    }

    @Nullable
    protected String pendingToString() {
        Object localValue = this.value;
        if (localValue instanceof SetFuture) {
            return "setFuture=[" + ((SetFuture)localValue).future + "]";
        }
        if (this instanceof ScheduledFuture) {
            return "remaining delay=[" + ((ScheduledFuture)((Object)this)).getDelay(TimeUnit.MILLISECONDS) + " ms]";
        }
        return null;
    }

    private void addDoneString(StringBuilder builder) {
        try {
            Object value = Futures.getDone(this);
            builder.append("SUCCESS, result=[").append(value).append("]");
        }
        catch (ExecutionException e) {
            builder.append("FAILURE, cause=[").append(e.getCause()).append("]");
        }
        catch (CancellationException e) {
            builder.append("CANCELLED");
        }
        catch (RuntimeException e) {
            builder.append("UNKNOWN, cause=[").append(e.getClass()).append(" thrown from get()]");
        }
    }

    private static void executeListener(Runnable runnable, Executor executor) {
        try {
            executor.execute(runnable);
        }
        catch (RuntimeException e) {
            log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, e);
        }
    }

    private static CancellationException cancellationExceptionWithCause(@Nullable String message, @Nullable Throwable cause) {
        CancellationException exception = new CancellationException(message);
        exception.initCause(cause);
        return exception;
    }

    static {
        AtomicHelper helper;
        GENERATE_CANCELLATION_CAUSES = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
        log = Logger.getLogger(AbstractFuture.class.getName());
        try {
            helper = new UnsafeAtomicHelper();
        }
        catch (Throwable unsafeFailure2) {
            try {
                helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Thread.class, "thread"), AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Waiter.class, "next"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Waiter.class, "waiters"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Listener.class, "listeners"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, "value"));
            }
            catch (Throwable atomicReferenceFieldUpdaterFailure) {
                log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", unsafeFailure2);
                log.log(Level.SEVERE, "SafeAtomicHelper is broken!", atomicReferenceFieldUpdaterFailure);
                helper = new SynchronizedHelper();
            }
        }
        ATOMIC_HELPER = helper;
        Class<LockSupport> unsafeFailure2 = LockSupport.class;
        NULL = new Object();
    }

    private static final class SynchronizedHelper
    extends AtomicHelper {
        private SynchronizedHelper() {
            super();
        }

        @Override
        void putThread(Waiter waiter, Thread newValue) {
            waiter.thread = newValue;
        }

        @Override
        void putNext(Waiter waiter, Waiter newValue) {
            waiter.next = newValue;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        boolean casWaiters(AbstractFuture<?> future, Waiter expect, Waiter update) {
            AbstractFuture<?> abstractFuture = future;
            synchronized (abstractFuture) {
                if (future.waiters == expect) {
                    future.waiters = update;
                    return true;
                }
                return false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        boolean casListeners(AbstractFuture<?> future, Listener expect, Listener update) {
            AbstractFuture<?> abstractFuture = future;
            synchronized (abstractFuture) {
                if (future.listeners == expect) {
                    future.listeners = update;
                    return true;
                }
                return false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        boolean casValue(AbstractFuture<?> future, Object expect, Object update) {
            AbstractFuture<?> abstractFuture = future;
            synchronized (abstractFuture) {
                if (future.value == expect) {
                    future.value = update;
                    return true;
                }
                return false;
            }
        }
    }

    private static final class SafeAtomicHelper
    extends AtomicHelper {
        final AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;

        SafeAtomicHelper(AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater, AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater, AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater, AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater, AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater) {
            super();
            this.waiterThreadUpdater = waiterThreadUpdater;
            this.waiterNextUpdater = waiterNextUpdater;
            this.waitersUpdater = waitersUpdater;
            this.listenersUpdater = listenersUpdater;
            this.valueUpdater = valueUpdater;
        }

        @Override
        void putThread(Waiter waiter, Thread newValue) {
            this.waiterThreadUpdater.lazySet(waiter, newValue);
        }

        @Override
        void putNext(Waiter waiter, Waiter newValue) {
            this.waiterNextUpdater.lazySet(waiter, newValue);
        }

        @Override
        boolean casWaiters(AbstractFuture<?> future, Waiter expect, Waiter update) {
            return this.waitersUpdater.compareAndSet(future, expect, update);
        }

        @Override
        boolean casListeners(AbstractFuture<?> future, Listener expect, Listener update) {
            return this.listenersUpdater.compareAndSet(future, expect, update);
        }

        @Override
        boolean casValue(AbstractFuture<?> future, Object expect, Object update) {
            return this.valueUpdater.compareAndSet(future, expect, update);
        }
    }

    private static final class UnsafeAtomicHelper
    extends AtomicHelper {
        static final Unsafe UNSAFE;
        static final long LISTENERS_OFFSET;
        static final long WAITERS_OFFSET;
        static final long VALUE_OFFSET;
        static final long WAITER_THREAD_OFFSET;
        static final long WAITER_NEXT_OFFSET;

        private UnsafeAtomicHelper() {
            super();
        }

        @Override
        void putThread(Waiter waiter, Thread newValue) {
            UNSAFE.putObject((Object)waiter, WAITER_THREAD_OFFSET, (Object)newValue);
        }

        @Override
        void putNext(Waiter waiter, Waiter newValue) {
            UNSAFE.putObject((Object)waiter, WAITER_NEXT_OFFSET, (Object)newValue);
        }

        @Override
        boolean casWaiters(AbstractFuture<?> future, Waiter expect, Waiter update) {
            return UNSAFE.compareAndSwapObject(future, WAITERS_OFFSET, expect, update);
        }

        @Override
        boolean casListeners(AbstractFuture<?> future, Listener expect, Listener update) {
            return UNSAFE.compareAndSwapObject(future, LISTENERS_OFFSET, expect, update);
        }

        @Override
        boolean casValue(AbstractFuture<?> future, Object expect, Object update) {
            return UNSAFE.compareAndSwapObject(future, VALUE_OFFSET, expect, update);
        }

        static {
            Unsafe unsafe = null;
            try {
                unsafe = Unsafe.getUnsafe();
            }
            catch (SecurityException tryReflectionInstead) {
                try {
                    unsafe = (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                        @Override
                        public Unsafe run() throws Exception {
                            Class<Unsafe> k = Unsafe.class;
                            for (Field f : k.getDeclaredFields()) {
                                f.setAccessible(true);
                                Object x = f.get(null);
                                if (!k.isInstance(x)) continue;
                                return k.cast(x);
                            }
                            throw new NoSuchFieldError("the Unsafe");
                        }
                    });
                }
                catch (PrivilegedActionException e) {
                    throw new RuntimeException("Could not initialize intrinsics", e.getCause());
                }
            }
            try {
                Class<AbstractFuture> abstractFuture = AbstractFuture.class;
                WAITERS_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("waiters"));
                LISTENERS_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("listeners"));
                VALUE_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("value"));
                WAITER_THREAD_OFFSET = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("thread"));
                WAITER_NEXT_OFFSET = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("next"));
                UNSAFE = unsafe;
            }
            catch (Exception e) {
                Throwables.throwIfUnchecked(e);
                throw new RuntimeException(e);
            }
        }

    }

    private static abstract class AtomicHelper {
        private AtomicHelper() {
        }

        abstract void putThread(Waiter var1, Thread var2);

        abstract void putNext(Waiter var1, Waiter var2);

        abstract boolean casWaiters(AbstractFuture<?> var1, Waiter var2, Waiter var3);

        abstract boolean casListeners(AbstractFuture<?> var1, Listener var2, Listener var3);

        abstract boolean casValue(AbstractFuture<?> var1, Object var2, Object var3);
    }

    private static final class SetFuture<V>
    implements Runnable {
        final AbstractFuture<V> owner;
        final ListenableFuture<? extends V> future;

        SetFuture(AbstractFuture<V> owner, ListenableFuture<? extends V> future) {
            this.owner = owner;
            this.future = future;
        }

        @Override
        public void run() {
            if (this.owner.value != this) {
                return;
            }
            Object valueToSet = AbstractFuture.getFutureValue(this.future);
            if (ATOMIC_HELPER.casValue(this.owner, this, valueToSet)) {
                AbstractFuture.complete(this.owner);
            }
        }
    }

    private static final class Cancellation {
        final boolean wasInterrupted;
        @Nullable
        final Throwable cause;

        Cancellation(boolean wasInterrupted, @Nullable Throwable cause) {
            this.wasInterrupted = wasInterrupted;
            this.cause = cause;
        }
    }

    private static final class Failure {
        static final Failure FALLBACK_INSTANCE = new Failure(new Throwable("Failure occurred while trying to finish a future."){

            @Override
            public synchronized Throwable fillInStackTrace() {
                return this;
            }
        });
        final Throwable exception;

        Failure(Throwable exception) {
            this.exception = Preconditions.checkNotNull(exception);
        }

    }

    private static final class Listener {
        static final Listener TOMBSTONE = new Listener(null, null);
        final Runnable task;
        final Executor executor;
        @Nullable
        Listener next;

        Listener(Runnable task, Executor executor) {
            this.task = task;
            this.executor = executor;
        }
    }

    private static final class Waiter {
        static final Waiter TOMBSTONE = new Waiter(false);
        @Nullable
        volatile Thread thread;
        @Nullable
        volatile Waiter next;

        Waiter(boolean unused) {
        }

        Waiter() {
            ATOMIC_HELPER.putThread(this, Thread.currentThread());
        }

        void setNext(Waiter next) {
            ATOMIC_HELPER.putNext(this, next);
        }

        void unpark() {
            Thread w = this.thread;
            if (w != null) {
                this.thread = null;
                LockSupport.unpark(w);
            }
        }
    }

    static abstract class TrustedFuture<V>
    extends AbstractFuture<V> {
        TrustedFuture() {
        }

        @CanIgnoreReturnValue
        @Override
        public final V get() throws InterruptedException, ExecutionException {
            return super.get();
        }

        @CanIgnoreReturnValue
        @Override
        public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return super.get(timeout, unit);
        }

        @Override
        public final boolean isDone() {
            return super.isDone();
        }

        @Override
        public final boolean isCancelled() {
            return super.isCancelled();
        }

        @Override
        public final void addListener(Runnable listener, Executor executor) {
            super.addListener(listener, executor);
        }

        @CanIgnoreReturnValue
        @Override
        public final boolean cancel(boolean mayInterruptIfRunning) {
            return super.cancel(mayInterruptIfRunning);
        }
    }

}


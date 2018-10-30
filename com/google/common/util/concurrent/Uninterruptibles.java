/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtCompatible(emulated=true)
public final class Uninterruptibles {
    @GwtIncompatible
    public static void awaitUninterruptibly(CountDownLatch latch) {
        boolean interrupted = false;
        do {
            try {
                latch.await();
                return;
            }
            catch (InterruptedException e) {
                interrupted = true;
                continue;
            }
            break;
        } while (true);
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @CanIgnoreReturnValue
    @GwtIncompatible
    public static boolean awaitUninterruptibly(CountDownLatch latch, long timeout, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            do {
                try {
                    boolean bl = latch.await(remainingNanos, TimeUnit.NANOSECONDS);
                    return bl;
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                    continue;
                }
                break;
            } while (true);
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @GwtIncompatible
    public static void joinUninterruptibly(Thread toJoin) {
        boolean interrupted = false;
        do {
            try {
                toJoin.join();
                return;
            }
            catch (InterruptedException e) {
                interrupted = true;
                continue;
            }
            break;
        } while (true);
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @CanIgnoreReturnValue
    public static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
        boolean interrupted = false;
        do {
            try {
                V v = future.get();
                return v;
            }
            catch (InterruptedException e) {
                interrupted = true;
                continue;
            }
            break;
        } while (true);
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @CanIgnoreReturnValue
    @GwtIncompatible
    public static <V> V getUninterruptibly(Future<V> future, long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            do {
                V v;
                try {
                    v = future.get(remainingNanos, TimeUnit.NANOSECONDS);
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                    continue;
                }
                return v;
                break;
            } while (true);
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @GwtIncompatible
    public static void joinUninterruptibly(Thread toJoin, long timeout, TimeUnit unit) {
        Preconditions.checkNotNull(toJoin);
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            do {
                try {
                    TimeUnit.NANOSECONDS.timedJoin(toJoin, remainingNanos);
                    return;
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                    continue;
                }
                break;
            } while (true);
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @GwtIncompatible
    public static <E> E takeUninterruptibly(BlockingQueue<E> queue) {
        boolean interrupted = false;
        do {
            try {
                E e = queue.take();
                return e;
            }
            catch (InterruptedException e) {
                interrupted = true;
                continue;
            }
            break;
        } while (true);
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @GwtIncompatible
    public static <E> void putUninterruptibly(BlockingQueue<E> queue, E element) {
        boolean interrupted = false;
        do {
            try {
                queue.put(element);
                return;
            }
            catch (InterruptedException e) {
                interrupted = true;
                continue;
            }
            break;
        } while (true);
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @GwtIncompatible
    public static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(sleepFor);
            long end = System.nanoTime() + remainingNanos;
            do {
                try {
                    TimeUnit.NANOSECONDS.sleep(remainingNanos);
                    return;
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                    continue;
                }
                break;
            } while (true);
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @GwtIncompatible
    public static boolean tryAcquireUninterruptibly(Semaphore semaphore, long timeout, TimeUnit unit) {
        return Uninterruptibles.tryAcquireUninterruptibly(semaphore, 1, timeout, unit);
    }

    @GwtIncompatible
    public static boolean tryAcquireUninterruptibly(Semaphore semaphore, int permits, long timeout, TimeUnit unit) {
        boolean interrupted = false;
        try {
            long remainingNanos = unit.toNanos(timeout);
            long end = System.nanoTime() + remainingNanos;
            do {
                try {
                    boolean bl = semaphore.tryAcquire(permits, remainingNanos, TimeUnit.NANOSECONDS);
                    return bl;
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                    continue;
                }
                break;
            } while (true);
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private Uninterruptibles() {
    }
}


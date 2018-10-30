/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.hash;

import com.google.common.annotations.GwtIncompatible;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import javax.annotation.Nullable;
import sun.misc.Unsafe;

@GwtIncompatible
abstract class Striped64
extends Number {
    static final ThreadLocal<int[]> threadHashCode = new ThreadLocal<T>();
    static final Random rng = new Random();
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    volatile transient Cell[] cells;
    volatile transient long base;
    volatile transient int busy;
    private static final Unsafe UNSAFE;
    private static final long baseOffset;
    private static final long busyOffset;

    Striped64() {
    }

    final boolean casBase(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, baseOffset, cmp, val);
    }

    final boolean casBusy() {
        return UNSAFE.compareAndSwapInt(this, busyOffset, 0, 1);
    }

    abstract long fn(long var1, long var3);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    final void retryUpdate(long x, @Nullable int[] hc, boolean wasUncontended) {
        int h;
        if (hc == null) {
            hc = new int[1];
            threadHashCode.set(hc);
            int r = rng.nextInt();
            hc[0] = r == 0 ? 1 : r;
            h = hc[0];
        } else {
            h = hc[0];
        }
        boolean collide = false;
        do {
            long v;
            Cell[] as;
            int n;
            if ((as = this.cells) != null && (n = as.length) > 0) {
                Cell a = as[n - 1 & h];
                if (a == null) {
                    if (this.busy == 0) {
                        Cell r = new Cell(x);
                        if (this.busy == 0 && this.casBusy()) {
                            boolean created;
                            created = false;
                            try {
                                int m;
                                int j;
                                Cell[] rs = this.cells;
                                if (rs != null && (m = rs.length) > 0 && rs[j = m - 1 & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            }
                            finally {
                                this.busy = 0;
                            }
                            if (!created) continue;
                            return;
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) {
                    wasUncontended = true;
                } else {
                    v = a.value;
                    if (a.cas(v, this.fn(v, x))) return;
                    if (n >= NCPU || this.cells != as) {
                        collide = false;
                    } else if (!collide) {
                        collide = true;
                    } else if (this.busy == 0 && this.casBusy()) {
                        try {
                            if (this.cells == as) {
                                Cell[] rs = new Cell[n << 1];
                                for (int i = 0; i < n; ++i) {
                                    rs[i] = as[i];
                                }
                                this.cells = rs;
                            }
                        }
                        finally {
                            this.busy = 0;
                        }
                        collide = false;
                        continue;
                    }
                }
                h ^= h << 13;
                h ^= h >>> 17;
                h ^= h << 5;
                hc[0] = h;
                continue;
            }
            if (this.busy == 0 && this.cells == as && this.casBusy()) {
                boolean init;
                init = false;
                try {
                    if (this.cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(x);
                        this.cells = rs;
                        init = true;
                    }
                }
                finally {
                    this.busy = 0;
                }
                if (!init) continue;
                return;
            }
            v = this.base;
            if (this.casBase(v, this.fn(v, x))) return;
        } while (true);
    }

    final void internalReset(long initialValue) {
        Cell[] as = this.cells;
        this.base = initialValue;
        if (as != null) {
            for (Cell a : as) {
                if (a == null) continue;
                a.value = initialValue;
            }
        }
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (SecurityException securityException) {
            try {
                return (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

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
    }

    static {
        try {
            UNSAFE = Striped64.getUnsafe();
            Class<Striped64> sk = Striped64.class;
            baseOffset = UNSAFE.objectFieldOffset(sk.getDeclaredField("base"));
            busyOffset = UNSAFE.objectFieldOffset(sk.getDeclaredField("busy"));
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }

    static final class Cell {
        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long value;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;
        private static final Unsafe UNSAFE;
        private static final long valueOffset;

        Cell(long x) {
            this.value = x;
        }

        final boolean cas(long cmp, long val) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
        }

        static {
            try {
                UNSAFE = Striped64.getUnsafe();
                Class<Cell> ak = Cell.class;
                valueOffset = UNSAFE.objectFieldOffset(ak.getDeclaredField("value"));
            }
            catch (Exception e) {
                throw new Error(e);
            }
        }
    }

}


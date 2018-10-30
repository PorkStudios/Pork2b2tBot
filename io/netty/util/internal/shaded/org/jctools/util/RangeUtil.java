/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.util;

public final class RangeUtil {
    public static long checkPositive(long n, String name) {
        if (n <= 0L) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: > 0)");
        }
        return n;
    }

    public static int checkPositiveOrZero(int n, String name) {
        if (n < 0) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: >= 0)");
        }
        return n;
    }

    public static int checkLessThan(int n, int expected, String name) {
        if (n >= expected) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: < " + expected + ')');
        }
        return n;
    }

    public static int checkLessThanOrEqual(int n, long expected, String name) {
        if ((long)n > expected) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: <= " + expected + ')');
        }
        return n;
    }

    public static int checkGreaterThanOrEqual(int n, int expected, String name) {
        if (n < expected) {
            throw new IllegalArgumentException(name + ": " + n + " (expected: >= " + expected + ')');
        }
        return n;
    }
}


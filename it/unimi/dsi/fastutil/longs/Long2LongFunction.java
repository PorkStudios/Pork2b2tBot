/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongUnaryOperator;

@FunctionalInterface
public interface Long2LongFunction
extends Function<Long, Long>,
LongUnaryOperator {
    @Override
    default public long applyAsLong(long operand) {
        return this.get(operand);
    }

    @Override
    default public long put(long key, long value) {
        throw new UnsupportedOperationException();
    }

    public long get(long var1);

    default public long remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Long put(Long key, Long value) {
        long k = key;
        boolean containsKey = this.containsKey(k);
        long v = this.put(k, (long)value);
        return containsKey ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long get(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Long.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }
}


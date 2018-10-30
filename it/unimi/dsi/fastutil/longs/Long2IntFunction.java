/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface Long2IntFunction
extends Function<Long, Integer>,
LongToIntFunction {
    @Override
    default public int applyAsInt(long operand) {
        return this.get(operand);
    }

    @Override
    default public int put(long key, int value) {
        throw new UnsupportedOperationException();
    }

    public int get(long var1);

    default public int remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Integer put(Long key, Integer value) {
        long k = key;
        boolean containsKey = this.containsKey(k);
        int v = this.put(k, (int)value);
        return containsKey ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer get(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        int v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Integer.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }
}


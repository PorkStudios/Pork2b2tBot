/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface Int2LongFunction
extends Function<Integer, Long>,
IntToLongFunction {
    @Override
    default public long applyAsLong(int operand) {
        return this.get(operand);
    }

    @Override
    default public long put(int key, long value) {
        throw new UnsupportedOperationException();
    }

    public long get(int var1);

    default public long remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Long put(Integer key, Long value) {
        int k = key;
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
        int k = (Integer)key;
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Long.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }
}


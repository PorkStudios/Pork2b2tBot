/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface Long2ShortFunction
extends Function<Long, Short>,
LongToIntFunction {
    @Override
    default public int applyAsInt(long operand) {
        return this.get(operand);
    }

    @Override
    default public short put(long key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(long var1);

    default public short remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Short put(Long key, Short value) {
        long k = key;
        boolean containsKey = this.containsKey(k);
        short v = this.put(k, (short)value);
        return containsKey ? Short.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Short get(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface Short2LongFunction
extends Function<Short, Long>,
IntToLongFunction {
    @Deprecated
    @Override
    default public long applyAsLong(int operand) {
        return this.get(SafeMath.safeIntToShort(operand));
    }

    @Override
    default public long put(short key, long value) {
        throw new UnsupportedOperationException();
    }

    public long get(short var1);

    default public long remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Long put(Short key, Long value) {
        short k = key;
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
        short k = (Short)key;
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        return this.containsKey(k) ? Long.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(short key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Short)key);
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }
}


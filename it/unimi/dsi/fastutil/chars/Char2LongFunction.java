/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntToLongFunction;

@FunctionalInterface
public interface Char2LongFunction
extends Function<Character, Long>,
IntToLongFunction {
    @Deprecated
    @Override
    default public long applyAsLong(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public long put(char key, long value) {
        throw new UnsupportedOperationException();
    }

    public long get(char var1);

    default public long remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Long put(Character key, Long value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Long.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }
}


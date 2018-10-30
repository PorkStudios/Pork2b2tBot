/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import java.util.function.IntPredicate;

@FunctionalInterface
public interface Int2BooleanFunction
extends Function<Integer, Boolean>,
IntPredicate {
    @Override
    default public boolean test(int operand) {
        return this.get(operand);
    }

    @Override
    default public boolean put(int key, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean get(int var1);

    default public boolean remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Boolean put(Integer key, Boolean value) {
        int k = key;
        boolean containsKey = this.containsKey(k);
        boolean v = this.put(k, (boolean)value);
        return containsKey ? Boolean.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Boolean get(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        boolean v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Boolean remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Boolean.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(boolean rv) {
        throw new UnsupportedOperationException();
    }

    default public boolean defaultReturnValue() {
        return false;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntPredicate;

@FunctionalInterface
public interface Char2BooleanFunction
extends Function<Character, Boolean>,
IntPredicate {
    @Deprecated
    @Override
    default public boolean test(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public boolean put(char key, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean get(char var1);

    default public boolean remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Boolean put(Character key, Boolean value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        boolean v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Boolean remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Boolean.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(boolean rv) {
        throw new UnsupportedOperationException();
    }

    default public boolean defaultReturnValue() {
        return false;
    }
}


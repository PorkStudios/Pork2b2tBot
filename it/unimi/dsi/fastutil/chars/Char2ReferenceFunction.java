/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntFunction;

@FunctionalInterface
public interface Char2ReferenceFunction<V>
extends Function<Character, V>,
IntFunction<V> {
    @Deprecated
    @Override
    default public V apply(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public V put(char key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(char var1);

    default public V remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public V put(Character key, V value) {
        char k = key.charValue();
        boolean containsKey = this.containsKey(k);
        V v = this.put(k, value);
        return (V)(containsKey ? v : null);
    }

    @Deprecated
    @Override
    default public V get(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }
}


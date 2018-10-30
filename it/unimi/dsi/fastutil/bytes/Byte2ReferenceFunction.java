/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntFunction;

@FunctionalInterface
public interface Byte2ReferenceFunction<V>
extends Function<Byte, V>,
IntFunction<V> {
    @Deprecated
    @Override
    default public V apply(int operand) {
        return this.get(SafeMath.safeIntToByte(operand));
    }

    @Override
    default public V put(byte key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(byte var1);

    default public V remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public V put(Byte key, V value) {
        byte k = key;
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
        byte k = (Byte)key;
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(byte key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Byte)key);
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }
}


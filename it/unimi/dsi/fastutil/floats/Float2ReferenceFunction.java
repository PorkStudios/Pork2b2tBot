/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface Float2ReferenceFunction<V>
extends Function<Float, V>,
DoubleFunction<V> {
    @Deprecated
    @Override
    default public V apply(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public V put(float key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(float var1);

    default public V remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public V put(Float key, V value) {
        float k = key.floatValue();
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
        float k = ((Float)key).floatValue();
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }
}


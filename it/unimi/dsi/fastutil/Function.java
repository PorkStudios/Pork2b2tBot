/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil;

@FunctionalInterface
public interface Function<K, V>
extends java.util.function.Function<K, V> {
    @Override
    default public V apply(K key) {
        return this.get(key);
    }

    default public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(Object var1);

    default public boolean containsKey(Object key) {
        return true;
    }

    default public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    default public int size() {
        return -1;
    }

    default public void clear() {
        throw new UnsupportedOperationException();
    }
}


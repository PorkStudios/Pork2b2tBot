/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import java.util.Map;

public interface IntObjectMap<V>
extends Map<Integer, V> {
    public V get(int var1);

    @Override
    public V put(int var1, V var2);

    public V remove(int var1);

    public Iterable<PrimitiveEntry<V>> entries();

    public boolean containsKey(int var1);

    public static interface PrimitiveEntry<V> {
        public int key();

        public V value();

        public void setValue(V var1);
    }

}


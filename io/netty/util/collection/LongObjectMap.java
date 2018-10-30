/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import java.util.Map;

public interface LongObjectMap<V>
extends Map<Long, V> {
    public V get(long var1);

    @Override
    public V put(long var1, V var3);

    public V remove(long var1);

    public Iterable<PrimitiveEntry<V>> entries();

    public boolean containsKey(long var1);

    public static interface PrimitiveEntry<V> {
        public long key();

        public V value();

        public void setValue(V var1);
    }

}


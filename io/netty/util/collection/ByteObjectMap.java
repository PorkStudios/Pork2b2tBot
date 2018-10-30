/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import java.util.Map;

public interface ByteObjectMap<V>
extends Map<Byte, V> {
    public V get(byte var1);

    @Override
    public V put(byte var1, V var2);

    public V remove(byte var1);

    public Iterable<PrimitiveEntry<V>> entries();

    public boolean containsKey(byte var1);

    public static interface PrimitiveEntry<V> {
        public byte key();

        public V value();

        public void setValue(V var1);
    }

}


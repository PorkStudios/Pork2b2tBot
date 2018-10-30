/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2ByteFunction<K>
extends Function<K, Byte>,
ToIntFunction<K> {
    @Override
    default public int applyAsInt(K operand) {
        return this.getByte(operand);
    }

    @Override
    default public byte put(K key, byte value) {
        throw new UnsupportedOperationException();
    }

    public byte getByte(Object var1);

    default public byte removeByte(Object key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Byte put(K key, Byte value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        byte v = this.put(k, (byte)value);
        return containsKey ? Byte.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Byte get(Object key) {
        Object k = key;
        byte v = this.getByte(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Byte remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Byte.valueOf(this.removeByte(k)) : null;
    }

    default public void defaultReturnValue(byte rv) {
        throw new UnsupportedOperationException();
    }

    default public byte defaultReturnValue() {
        return 0;
    }
}


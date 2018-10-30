/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToLongFunction;

@FunctionalInterface
public interface Reference2LongFunction<K>
extends Function<K, Long>,
ToLongFunction<K> {
    @Override
    default public long applyAsLong(K operand) {
        return this.getLong(operand);
    }

    @Override
    default public long put(K key, long value) {
        throw new UnsupportedOperationException();
    }

    public long getLong(Object var1);

    default public long removeLong(Object key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Long put(K key, Long value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        long v = this.put(k, (long)value);
        return containsKey ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long get(Object key) {
        Object k = key;
        long v = this.getLong(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Long.valueOf(this.removeLong(k)) : null;
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }
}


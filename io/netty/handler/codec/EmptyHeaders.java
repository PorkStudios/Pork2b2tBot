/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.Headers;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmptyHeaders<K, V, T extends Headers<K, V, T>>
implements Headers<K, V, T> {
    @Override
    public V get(K name) {
        return null;
    }

    @Override
    public V get(K name, V defaultValue) {
        return null;
    }

    @Override
    public V getAndRemove(K name) {
        return null;
    }

    @Override
    public V getAndRemove(K name, V defaultValue) {
        return null;
    }

    @Override
    public List<V> getAll(K name) {
        return Collections.emptyList();
    }

    @Override
    public List<V> getAllAndRemove(K name) {
        return Collections.emptyList();
    }

    @Override
    public Boolean getBoolean(K name) {
        return null;
    }

    @Override
    public boolean getBoolean(K name, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public Byte getByte(K name) {
        return null;
    }

    @Override
    public byte getByte(K name, byte defaultValue) {
        return defaultValue;
    }

    @Override
    public Character getChar(K name) {
        return null;
    }

    @Override
    public char getChar(K name, char defaultValue) {
        return defaultValue;
    }

    @Override
    public Short getShort(K name) {
        return null;
    }

    @Override
    public short getShort(K name, short defaultValue) {
        return defaultValue;
    }

    @Override
    public Integer getInt(K name) {
        return null;
    }

    @Override
    public int getInt(K name, int defaultValue) {
        return defaultValue;
    }

    @Override
    public Long getLong(K name) {
        return null;
    }

    @Override
    public long getLong(K name, long defaultValue) {
        return defaultValue;
    }

    @Override
    public Float getFloat(K name) {
        return null;
    }

    @Override
    public float getFloat(K name, float defaultValue) {
        return defaultValue;
    }

    @Override
    public Double getDouble(K name) {
        return null;
    }

    @Override
    public double getDouble(K name, double defaultValue) {
        return defaultValue;
    }

    @Override
    public Long getTimeMillis(K name) {
        return null;
    }

    @Override
    public long getTimeMillis(K name, long defaultValue) {
        return defaultValue;
    }

    @Override
    public Boolean getBooleanAndRemove(K name) {
        return null;
    }

    @Override
    public boolean getBooleanAndRemove(K name, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public Byte getByteAndRemove(K name) {
        return null;
    }

    @Override
    public byte getByteAndRemove(K name, byte defaultValue) {
        return defaultValue;
    }

    @Override
    public Character getCharAndRemove(K name) {
        return null;
    }

    @Override
    public char getCharAndRemove(K name, char defaultValue) {
        return defaultValue;
    }

    @Override
    public Short getShortAndRemove(K name) {
        return null;
    }

    @Override
    public short getShortAndRemove(K name, short defaultValue) {
        return defaultValue;
    }

    @Override
    public Integer getIntAndRemove(K name) {
        return null;
    }

    @Override
    public int getIntAndRemove(K name, int defaultValue) {
        return defaultValue;
    }

    @Override
    public Long getLongAndRemove(K name) {
        return null;
    }

    @Override
    public long getLongAndRemove(K name, long defaultValue) {
        return defaultValue;
    }

    @Override
    public Float getFloatAndRemove(K name) {
        return null;
    }

    @Override
    public float getFloatAndRemove(K name, float defaultValue) {
        return defaultValue;
    }

    @Override
    public Double getDoubleAndRemove(K name) {
        return null;
    }

    @Override
    public double getDoubleAndRemove(K name, double defaultValue) {
        return defaultValue;
    }

    @Override
    public Long getTimeMillisAndRemove(K name) {
        return null;
    }

    @Override
    public long getTimeMillisAndRemove(K name, long defaultValue) {
        return defaultValue;
    }

    @Override
    public boolean contains(K name) {
        return false;
    }

    @Override
    public boolean contains(K name, V value) {
        return false;
    }

    @Override
    public boolean containsObject(K name, Object value) {
        return false;
    }

    @Override
    public boolean containsBoolean(K name, boolean value) {
        return false;
    }

    @Override
    public boolean containsByte(K name, byte value) {
        return false;
    }

    @Override
    public boolean containsChar(K name, char value) {
        return false;
    }

    @Override
    public boolean containsShort(K name, short value) {
        return false;
    }

    @Override
    public boolean containsInt(K name, int value) {
        return false;
    }

    @Override
    public boolean containsLong(K name, long value) {
        return false;
    }

    @Override
    public boolean containsFloat(K name, float value) {
        return false;
    }

    @Override
    public boolean containsDouble(K name, double value) {
        return false;
    }

    @Override
    public boolean containsTimeMillis(K name, long value) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Set<K> names() {
        return Collections.emptySet();
    }

    @Override
    public T add(K name, V value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T add(K name, Iterable<? extends V> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public /* varargs */ T add(K name, V ... values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addObject(K name, Object value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addObject(K name, Iterable<?> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public /* varargs */ T addObject(K name, Object ... values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addBoolean(K name, boolean value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addByte(K name, byte value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addChar(K name, char value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addShort(K name, short value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addInt(K name, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addLong(K name, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addFloat(K name, float value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addDouble(K name, double value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T addTimeMillis(K name, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T add(Headers<? extends K, ? extends V, ?> headers) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T set(K name, V value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T set(K name, Iterable<? extends V> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public /* varargs */ T set(K name, V ... values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setObject(K name, Object value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setObject(K name, Iterable<?> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public /* varargs */ T setObject(K name, Object ... values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setBoolean(K name, boolean value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setByte(K name, byte value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setChar(K name, char value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setShort(K name, short value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setInt(K name, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setLong(K name, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setFloat(K name, float value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setDouble(K name, double value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setTimeMillis(K name, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T set(Headers<? extends K, ? extends V, ?> headers) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public T setAll(Headers<? extends K, ? extends V, ?> headers) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean remove(K name) {
        return false;
    }

    @Override
    public T clear() {
        return this.thisT();
    }

    public Iterator<V> valueIterator(K name) {
        List empty = Collections.emptyList();
        return empty.iterator();
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        List empty = Collections.emptyList();
        return empty.iterator();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Headers)) {
            return false;
        }
        Headers rhs = (Headers)o;
        return this.isEmpty() && rhs.isEmpty();
    }

    public int hashCode() {
        return -1028477387;
    }

    public String toString() {
        return this.getClass().getSimpleName() + '[' + ']';
    }

    private T thisT() {
        return (T)this;
    }
}


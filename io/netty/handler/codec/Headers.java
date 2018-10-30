/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Headers<K, V, T extends Headers<K, V, T>>
extends Iterable<Map.Entry<K, V>> {
    public V get(K var1);

    public V get(K var1, V var2);

    public V getAndRemove(K var1);

    public V getAndRemove(K var1, V var2);

    public List<V> getAll(K var1);

    public List<V> getAllAndRemove(K var1);

    public Boolean getBoolean(K var1);

    public boolean getBoolean(K var1, boolean var2);

    public Byte getByte(K var1);

    public byte getByte(K var1, byte var2);

    public Character getChar(K var1);

    public char getChar(K var1, char var2);

    public Short getShort(K var1);

    public short getShort(K var1, short var2);

    public Integer getInt(K var1);

    public int getInt(K var1, int var2);

    public Long getLong(K var1);

    public long getLong(K var1, long var2);

    public Float getFloat(K var1);

    public float getFloat(K var1, float var2);

    public Double getDouble(K var1);

    public double getDouble(K var1, double var2);

    public Long getTimeMillis(K var1);

    public long getTimeMillis(K var1, long var2);

    public Boolean getBooleanAndRemove(K var1);

    public boolean getBooleanAndRemove(K var1, boolean var2);

    public Byte getByteAndRemove(K var1);

    public byte getByteAndRemove(K var1, byte var2);

    public Character getCharAndRemove(K var1);

    public char getCharAndRemove(K var1, char var2);

    public Short getShortAndRemove(K var1);

    public short getShortAndRemove(K var1, short var2);

    public Integer getIntAndRemove(K var1);

    public int getIntAndRemove(K var1, int var2);

    public Long getLongAndRemove(K var1);

    public long getLongAndRemove(K var1, long var2);

    public Float getFloatAndRemove(K var1);

    public float getFloatAndRemove(K var1, float var2);

    public Double getDoubleAndRemove(K var1);

    public double getDoubleAndRemove(K var1, double var2);

    public Long getTimeMillisAndRemove(K var1);

    public long getTimeMillisAndRemove(K var1, long var2);

    public boolean contains(K var1);

    public boolean contains(K var1, V var2);

    public boolean containsObject(K var1, Object var2);

    public boolean containsBoolean(K var1, boolean var2);

    public boolean containsByte(K var1, byte var2);

    public boolean containsChar(K var1, char var2);

    public boolean containsShort(K var1, short var2);

    public boolean containsInt(K var1, int var2);

    public boolean containsLong(K var1, long var2);

    public boolean containsFloat(K var1, float var2);

    public boolean containsDouble(K var1, double var2);

    public boolean containsTimeMillis(K var1, long var2);

    public int size();

    public boolean isEmpty();

    public Set<K> names();

    public T add(K var1, V var2);

    public T add(K var1, Iterable<? extends V> var2);

    public /* varargs */ T add(K var1, V ... var2);

    public T addObject(K var1, Object var2);

    public T addObject(K var1, Iterable<?> var2);

    public /* varargs */ T addObject(K var1, Object ... var2);

    public T addBoolean(K var1, boolean var2);

    public T addByte(K var1, byte var2);

    public T addChar(K var1, char var2);

    public T addShort(K var1, short var2);

    public T addInt(K var1, int var2);

    public T addLong(K var1, long var2);

    public T addFloat(K var1, float var2);

    public T addDouble(K var1, double var2);

    public T addTimeMillis(K var1, long var2);

    public T add(Headers<? extends K, ? extends V, ?> var1);

    public T set(K var1, V var2);

    public T set(K var1, Iterable<? extends V> var2);

    public /* varargs */ T set(K var1, V ... var2);

    public T setObject(K var1, Object var2);

    public T setObject(K var1, Iterable<?> var2);

    public /* varargs */ T setObject(K var1, Object ... var2);

    public T setBoolean(K var1, boolean var2);

    public T setByte(K var1, byte var2);

    public T setChar(K var1, char var2);

    public T setShort(K var1, short var2);

    public T setInt(K var1, int var2);

    public T setLong(K var1, long var2);

    public T setFloat(K var1, float var2);

    public T setDouble(K var1, double var2);

    public T setTimeMillis(K var1, long var2);

    public T set(Headers<? extends K, ? extends V, ?> var1);

    public T setAll(Headers<? extends K, ? extends V, ?> var1);

    public boolean remove(K var1);

    public T clear();

    @Override
    public Iterator<Map.Entry<K, V>> iterator();
}


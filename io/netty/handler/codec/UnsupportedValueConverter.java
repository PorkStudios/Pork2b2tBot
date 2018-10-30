/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.ValueConverter;

public final class UnsupportedValueConverter<V>
implements ValueConverter<V> {
    private static final UnsupportedValueConverter INSTANCE = new UnsupportedValueConverter<V>();

    private UnsupportedValueConverter() {
    }

    public static <V> UnsupportedValueConverter<V> instance() {
        return INSTANCE;
    }

    @Override
    public V convertObject(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertBoolean(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean convertToBoolean(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertByte(byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte convertToByte(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertChar(char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char convertToChar(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertShort(short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short convertToShort(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertInt(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int convertToInt(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertLong(long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long convertToLong(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertTimeMillis(long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long convertToTimeMillis(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertFloat(float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float convertToFloat(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V convertDouble(double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double convertToDouble(V value) {
        throw new UnsupportedOperationException();
    }
}


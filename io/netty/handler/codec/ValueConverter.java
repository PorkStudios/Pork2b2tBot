/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

public interface ValueConverter<T> {
    public T convertObject(Object var1);

    public T convertBoolean(boolean var1);

    public boolean convertToBoolean(T var1);

    public T convertByte(byte var1);

    public byte convertToByte(T var1);

    public T convertChar(char var1);

    public char convertToChar(T var1);

    public T convertShort(short var1);

    public short convertToShort(T var1);

    public T convertInt(int var1);

    public int convertToInt(T var1);

    public T convertLong(long var1);

    public long convertToLong(T var1);

    public T convertTimeMillis(long var1);

    public long convertToTimeMillis(T var1);

    public T convertFloat(float var1);

    public float convertToFloat(T var1);

    public T convertDouble(double var1);

    public double convertToDouble(T var1);
}


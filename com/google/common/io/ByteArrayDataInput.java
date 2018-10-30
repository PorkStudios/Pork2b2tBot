/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.DataInput;

@GwtIncompatible
public interface ByteArrayDataInput
extends DataInput {
    @Override
    public void readFully(byte[] var1);

    @Override
    public void readFully(byte[] var1, int var2, int var3);

    @Override
    public int skipBytes(int var1);

    @CanIgnoreReturnValue
    @Override
    public boolean readBoolean();

    @CanIgnoreReturnValue
    @Override
    public byte readByte();

    @CanIgnoreReturnValue
    @Override
    public int readUnsignedByte();

    @CanIgnoreReturnValue
    @Override
    public short readShort();

    @CanIgnoreReturnValue
    @Override
    public int readUnsignedShort();

    @CanIgnoreReturnValue
    @Override
    public char readChar();

    @CanIgnoreReturnValue
    @Override
    public int readInt();

    @CanIgnoreReturnValue
    @Override
    public long readLong();

    @CanIgnoreReturnValue
    @Override
    public float readFloat();

    @CanIgnoreReturnValue
    @Override
    public double readDouble();

    @CanIgnoreReturnValue
    @Override
    public String readLine();

    @CanIgnoreReturnValue
    @Override
    public String readUTF();
}


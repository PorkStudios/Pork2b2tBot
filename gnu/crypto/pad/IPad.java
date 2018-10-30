/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.pad;

import gnu.crypto.pad.WrongPaddingException;

public interface IPad {
    public String name();

    public void init(int var1) throws IllegalStateException;

    public byte[] pad(byte[] var1, int var2, int var3);

    public int unpad(byte[] var1, int var2, int var3) throws WrongPaddingException;

    public void reset();

    public boolean selfTest();
}


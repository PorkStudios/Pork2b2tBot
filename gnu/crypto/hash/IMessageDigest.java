/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

public interface IMessageDigest
extends Cloneable {
    public String name();

    public int hashSize();

    public int blockSize();

    public void update(byte var1);

    public void update(byte[] var1, int var2, int var3);

    public byte[] digest();

    public void reset();

    public boolean selfTest();

    public Object clone();
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import java.security.InvalidKeyException;
import java.util.Iterator;

interface IBlockCipherSpi
extends Cloneable {
    public Iterator blockSizes();

    public Iterator keySizes();

    public Object makeKey(byte[] var1, int var2) throws InvalidKeyException;

    public void encrypt(byte[] var1, int var2, byte[] var3, int var4, Object var5, int var6);

    public void decrypt(byte[] var1, int var2, byte[] var3, int var4, Object var5, int var6);

    public boolean selfTest();
}


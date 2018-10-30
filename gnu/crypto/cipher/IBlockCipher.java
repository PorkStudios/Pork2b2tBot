/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.cipher;

import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.Map;

public interface IBlockCipher
extends Cloneable {
    public static final String CIPHER_BLOCK_SIZE = "gnu.crypto.cipher.block.size";
    public static final String KEY_MATERIAL = "gnu.crypto.cipher.key.material";

    public String name();

    public int defaultBlockSize();

    public int defaultKeySize();

    public Iterator blockSizes();

    public Iterator keySizes();

    public Object clone();

    public void init(Map var1) throws InvalidKeyException, IllegalStateException;

    public int currentBlockSize() throws IllegalStateException;

    public void reset();

    public void encryptBlock(byte[] var1, int var2, byte[] var3, int var4) throws IllegalStateException;

    public void decryptBlock(byte[] var1, int var2, byte[] var3, int var4) throws IllegalStateException;

    public boolean selfTest();
}


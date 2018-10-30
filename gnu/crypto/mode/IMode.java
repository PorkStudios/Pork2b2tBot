/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.cipher.IBlockCipher;

public interface IMode
extends IBlockCipher {
    public static final String STATE = "gnu.crypto.mode.state";
    public static final String MODE_BLOCK_SIZE = "gnu.crypto.mode.block.size";
    public static final String IV = "gnu.crypto.mode.iv";
    public static final int ENCRYPTION = 1;
    public static final int DECRYPTION = 2;

    public void update(byte[] var1, int var2, byte[] var3, int var4) throws IllegalStateException;
}


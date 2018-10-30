/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.spec;

import gnu.crypto.util.Util;
import java.security.spec.AlgorithmParameterSpec;

public class BlockCipherParameterSpec
implements AlgorithmParameterSpec {
    protected byte[] iv;
    protected int blockSize;
    protected int keySize;

    public byte[] getIV() {
        return this.iv;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public int getKeySize() {
        return this.keySize;
    }

    public String toString() {
        return this.getClass().getName() + " { " + (this.iv != null ? new StringBuffer("IV=").append(Util.toString(this.iv)).append(", ").toString() : "") + "BS=" + this.blockSize + ", KS=" + this.keySize + " }";
    }

    public BlockCipherParameterSpec(byte[] iv, int blockSize, int keySize) {
        this.iv = iv != null ? (byte[])iv.clone() : null;
        this.blockSize = blockSize;
        this.keySize = keySize;
    }

    public BlockCipherParameterSpec(int blockSize, int keySize) {
        this(null, blockSize, keySize);
    }
}


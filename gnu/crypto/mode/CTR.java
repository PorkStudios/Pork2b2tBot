/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.BaseMode;
import java.math.BigInteger;

public class CTR
extends BaseMode
implements Cloneable {
    private BigInteger T;

    public Object clone() {
        return new CTR(this);
    }

    public void setup() {
        if (this.modeBlockSize != this.cipherBlockSize) {
            throw new IllegalArgumentException();
        }
        byte[] tBytes = new byte[this.modeBlockSize + 1];
        tBytes[0] = -128;
        int i = 0;
        while (i < this.modeBlockSize) {
            tBytes[i + 1] = (byte)(256 - this.modeBlockSize + i);
            ++i;
        }
        this.T = new BigInteger(1, tBytes);
    }

    public void teardown() {
        this.T = null;
    }

    public void encryptBlock(byte[] in, int i, byte[] out, int o) {
        this.ctr(in, i, out, o);
    }

    public void decryptBlock(byte[] in, int i, byte[] out, int o) {
        this.ctr(in, i, out, o);
    }

    private final void ctr(byte[] in, int inOffset, byte[] out, int outOffset) {
        this.T = this.T.add(BigInteger.ONE);
        byte[] O = this.T.toByteArray();
        int ndx = O.length - this.modeBlockSize;
        this.cipher.encryptBlock(O, ndx, O, ndx);
        int i = 0;
        while (i < this.modeBlockSize) {
            out[outOffset++] = (byte)(in[inOffset++] ^ O[ndx++]);
            ++i;
        }
    }

    CTR(IBlockCipher underlyingCipher, int cipherBlockSize) {
        super("ctr", underlyingCipher, cipherBlockSize);
    }

    private CTR(CTR that) {
        this((IBlockCipher)that.cipher.clone(), that.cipherBlockSize);
    }
}


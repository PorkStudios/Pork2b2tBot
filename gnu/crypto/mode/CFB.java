/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.BaseMode;

public class CFB
extends BaseMode {
    private byte[] shiftRegister;
    private byte[] scratch;

    public Object clone() {
        return new CFB(this);
    }

    public void setup() {
        if (this.modeBlockSize > this.cipherBlockSize) {
            throw new IllegalArgumentException("CFB block size cannot be larger than the cipher block size");
        }
        this.shiftRegister = new byte[this.cipherBlockSize];
        this.scratch = new byte[this.cipherBlockSize];
        System.arraycopy(this.iv, 0, this.shiftRegister, 0, Math.min(this.iv.length, this.cipherBlockSize));
    }

    public void teardown() {
        if (this.shiftRegister != null) {
            int i = 0;
            while (i < this.shiftRegister.length) {
                this.shiftRegister[i] = 0;
                ++i;
            }
        }
        this.shiftRegister = null;
    }

    public void encryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) {
        this.cipher.encryptBlock(this.shiftRegister, 0, this.scratch, 0);
        int i = 0;
        while (i < this.modeBlockSize) {
            out[outOffset + i] = (byte)(in[inOffset + i] ^ this.scratch[i]);
            ++i;
        }
        System.arraycopy(this.shiftRegister, this.modeBlockSize, this.shiftRegister, 0, this.cipherBlockSize - this.modeBlockSize);
        System.arraycopy(out, outOffset, this.shiftRegister, this.cipherBlockSize - this.modeBlockSize, this.modeBlockSize);
    }

    public void decryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) {
        this.cipher.encryptBlock(this.shiftRegister, 0, this.scratch, 0);
        int i = 0;
        while (i < this.modeBlockSize) {
            out[outOffset + i] = (byte)(in[inOffset + i] ^ this.scratch[i]);
            ++i;
        }
        System.arraycopy(this.shiftRegister, this.modeBlockSize, this.shiftRegister, 0, this.cipherBlockSize - this.modeBlockSize);
        System.arraycopy(in, inOffset, this.shiftRegister, this.cipherBlockSize - this.modeBlockSize, this.modeBlockSize);
    }

    CFB(IBlockCipher underlyingCipher, int cipherBlockSize) {
        super("cfb", underlyingCipher, cipherBlockSize);
    }

    private CFB(CFB that) {
        this((IBlockCipher)that.cipher.clone(), that.cipherBlockSize);
    }
}


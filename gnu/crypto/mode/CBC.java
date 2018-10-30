/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.BaseMode;

public class CBC
extends BaseMode
implements Cloneable {
    private byte[] lastBlock;
    private byte[] scratch;

    public Object clone() {
        return new CBC(this);
    }

    public void setup() {
        if (this.modeBlockSize != this.cipherBlockSize) {
            throw new IllegalArgumentException();
        }
        this.scratch = new byte[this.cipherBlockSize];
        this.lastBlock = new byte[this.cipherBlockSize];
        int i = 0;
        while (i < this.lastBlock.length && i < this.iv.length) {
            this.lastBlock[i] = this.iv[i];
            ++i;
        }
    }

    public void teardown() {
        this.lastBlock = null;
        this.scratch = null;
    }

    public void encryptBlock(byte[] in, int i, byte[] out, int o) {
        int k = 0;
        while (k < this.scratch.length) {
            this.scratch[k] = (byte)(this.lastBlock[k] ^ in[k + i]);
            ++k;
        }
        this.cipher.encryptBlock(this.scratch, 0, out, o);
        System.arraycopy(out, o, this.lastBlock, 0, this.cipherBlockSize);
    }

    public void decryptBlock(byte[] in, int i, byte[] out, int o) {
        byte[] buf = new byte[this.cipherBlockSize];
        System.arraycopy(in, i, buf, 0, this.cipherBlockSize);
        this.cipher.decryptBlock(in, i, this.scratch, 0);
        int k = 0;
        while (k < this.scratch.length) {
            out[o + k] = (byte)(this.lastBlock[k] ^ this.scratch[k]);
            ++k;
        }
        System.arraycopy(buf, 0, this.lastBlock, 0, this.cipherBlockSize);
    }

    CBC(IBlockCipher underlyingCipher, int cipherBlockSize) {
        super("cbc", underlyingCipher, cipherBlockSize);
    }

    private CBC(CBC that) {
        this((IBlockCipher)that.cipher.clone(), that.cipherBlockSize);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.BaseMode;

public class OFB
extends BaseMode
implements Cloneable {
    private byte[] outputBlock;

    public Object clone() {
        return new OFB(this);
    }

    public void setup() {
        if (this.modeBlockSize != this.cipherBlockSize) {
            throw new IllegalArgumentException("gnu.crypto.mode.block.size");
        }
        this.outputBlock = (byte[])this.iv.clone();
    }

    public void teardown() {
    }

    public void encryptBlock(byte[] in, int i, byte[] out, int o) {
        this.cipher.encryptBlock(this.outputBlock, 0, this.outputBlock, 0);
        int j = 0;
        while (j < this.cipherBlockSize) {
            out[o++] = (byte)(in[i++] ^ this.outputBlock[j++]);
        }
    }

    public void decryptBlock(byte[] in, int i, byte[] out, int o) {
        this.encryptBlock(in, i, out, o);
    }

    OFB(IBlockCipher underlyingCipher, int cipherBlockSize) {
        super("ofb", underlyingCipher, cipherBlockSize);
    }

    private OFB(OFB that) {
        this((IBlockCipher)that.cipher.clone(), that.cipherBlockSize);
    }
}


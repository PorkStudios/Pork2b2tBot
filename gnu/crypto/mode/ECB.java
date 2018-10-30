/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.BaseMode;

public class ECB
extends BaseMode
implements Cloneable {
    public Object clone() {
        return new ECB(this);
    }

    public void setup() {
        if (this.modeBlockSize != this.cipherBlockSize) {
            throw new IllegalArgumentException("gnu.crypto.mode.block.size");
        }
    }

    public void teardown() {
    }

    public void encryptBlock(byte[] in, int i, byte[] out, int o) {
        this.cipher.encryptBlock(in, i, out, o);
    }

    public void decryptBlock(byte[] in, int i, byte[] out, int o) {
        this.cipher.decryptBlock(in, i, out, o);
    }

    ECB(IBlockCipher underlyingCipher, int cipherBlockSize) {
        super("ecb", underlyingCipher, cipherBlockSize);
    }

    private ECB(ECB that) {
        this((IBlockCipher)that.cipher.clone(), that.cipherBlockSize);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mode;

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.BaseMode;
import java.math.BigInteger;

public class ICM
extends BaseMode
implements Cloneable {
    private static final BigInteger TWO_FIFTY_SIX = new BigInteger("256");
    private BigInteger maxBlocksPerSegment;
    private BigInteger counterRange;
    private BigInteger C0;
    private BigInteger blockNdx;

    public Object clone() {
        return new ICM(this);
    }

    public void setup() {
        if (this.modeBlockSize != this.cipherBlockSize) {
            throw new IllegalArgumentException();
        }
        this.counterRange = TWO_FIFTY_SIX.pow(this.cipherBlockSize);
        this.maxBlocksPerSegment = TWO_FIFTY_SIX.pow(this.cipherBlockSize / 2);
        BigInteger r = new BigInteger(1, this.iv);
        this.C0 = this.maxBlocksPerSegment.add(r).modPow(BigInteger.ONE, this.counterRange);
        this.blockNdx = BigInteger.ZERO;
    }

    public void teardown() {
        this.counterRange = null;
        this.maxBlocksPerSegment = null;
        this.C0 = null;
        this.blockNdx = null;
    }

    public void encryptBlock(byte[] in, int i, byte[] out, int o) {
        this.icm(in, i, out, o);
    }

    public void decryptBlock(byte[] in, int i, byte[] out, int o) {
        this.icm(in, i, out, o);
    }

    private final void icm(byte[] in, int inOffset, byte[] out, int outOffset) {
        if (this.blockNdx.compareTo(this.maxBlocksPerSegment) >= 0) {
            throw new RuntimeException("Maximum blocks for segment reached");
        }
        BigInteger Ci = this.C0.add(this.blockNdx).modPow(BigInteger.ONE, this.counterRange);
        byte[] result = Ci.toByteArray();
        int limit = result.length;
        int ndx = 0;
        if (limit < this.cipherBlockSize) {
            byte[] data = new byte[this.cipherBlockSize];
            System.arraycopy(result, 0, data, this.cipherBlockSize - limit, limit);
            result = data;
        } else if (limit > this.cipherBlockSize) {
            ndx = limit - this.cipherBlockSize;
        }
        this.cipher.encryptBlock(result, ndx, result, ndx);
        this.blockNdx = this.blockNdx.add(BigInteger.ONE);
        int i = 0;
        while (i < this.modeBlockSize) {
            out[outOffset++] = (byte)(in[inOffset++] ^ result[ndx++]);
            ++i;
        }
    }

    ICM(IBlockCipher underlyingCipher, int cipherBlockSize) {
        super("icm", underlyingCipher, cipherBlockSize);
    }

    private ICM(ICM that) {
        this((IBlockCipher)that.cipher.clone(), that.cipherBlockSize);
    }
}


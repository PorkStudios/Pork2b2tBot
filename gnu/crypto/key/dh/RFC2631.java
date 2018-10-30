/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import gnu.crypto.hash.Sha160;
import gnu.crypto.util.PRNG;
import gnu.crypto.util.Prime;
import java.math.BigInteger;
import java.security.SecureRandom;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class RFC2631 {
    public static final int DH_PARAMS_SEED = 0;
    public static final int DH_PARAMS_COUNTER = 1;
    public static final int DH_PARAMS_Q = 2;
    public static final int DH_PARAMS_P = 3;
    public static final int DH_PARAMS_J = 4;
    public static final int DH_PARAMS_G = 5;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private Sha160 sha;
    private int m;
    private int L;
    private SecureRandom rnd;

    public BigInteger[] generateParameters() {
        BigInteger SEED;
        BigInteger p;
        BigInteger q;
        int counter;
        byte[] seedBytes = new byte[this.m / 8];
        int m_ = (this.m + 159) / 160;
        int L_ = (this.L + 159) / 160;
        int N_ = (this.L + 1023) / 1024;
        block0 : do {
            this.nextRandomBytes(seedBytes);
            SEED = new BigInteger(1, seedBytes).setBit(this.m - 1).setBit(0);
            BigInteger U = BigInteger.ZERO;
            int i = 0;
            while (i < m_) {
                byte[] u1 = SEED.add(BigInteger.valueOf(i)).toByteArray();
                byte[] u2 = SEED.add(BigInteger.valueOf(m_ + i)).toByteArray();
                this.sha.update(u1, 0, u1.length);
                u1 = this.sha.digest();
                this.sha.update(u2, 0, u2.length);
                u2 = this.sha.digest();
                int j = 0;
                while (j < u1.length) {
                    byte[] arrby = u1;
                    int n = j;
                    arrby[n] = (byte)(arrby[n] ^ u2[j]);
                    ++j;
                }
                U = U.add(new BigInteger(1, u1).multiply(TWO.pow(160 * i)));
                ++i;
            }
            q = U.setBit(this.m - 1).setBit(0);
            if (!Prime.isProbablePrime(q)) continue;
            counter = 0;
            do {
                BigInteger R = SEED.add(BigInteger.valueOf(2 * m_)).add(BigInteger.valueOf(L_ * counter));
                BigInteger V = BigInteger.ZERO;
                i = 0;
                while (i < L_) {
                    byte[] v = R.toByteArray();
                    this.sha.update(v, 0, v.length);
                    v = this.sha.digest();
                    V = V.add(new BigInteger(1, v).multiply(TWO.pow(160 * i)));
                    ++i;
                }
                BigInteger W = V.mod(TWO.pow(this.L));
                BigInteger X = W.setBit(this.L - 1);
                p = X.add(BigInteger.ONE).subtract(X.mod(TWO.multiply(q)));
                if (Prime.isProbablePrime(p)) break block0;
            } while (++counter < 4096 * N_);
        } while (true);
        BigInteger e = p.subtract(BigInteger.ONE).divide(q);
        BigInteger h = TWO;
        BigInteger p_minus_1 = p.subtract(BigInteger.ONE);
        BigInteger g = TWO;
        while (h.compareTo(p_minus_1) < 0) {
            g = h.modPow(e, p);
            if (!g.equals(BigInteger.ONE)) break;
            h = h.add(BigInteger.ONE);
        }
        return new BigInteger[]{SEED, BigInteger.valueOf(counter), q, p, e, g};
    }

    private final void nextRandomBytes(byte[] buffer) {
        if (this.rnd != null) {
            this.rnd.nextBytes(buffer);
        } else {
            PRNG.nextBytes(buffer);
        }
    }

    private final /* synthetic */ void this() {
        this.sha = new Sha160();
        this.rnd = null;
    }

    public RFC2631(int m, int L, SecureRandom rnd) {
        this.this();
        this.m = m;
        this.L = L;
        this.rnd = rnd;
    }
}


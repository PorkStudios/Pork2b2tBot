/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dss;

import gnu.crypto.hash.Sha160;
import gnu.crypto.util.PRNG;
import gnu.crypto.util.Prime;
import java.math.BigInteger;
import java.security.SecureRandom;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class FIPS186 {
    public static final int DSA_PARAMS_SEED = 0;
    public static final int DSA_PARAMS_COUNTER = 1;
    public static final int DSA_PARAMS_Q = 2;
    public static final int DSA_PARAMS_P = 3;
    public static final int DSA_PARAMS_E = 4;
    public static final int DSA_PARAMS_G = 5;
    private static final BigInteger TWO = new BigInteger("2");
    private static final BigInteger TWO_POW_160 = TWO.pow(160);
    private Sha160 sha;
    private int L;
    private SecureRandom rnd;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public BigInteger[] generateParameters() {
        kb = new byte[20];
        b = (this.L - 1) % 160;
        n = (this.L - 1 - b) / 160;
        V = new BigInteger[n + 1];
        do {
            this.nextRandomBytes(kb);
            SEED = new BigInteger(1, kb).setBit(159).setBit(0);
            alpha = SEED.add(BigInteger.ONE).mod(FIPS186.TWO_POW_160);
            var20_19 = this.sha;
            // MONITORENTER : var20_19
            a = SEED.toByteArray();
            this.sha.update(a, 0, a.length);
            a = this.sha.digest();
            u = alpha.toByteArray();
            this.sha.update(u, 0, u.length);
            u = this.sha.digest();
            // MONITOREXIT : var20_19
            i = 0;
            ** GOTO lbl28
lbl-1000: // 1 sources:
            {
                v1 = a;
                v2 = i;
                v1[v2] = (byte)(v1[v2] ^ u[i]);
                ++i;
lbl28: // 2 sources:
                ** while (i < a.length)
            }
lbl29: // 1 sources:
            U = new BigInteger(1, a);
            q = U.setBit(159).setBit(0);
            if (!Prime.isProbablePrime(q)) continue;
            counter = 0;
            offset = 2;
            do {
                block12 : {
                    OFFSET = BigInteger.valueOf((long)offset & 0xFFFFFFFFL);
                    SEED_PLUS_OFFSET = SEED.add(OFFSET);
                    var20_19 = this.sha;
                    // MONITORENTER : var20_19
                    k = 0;
                    do {
                        if (k > n) break;
                        a = SEED_PLUS_OFFSET.add(BigInteger.valueOf((long)k & 0xFFFFFFFFL)).mod(FIPS186.TWO_POW_160).toByteArray();
                        this.sha.update(a, 0, a.length);
                        V[k] = new BigInteger(1, this.sha.digest());
                        ++k;
                    } while (true);
                    // MONITOREXIT : var20_19
                    W = V[0];
                    k = 1;
                    while (k < n) {
                        W = W.add(V[k].multiply(FIPS186.TWO.pow(k * 160)));
                        ++k;
                    }
                    X = (W = W.add(V[n].mod(FIPS186.TWO.pow(b)).multiply(FIPS186.TWO.pow(n * 160)))).add(FIPS186.TWO.pow(this.L - 1));
                    p = X.subtract((c = X.mod(FIPS186.TWO.multiply(q))).subtract(BigInteger.ONE));
                    if (p.compareTo(FIPS186.TWO.pow(this.L - 1)) < 0 || !Prime.isProbablePrime(p)) break block12;
                    e = p.subtract(BigInteger.ONE).divide(q);
                    h = FIPS186.TWO;
                    p_minus_1 = p.subtract(BigInteger.ONE);
                    g = FIPS186.TWO;
                    ** GOTO lbl73
                }
                offset += n + 1;
            } while (++counter < 4096);
        } while (true);
        while ((g = h.modPow(e, p)).equals(BigInteger.ONE)) {
            h = h.add(BigInteger.ONE);
lbl73: // 2 sources:
            if (h.compareTo(p_minus_1) < 0) continue;
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

    public FIPS186(int L, SecureRandom rnd) {
        this.this();
        this.L = L;
        this.rnd = rnd;
    }
}


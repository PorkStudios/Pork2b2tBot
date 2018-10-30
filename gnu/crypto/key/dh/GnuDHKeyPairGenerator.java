/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import gnu.crypto.hash.Sha160;
import gnu.crypto.key.IKeyPairGenerator;
import gnu.crypto.key.dh.GnuDHPrivateKey;
import gnu.crypto.key.dh.GnuDHPublicKey;
import gnu.crypto.key.dh.RFC2631;
import gnu.crypto.util.PRNG;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;
import javax.crypto.spec.DHGenParameterSpec;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class GnuDHKeyPairGenerator
implements IKeyPairGenerator {
    private static final String NAME = "dh";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 5;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.dh.prng";
    public static final String DH_PARAMETERS = "gnu.crypto.dh.params";
    public static final String PRIME_SIZE = "gnu.crypto.dh.L";
    public static final String EXPONENT_SIZE = "gnu.crypto.dh.m";
    private static final int DEFAULT_PRIME_SIZE = 512;
    private static final int DEFAULT_EXPONENT_SIZE = 160;
    private Sha160 sha;
    private SecureRandom rnd;
    private int l;
    private int m;
    private BigInteger seed;
    private BigInteger counter;
    private BigInteger q;
    private BigInteger p;
    private BigInteger j;
    private BigInteger g;

    private static final void debug(String s) {
        err.println(">>> dh: " + s);
    }

    public String name() {
        return NAME;
    }

    public void setup(Map attributes) {
        this.rnd = (SecureRandom)attributes.get(SOURCE_OF_RANDOMNESS);
        DHGenParameterSpec params = (DHGenParameterSpec)attributes.get(DH_PARAMETERS);
        if (params != null) {
            this.l = params.getPrimeSize();
            this.m = params.getExponentSize();
        } else {
            Integer bi = (Integer)attributes.get(PRIME_SIZE);
            this.l = bi == null ? 512 : bi;
            bi = (Integer)attributes.get(EXPONENT_SIZE);
            int n = this.m = bi == null ? 160 : bi;
        }
        if (this.l % 256 != 0 || this.l < 512) {
            throw new IllegalArgumentException("invalid modulus size");
        }
        if (this.m % 8 != 0 || this.m < 160) {
            throw new IllegalArgumentException("invalid exponent size");
        }
        if (this.m > this.l) {
            throw new IllegalArgumentException("exponent size > modulus size");
        }
    }

    public KeyPair generate() {
        BigInteger x;
        if (this.p == null) {
            BigInteger[] params = new RFC2631(this.m, this.l, this.rnd).generateParameters();
            this.seed = params[0];
            this.counter = params[1];
            this.q = params[2];
            this.p = params[3];
            this.j = params[4];
            this.g = params[5];
        }
        BigInteger q_minus_1 = this.q.subtract(BigInteger.ONE);
        byte[] mag = new byte[(this.m + 7) / 8];
        do {
            this.nextRandomBytes(mag);
        } while ((x = new BigInteger(1, mag)).bitLength() != this.m || x.compareTo(BigInteger.ONE) <= 0 || x.compareTo(q_minus_1) >= 0);
        BigInteger y = this.g.modPow(x, this.p);
        GnuDHPrivateKey secK = new GnuDHPrivateKey(this.q, this.p, this.g, x);
        GnuDHPublicKey pubK = new GnuDHPublicKey(this.q, this.p, this.g, y);
        return new KeyPair(pubK, secK);
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

    public GnuDHKeyPairGenerator() {
        this.this();
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import gnu.crypto.key.IKeyPairGenerator;
import gnu.crypto.key.srp6.SRPAlgorithm;
import gnu.crypto.key.srp6.SRPPrivateKey;
import gnu.crypto.key.srp6.SRPPublicKey;
import gnu.crypto.util.PRNG;
import gnu.crypto.util.Prime;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class SRPKeyPairGenerator
implements IKeyPairGenerator {
    private static final String NAME = "srp";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 5;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    public static final String MODULUS_LENGTH = "gnu.crypto.srp.L";
    public static final String USE_DEFAULTS = "gnu.crypto.srp.use.defaults";
    public static final String SHARED_MODULUS = "gnu.crypto.srp.N";
    public static final String GENERATOR = "gnu.crypto.srp.g";
    public static final String USER_VERIFIER = "gnu.crypto.srp.v";
    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.srp.prng";
    private static final int DEFAULT_MODULUS_LENGTH = 1024;
    private SecureRandom rnd;
    private int l;
    private BigInteger N;
    private BigInteger g;
    private BigInteger v;

    private static final void debug(String s) {
        err.println(">>> srp: " + s);
    }

    public String name() {
        return NAME;
    }

    public void setup(Map attributes) {
        this.rnd = (SecureRandom)attributes.get(SOURCE_OF_RANDOMNESS);
        this.N = (BigInteger)attributes.get(SHARED_MODULUS);
        if (this.N != null) {
            this.l = this.N.bitLength();
            this.g = (BigInteger)attributes.get(GENERATOR);
            if (this.g == null) {
                this.g = TWO;
            }
            SRPAlgorithm.checkParams(this.N, this.g);
        } else {
            Boolean useDefaults = (Boolean)attributes.get(USE_DEFAULTS);
            if (useDefaults == null) {
                useDefaults = Boolean.TRUE;
            }
            Integer L = (Integer)attributes.get(MODULUS_LENGTH);
            this.l = 1024;
            if (useDefaults.equals(Boolean.TRUE)) {
                if (L != null) {
                    this.l = L;
                    switch (this.l) {
                        case 512: {
                            this.N = SRPAlgorithm.N_512;
                            break;
                        }
                        case 640: {
                            this.N = SRPAlgorithm.N_640;
                            break;
                        }
                        case 768: {
                            this.N = SRPAlgorithm.N_768;
                            break;
                        }
                        case 1024: {
                            this.N = SRPAlgorithm.N_1024;
                            break;
                        }
                        case 1280: {
                            this.N = SRPAlgorithm.N_1280;
                            break;
                        }
                        case 1536: {
                            this.N = SRPAlgorithm.N_1536;
                            break;
                        }
                        case 2048: {
                            this.N = SRPAlgorithm.N_2048;
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("unknown default shared modulus bit length");
                        }
                    }
                    this.g = TWO;
                    this.l = this.N.bitLength();
                }
            } else if (L != null) {
                this.l = L;
                if (this.l % 256 != 0 || this.l < 512 || this.l > 2048) {
                    throw new IllegalArgumentException("invalid shared modulus bit length");
                }
            }
        }
        this.v = (BigInteger)attributes.get(USER_VERIFIER);
    }

    public KeyPair generate() {
        if (this.N == null) {
            BigInteger[] params = this.generateParameters();
            BigInteger q = params[0];
            this.N = params[1];
            this.g = params[2];
        }
        return this.v != null ? this.hostKeyPair() : this.userKeyPair();
    }

    private final synchronized BigInteger[] generateParameters() {
        BigInteger p;
        BigInteger q;
        byte[] qBytes = new byte[this.l / 8];
        do {
            this.nextRandomBytes(qBytes);
            q = new BigInteger(1, qBytes);
        } while (!Prime.isProbablePrime(q = q.setBit(0).setBit(this.l - 2).clearBit(this.l - 1)) || (p = q.multiply(TWO).add(ONE)).bitLength() != this.l || !Prime.isProbablePrime(p));
        BigInteger p_minus_1 = p.subtract(ONE);
        BigInteger g = TWO;
        BigInteger h = TWO;
        while (h.compareTo(p_minus_1) < 0) {
            g = h.modPow(TWO, p);
            if (!g.equals(ONE)) break;
            h = h.add(ONE);
        }
        return new BigInteger[]{q, p, g};
    }

    private final KeyPair hostKeyPair() {
        BigInteger B;
        BigInteger b;
        byte[] bBytes = new byte[(this.l + 7) / 8];
        do {
            this.nextRandomBytes(bBytes);
        } while ((b = new BigInteger(1, bBytes)).compareTo(ONE) <= 0 || b.compareTo(this.N) >= 0 || (B = THREE.multiply(this.v).add(this.g.modPow(b, this.N)).mod(this.N)).compareTo(ZERO) == 0 || B.compareTo(this.N) >= 0);
        KeyPair result = new KeyPair(new SRPPublicKey(new BigInteger[]{this.N, this.g, B}), new SRPPrivateKey(new BigInteger[]{this.N, this.g, b, this.v}));
        return result;
    }

    private final KeyPair userKeyPair() {
        BigInteger a;
        BigInteger A;
        byte[] aBytes = new byte[(this.l + 7) / 8];
        do {
            this.nextRandomBytes(aBytes);
        } while ((a = new BigInteger(1, aBytes)).compareTo(ONE) <= 0 || a.compareTo(this.N) >= 0 || (A = this.g.modPow(a, this.N)).compareTo(ZERO) == 0 || A.compareTo(this.N) >= 0);
        KeyPair result = new KeyPair(new SRPPublicKey(new BigInteger[]{this.N, this.g, A}), new SRPPrivateKey(new BigInteger[]{this.N, this.g, a}));
        return result;
    }

    private final void nextRandomBytes(byte[] buffer) {
        if (this.rnd != null) {
            this.rnd.nextBytes(buffer);
        } else {
            PRNG.nextBytes(buffer);
        }
    }

    private final /* synthetic */ void this() {
        this.rnd = null;
    }

    public SRPKeyPairGenerator() {
        this.this();
    }
}


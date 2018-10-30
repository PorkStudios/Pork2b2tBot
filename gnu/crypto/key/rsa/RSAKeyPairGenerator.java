/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.rsa;

import gnu.crypto.key.IKeyPairGenerator;
import gnu.crypto.key.rsa.GnuRSAPrivateKey;
import gnu.crypto.key.rsa.GnuRSAPublicKey;
import gnu.crypto.util.PRNG;
import gnu.crypto.util.Prime;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class RSAKeyPairGenerator
implements IKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.valueOf(2);
    public static final String MODULUS_LENGTH = "gnu.crypto.rsa.L";
    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.rsa.prng";
    public static final String RSA_PARAMETERS = "gnu.crypto.rsa.params";
    private static final int DEFAULT_MODULUS_LENGTH = 1024;
    private int L;
    private BigInteger e;
    private SecureRandom rnd;

    public String name() {
        return "rsa";
    }

    public void setup(Map attributes) {
        this.rnd = (SecureRandom)attributes.get(SOURCE_OF_RANDOMNESS);
        RSAKeyGenParameterSpec params = (RSAKeyGenParameterSpec)attributes.get(RSA_PARAMETERS);
        if (params != null) {
            this.L = params.getKeysize();
            this.e = params.getPublicExponent();
        } else {
            Integer l = (Integer)attributes.get(MODULUS_LENGTH);
            int n = this.L = l == null ? 1024 : l;
        }
        if (this.L < 1024) {
            throw new IllegalArgumentException(MODULUS_LENGTH);
        }
    }

    public KeyPair generate() {
        BigInteger p;
        BigInteger n;
        BigInteger q;
        int M = (this.L + 1) / 2;
        BigInteger lower = TWO.pow(M - 1);
        BigInteger upper = TWO.pow(M).subtract(ONE);
        byte[] kb = new byte[(M + 7) / 8];
        do {
            this.nextRandomBytes(kb);
        } while ((p = new BigInteger(1, kb).setBit(0)).compareTo(lower) < 0 || p.compareTo(upper) > 0 || !Prime.isProbablePrime(p) || !p.gcd(this.e).equals(ONE));
        do {
            this.nextRandomBytes(kb);
        } while ((n = p.multiply(q = new BigInteger(1, kb).setBit(0))).bitLength() != this.L || !Prime.isProbablePrime(q) || !q.gcd(this.e).equals(ONE));
        BigInteger phi = p.subtract(ONE).multiply(q.subtract(ONE));
        BigInteger d = this.e.modInverse(phi);
        GnuRSAPublicKey pubK = new GnuRSAPublicKey(n, this.e);
        GnuRSAPrivateKey secK = new GnuRSAPrivateKey(p, q, this.e, d);
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
        this.e = BigInteger.valueOf(65537L);
        this.rnd = null;
    }

    public RSAKeyPairGenerator() {
        this.this();
    }
}


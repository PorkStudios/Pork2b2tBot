/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig.rsa;

import gnu.crypto.Properties;
import gnu.crypto.util.PRNG;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSA {
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;

    public static final BigInteger sign(PrivateKey K, BigInteger m) {
        try {
            return RSA.RSADP((RSAPrivateKey)K, m);
        }
        catch (IllegalArgumentException x) {
            throw new IllegalArgumentException("message representative out of range");
        }
    }

    public static final BigInteger verify(PublicKey K, BigInteger s) {
        try {
            return RSA.RSAEP((RSAPublicKey)K, s);
        }
        catch (IllegalArgumentException x) {
            throw new IllegalArgumentException("signature representative out of range");
        }
    }

    public static final BigInteger encrypt(PublicKey K, BigInteger m) {
        try {
            return RSA.RSAEP((RSAPublicKey)K, m);
        }
        catch (IllegalArgumentException x) {
            throw new IllegalArgumentException("message representative out of range");
        }
    }

    public static final BigInteger decrypt(PrivateKey K, BigInteger c) {
        try {
            return RSA.RSADP((RSAPrivateKey)K, c);
        }
        catch (IllegalArgumentException x) {
            throw new IllegalArgumentException("ciphertext representative out of range");
        }
    }

    public static final byte[] I2OSP(BigInteger s, int k) {
        byte[] result = s.toByteArray();
        if (result.length < k) {
            byte[] newResult = new byte[k];
            System.arraycopy(result, 0, newResult, k - result.length, result.length);
            result = newResult;
        } else if (result.length > k) {
            int limit = result.length - k;
            int i = 0;
            while (i < limit) {
                if (result[i] != 0) {
                    throw new IllegalArgumentException("integer too large");
                }
                ++i;
            }
            byte[] newResult = new byte[k];
            System.arraycopy(result, limit, newResult, 0, k);
            result = newResult;
        }
        return result;
    }

    private static final BigInteger RSAEP(RSAPublicKey K, BigInteger m) {
        BigInteger n = K.getModulus();
        if (m.compareTo(ZERO) < 0 || m.compareTo(n.subtract(ONE)) > 0) {
            throw new IllegalArgumentException();
        }
        BigInteger e = K.getPublicExponent();
        BigInteger result = m.modPow(e, n);
        return result;
    }

    private static final BigInteger RSADP(RSAPrivateKey K, BigInteger c) {
        BigInteger result;
        BigInteger n = K.getModulus();
        if (c.compareTo(ZERO) < 0 || c.compareTo(n.subtract(ONE)) > 0) {
            throw new IllegalArgumentException();
        }
        if (!(K instanceof RSAPrivateCrtKey)) {
            BigInteger d = K.getPrivateExponent();
            result = c.modPow(d, n);
        } else {
            boolean rsaBlinding = Properties.doRSABlinding();
            BigInteger r = null;
            BigInteger e = null;
            if (rsaBlinding) {
                BigInteger x;
                r = RSA.newR(n);
                e = ((RSAPrivateCrtKey)K).getPublicExponent();
                c = x = r.modPow(e, n).multiply(c).mod(n);
            }
            BigInteger p = ((RSAPrivateCrtKey)K).getPrimeP();
            BigInteger q = ((RSAPrivateCrtKey)K).getPrimeQ();
            BigInteger dP = ((RSAPrivateCrtKey)K).getPrimeExponentP();
            BigInteger dQ = ((RSAPrivateCrtKey)K).getPrimeExponentQ();
            BigInteger qInv = ((RSAPrivateCrtKey)K).getCrtCoefficient();
            BigInteger m_1 = c.modPow(dP, p);
            BigInteger m_2 = c.modPow(dQ, q);
            BigInteger h = m_1.subtract(m_2).multiply(qInv).mod(p);
            result = m_2.add(q.multiply(h));
            if (rsaBlinding) {
                result = result.multiply(r.modInverse(n)).mod(n);
            }
        }
        return result;
    }

    private static final BigInteger newR(BigInteger N) {
        int b;
        int upper = (N.bitLength() + 7) / 8;
        int lower = upper / 2;
        byte[] bl = new byte[1];
        do {
            PRNG.nextBytes(bl);
        } while ((b = bl[0] & 255) < lower || b > upper);
        byte[] buffer = new byte[b];
        PRNG.nextBytes(buffer);
        return new BigInteger(1, buffer);
    }

    private RSA() {
    }
}


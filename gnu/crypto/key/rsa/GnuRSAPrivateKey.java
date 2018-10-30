/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.rsa;

import gnu.crypto.Registry;
import gnu.crypto.key.rsa.GnuRSAKey;
import gnu.crypto.key.rsa.RSAKeyPairRawCodec;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;

public class GnuRSAPrivateKey
extends GnuRSAKey
implements PrivateKey,
RSAPrivateCrtKey {
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger d;
    private final BigInteger dP;
    private final BigInteger dQ;
    private final BigInteger qInv;

    public static GnuRSAPrivateKey valueOf(byte[] k) {
        if (k[0] == Registry.MAGIC_RAW_RSA_PRIVATE_KEY[0]) {
            RSAKeyPairRawCodec codec = new RSAKeyPairRawCodec();
            return (GnuRSAPrivateKey)codec.decodePrivateKey(k);
        }
        throw new IllegalArgumentException("magic");
    }

    public BigInteger getPrimeP() {
        return this.p;
    }

    public BigInteger getPrimeQ() {
        return this.q;
    }

    public BigInteger getPrimeExponentP() {
        return this.dP;
    }

    public BigInteger getPrimeExponentQ() {
        return this.dQ;
    }

    public BigInteger getCrtCoefficient() {
        return this.qInv;
    }

    public BigInteger getPrivateExponent() {
        return this.d;
    }

    public byte[] getEncoded(int format) {
        byte[] result;
        switch (format) {
            case 1: {
                result = new RSAKeyPairRawCodec().encodePrivateKey(this);
                break;
            }
            default: {
                throw new IllegalArgumentException("format");
            }
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof RSAPrivateKey) {
            RSAPrivateKey that = (RSAPrivateKey)obj;
            boolean bl = false;
            if (super.equals(that) && this.d.equals(that.getPrivateExponent())) {
                bl = true;
            }
            return bl;
        }
        if (obj instanceof RSAPrivateCrtKey) {
            RSAPrivateCrtKey that = (RSAPrivateCrtKey)obj;
            boolean bl = false;
            if (super.equals(that) && this.p.equals(that.getPrimeP()) && this.q.equals(that.getPrimeQ()) && this.dP.equals(that.getPrimeExponentP()) && this.dQ.equals(that.getPrimeExponentQ()) && this.qInv.equals(that.getCrtCoefficient())) {
                bl = true;
            }
            return bl;
        }
        return false;
    }

    public GnuRSAPrivateKey(BigInteger p, BigInteger q, BigInteger e, BigInteger d) {
        super(p.multiply(q), e);
        this.p = p;
        this.q = q;
        this.d = d;
        this.dP = e.modInverse(p.subtract(BigInteger.ONE));
        this.dQ = e.modInverse(q.subtract(BigInteger.ONE));
        this.qInv = q.modInverse(p);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.rsa;

import java.math.BigInteger;
import java.security.Key;
import java.security.interfaces.RSAKey;

public abstract class GnuRSAKey
implements Key,
RSAKey {
    private final BigInteger n;
    private final BigInteger e;

    public BigInteger getModulus() {
        return this.getN();
    }

    public String getAlgorithm() {
        return "rsa";
    }

    public byte[] getEncoded() {
        return this.getEncoded(1);
    }

    public String getFormat() {
        return null;
    }

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getPublicExponent() {
        return this.getE();
    }

    public BigInteger getE() {
        return this.e;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RSAKey)) {
            return false;
        }
        RSAKey that = (RSAKey)obj;
        return this.n.equals(that.getModulus());
    }

    public abstract byte[] getEncoded(int var1);

    protected GnuRSAKey(BigInteger n, BigInteger e) {
        this.n = n;
        this.e = e;
    }
}


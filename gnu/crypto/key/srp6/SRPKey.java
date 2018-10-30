/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.Key;

public abstract class SRPKey
implements Key,
Serializable {
    protected final BigInteger N;
    protected final BigInteger g;

    public String getAlgorithm() {
        return "srp";
    }

    public byte[] getEncoded() {
        return this.getEncoded(1);
    }

    public String getFormat() {
        return null;
    }

    public BigInteger getN() {
        return this.N;
    }

    public BigInteger getG() {
        return this.g;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SRPKey)) {
            return false;
        }
        SRPKey that = (SRPKey)obj;
        boolean bl = false;
        if (this.N.equals(that.getN()) && this.g.equals(that.getG())) {
            bl = true;
        }
        return bl;
    }

    public abstract byte[] getEncoded(int var1);

    protected SRPKey(BigInteger N, BigInteger g) {
        this.N = N;
        this.g = g;
    }
}


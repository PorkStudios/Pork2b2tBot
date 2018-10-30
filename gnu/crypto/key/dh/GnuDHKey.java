/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import java.math.BigInteger;
import java.security.Key;
import javax.crypto.interfaces.DHKey;
import javax.crypto.spec.DHParameterSpec;

public abstract class GnuDHKey
implements Key,
DHKey {
    protected BigInteger q;
    protected BigInteger p;
    protected BigInteger g;

    public DHParameterSpec getParams() {
        if (this.q == null) {
            return new DHParameterSpec(this.p, this.g);
        }
        return new DHParameterSpec(this.p, this.g, this.q.bitLength());
    }

    public String getAlgorithm() {
        return "dh";
    }

    public String getFormat() {
        return null;
    }

    public BigInteger getQ() {
        return this.q;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DHKey)) {
            return false;
        }
        DHKey that = (DHKey)obj;
        boolean bl = false;
        if (this.p.equals(that.getParams().getP()) && this.g.equals(that.getParams().getG())) {
            bl = true;
        }
        return bl;
    }

    protected GnuDHKey(BigInteger q, BigInteger p, BigInteger g) {
        this.q = q;
        this.p = p;
        this.g = g;
    }
}


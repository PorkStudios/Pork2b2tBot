/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dss;

import java.math.BigInteger;
import java.security.Key;
import java.security.interfaces.DSAKey;
import java.security.interfaces.DSAParams;
import java.security.spec.DSAParameterSpec;

public abstract class DSSKey
implements Key,
DSAKey {
    protected final BigInteger p;
    protected final BigInteger q;
    protected final BigInteger g;

    public DSAParams getParams() {
        return new DSAParameterSpec(this.p, this.q, this.g);
    }

    public String getAlgorithm() {
        return "dss";
    }

    public byte[] getEncoded() {
        return this.getEncoded(1);
    }

    public String getFormat() {
        return null;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DSAKey)) {
            return false;
        }
        DSAKey that = (DSAKey)obj;
        boolean bl = false;
        if (this.p.equals(that.getParams().getP()) && this.q.equals(that.getParams().getQ()) && this.g.equals(that.getParams().getG())) {
            bl = true;
        }
        return bl;
    }

    public abstract byte[] getEncoded(int var1);

    protected DSSKey(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig.dss;

import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.hash.Sha160;
import gnu.crypto.prng.IRandom;
import gnu.crypto.sig.BaseSignature;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DSSSignature
extends BaseSignature {
    public static final BigInteger[] sign(DSAPrivateKey k, byte[] h) {
        DSSSignature sig = new DSSSignature();
        HashMap<String, DSAPrivateKey> attributes = new HashMap<String, DSAPrivateKey>();
        attributes.put("gnu.crypto.sig.private.key", k);
        sig.setupSign(attributes);
        return sig.computeRS(h);
    }

    public static final BigInteger[] sign(DSAPrivateKey k, byte[] h, Random rnd) {
        DSSSignature sig = new DSSSignature();
        HashMap<String, Serializable> attributes = new HashMap<String, Serializable>();
        attributes.put("gnu.crypto.sig.private.key", k);
        if (rnd != null) {
            attributes.put("gnu.crypto.sig.prng", rnd);
        }
        sig.setupSign(attributes);
        return sig.computeRS(h);
    }

    public static final BigInteger[] sign(DSAPrivateKey k, byte[] h, IRandom irnd) {
        DSSSignature sig = new DSSSignature();
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("gnu.crypto.sig.private.key", k);
        if (irnd != null) {
            attributes.put("gnu.crypto.sig.prng", irnd);
        }
        sig.setupSign(attributes);
        return sig.computeRS(h);
    }

    public static final boolean verify(DSAPublicKey k, byte[] h, BigInteger[] rs) {
        DSSSignature sig = new DSSSignature();
        HashMap<String, DSAPublicKey> attributes = new HashMap<String, DSAPublicKey>();
        attributes.put("gnu.crypto.sig.public.key", k);
        sig.setupVerify(attributes);
        return sig.checkRS(rs, h);
    }

    public Object clone() {
        return new DSSSignature(this);
    }

    protected void setupForVerification(PublicKey k) throws IllegalArgumentException {
        if (!(k instanceof DSAPublicKey)) {
            throw new IllegalArgumentException();
        }
        this.publicKey = k;
    }

    protected void setupForSigning(PrivateKey k) throws IllegalArgumentException {
        if (!(k instanceof DSAPrivateKey)) {
            throw new IllegalArgumentException();
        }
        this.privateKey = k;
    }

    protected Object generateSignature() throws IllegalStateException {
        BigInteger[] rs = this.computeRS(this.md.digest());
        return this.encodeSignature(rs[0], rs[1]);
    }

    protected boolean verifySignature(Object sig) throws IllegalStateException {
        BigInteger[] rs = this.decodeSignature(sig);
        return this.checkRS(rs, this.md.digest());
    }

    private final Object encodeSignature(BigInteger r, BigInteger s) {
        return new BigInteger[]{r, s};
    }

    private final BigInteger[] decodeSignature(Object signature) {
        return (BigInteger[])signature;
    }

    private final BigInteger[] computeRS(byte[] digestBytes) {
        BigInteger s;
        BigInteger r;
        BigInteger k;
        BigInteger p = ((DSAPrivateKey)this.privateKey).getParams().getP();
        BigInteger q = ((DSAPrivateKey)this.privateKey).getParams().getQ();
        BigInteger g = ((DSAPrivateKey)this.privateKey).getParams().getG();
        BigInteger x = ((DSAPrivateKey)this.privateKey).getX();
        BigInteger m = new BigInteger(1, digestBytes);
        byte[] kb = new byte[20];
        do {
            this.nextRandomBytes(kb);
            k = new BigInteger(1, kb);
            k.clearBit(159);
        } while ((r = g.modPow(k, p).mod(q)).equals(BigInteger.ZERO) || (s = m.add(x.multiply(r)).multiply(k.modInverse(q)).mod(q)).equals(BigInteger.ZERO));
        return new BigInteger[]{r, s};
    }

    private final boolean checkRS(BigInteger[] rs, byte[] digestBytes) {
        BigInteger r = rs[0];
        BigInteger s = rs[1];
        BigInteger g = ((DSAPublicKey)this.publicKey).getParams().getG();
        BigInteger p = ((DSAPublicKey)this.publicKey).getParams().getP();
        BigInteger q = ((DSAPublicKey)this.publicKey).getParams().getQ();
        BigInteger y = ((DSAPublicKey)this.publicKey).getY();
        BigInteger w = s.modInverse(q);
        BigInteger u1 = w.multiply(new BigInteger(1, digestBytes)).mod(q);
        BigInteger u2 = r.multiply(w).mod(q);
        BigInteger v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);
        return v.equals(r);
    }

    public DSSSignature() {
        super("dss", new Sha160());
    }

    private DSSSignature(DSSSignature that) {
        this();
        this.publicKey = that.publicKey;
        this.privateKey = that.privateKey;
        this.md = (IMessageDigest)that.md.clone();
    }
}


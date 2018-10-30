/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.sasl.srp.SRPRegistry;
import gnu.crypto.util.Util;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashMap;

public final class SRP {
    private static final HashMap algorithms = new HashMap();
    private static final byte COLON = 58;
    private IMessageDigest mda;

    public static final synchronized SRP instance(String mdName) {
        SRP result;
        if (mdName != null) {
            mdName = mdName.trim().toLowerCase();
        }
        if (mdName == null || mdName.equals("")) {
            mdName = SRPRegistry.SRP_DEFAULT_DIGEST_NAME;
        }
        if ((result = (SRP)algorithms.get(mdName)) == null) {
            IMessageDigest mda = HashFactory.getInstance(mdName);
            result = new SRP(mda);
            algorithms.put(mdName, result);
        }
        return result;
    }

    private static final byte[] xor(byte[] b1, byte[] b2, int length) {
        byte[] result = new byte[length];
        int i = 0;
        while (i < length) {
            result[i] = (byte)(b1[i] ^ b2[i]);
            ++i;
        }
        return result;
    }

    public final String getAlgorithm() {
        return this.mda.name();
    }

    public final IMessageDigest newDigest() {
        return (IMessageDigest)this.mda.clone();
    }

    public final byte[] digest(byte[] src) {
        IMessageDigest hash = (IMessageDigest)this.mda.clone();
        hash.update(src, 0, src.length);
        return hash.digest();
    }

    public final byte[] digest(String src) throws UnsupportedEncodingException {
        return this.digest(src.getBytes("US-ASCII"));
    }

    public final byte[] xor(byte[] a, byte[] b) {
        return SRP.xor(a, b, this.mda.hashSize());
    }

    public final byte[] generateM1(BigInteger N, BigInteger g, String U, byte[] s, BigInteger A, BigInteger B, byte[] K, String I, String L, byte[] cn, byte[] cCB) throws UnsupportedEncodingException {
        IMessageDigest hash = (IMessageDigest)this.mda.clone();
        byte[] b = this.xor(this.digest(Util.trim(N)), this.digest(Util.trim(g)));
        hash.update(b, 0, b.length);
        b = this.digest(U);
        hash.update(b, 0, b.length);
        hash.update(s, 0, s.length);
        b = Util.trim(A);
        hash.update(b, 0, b.length);
        b = Util.trim(B);
        hash.update(b, 0, b.length);
        hash.update(K, 0, K.length);
        b = this.digest(I);
        hash.update(b, 0, b.length);
        b = this.digest(L);
        hash.update(b, 0, b.length);
        hash.update(cn, 0, cn.length);
        hash.update(cCB, 0, cCB.length);
        return hash.digest();
    }

    public final byte[] generateM2(BigInteger A, byte[] M1, byte[] K, String U, String I, String o, byte[] sid, int ttl, byte[] cIV, byte[] sIV, byte[] sCB) throws UnsupportedEncodingException {
        IMessageDigest hash = (IMessageDigest)this.mda.clone();
        byte[] b = Util.trim(A);
        hash.update(b, 0, b.length);
        hash.update(M1, 0, M1.length);
        hash.update(K, 0, K.length);
        b = this.digest(U);
        hash.update(b, 0, b.length);
        b = this.digest(I);
        hash.update(b, 0, b.length);
        b = this.digest(o);
        hash.update(b, 0, b.length);
        hash.update(sid, 0, sid.length);
        hash.update((byte)(ttl >>> 24));
        hash.update((byte)(ttl >>> 16));
        hash.update((byte)(ttl >>> 8));
        hash.update((byte)ttl);
        hash.update(cIV, 0, cIV.length);
        hash.update(sIV, 0, sIV.length);
        hash.update(sCB, 0, sCB.length);
        return hash.digest();
    }

    public final byte[] generateKn(byte[] K, byte[] cn, byte[] sn) {
        IMessageDigest hash = (IMessageDigest)this.mda.clone();
        hash.update(K, 0, K.length);
        hash.update(cn, 0, cn.length);
        hash.update(sn, 0, sn.length);
        return hash.digest();
    }

    public final byte[] computeX(byte[] s, String user, String password) throws UnsupportedEncodingException {
        return this.computeX(s, user.getBytes("US-ASCII"), password.getBytes("US-ASCII"));
    }

    public final byte[] computeX(byte[] s, String user, byte[] p) throws UnsupportedEncodingException {
        return this.computeX(s, user.getBytes("US-ASCII"), p);
    }

    private final byte[] computeX(byte[] s, byte[] user, byte[] p) {
        IMessageDigest hash = (IMessageDigest)this.mda.clone();
        hash.update(user, 0, user.length);
        hash.update((byte)58);
        hash.update(p, 0, p.length);
        byte[] up = hash.digest();
        hash.update(s, 0, s.length);
        hash.update(up, 0, up.length);
        return hash.digest();
    }

    private SRP(IMessageDigest mda) {
        this.mda = mda;
    }
}


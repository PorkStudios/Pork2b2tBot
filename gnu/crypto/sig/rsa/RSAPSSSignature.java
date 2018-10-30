/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig.rsa;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.sig.BaseSignature;
import gnu.crypto.sig.rsa.EMSA_PSS;
import gnu.crypto.sig.rsa.RSA;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSAPSSSignature
extends BaseSignature {
    private static final String NAME = "rsa-pss";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 1;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private EMSA_PSS pss;
    private int sLen;

    private static final void debug(String s) {
        err.println(">>> rsa-pss: " + s);
    }

    public Object clone() {
        return new RSAPSSSignature(this);
    }

    protected void setupForVerification(PublicKey k) throws IllegalArgumentException {
        if (!(k instanceof RSAPublicKey)) {
            throw new IllegalArgumentException();
        }
        this.publicKey = (RSAPublicKey)k;
    }

    protected void setupForSigning(PrivateKey k) throws IllegalArgumentException {
        if (!(k instanceof RSAPrivateKey)) {
            throw new IllegalArgumentException();
        }
        this.privateKey = (RSAPrivateKey)k;
    }

    protected Object generateSignature() throws IllegalStateException {
        int modBits = ((RSAPrivateKey)this.privateKey).getModulus().bitLength();
        byte[] salt = new byte[this.sLen];
        this.nextRandomBytes(salt);
        byte[] EM = this.pss.encode(this.md.digest(), modBits - 1, salt);
        BigInteger m = new BigInteger(1, EM);
        BigInteger s = RSA.sign(this.privateKey, m);
        int k = (modBits + 7) / 8;
        return RSA.I2OSP(s, k);
    }

    protected boolean verifySignature(Object sig) throws IllegalStateException {
        if (this.publicKey == null) {
            throw new IllegalStateException();
        }
        byte[] S = (byte[])sig;
        int modBits = ((RSAPublicKey)this.publicKey).getModulus().bitLength();
        int k = (modBits + 7) / 8;
        if (S.length != k) {
            return false;
        }
        BigInteger s = new BigInteger(1, S);
        BigInteger m = null;
        try {
            m = RSA.verify(this.publicKey, s);
        }
        catch (IllegalArgumentException x) {
            return false;
        }
        int emBits = modBits - 1;
        int emLen = (emBits + 7) / 8;
        byte[] EM = m.toByteArray();
        if (EM.length > emLen) {
            return false;
        }
        if (EM.length < emLen) {
            byte[] newEM = new byte[emLen];
            System.arraycopy(EM, 0, newEM, emLen - EM.length, EM.length);
            EM = newEM;
        }
        byte[] mHash = this.md.digest();
        boolean result = false;
        try {
            result = this.pss.decode(mHash, EM, emBits, this.sLen);
        }
        catch (IllegalArgumentException x) {
            result = false;
        }
        return result;
    }

    public RSAPSSSignature() {
        this("sha-160", 0);
    }

    public RSAPSSSignature(String mdName) {
        this(mdName, 0);
    }

    public RSAPSSSignature(String mdName, int sLen) {
        super(NAME, HashFactory.getInstance(mdName));
        this.pss = EMSA_PSS.getInstance(mdName);
        this.sLen = sLen;
    }

    private RSAPSSSignature(RSAPSSSignature that) {
        this(that.md.name(), that.sLen);
        this.publicKey = that.publicKey;
        this.privateKey = that.privateKey;
        this.md = (IMessageDigest)that.md.clone();
        this.pss = (EMSA_PSS)that.pss.clone();
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig.rsa;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.sig.BaseSignature;
import gnu.crypto.sig.rsa.EMSA_PKCS1_V1_5;
import gnu.crypto.sig.rsa.RSA;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

public class RSAPKCS1V1_5Signature
extends BaseSignature {
    private EMSA_PKCS1_V1_5 pkcs1;

    public Object clone() {
        return new RSAPKCS1V1_5Signature(this);
    }

    protected void setupForVerification(PublicKey k) throws IllegalArgumentException {
        if (!(k instanceof RSAPublicKey)) {
            throw new IllegalArgumentException();
        }
        this.publicKey = k;
    }

    protected void setupForSigning(PrivateKey k) throws IllegalArgumentException {
        if (!(k instanceof RSAPrivateKey)) {
            throw new IllegalArgumentException();
        }
        this.privateKey = k;
    }

    protected Object generateSignature() throws IllegalStateException {
        int modBits = ((RSAPrivateKey)this.privateKey).getModulus().bitLength();
        int k = (modBits + 7) / 8;
        byte[] EM = this.pkcs1.encode(this.md.digest(), k);
        BigInteger m = new BigInteger(1, EM);
        BigInteger s = RSA.sign(this.privateKey, m);
        return RSA.I2OSP(s, k);
    }

    protected boolean verifySignature(Object sig) throws IllegalStateException {
        byte[] EM;
        BigInteger m;
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
        try {
            m = RSA.verify(this.publicKey, s);
        }
        catch (IllegalArgumentException x) {
            return false;
        }
        try {
            EM = RSA.I2OSP(m, k);
        }
        catch (IllegalArgumentException x) {
            return false;
        }
        byte[] EMp = this.pkcs1.encode(this.md.digest(), k);
        return Arrays.equals(EM, EMp);
    }

    public RSAPKCS1V1_5Signature() {
        this("sha-160");
    }

    public RSAPKCS1V1_5Signature(String mdName) {
        super("rsa-pkcs1-v1.5", HashFactory.getInstance(mdName));
        this.pkcs1 = EMSA_PKCS1_V1_5.getInstance(mdName);
    }

    private RSAPKCS1V1_5Signature(RSAPKCS1V1_5Signature that) {
        this(that.md.name());
        this.publicKey = that.publicKey;
        this.privateKey = that.privateKey;
        this.md = (IMessageDigest)that.md.clone();
        this.pkcs1 = (EMSA_PKCS1_V1_5)that.pkcs1.clone();
    }
}


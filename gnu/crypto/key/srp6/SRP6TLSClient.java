/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.key.srp6.SRP6KeyAgreement;
import gnu.crypto.key.srp6.SRPKeyPairGenerator;
import gnu.crypto.key.srp6.SRPPrivateKey;
import gnu.crypto.key.srp6.SRPPublicKey;
import gnu.crypto.sasl.srp.SRP;
import gnu.crypto.util.Util;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class SRP6TLSClient
extends SRP6KeyAgreement {
    private String I;
    private byte[] p;
    private KeyPair userKeyPair;

    protected void engineInit(Map attributes) throws KeyAgreementException {
        this.rnd = (SecureRandom)attributes.get("gnu.crypto.srp6.ka.prng");
        String md = (String)attributes.get("gnu.crypto.srp6.ka.H");
        if (md == null || "".equals(md.trim())) {
            throw new KeyAgreementException("missing hash function");
        }
        this.srp = SRP.instance(md);
        this.I = (String)attributes.get("gnu.crypto.srp6.ka.I");
        if (this.I == null) {
            throw new KeyAgreementException("missing user identity");
        }
        this.p = (byte[])attributes.get("gnu.crypto.srp6.ka.p");
        if (this.p == null) {
            throw new KeyAgreementException("missing user password");
        }
    }

    protected OutgoingMessage engineProcessMessage(IncomingMessage in) throws KeyAgreementException {
        switch (this.step) {
            case 0: {
                return this.sendIdentity(in);
            }
            case 1: {
                return this.computeSharedSecret(in);
            }
        }
        throw new IllegalStateException("unexpected state");
    }

    protected void engineReset() {
        this.I = null;
        this.p = null;
        this.userKeyPair = null;
        super.engineReset();
    }

    private final OutgoingMessage sendIdentity(IncomingMessage in) throws KeyAgreementException {
        OutgoingMessage result = new OutgoingMessage();
        result.writeString(this.I);
        return result;
    }

    protected OutgoingMessage computeSharedSecret(IncomingMessage in) throws KeyAgreementException {
        BigInteger x;
        BigInteger S;
        this.N = in.readMPI();
        this.g = in.readMPI();
        BigInteger s = in.readMPI();
        BigInteger B = in.readMPI();
        if (B.mod(this.N).equals(BigInteger.ZERO)) {
            throw new KeyAgreementException("illegal value for B");
        }
        SRPKeyPairGenerator kpg = new SRPKeyPairGenerator();
        HashMap<String, Serializable> attributes = new HashMap<String, Serializable>();
        if (this.rnd != null) {
            attributes.put("gnu.crypto.srp.prng", this.rnd);
        }
        attributes.put("gnu.crypto.srp.N", this.N);
        attributes.put("gnu.crypto.srp.g", this.g);
        kpg.setup(attributes);
        this.userKeyPair = kpg.generate();
        BigInteger A = ((SRPPublicKey)this.userKeyPair.getPublic()).getY();
        BigInteger u = this.uValue(A, B);
        if (u.mod(this.N).equals(BigInteger.ZERO)) {
            throw new KeyAgreementException("u is zero");
        }
        try {
            x = new BigInteger(1, this.srp.computeX(Util.trim(s), this.I, this.p));
        }
        catch (Exception e) {
            throw new KeyAgreementException("computeSharedSecret()", e);
        }
        BigInteger a = ((SRPPrivateKey)this.userKeyPair.getPrivate()).getX();
        this.K = S = B.subtract(THREE.multiply(this.g.modPow(x, this.N))).modPow(a.add(u.multiply(x)), this.N);
        OutgoingMessage result = new OutgoingMessage();
        result.writeMPI(A);
        this.complete = true;
        return result;
    }
}


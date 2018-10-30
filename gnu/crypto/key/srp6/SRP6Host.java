/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.key.srp6.SRP6KeyAgreement;
import gnu.crypto.key.srp6.SRPKeyPairGenerator;
import gnu.crypto.key.srp6.SRPPrivateKey;
import gnu.crypto.key.srp6.SRPPublicKey;
import gnu.crypto.sasl.srp.SRP;
import gnu.crypto.sasl.srp.SRPAuthInfoProvider;
import gnu.crypto.util.Util;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class SRP6Host
extends SRP6KeyAgreement {
    private KeyPair hostKeyPair;
    private SRPAuthInfoProvider passwordDB;

    protected void engineInit(Map attributes) throws KeyAgreementException {
        this.rnd = (SecureRandom)attributes.get("gnu.crypto.srp6.ka.prng");
        this.N = (BigInteger)attributes.get("gnu.crypto.srp6.ka.N");
        if (this.N == null) {
            throw new KeyAgreementException("missing shared modulus");
        }
        this.g = (BigInteger)attributes.get("gnu.crypto.srp6.ka.g");
        if (this.g == null) {
            throw new KeyAgreementException("missing generator");
        }
        String md = (String)attributes.get("gnu.crypto.srp6.ka.H");
        if (md == null || "".equals(md.trim())) {
            throw new KeyAgreementException("missing hash function");
        }
        this.srp = SRP.instance(md);
        this.passwordDB = (SRPAuthInfoProvider)attributes.get("gnu.crypto.srp6.ka.password.db");
        if (this.passwordDB == null) {
            throw new KeyAgreementException("missing SRP password database");
        }
    }

    protected OutgoingMessage engineProcessMessage(IncomingMessage in) throws KeyAgreementException {
        switch (this.step) {
            case 0: {
                return this.computeSharedSecret(in);
            }
        }
        throw new IllegalStateException("unexpected state");
    }

    protected void engineReset() {
        this.hostKeyPair = null;
        super.engineReset();
    }

    private final OutgoingMessage computeSharedSecret(IncomingMessage in) throws KeyAgreementException {
        Map credentials;
        String I = in.readString();
        BigInteger A = in.readMPI();
        try {
            HashMap<String, String> userID = new HashMap<String, String>();
            userID.put("gnu.crypto.sasl.username", I);
            userID.put("srp.md.name", this.srp.getAlgorithm());
            credentials = this.passwordDB.lookup(userID);
        }
        catch (IOException x) {
            throw new KeyAgreementException("computeSharedSecret()", x);
        }
        BigInteger s = new BigInteger(1, Util.fromBase64((String)credentials.get("srp.salt")));
        BigInteger v = new BigInteger(1, Util.fromBase64((String)credentials.get("srp.user.verifier")));
        SRPKeyPairGenerator kpg = new SRPKeyPairGenerator();
        HashMap<String, Serializable> attributes = new HashMap<String, Serializable>();
        if (this.rnd != null) {
            attributes.put("gnu.crypto.srp.prng", this.rnd);
        }
        attributes.put("gnu.crypto.srp.N", this.N);
        attributes.put("gnu.crypto.srp.g", this.g);
        attributes.put("gnu.crypto.srp.v", v);
        kpg.setup(attributes);
        this.hostKeyPair = kpg.generate();
        BigInteger B = ((SRPPublicKey)this.hostKeyPair.getPublic()).getY();
        BigInteger u = this.uValue(A, B);
        BigInteger b = ((SRPPrivateKey)this.hostKeyPair.getPrivate()).getX();
        BigInteger S = A.multiply(v.modPow(u, this.N)).modPow(b, this.N);
        byte[] sBytes = Util.trim(S);
        IMessageDigest hash = this.srp.newDigest();
        hash.update(sBytes, 0, sBytes.length);
        this.K = new BigInteger(1, hash.digest());
        OutgoingMessage result = new OutgoingMessage();
        result.writeMPI(s);
        result.writeMPI(B);
        this.complete = true;
        return result;
    }
}


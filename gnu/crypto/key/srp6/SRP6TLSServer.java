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

public class SRP6TLSServer
extends SRP6KeyAgreement {
    private KeyPair hostKeyPair;
    private SRPAuthInfoProvider passwordDB;

    protected void engineInit(Map attributes) throws KeyAgreementException {
        this.rnd = (SecureRandom)attributes.get("gnu.crypto.srp6.ka.prng");
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
                return this.sendParameters(in);
            }
            case 1: {
                return this.computeSharedSecret(in);
            }
        }
        throw new IllegalStateException("unexpected state");
    }

    protected void engineReset() {
        this.hostKeyPair = null;
        super.engineReset();
    }

    private final OutgoingMessage sendParameters(IncomingMessage in) throws KeyAgreementException {
        Map configuration;
        Map credentials;
        String I = in.readString();
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
        try {
            String mode = (String)credentials.get("srp.config.ndx");
            configuration = this.passwordDB.getConfiguration(mode);
        }
        catch (IOException x) {
            throw new KeyAgreementException("computeSharedSecret()", x);
        }
        this.N = new BigInteger(1, Util.fromBase64((String)configuration.get("srp.N")));
        this.g = new BigInteger(1, Util.fromBase64((String)configuration.get("srp.g")));
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
        OutgoingMessage result = new OutgoingMessage();
        result.writeMPI(this.N);
        result.writeMPI(this.g);
        result.writeMPI(s);
        result.writeMPI(B);
        return result;
    }

    protected OutgoingMessage computeSharedSecret(IncomingMessage in) throws KeyAgreementException {
        BigInteger S;
        BigInteger A = in.readMPI();
        if (A.mod(this.N).equals(BigInteger.ZERO)) {
            throw new KeyAgreementException("illegal value for A");
        }
        BigInteger B = ((SRPPublicKey)this.hostKeyPair.getPublic()).getY();
        BigInteger u = this.uValue(A, B);
        BigInteger b = ((SRPPrivateKey)this.hostKeyPair.getPrivate()).getX();
        BigInteger v = ((SRPPrivateKey)this.hostKeyPair.getPrivate()).getV();
        this.K = S = A.multiply(v.modPow(u, this.N)).modPow(b, this.N);
        this.complete = true;
        return null;
    }
}


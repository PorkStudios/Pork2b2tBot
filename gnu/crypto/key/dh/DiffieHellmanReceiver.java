/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.key.dh.DiffieHellmanKeyAgreement;
import gnu.crypto.prng.IRandom;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;

public class DiffieHellmanReceiver
extends DiffieHellmanKeyAgreement {
    private BigInteger y;

    protected void engineInit(Map attributes) throws KeyAgreementException {
        Object random = attributes.get("gnu.crypto.dh.ka.prng");
        this.rnd = null;
        this.irnd = null;
        if (random instanceof SecureRandom) {
            this.rnd = (SecureRandom)random;
        } else if (random instanceof IRandom) {
            this.irnd = (IRandom)random;
        }
        this.ownerKey = (DHPrivateKey)attributes.get("gnu.crypto.dh.ka.owner.private.key");
        if (this.ownerKey == null) {
            throw new KeyAgreementException("missing owner's private key");
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

    private final OutgoingMessage computeSharedSecret(IncomingMessage in) throws KeyAgreementException {
        BigInteger m1 = in.readMPI();
        if (m1 == null) {
            throw new KeyAgreementException("missing message (1)");
        }
        BigInteger p = this.ownerKey.getParams().getP();
        BigInteger g = this.ownerKey.getParams().getG();
        BigInteger p_minus_2 = p.subtract(TWO);
        byte[] xBytes = new byte[(p_minus_2.bitLength() + 7) / 8];
        do {
            this.nextRandomBytes(xBytes);
            this.y = new BigInteger(1, xBytes);
        } while (this.y.compareTo(TWO) < 0 || this.y.compareTo(p_minus_2) > 0);
        this.ZZ = m1.modPow(this.y, p);
        this.complete = true;
        OutgoingMessage result = new OutgoingMessage();
        result.writeMPI(g.modPow(this.y, p));
        return result;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import gnu.crypto.key.IncomingMessage;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.key.OutgoingMessage;
import gnu.crypto.key.dh.ElGamalKeyAgreement;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;

public class ElGamalReceiver
extends ElGamalKeyAgreement {
    private DHPrivateKey B;

    protected void engineInit(Map attributes) throws KeyAgreementException {
        this.rnd = (SecureRandom)attributes.get("gnu.crypto.elgamal.ka.prng");
        this.B = (DHPrivateKey)attributes.get("gnu.crypto.elgamal.ka.recipient.private.key");
        if (this.B == null) {
            throw new KeyAgreementException("missing recipient private key");
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
        this.ZZ = m1.modPow(this.B.getX(), this.B.getParams().getP());
        this.complete = true;
        return null;
    }
}


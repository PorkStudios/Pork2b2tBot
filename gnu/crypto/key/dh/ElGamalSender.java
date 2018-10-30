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
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class ElGamalSender
extends ElGamalKeyAgreement {
    private DHPublicKey B;

    protected void engineInit(Map attributes) throws KeyAgreementException {
        this.rnd = (SecureRandom)attributes.get("gnu.crypto.elgamal.ka.prng");
        this.B = (DHPublicKey)attributes.get("gnu.crypto.elgamal.ka.recipient.public.key");
        if (this.B == null) {
            throw new KeyAgreementException("missing recipient public key");
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
        BigInteger x;
        BigInteger p = this.B.getParams().getP();
        BigInteger g = this.B.getParams().getG();
        BigInteger yb = this.B.getY();
        BigInteger p_minus_2 = p.subtract(TWO);
        byte[] xBytes = new byte[(p_minus_2.bitLength() + 7) / 8];
        do {
            this.nextRandomBytes(xBytes);
        } while ((x = new BigInteger(1, xBytes)).compareTo(TWO) >= 0 && x.compareTo(p_minus_2) <= 0);
        OutgoingMessage result = new OutgoingMessage();
        result.writeMPI(g.modPow(x, p));
        this.ZZ = yb.modPow(x, p);
        this.complete = true;
        return result;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import gnu.crypto.key.BaseKeyAgreementParty;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.util.Util;
import java.math.BigInteger;

public abstract class ElGamalKeyAgreement
extends BaseKeyAgreementParty {
    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.elgamal.ka.prng";
    public static final String KA_ELGAMAL_RECIPIENT_PRIVATE_KEY = "gnu.crypto.elgamal.ka.recipient.private.key";
    public static final String KA_ELGAMAL_RECIPIENT_PUBLIC_KEY = "gnu.crypto.elgamal.ka.recipient.public.key";
    protected BigInteger ZZ;

    protected byte[] engineSharedSecret() throws KeyAgreementException {
        return Util.trim(this.ZZ);
    }

    protected void engineReset() {
        this.ZZ = null;
    }

    protected ElGamalKeyAgreement() {
        super("elgamal");
    }
}


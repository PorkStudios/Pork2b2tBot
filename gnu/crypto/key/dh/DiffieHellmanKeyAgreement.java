/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dh;

import gnu.crypto.key.BaseKeyAgreementParty;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.util.Util;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPrivateKey;

public abstract class DiffieHellmanKeyAgreement
extends BaseKeyAgreementParty {
    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.dh.ka.prng";
    public static final String KA_DIFFIE_HELLMAN_OWNER_PRIVATE_KEY = "gnu.crypto.dh.ka.owner.private.key";
    protected DHPrivateKey ownerKey;
    protected BigInteger ZZ;

    protected byte[] engineSharedSecret() throws KeyAgreementException {
        return Util.trim(this.ZZ);
    }

    protected void engineReset() {
        this.ownerKey = null;
        this.ZZ = null;
    }

    protected DiffieHellmanKeyAgreement() {
        super("dh");
    }
}


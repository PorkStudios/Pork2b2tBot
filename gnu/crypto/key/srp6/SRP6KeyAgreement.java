/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.srp6;

import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.key.BaseKeyAgreementParty;
import gnu.crypto.key.KeyAgreementException;
import gnu.crypto.sasl.srp.SRP;
import gnu.crypto.util.Util;
import java.math.BigInteger;

public abstract class SRP6KeyAgreement
extends BaseKeyAgreementParty {
    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.srp6.ka.prng";
    public static final String SHARED_MODULUS = "gnu.crypto.srp6.ka.N";
    public static final String GENERATOR = "gnu.crypto.srp6.ka.g";
    public static final String HASH_FUNCTION = "gnu.crypto.srp6.ka.H";
    public static final String USER_IDENTITY = "gnu.crypto.srp6.ka.I";
    public static final String USER_PASSWORD = "gnu.crypto.srp6.ka.p";
    public static final String HOST_PASSWORD_DB = "gnu.crypto.srp6.ka.password.db";
    protected static final BigInteger THREE = BigInteger.valueOf(3);
    protected SRP srp;
    protected BigInteger N;
    protected BigInteger g;
    protected BigInteger K;

    protected byte[] engineSharedSecret() throws KeyAgreementException {
        return Util.trim(this.K);
    }

    protected void engineReset() {
        this.srp = null;
        this.N = null;
        this.g = null;
        this.K = null;
    }

    protected BigInteger uValue(BigInteger A, BigInteger B) {
        IMessageDigest hash = this.srp.newDigest();
        byte[] b = Util.trim(A);
        hash.update(b, 0, b.length);
        b = Util.trim(B);
        hash.update(b, 0, b.length);
        return new BigInteger(1, hash.digest());
    }

    protected SRP6KeyAgreement() {
        super("srp6");
    }
}


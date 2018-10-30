/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.mac.IMac;
import gnu.crypto.mac.MacFactory;
import gnu.crypto.sasl.srp.KDF;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.security.sasl.SaslException;

public final class IALG
implements Cloneable {
    private IMac hmac;

    static final synchronized IALG getInstance(String algorithm) throws SaslException {
        IMac hmac = MacFactory.getInstance(algorithm);
        if (hmac == null) {
            throw new SaslException("getInstance()", new NoSuchAlgorithmException(algorithm));
        }
        return new IALG(hmac);
    }

    public final Object clone() throws CloneNotSupportedException {
        return new IALG((IMac)this.hmac.clone());
    }

    public final void init(KDF kdf) throws SaslException {
        try {
            byte[] sk = kdf.derive(this.hmac.macSize());
            HashMap<String, byte[]> map = new HashMap<String, byte[]>();
            map.put("gnu.crypto.mac.key.material", sk);
            this.hmac.init(map);
        }
        catch (InvalidKeyException x) {
            throw new SaslException("getInstance()", x);
        }
    }

    public final void update(byte[] data) {
        this.hmac.update(data, 0, data.length);
    }

    public final void update(byte[] data, int offset, int length) {
        this.hmac.update(data, offset, length);
    }

    public final byte[] doFinal() {
        return this.hmac.digest();
    }

    public final int length() {
        return this.hmac.macSize();
    }

    private IALG(IMac hmac) {
        this.hmac = hmac;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.mac;

import gnu.crypto.mac.IMac;
import gnu.crypto.mac.MacFactory;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.MacSpi;

class MacAdapter
extends MacSpi {
    protected IMac mac;
    protected Map attributes;

    public Object clone() {
        return new MacAdapter(this);
    }

    protected byte[] engineDoFinal() {
        byte[] result = this.mac.digest();
        this.engineReset();
        return result;
    }

    protected int engineGetMacLength() {
        return this.mac.macSize();
    }

    protected void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!key.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("unknown key format " + key.getFormat());
        }
        this.attributes.put("gnu.crypto.mac.key.material", key.getEncoded());
        this.mac.reset();
        this.mac.init(this.attributes);
    }

    protected void engineReset() {
        this.mac.reset();
    }

    protected void engineUpdate(byte b) {
        this.mac.update(b);
    }

    protected void engineUpdate(byte[] in, int off, int len) {
        this.mac.update(in, off, len);
    }

    private MacAdapter(MacAdapter that) {
        this.mac = (IMac)that.mac.clone();
        this.attributes = new HashMap(that.attributes);
    }

    protected MacAdapter(String name) {
        this.mac = MacFactory.getInstance(name);
        this.attributes = new HashMap();
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.sig;

import gnu.crypto.jce.sig.KeyPairGeneratorAdapter;
import gnu.crypto.key.IKeyPairGenerator;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.util.HashMap;
import java.util.Map;

public class DSSKeyPairGeneratorSpi
extends KeyPairGeneratorAdapter {
    public void initialize(int keysize, SecureRandom random) {
        HashMap<String, Serializable> attributes = new HashMap<String, Serializable>();
        attributes.put("gnu.crypto.dss.L", new Integer(keysize));
        if (random != null) {
            attributes.put("gnu.crypto.dss.prng", random);
        }
        this.adaptee.setup(attributes);
    }

    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        if (params != null) {
            if (!(params instanceof DSAParameterSpec)) {
                throw new InvalidAlgorithmParameterException("params");
            }
            attributes.put("gnu.crypto.dss.params", params);
        }
        if (random != null) {
            attributes.put("gnu.crypto.dss.prng", random);
        }
        this.adaptee.setup(attributes);
    }

    public DSSKeyPairGeneratorSpi() {
        super("dss");
    }
}


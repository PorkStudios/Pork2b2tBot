/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key;

import gnu.crypto.key.IKeyPairGenerator;
import gnu.crypto.key.dh.GnuDHKeyPairGenerator;
import gnu.crypto.key.dss.DSSKeyPairGenerator;
import gnu.crypto.key.rsa.RSAKeyPairGenerator;
import gnu.crypto.key.srp6.SRPKeyPairGenerator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KeyPairGeneratorFactory {
    public static IKeyPairGenerator getInstance(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        IKeyPairGenerator result = null;
        if (name.equalsIgnoreCase("dsa") || name.equals("dss")) {
            result = new DSSKeyPairGenerator();
        } else if (name.equalsIgnoreCase("rsa")) {
            result = new RSAKeyPairGenerator();
        } else if (name.equalsIgnoreCase("dh")) {
            result = new GnuDHKeyPairGenerator();
        } else if (name.equalsIgnoreCase("srp")) {
            result = new SRPKeyPairGenerator();
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.add("dss");
        hs.add("rsa");
        hs.add("dh");
        hs.add("srp");
        return Collections.unmodifiableSet(hs);
    }

    private KeyPairGeneratorFactory() {
    }
}


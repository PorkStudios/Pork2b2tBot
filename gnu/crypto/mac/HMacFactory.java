/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.Registry;
import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.mac.HMac;
import gnu.crypto.mac.IMac;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HMacFactory
implements Registry {
    public static IMac getInstance(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        if (!(name = name.toLowerCase()).startsWith("hmac-")) {
            return null;
        }
        HMac result = new HMac(HashFactory.getInstance(name = name.substring("hmac-".length()).trim()));
        if (result != null && !result.selfTest()) {
            throw new InternalError(result.name());
        }
        return result;
    }

    public static final Set getNames() {
        Set hashNames = HashFactory.getNames();
        HashSet<String> hs = new HashSet<String>();
        Iterator it = hashNames.iterator();
        while (it.hasNext()) {
            hs.add("hmac-" + (String)it.next());
        }
        return Collections.unmodifiableSet(hs);
    }

    private HMacFactory() {
    }
}


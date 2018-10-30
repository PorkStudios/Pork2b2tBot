/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.Registry;
import gnu.crypto.mac.BaseMac;
import gnu.crypto.mac.HMacFactory;
import gnu.crypto.mac.IMac;
import gnu.crypto.mac.TMMH16;
import gnu.crypto.mac.UHash32;
import gnu.crypto.mac.UMac32;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MacFactory
implements Registry {
    public static IMac getInstance(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        if ((name = name.toLowerCase()).startsWith("hmac-")) {
            return HMacFactory.getInstance(name);
        }
        BaseMac result = null;
        if (name.equalsIgnoreCase("uhash32")) {
            result = new UHash32();
        } else if (name.equalsIgnoreCase("umac32")) {
            result = new UMac32();
        } else if (name.equalsIgnoreCase("tmmh16")) {
            result = new TMMH16();
        }
        if (result != null && !result.selfTest()) {
            throw new InternalError(result.name());
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.addAll(HMacFactory.getNames());
        hs.add("uhash32");
        hs.add("umac32");
        hs.add("tmmh16");
        return Collections.unmodifiableSet(hs);
    }

    private MacFactory() {
    }
}


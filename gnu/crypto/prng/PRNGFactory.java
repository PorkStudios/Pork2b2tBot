/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.prng;

import gnu.crypto.Registry;
import gnu.crypto.mac.HMacFactory;
import gnu.crypto.mac.IMac;
import gnu.crypto.mac.MacFactory;
import gnu.crypto.prng.ARCFour;
import gnu.crypto.prng.BasePRNG;
import gnu.crypto.prng.ICMGenerator;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.MDGenerator;
import gnu.crypto.prng.PBKDF2;
import gnu.crypto.prng.UMacGenerator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PRNGFactory
implements Registry {
    public static final IRandom getInstance(String prng) {
        if (prng == null) {
            return null;
        }
        prng = prng.trim();
        BasePRNG result = null;
        if (prng.equalsIgnoreCase("arcfour") || prng.equalsIgnoreCase("rc4")) {
            result = new ARCFour();
        } else if (prng.equalsIgnoreCase("icm")) {
            result = new ICMGenerator();
        } else if (prng.equalsIgnoreCase("md")) {
            result = new MDGenerator();
        } else if (prng.equalsIgnoreCase("umac-kdf")) {
            result = new UMacGenerator();
        } else if (prng.toLowerCase().startsWith("pbkdf2-")) {
            String macName = prng.substring("pbkdf2-".length());
            IMac mac = MacFactory.getInstance(macName);
            if (mac == null) {
                return null;
            }
            result = new PBKDF2(mac);
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.add("icm");
        hs.add("md");
        hs.add("umac-kdf");
        Iterator it = HMacFactory.getNames().iterator();
        while (it.hasNext()) {
            hs.add("pbkdf2-" + (String)it.next());
        }
        return Collections.unmodifiableSet(hs);
    }

    private PRNGFactory() {
    }
}


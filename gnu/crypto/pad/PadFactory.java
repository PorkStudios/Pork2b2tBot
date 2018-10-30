/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.pad;

import gnu.crypto.Registry;
import gnu.crypto.pad.BasePad;
import gnu.crypto.pad.IPad;
import gnu.crypto.pad.PKCS1_V1_5;
import gnu.crypto.pad.PKCS7;
import gnu.crypto.pad.TBC;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PadFactory
implements Registry {
    public static final IPad getInstance(String pad) {
        if (pad == null) {
            return null;
        }
        pad = pad.trim();
        BasePad result = null;
        if (pad.equalsIgnoreCase("pkcs7")) {
            result = new PKCS7();
        } else if (pad.equalsIgnoreCase("tbc")) {
            result = new TBC();
        } else if (pad.equalsIgnoreCase("eme-pkcs1-v1.5")) {
            result = new PKCS1_V1_5();
        }
        if (result != null && !result.selfTest()) {
            throw new InternalError(result.name());
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.add("pkcs7");
        hs.add("tbc");
        hs.add("eme-pkcs1-v1.5");
        return Collections.unmodifiableSet(hs);
    }

    private PadFactory() {
    }
}


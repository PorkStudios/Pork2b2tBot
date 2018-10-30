/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.util.Util;
import java.security.MessageDigest;

public class SaslUtil {
    public static final boolean validEmailAddress(String address) {
        boolean bl = false;
        if (address.indexOf("@") != -1) {
            bl = true;
        }
        return bl;
    }

    public static final String dump(MessageDigest md) {
        String result;
        try {
            result = Util.dumpString(((MessageDigest)md.clone()).digest());
        }
        catch (Exception ignored) {
            result = "...";
        }
        return result;
    }

    private SaslUtil() {
    }
}


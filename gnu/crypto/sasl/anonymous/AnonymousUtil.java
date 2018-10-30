/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.anonymous;

import gnu.crypto.sasl.SaslUtil;

public class AnonymousUtil {
    static boolean isValidTraceInformation(String traceInformation) {
        if (traceInformation == null) {
            return false;
        }
        if (traceInformation.length() == 0) {
            return true;
        }
        if (SaslUtil.validEmailAddress(traceInformation)) {
            return true;
        }
        return AnonymousUtil.isValidToken(traceInformation);
    }

    static boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }
        if (token.length() == 0) {
            return false;
        }
        if (token.length() > 255) {
            return false;
        }
        if (token.indexOf(64) != -1) {
            return false;
        }
        int i = 0;
        while (i < token.length()) {
            char c = token.charAt(i);
            if (c < ' ' || c > '~') {
                return false;
            }
            ++i;
        }
        return true;
    }

    private AnonymousUtil() {
    }
}


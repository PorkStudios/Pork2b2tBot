/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import java.util.BitSet;

@Deprecated
final class CookieUtil {
    private static final BitSet VALID_COOKIE_VALUE_OCTETS = CookieUtil.validCookieValueOctets();
    private static final BitSet VALID_COOKIE_NAME_OCTETS = CookieUtil.validCookieNameOctets(VALID_COOKIE_VALUE_OCTETS);

    private static BitSet validCookieValueOctets() {
        BitSet bits = new BitSet(8);
        for (int i = 35; i < 127; ++i) {
            bits.set(i);
        }
        bits.set(34, false);
        bits.set(44, false);
        bits.set(59, false);
        bits.set(92, false);
        return bits;
    }

    private static BitSet validCookieNameOctets(BitSet validCookieValueOctets) {
        BitSet bits = new BitSet(8);
        bits.or(validCookieValueOctets);
        bits.set(40, false);
        bits.set(41, false);
        bits.set(60, false);
        bits.set(62, false);
        bits.set(64, false);
        bits.set(58, false);
        bits.set(47, false);
        bits.set(91, false);
        bits.set(93, false);
        bits.set(63, false);
        bits.set(61, false);
        bits.set(123, false);
        bits.set(125, false);
        bits.set(32, false);
        bits.set(9, false);
        return bits;
    }

    static int firstInvalidCookieNameOctet(CharSequence cs) {
        return CookieUtil.firstInvalidOctet(cs, VALID_COOKIE_NAME_OCTETS);
    }

    static int firstInvalidCookieValueOctet(CharSequence cs) {
        return CookieUtil.firstInvalidOctet(cs, VALID_COOKIE_VALUE_OCTETS);
    }

    static int firstInvalidOctet(CharSequence cs, BitSet bits) {
        for (int i = 0; i < cs.length(); ++i) {
            char c = cs.charAt(i);
            if (bits.get(c)) continue;
            return i;
        }
        return -1;
    }

    static CharSequence unwrapValue(CharSequence cs) {
        int len = cs.length();
        if (len > 0 && cs.charAt(0) == '\"') {
            if (len >= 2 && cs.charAt(len - 1) == '\"') {
                return len == 2 ? "" : cs.subSequence(1, len - 1);
            }
            return null;
        }
        return cs;
    }

    private CookieUtil() {
    }
}


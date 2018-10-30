/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.InternalThreadLocalMap;
import java.util.BitSet;

final class CookieUtil {
    private static final BitSet VALID_COOKIE_NAME_OCTETS = CookieUtil.validCookieNameOctets();
    private static final BitSet VALID_COOKIE_VALUE_OCTETS = CookieUtil.validCookieValueOctets();
    private static final BitSet VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS = CookieUtil.validCookieAttributeValueOctets();

    private static BitSet validCookieNameOctets() {
        int[] separators;
        BitSet bits = new BitSet();
        for (int i = 32; i < 127; ++i) {
            bits.set(i);
        }
        for (int separator : separators = new int[]{40, 41, 60, 62, 64, 44, 59, 58, 92, 34, 47, 91, 93, 63, 61, 123, 125, 32, 9}) {
            bits.set(separator, false);
        }
        return bits;
    }

    private static BitSet validCookieValueOctets() {
        int i;
        BitSet bits = new BitSet();
        bits.set(33);
        for (i = 35; i <= 43; ++i) {
            bits.set(i);
        }
        for (i = 45; i <= 58; ++i) {
            bits.set(i);
        }
        for (i = 60; i <= 91; ++i) {
            bits.set(i);
        }
        for (i = 93; i <= 126; ++i) {
            bits.set(i);
        }
        return bits;
    }

    private static BitSet validCookieAttributeValueOctets() {
        BitSet bits = new BitSet();
        for (int i = 32; i < 127; ++i) {
            bits.set(i);
        }
        bits.set(59, false);
        return bits;
    }

    static StringBuilder stringBuilder() {
        return InternalThreadLocalMap.get().stringBuilder();
    }

    static String stripTrailingSeparatorOrNull(StringBuilder buf) {
        return buf.length() == 0 ? null : CookieUtil.stripTrailingSeparator(buf);
    }

    static String stripTrailingSeparator(StringBuilder buf) {
        if (buf.length() > 0) {
            buf.setLength(buf.length() - 2);
        }
        return buf.toString();
    }

    static void add(StringBuilder sb, String name, long val) {
        sb.append(name);
        sb.append('=');
        sb.append(val);
        sb.append(';');
        sb.append(' ');
    }

    static void add(StringBuilder sb, String name, String val) {
        sb.append(name);
        sb.append('=');
        sb.append(val);
        sb.append(';');
        sb.append(' ');
    }

    static void add(StringBuilder sb, String name) {
        sb.append(name);
        sb.append(';');
        sb.append(' ');
    }

    static void addQuoted(StringBuilder sb, String name, String val) {
        if (val == null) {
            val = "";
        }
        sb.append(name);
        sb.append('=');
        sb.append('\"');
        sb.append(val);
        sb.append('\"');
        sb.append(';');
        sb.append(' ');
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

    static String validateAttributeValue(String name, String value) {
        if (value == null) {
            return null;
        }
        if ((value = value.trim()).isEmpty()) {
            return null;
        }
        int i = CookieUtil.firstInvalidOctet(value, VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS);
        if (i != -1) {
            throw new IllegalArgumentException(name + " contains the prohibited characters: " + value.charAt(i));
        }
        return value;
    }

    private CookieUtil() {
    }
}


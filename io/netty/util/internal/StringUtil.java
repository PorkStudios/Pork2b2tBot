/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class StringUtil {
    public static final String EMPTY_STRING = "";
    public static final String NEWLINE;
    public static final char DOUBLE_QUOTE = '\"';
    public static final char COMMA = ',';
    public static final char LINE_FEED = '\n';
    public static final char CARRIAGE_RETURN = '\r';
    public static final char TAB = '\t';
    public static final char SPACE = ' ';
    private static final String[] BYTE2HEX_PAD;
    private static final String[] BYTE2HEX_NOPAD;
    private static final int CSV_NUMBER_ESCAPE_CHARACTERS = 7;
    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    private StringUtil() {
    }

    public static String substringAfter(String value, char delim) {
        int pos = value.indexOf(delim);
        if (pos >= 0) {
            return value.substring(pos + 1);
        }
        return null;
    }

    public static boolean commonSuffixOfLength(String s, String p, int len) {
        return s != null && p != null && len >= 0 && s.regionMatches(s.length() - len, p, p.length() - len, len);
    }

    public static String byteToHexStringPadded(int value) {
        return BYTE2HEX_PAD[value & 255];
    }

    public static <T extends Appendable> T byteToHexStringPadded(T buf, int value) {
        try {
            buf.append(StringUtil.byteToHexStringPadded(value));
        }
        catch (IOException e) {
            PlatformDependent.throwException(e);
        }
        return buf;
    }

    public static String toHexStringPadded(byte[] src) {
        return StringUtil.toHexStringPadded(src, 0, src.length);
    }

    public static String toHexStringPadded(byte[] src, int offset, int length) {
        return StringUtil.toHexStringPadded(new StringBuilder(length << 1), src, offset, length).toString();
    }

    public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src) {
        return StringUtil.toHexStringPadded(dst, src, 0, src.length);
    }

    public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src, int offset, int length) {
        int end = offset + length;
        for (int i = offset; i < end; ++i) {
            StringUtil.byteToHexStringPadded(dst, src[i]);
        }
        return dst;
    }

    public static String byteToHexString(int value) {
        return BYTE2HEX_NOPAD[value & 255];
    }

    public static <T extends Appendable> T byteToHexString(T buf, int value) {
        try {
            buf.append(StringUtil.byteToHexString(value));
        }
        catch (IOException e) {
            PlatformDependent.throwException(e);
        }
        return buf;
    }

    public static String toHexString(byte[] src) {
        return StringUtil.toHexString(src, 0, src.length);
    }

    public static String toHexString(byte[] src, int offset, int length) {
        return StringUtil.toHexString(new StringBuilder(length << 1), src, offset, length).toString();
    }

    public static <T extends Appendable> T toHexString(T dst, byte[] src) {
        return StringUtil.toHexString(dst, src, 0, src.length);
    }

    public static <T extends Appendable> T toHexString(T dst, byte[] src, int offset, int length) {
        int i;
        assert (length >= 0);
        if (length == 0) {
            return dst;
        }
        int end = offset + length;
        int endMinusOne = end - 1;
        for (i = offset; i < endMinusOne && src[i] == 0; ++i) {
        }
        StringUtil.byteToHexString(dst, src[i++]);
        int remaining = end - i;
        StringUtil.toHexStringPadded(dst, src, i, remaining);
        return dst;
    }

    public static int decodeHexNibble(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 65 + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 97 + 10;
        }
        return -1;
    }

    public static byte decodeHexByte(CharSequence s, int pos) {
        int hi = StringUtil.decodeHexNibble(s.charAt(pos));
        int lo = StringUtil.decodeHexNibble(s.charAt(pos + 1));
        if (hi == -1 || lo == -1) {
            throw new IllegalArgumentException(String.format("invalid hex byte '%s' at index %d of '%s'", s.subSequence(pos, pos + 2), pos, s));
        }
        return (byte)((hi << 4) + lo);
    }

    public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) {
        if (length < 0 || (length & 1) != 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length == 0) {
            return EmptyArrays.EMPTY_BYTES;
        }
        byte[] bytes = new byte[length >>> 1];
        for (int i = 0; i < length; i += 2) {
            bytes[i >>> 1] = StringUtil.decodeHexByte(hexDump, fromIndex + i);
        }
        return bytes;
    }

    public static byte[] decodeHexDump(CharSequence hexDump) {
        return StringUtil.decodeHexDump(hexDump, 0, hexDump.length());
    }

    public static String simpleClassName(Object o) {
        if (o == null) {
            return "null_object";
        }
        return StringUtil.simpleClassName(o.getClass());
    }

    public static String simpleClassName(Class<?> clazz) {
        String className = ObjectUtil.checkNotNull(clazz, "clazz").getName();
        int lastDotIdx = className.lastIndexOf(46);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }

    public static CharSequence escapeCsv(CharSequence value) {
        return StringUtil.escapeCsv(value, false);
    }

    public static CharSequence escapeCsv(CharSequence value, boolean trimWhiteSpace) {
        int last;
        int start;
        int length = ObjectUtil.checkNotNull(value, "value").length();
        if (trimWhiteSpace) {
            start = StringUtil.indexOfFirstNonOwsChar(value, length);
            last = StringUtil.indexOfLastNonOwsChar(value, start, length);
        } else {
            start = 0;
            last = length - 1;
        }
        if (start > last) {
            return EMPTY_STRING;
        }
        int firstUnescapedSpecial = -1;
        boolean quoted = false;
        if (StringUtil.isDoubleQuote(value.charAt(start))) {
            boolean bl = quoted = StringUtil.isDoubleQuote(value.charAt(last)) && last > start;
            if (quoted) {
                ++start;
                --last;
            } else {
                firstUnescapedSpecial = start;
            }
        }
        if (firstUnescapedSpecial < 0) {
            int i;
            if (quoted) {
                for (i = start; i <= last; ++i) {
                    if (!StringUtil.isDoubleQuote(value.charAt(i))) continue;
                    if (i == last || !StringUtil.isDoubleQuote(value.charAt(i + 1))) {
                        firstUnescapedSpecial = i;
                        break;
                    }
                    ++i;
                }
            } else {
                for (i = start; i <= last; ++i) {
                    char c = value.charAt(i);
                    if (c == '\n' || c == '\r' || c == ',') {
                        firstUnescapedSpecial = i;
                        break;
                    }
                    if (!StringUtil.isDoubleQuote(c)) continue;
                    if (i == last || !StringUtil.isDoubleQuote(value.charAt(i + 1))) {
                        firstUnescapedSpecial = i;
                        break;
                    }
                    ++i;
                }
            }
            if (firstUnescapedSpecial < 0) {
                return quoted ? value.subSequence(start - 1, last + 2) : value.subSequence(start, last + 1);
            }
        }
        StringBuilder result = new StringBuilder(last - start + 1 + 7);
        result.append('\"').append(value, start, firstUnescapedSpecial);
        for (int i = firstUnescapedSpecial; i <= last; ++i) {
            char c = value.charAt(i);
            if (StringUtil.isDoubleQuote(c)) {
                result.append('\"');
                if (i < last && StringUtil.isDoubleQuote(value.charAt(i + 1))) {
                    ++i;
                }
            }
            result.append(c);
        }
        return result.append('\"');
    }

    public static CharSequence unescapeCsv(CharSequence value) {
        boolean quoted;
        int length = ObjectUtil.checkNotNull(value, "value").length();
        if (length == 0) {
            return value;
        }
        int last = length - 1;
        boolean bl = quoted = StringUtil.isDoubleQuote(value.charAt(0)) && StringUtil.isDoubleQuote(value.charAt(last)) && length != 1;
        if (!quoted) {
            StringUtil.validateCsvFormat(value);
            return value;
        }
        StringBuilder unescaped = InternalThreadLocalMap.get().stringBuilder();
        for (int i = 1; i < last; ++i) {
            char current = value.charAt(i);
            if (current == '\"') {
                if (StringUtil.isDoubleQuote(value.charAt(i + 1)) && i + 1 != last) {
                    ++i;
                } else {
                    throw StringUtil.newInvalidEscapedCsvFieldException(value, i);
                }
            }
            unescaped.append(current);
        }
        return unescaped.toString();
    }

    public static List<CharSequence> unescapeCsvFields(CharSequence value) {
        ArrayList<CharSequence> unescaped = new ArrayList<CharSequence>(2);
        StringBuilder current = InternalThreadLocalMap.get().stringBuilder();
        boolean quoted = false;
        int last = value.length() - 1;
        block8 : for (int i = 0; i <= last; ++i) {
            char c = value.charAt(i);
            if (quoted) {
                switch (c) {
                    case '\"': {
                        char next;
                        if (i == last) {
                            unescaped.add(current.toString());
                            return unescaped;
                        }
                        if ((next = value.charAt(++i)) == '\"') {
                            current.append('\"');
                            break;
                        }
                        if (next == ',') {
                            quoted = false;
                            unescaped.add(current.toString());
                            current.setLength(0);
                            break;
                        }
                        throw StringUtil.newInvalidEscapedCsvFieldException(value, i - 1);
                    }
                    default: {
                        current.append(c);
                        break;
                    }
                }
                continue;
            }
            switch (c) {
                case ',': {
                    unescaped.add(current.toString());
                    current.setLength(0);
                    continue block8;
                }
                case '\"': {
                    if (current.length() == 0) {
                        quoted = true;
                        continue block8;
                    }
                }
                case '\n': 
                case '\r': {
                    throw StringUtil.newInvalidEscapedCsvFieldException(value, i);
                }
                default: {
                    current.append(c);
                }
            }
        }
        if (quoted) {
            throw StringUtil.newInvalidEscapedCsvFieldException(value, last);
        }
        unescaped.add(current.toString());
        return unescaped;
    }

    private static void validateCsvFormat(CharSequence value) {
        int length = value.length();
        for (int i = 0; i < length; ++i) {
            switch (value.charAt(i)) {
                case '\n': 
                case '\r': 
                case '\"': 
                case ',': {
                    throw StringUtil.newInvalidEscapedCsvFieldException(value, i);
                }
            }
        }
    }

    private static IllegalArgumentException newInvalidEscapedCsvFieldException(CharSequence value, int index) {
        return new IllegalArgumentException("invalid escaped CSV field: " + value + " index: " + index);
    }

    public static int length(String s) {
        return s == null ? 0 : s.length();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static int indexOfNonWhiteSpace(CharSequence seq, int offset) {
        while (offset < seq.length()) {
            if (!Character.isWhitespace(seq.charAt(offset))) {
                return offset;
            }
            ++offset;
        }
        return -1;
    }

    public static boolean isSurrogate(char c) {
        return c >= '\ud800' && c <= '\udfff';
    }

    private static boolean isDoubleQuote(char c) {
        return c == '\"';
    }

    public static boolean endsWith(CharSequence s, char c) {
        int len = s.length();
        return len > 0 && s.charAt(len - 1) == c;
    }

    public static CharSequence trimOws(CharSequence value) {
        int length = value.length();
        if (length == 0) {
            return value;
        }
        int start = StringUtil.indexOfFirstNonOwsChar(value, length);
        int end = StringUtil.indexOfLastNonOwsChar(value, start, length);
        return start == 0 && end == length - 1 ? value : value.subSequence(start, end + 1);
    }

    private static int indexOfFirstNonOwsChar(CharSequence value, int length) {
        int i;
        for (i = 0; i < length && StringUtil.isOws(value.charAt(i)); ++i) {
        }
        return i;
    }

    private static int indexOfLastNonOwsChar(CharSequence value, int start, int length) {
        int i;
        for (i = length - 1; i > start && StringUtil.isOws(value.charAt(i)); --i) {
        }
        return i;
    }

    private static boolean isOws(char c) {
        return c == ' ' || c == '\t';
    }

    static {
        int i;
        NEWLINE = SystemPropertyUtil.get("line.separator", "\n");
        BYTE2HEX_PAD = new String[256];
        BYTE2HEX_NOPAD = new String[256];
        for (i = 0; i < 10; ++i) {
            StringUtil.BYTE2HEX_PAD[i] = "0" + i;
            StringUtil.BYTE2HEX_NOPAD[i] = String.valueOf(i);
        }
        while (i < 16) {
            char c = (char)(97 + i - 10);
            StringUtil.BYTE2HEX_PAD[i] = "0" + c;
            StringUtil.BYTE2HEX_NOPAD[i] = String.valueOf(c);
            ++i;
        }
        while (i < BYTE2HEX_PAD.length) {
            String str;
            StringUtil.BYTE2HEX_PAD[i] = str = Integer.toHexString(i);
            StringUtil.BYTE2HEX_NOPAD[i] = str;
            ++i;
        }
    }
}


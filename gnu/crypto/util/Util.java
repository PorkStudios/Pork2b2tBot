/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.util;

import java.math.BigInteger;

public class Util {
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final String BASE64_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz./";
    private static final char[] BASE64_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz./".toCharArray();

    public static String toString(byte[] ba) {
        return Util.toString(ba, 0, ba.length);
    }

    public static final String toString(byte[] ba, int offset, int length) {
        char[] buf = new char[length * 2];
        int i = 0;
        int j = 0;
        while (i < length) {
            byte k = ba[offset + i++];
            buf[j++] = HEX_DIGITS[k >>> 4 & 15];
            buf[j++] = HEX_DIGITS[k & 15];
        }
        return new String(buf);
    }

    public static String toReversedString(byte[] ba) {
        return Util.toReversedString(ba, 0, ba.length);
    }

    public static final String toReversedString(byte[] ba, int offset, int length) {
        char[] buf = new char[length * 2];
        int i = offset + length - 1;
        int j = 0;
        while (i >= offset) {
            byte k = ba[offset + i--];
            buf[j++] = HEX_DIGITS[k >>> 4 & 15];
            buf[j++] = HEX_DIGITS[k & 15];
        }
        return new String(buf);
    }

    public static byte[] toBytesFromString(String s) {
        int limit = s.length();
        byte[] result = new byte[(limit + 1) / 2];
        int i = 0;
        int j = 0;
        if (limit % 2 == 1) {
            result[j++] = (byte)Util.fromDigit(s.charAt(i++));
        }
        while (i < limit) {
            result[j] = (byte)(Util.fromDigit(s.charAt(i++)) << 4);
            byte[] arrby = result;
            int n = j++;
            arrby[n] = (byte)(arrby[n] | (byte)Util.fromDigit(s.charAt(i++)));
        }
        return result;
    }

    public static byte[] toReversedBytesFromString(String s) {
        int limit = s.length();
        byte[] result = new byte[(limit + 1) / 2];
        int i = 0;
        if (limit % 2 == 1) {
            result[i++] = (byte)Util.fromDigit(s.charAt(--limit));
        }
        while (limit > 0) {
            result[i] = (byte)Util.fromDigit(s.charAt(--limit));
            byte[] arrby = result;
            int n = i++;
            arrby[n] = (byte)(arrby[n] | (byte)(Util.fromDigit(s.charAt(--limit)) << 4));
        }
        return result;
    }

    public static int fromDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 65 + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 97 + 10;
        }
        throw new IllegalArgumentException("Invalid hexadecimal digit: " + c);
    }

    public static String toString(int n) {
        char[] buf = new char[8];
        int i = 7;
        while (i >= 0) {
            buf[i] = HEX_DIGITS[n & 15];
            n >>>= 4;
            --i;
        }
        return new String(buf);
    }

    public static String toString(int[] ia) {
        int length = ia.length;
        char[] buf = new char[length * 8];
        int i = 0;
        int j = 0;
        while (i < length) {
            int k = ia[i];
            buf[j++] = HEX_DIGITS[k >>> 28 & 15];
            buf[j++] = HEX_DIGITS[k >>> 24 & 15];
            buf[j++] = HEX_DIGITS[k >>> 20 & 15];
            buf[j++] = HEX_DIGITS[k >>> 16 & 15];
            buf[j++] = HEX_DIGITS[k >>> 12 & 15];
            buf[j++] = HEX_DIGITS[k >>> 8 & 15];
            buf[j++] = HEX_DIGITS[k >>> 4 & 15];
            buf[j++] = HEX_DIGITS[k & 15];
            ++i;
        }
        return new String(buf);
    }

    public static String toString(long n) {
        char[] b = new char[16];
        int i = 15;
        while (i >= 0) {
            b[i] = HEX_DIGITS[(int)(n & 15L)];
            n >>>= 4;
            --i;
        }
        return new String(b);
    }

    public static String toUnicodeString(byte[] ba) {
        return Util.toUnicodeString(ba, 0, ba.length);
    }

    public static final String toUnicodeString(byte[] ba, int offset, int length) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        int j = 0;
        sb.append('\n').append("\"");
        while (i < length) {
            sb.append("\\u");
            byte k = ba[offset + i++];
            sb.append(HEX_DIGITS[k >>> 4 & 15]);
            sb.append(HEX_DIGITS[k & 15]);
            k = ba[offset + i++];
            sb.append(HEX_DIGITS[k >>> 4 & 15]);
            sb.append(HEX_DIGITS[k & 15]);
            if (++j % 8 != 0) continue;
            sb.append("\"+").append('\n').append("\"");
        }
        sb.append("\"").append('\n');
        return sb.toString();
    }

    public static String toUnicodeString(int[] ia) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        int j = 0;
        sb.append('\n').append("\"");
        while (i < ia.length) {
            int k = ia[i++];
            sb.append("\\u");
            sb.append(HEX_DIGITS[k >>> 28 & 15]);
            sb.append(HEX_DIGITS[k >>> 24 & 15]);
            sb.append(HEX_DIGITS[k >>> 20 & 15]);
            sb.append(HEX_DIGITS[k >>> 16 & 15]);
            sb.append("\\u");
            sb.append(HEX_DIGITS[k >>> 12 & 15]);
            sb.append(HEX_DIGITS[k >>> 8 & 15]);
            sb.append(HEX_DIGITS[k >>> 4 & 15]);
            sb.append(HEX_DIGITS[k & 15]);
            if (++j % 4 != 0) continue;
            sb.append("\"+").append('\n').append("\"");
        }
        sb.append("\"").append('\n');
        return sb.toString();
    }

    public static byte[] toBytesFromUnicode(String s) {
        int limit = s.length() * 2;
        byte[] result = new byte[limit];
        int i = 0;
        while (i < limit) {
            int c = s.charAt(i >>> 1);
            result[i] = (byte)((i & 1) == 0 ? c >>> 8 : c);
            ++i;
        }
        return result;
    }

    public static String dumpString(byte[] data, int offset, int length, String m) {
        if (data == null) {
            return m + "null\n";
        }
        StringBuffer sb = new StringBuffer(length * 3);
        if (length > 32) {
            sb.append(m).append("Hexadecimal dump of ").append(length).append(" bytes...\n");
        }
        int end = offset + length;
        int l = Integer.toString(length).length();
        if (l < 4) {
            l = 4;
        }
        while (offset < end) {
            if (length > 32) {
                String s = "         " + offset;
                sb.append(m).append(s.substring(s.length() - l)).append(": ");
            }
            int i = 0;
            while (i < 32 && offset + i + 7 < end) {
                sb.append(Util.toString(data, offset + i, 8)).append(' ');
                i += 8;
            }
            if (i < 32) {
                while (i < 32 && offset + i < end) {
                    sb.append(Util.byteToString(data[offset + i]));
                    ++i;
                }
            }
            sb.append('\n');
            offset += 32;
        }
        return sb.toString();
    }

    public static String dumpString(byte[] data) {
        return data == null ? "null\n" : Util.dumpString(data, 0, data.length, "");
    }

    public static String dumpString(byte[] data, String m) {
        return data == null ? "null\n" : Util.dumpString(data, 0, data.length, m);
    }

    public static String dumpString(byte[] data, int offset, int length) {
        return Util.dumpString(data, offset, length, "");
    }

    public static String byteToString(int n) {
        char[] buf = new char[]{HEX_DIGITS[n >>> 4 & 15], HEX_DIGITS[n & 15]};
        return new String(buf);
    }

    public static final String toBase64(byte[] buffer) {
        int len = buffer.length;
        int pos = len % 3;
        int b0 = 0;
        byte b1 = 0;
        byte b2 = 0;
        switch (pos) {
            case 1: {
                b2 = buffer[0];
                break;
            }
            case 2: {
                b1 = buffer[0];
                b2 = buffer[1];
                break;
            }
        }
        StringBuffer sb = new StringBuffer();
        boolean notleading = false;
        do {
            int c = (b0 & 252) >>> 2;
            if (notleading || c != 0) {
                sb.append(BASE64_CHARSET[c]);
                notleading = true;
            }
            c = (b0 & 3) << 4 | (b1 & 240) >>> 4;
            if (notleading || c != 0) {
                sb.append(BASE64_CHARSET[c]);
                notleading = true;
            }
            c = (b1 & 15) << 2 | (b2 & 192) >>> 6;
            if (notleading || c != 0) {
                sb.append(BASE64_CHARSET[c]);
                notleading = true;
            }
            c = b2 & 63;
            if (notleading || c != 0) {
                sb.append(BASE64_CHARSET[c]);
                notleading = true;
            }
            if (pos >= len) break;
            try {
                b0 = buffer[pos++];
                b1 = buffer[pos++];
                b2 = buffer[pos++];
            }
            catch (ArrayIndexOutOfBoundsException x) {
                break;
            }
        } while (true);
        if (notleading) {
            return sb.toString();
        }
        return "0";
    }

    /*
     * Exception decompiling
     */
    public static final byte[] fromBase64(String str) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [7[DOLOOP]], but top level block is 10[SIMPLE_IF_TAKEN]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:416)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:468)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2960)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:818)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:196)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:141)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:95)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:372)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:867)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:768)
        // org.benf.cfr.reader.Main.doJar(Main.java:141)
        // org.benf.cfr.reader.Main.main(Main.java:242)
        throw new IllegalStateException("Decompilation failed");
    }

    public static final byte[] trim(BigInteger n) {
        byte[] in = n.toByteArray();
        if (in.length == 0 || in[0] != 0) {
            return in;
        }
        int len = in.length;
        int i = 1;
        while (in[i] == 0 && i < len) {
            ++i;
        }
        byte[] result = new byte[len - i];
        System.arraycopy(in, i, result, 0, len - i);
        return result;
    }

    public static final String dump(BigInteger x) {
        return Util.dumpString(Util.trim(x));
    }

    private Util() {
    }
}


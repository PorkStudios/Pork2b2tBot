/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;

public final class NetUtil {
    public static final Inet4Address LOCALHOST4;
    public static final Inet6Address LOCALHOST6;
    public static final InetAddress LOCALHOST;
    public static final NetworkInterface LOOPBACK_IF;
    public static final int SOMAXCONN;
    private static final int IPV6_WORD_COUNT = 8;
    private static final int IPV6_MAX_CHAR_COUNT = 39;
    private static final int IPV6_BYTE_COUNT = 16;
    private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
    private static final int IPV6_MIN_SEPARATORS = 2;
    private static final int IPV6_MAX_SEPARATORS = 8;
    private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
    private static final int IPV4_SEPARATORS = 3;
    private static final boolean IPV4_PREFERRED;
    private static final boolean IPV6_ADDRESSES_PREFERRED;
    private static final InternalLogger logger;

    /*
     * Exception decompiling
     */
    private static Integer sysctlGetInt(String sysctlKey) throws IOException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 5[CATCHBLOCK]
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

    public static boolean isIpV4StackPreferred() {
        return IPV4_PREFERRED;
    }

    public static boolean isIpV6AddressesPreferred() {
        return IPV6_ADDRESSES_PREFERRED;
    }

    public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
        if (NetUtil.isValidIpV4Address(ipAddressString)) {
            return NetUtil.validIpV4ToBytes(ipAddressString);
        }
        if (NetUtil.isValidIpV6Address(ipAddressString)) {
            int percentPos;
            if (ipAddressString.charAt(0) == '[') {
                ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
            }
            if ((percentPos = ipAddressString.indexOf(37)) >= 0) {
                ipAddressString = ipAddressString.substring(0, percentPos);
            }
            return NetUtil.getIPv6ByName(ipAddressString, true);
        }
        return null;
    }

    private static int decimalDigit(String str, int pos) {
        return str.charAt(pos) - 48;
    }

    private static byte ipv4WordToByte(String ip, int from, int toExclusive) {
        int ret = NetUtil.decimalDigit(ip, from);
        if (++from == toExclusive) {
            return (byte)ret;
        }
        ret = ret * 10 + NetUtil.decimalDigit(ip, from);
        if (++from == toExclusive) {
            return (byte)ret;
        }
        return (byte)(ret * 10 + NetUtil.decimalDigit(ip, from));
    }

    static byte[] validIpV4ToBytes(String ip) {
        byte[] arrby = new byte[4];
        int i = ip.indexOf(46, 1);
        arrby[0] = NetUtil.ipv4WordToByte(ip, 0, i);
        int n = i + 1;
        i = ip.indexOf(46, i + 2);
        arrby[1] = NetUtil.ipv4WordToByte(ip, n, i);
        int n2 = i + 1;
        i = ip.indexOf(46, i + 2);
        arrby[2] = NetUtil.ipv4WordToByte(ip, n2, i);
        arrby[3] = NetUtil.ipv4WordToByte(ip, i + 1, ip.length());
        return arrby;
    }

    public static String intToIpAddress(int i) {
        StringBuilder buf = new StringBuilder(15);
        buf.append(i >> 24 & 255);
        buf.append('.');
        buf.append(i >> 16 & 255);
        buf.append('.');
        buf.append(i >> 8 & 255);
        buf.append('.');
        buf.append(i & 255);
        return buf.toString();
    }

    public static String bytesToIpAddress(byte[] bytes) {
        return NetUtil.bytesToIpAddress(bytes, 0, bytes.length);
    }

    public static String bytesToIpAddress(byte[] bytes, int offset, int length) {
        switch (length) {
            case 4: {
                return new StringBuilder(15).append(bytes[offset] & 255).append('.').append(bytes[offset + 1] & 255).append('.').append(bytes[offset + 2] & 255).append('.').append(bytes[offset + 3] & 255).toString();
            }
            case 16: {
                return NetUtil.toAddressString(bytes, offset, false);
            }
        }
        throw new IllegalArgumentException("length: " + length + " (expected: 4 or 16)");
    }

    public static boolean isValidIpV6Address(String ip) {
        int start;
        int compressBegin;
        int colons;
        int end = ip.length();
        if (end < 2) {
            return false;
        }
        char c = ip.charAt(0);
        if (c == '[') {
            if (ip.charAt(--end) != ']') {
                return false;
            }
            start = 1;
            c = ip.charAt(1);
        } else {
            start = 0;
        }
        if (c == ':') {
            if (ip.charAt(start + 1) != ':') {
                return false;
            }
            colons = 2;
            compressBegin = start;
            start += 2;
        } else {
            colons = 0;
            compressBegin = -1;
        }
        int wordLen = 0;
        block5 : for (int i = start; i < end; ++i) {
            c = ip.charAt(i);
            if (NetUtil.isValidHexChar(c)) {
                if (wordLen < 4) {
                    ++wordLen;
                    continue;
                }
                return false;
            }
            switch (c) {
                case ':': {
                    if (colons > 7) {
                        return false;
                    }
                    if (ip.charAt(i - 1) == ':') {
                        if (compressBegin >= 0) {
                            return false;
                        }
                        compressBegin = i - 1;
                    } else {
                        wordLen = 0;
                    }
                    ++colons;
                    continue block5;
                }
                case '.': {
                    if (compressBegin < 0 && colons != 6 || colons == 7 && compressBegin >= start || colons > 7) {
                        return false;
                    }
                    int ipv4Start = i - wordLen;
                    int j = ipv4Start - 2;
                    if (NetUtil.isValidIPv4MappedChar(ip.charAt(j))) {
                        if (!(NetUtil.isValidIPv4MappedChar(ip.charAt(j - 1)) && NetUtil.isValidIPv4MappedChar(ip.charAt(j - 2)) && NetUtil.isValidIPv4MappedChar(ip.charAt(j - 3)))) {
                            return false;
                        }
                        j -= 5;
                    }
                    while (j >= start) {
                        char tmpChar = ip.charAt(j);
                        if (tmpChar != '0' && tmpChar != ':') {
                            return false;
                        }
                        --j;
                    }
                    int ipv4End = ip.indexOf(37, ipv4Start + 7);
                    if (ipv4End < 0) {
                        ipv4End = end;
                    }
                    return NetUtil.isValidIpV4Address(ip, ipv4Start, ipv4End);
                }
                case '%': {
                    end = i;
                    break block5;
                }
                default: {
                    return false;
                }
            }
        }
        if (compressBegin < 0) {
            return colons == 7 && wordLen > 0;
        }
        return compressBegin + 2 == end || wordLen > 0 && (colons < 8 || compressBegin <= start);
    }

    private static boolean isValidIpV4Word(CharSequence word, int from, int toExclusive) {
        char c0;
        int len = toExclusive - from;
        if (len < 1 || len > 3 || (c0 = word.charAt(from)) < '0') {
            return false;
        }
        if (len == 3) {
            char c2;
            char c1 = word.charAt(from + 1);
            return c1 >= '0' && (c2 = word.charAt(from + 2)) >= '0' && (c0 <= '1' && c1 <= '9' && c2 <= '9' || c0 == '2' && c1 <= '5' && (c2 <= '5' || c1 < '5' && c2 <= '9'));
        }
        return c0 <= '9' && (len == 1 || NetUtil.isValidNumericChar(word.charAt(from + 1)));
    }

    private static boolean isValidHexChar(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
    }

    private static boolean isValidNumericChar(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isValidIPv4MappedChar(char c) {
        return c == 'f' || c == 'F';
    }

    private static boolean isValidIPv4MappedSeparators(byte b0, byte b1, boolean mustBeZero) {
        return b0 == b1 && (b0 == 0 || !mustBeZero && b1 == -1);
    }

    private static boolean isValidIPv4Mapped(byte[] bytes, int currentIndex, int compressBegin, int compressLength) {
        boolean mustBeZero = compressBegin + compressLength >= 14;
        return currentIndex <= 12 && currentIndex >= 2 && (!mustBeZero || compressBegin < 12) && NetUtil.isValidIPv4MappedSeparators(bytes[currentIndex - 1], bytes[currentIndex - 2], mustBeZero) && PlatformDependent.isZero(bytes, 0, currentIndex - 3);
    }

    public static boolean isValidIpV4Address(String ip) {
        return NetUtil.isValidIpV4Address(ip, 0, ip.length());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean isValidIpV4Address(String ip, int from, int toExcluded) {
        int len = toExcluded - from;
        if (len > 15) return false;
        if (len < 7) return false;
        int i = ip.indexOf(46, from + 1);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word(ip, from, i)) return false;
        from = i + 2;
        i = ip.indexOf(46, from);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word(ip, from - 1, i)) return false;
        from = i + 2;
        i = ip.indexOf(46, from);
        if (i <= 0) return false;
        if (!NetUtil.isValidIpV4Word(ip, from - 1, i)) return false;
        if (!NetUtil.isValidIpV4Word(ip, i + 1, toExcluded)) return false;
        return true;
    }

    public static Inet6Address getByName(CharSequence ip) {
        return NetUtil.getByName(ip, true);
    }

    public static Inet6Address getByName(CharSequence ip, boolean ipv4Mapped) {
        byte[] bytes = NetUtil.getIPv6ByName(ip, ipv4Mapped);
        if (bytes == null) {
            return null;
        }
        try {
            return Inet6Address.getByAddress(null, bytes, -1);
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getIPv6ByName(CharSequence ip, boolean ipv4Mapped) {
        int i;
        int tmp;
        boolean isCompressed;
        byte[] bytes = new byte[16];
        int ipLength = ip.length();
        int compressBegin = 0;
        int compressLength = 0;
        int currentIndex = 0;
        int value = 0;
        int begin = -1;
        int ipv6Separators = 0;
        int ipv4Separators = 0;
        boolean needsShift = false;
        block4 : for (i = 0; i < ipLength; ++i) {
            char c = ip.charAt(i);
            switch (c) {
                case ':': {
                    if (i - begin > 4 || ipv4Separators > 0 || ++ipv6Separators > 8 || currentIndex + 1 >= bytes.length) {
                        return null;
                    }
                    value <<= 4 - (i - begin) << 2;
                    if (compressLength > 0) {
                        compressLength -= 2;
                    }
                    bytes[currentIndex++] = (byte)((value & 15) << 4 | value >> 4 & 15);
                    bytes[currentIndex++] = (byte)((value >> 8 & 15) << 4 | value >> 12 & 15);
                    tmp = i + 1;
                    if (tmp < ipLength && ip.charAt(tmp) == ':') {
                        if (compressBegin != 0 || tmp < ipLength && ip.charAt(++tmp) == ':') {
                            return null;
                        }
                        needsShift = ++ipv6Separators == 2 && value == 0;
                        compressBegin = currentIndex;
                        compressLength = bytes.length - compressBegin - 2;
                        ++i;
                    }
                    value = 0;
                    begin = -1;
                    continue block4;
                }
                case '.': {
                    tmp = i - begin;
                    if (tmp > 3 || begin < 0 || ipv4Separators > 3 || ipv6Separators > 0 && currentIndex + compressLength < 12 || i + 1 >= ipLength || currentIndex >= bytes.length || ++ipv4Separators == 1 && (!ipv4Mapped || currentIndex != 0 && !NetUtil.isValidIPv4Mapped(bytes, currentIndex, compressBegin, compressLength) || tmp == 3 && (!NetUtil.isValidNumericChar(ip.charAt(i - 1)) || !NetUtil.isValidNumericChar(ip.charAt(i - 2)) || !NetUtil.isValidNumericChar(ip.charAt(i - 3))) || tmp == 2 && (!NetUtil.isValidNumericChar(ip.charAt(i - 1)) || !NetUtil.isValidNumericChar(ip.charAt(i - 2))) || tmp == 1 && !NetUtil.isValidNumericChar(ip.charAt(i - 1)))) {
                        return null;
                    }
                    if ((begin = ((value <<= 3 - tmp << 2) & 15) * 100 + (value >> 4 & 15) * 10 + (value >> 8 & 15)) < 0 || begin > 255) {
                        return null;
                    }
                    bytes[currentIndex++] = (byte)begin;
                    value = 0;
                    begin = -1;
                    continue block4;
                }
                default: {
                    if (!NetUtil.isValidHexChar(c) || ipv4Separators > 0 && !NetUtil.isValidNumericChar(c)) {
                        return null;
                    }
                    if (begin < 0) {
                        begin = i;
                    } else if (i - begin > 4) {
                        return null;
                    }
                    value += StringUtil.decodeHexNibble(c) << (i - begin << 2);
                }
            }
        }
        boolean bl = isCompressed = compressBegin > 0;
        if (ipv4Separators > 0) {
            if (begin > 0 && i - begin > 3 || ipv4Separators != 3 || currentIndex >= bytes.length) {
                return null;
            }
            if (ipv6Separators == 0) {
                compressLength = 12;
            } else if (ipv6Separators >= 2 && (!isCompressed && ipv6Separators == 6 && ip.charAt(0) != ':' || isCompressed && ipv6Separators < 8 && (ip.charAt(0) != ':' || compressBegin <= 2))) {
                compressLength -= 2;
            } else {
                return null;
            }
            begin = (value & 15) * 100 + (value >> 4 & 15) * 10 + ((value <<= 3 - (i - begin) << 2) >> 8 & 15);
            if (begin < 0 || begin > 255) {
                return null;
            }
            bytes[currentIndex++] = (byte)begin;
        } else {
            tmp = ipLength - 1;
            if (begin > 0 && i - begin > 4 || ipv6Separators < 2 || !isCompressed && (ipv6Separators + 1 != 8 || ip.charAt(0) == ':' || ip.charAt(tmp) == ':') || isCompressed && (ipv6Separators > 8 || ipv6Separators == 8 && (compressBegin <= 2 && ip.charAt(0) != ':' || compressBegin >= 14 && ip.charAt(tmp) != ':')) || currentIndex + 1 >= bytes.length || begin < 0 && ip.charAt(tmp - 1) != ':' || compressBegin > 2 && ip.charAt(0) == ':') {
                return null;
            }
            if (begin >= 0 && i - begin <= 4) {
                value <<= 4 - (i - begin) << 2;
            }
            bytes[currentIndex++] = (byte)((value & 15) << 4 | value >> 4 & 15);
            bytes[currentIndex++] = (byte)((value >> 8 & 15) << 4 | value >> 12 & 15);
        }
        i = currentIndex + compressLength;
        if (needsShift || i >= bytes.length) {
            if (i >= bytes.length) {
                ++compressBegin;
            }
            for (i = currentIndex; i < bytes.length; ++i) {
                for (begin = bytes.length - 1; begin >= compressBegin; --begin) {
                    bytes[begin] = bytes[begin - 1];
                }
                bytes[begin] = 0;
                ++compressBegin;
            }
        } else {
            for (i = 0; i < compressLength && (currentIndex = (begin = i + compressBegin) + compressLength) < bytes.length; ++i) {
                bytes[currentIndex] = bytes[begin];
                bytes[begin] = 0;
            }
        }
        if (ipv4Separators > 0) {
            bytes[11] = -1;
            bytes[10] = -1;
        }
        return bytes;
    }

    public static String toSocketAddressString(InetSocketAddress addr) {
        StringBuilder sb;
        String port = String.valueOf(addr.getPort());
        if (addr.isUnresolved()) {
            String hostString = PlatformDependent.javaVersion() >= 7 ? addr.getHostString() : addr.getHostName();
            sb = NetUtil.newSocketAddressStringBuilder(hostString, port, !NetUtil.isValidIpV6Address(hostString));
        } else {
            InetAddress address = addr.getAddress();
            String hostString = NetUtil.toAddressString(address);
            sb = NetUtil.newSocketAddressStringBuilder(hostString, port, address instanceof Inet4Address);
        }
        return sb.append(':').append(port).toString();
    }

    public static String toSocketAddressString(String host, int port) {
        String portStr = String.valueOf(port);
        return NetUtil.newSocketAddressStringBuilder(host, portStr, !NetUtil.isValidIpV6Address(host)).append(':').append(portStr).toString();
    }

    private static StringBuilder newSocketAddressStringBuilder(String host, String port, boolean ipv4) {
        int hostLen = host.length();
        if (ipv4) {
            return new StringBuilder(hostLen + 1 + port.length()).append(host);
        }
        StringBuilder stringBuilder = new StringBuilder(hostLen + 3 + port.length());
        if (hostLen > 1 && host.charAt(0) == '[' && host.charAt(hostLen - 1) == ']') {
            return stringBuilder.append(host);
        }
        return stringBuilder.append('[').append(host).append(']');
    }

    public static String toAddressString(InetAddress ip) {
        return NetUtil.toAddressString(ip, false);
    }

    public static String toAddressString(InetAddress ip, boolean ipv4Mapped) {
        if (ip instanceof Inet4Address) {
            return ip.getHostAddress();
        }
        if (!(ip instanceof Inet6Address)) {
            throw new IllegalArgumentException("Unhandled type: " + ip);
        }
        return NetUtil.toAddressString(ip.getAddress(), 0, ipv4Mapped);
    }

    private static String toAddressString(byte[] bytes, int offset, boolean ipv4Mapped) {
        StringBuilder b;
        int i;
        int currentLength;
        int[] words = new int[8];
        int end = offset + words.length;
        for (i = offset; i < end; ++i) {
            words[i] = (bytes[i << 1] & 255) << 8 | bytes[(i << 1) + 1] & 255;
        }
        int currentStart = -1;
        int shortestStart = -1;
        int shortestLength = 0;
        for (i = 0; i < words.length; ++i) {
            if (words[i] == 0) {
                if (currentStart >= 0) continue;
                currentStart = i;
                continue;
            }
            if (currentStart < 0) continue;
            currentLength = i - currentStart;
            if (currentLength > shortestLength) {
                shortestStart = currentStart;
                shortestLength = currentLength;
            }
            currentStart = -1;
        }
        if (currentStart >= 0 && (currentLength = i - currentStart) > shortestLength) {
            shortestStart = currentStart;
            shortestLength = currentLength;
        }
        if (shortestLength == 1) {
            shortestLength = 0;
            shortestStart = -1;
        }
        int shortestEnd = shortestStart + shortestLength;
        b = new StringBuilder(39);
        if (shortestEnd < 0) {
            b.append(Integer.toHexString(words[0]));
            for (i = 1; i < words.length; ++i) {
                b.append(':');
                b.append(Integer.toHexString(words[i]));
            }
        } else {
            boolean isIpv4Mapped;
            if (NetUtil.inRangeEndExclusive(0, shortestStart, shortestEnd)) {
                b.append("::");
                isIpv4Mapped = ipv4Mapped && shortestEnd == 5 && words[5] == 65535;
            } else {
                b.append(Integer.toHexString(words[0]));
                isIpv4Mapped = false;
            }
            for (i = 1; i < words.length; ++i) {
                if (!NetUtil.inRangeEndExclusive(i, shortestStart, shortestEnd)) {
                    if (!NetUtil.inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) {
                        if (!isIpv4Mapped || i == 6) {
                            b.append(':');
                        } else {
                            b.append('.');
                        }
                    }
                    if (isIpv4Mapped && i > 5) {
                        b.append(words[i] >> 8);
                        b.append('.');
                        b.append(words[i] & 255);
                        continue;
                    }
                    b.append(Integer.toHexString(words[i]));
                    continue;
                }
                if (NetUtil.inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) continue;
                b.append("::");
            }
        }
        return b.toString();
    }

    private static boolean inRangeEndExclusive(int value, int start, int end) {
        return value >= start && value < end;
    }

    private NetUtil() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        InetAddress loopbackAddr;
        NetworkInterface loopbackIface;
        block25 : {
            Enumeration<InetAddress> i;
            IPV4_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv4Stack", false);
            IPV6_ADDRESSES_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv6Addresses", false);
            logger = InternalLoggerFactory.getInstance(NetUtil.class);
            logger.debug("-Djava.net.preferIPv4Stack: {}", (Object)IPV4_PREFERRED);
            logger.debug("-Djava.net.preferIPv6Addresses: {}", (Object)IPV6_ADDRESSES_PREFERRED);
            byte[] LOCALHOST4_BYTES = new byte[]{127, 0, 0, 1};
            byte[] LOCALHOST6_BYTES = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
            Inet4Address localhost4 = null;
            try {
                localhost4 = (Inet4Address)InetAddress.getByAddress("localhost", LOCALHOST4_BYTES);
            }
            catch (Exception e) {
                PlatformDependent.throwException(e);
            }
            LOCALHOST4 = localhost4;
            Inet6Address localhost6 = null;
            try {
                localhost6 = (Inet6Address)InetAddress.getByAddress("localhost", LOCALHOST6_BYTES);
            }
            catch (Exception e) {
                PlatformDependent.throwException(e);
            }
            LOCALHOST6 = localhost6;
            ArrayList<NetworkInterface> ifaces = new ArrayList<NetworkInterface>();
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                if (interfaces != null) {
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface iface = interfaces.nextElement();
                        if (!SocketUtils.addressesFromNetworkInterface(iface).hasMoreElements()) continue;
                        ifaces.add(iface);
                    }
                }
            }
            catch (SocketException e) {
                logger.warn("Failed to retrieve the list of available network interfaces", e);
            }
            loopbackIface = null;
            loopbackAddr = null;
            block14 : for (NetworkInterface iface : ifaces) {
                i = SocketUtils.addressesFromNetworkInterface(iface);
                while (i.hasMoreElements()) {
                    InetAddress addr = i.nextElement();
                    if (!addr.isLoopbackAddress()) continue;
                    loopbackIface = iface;
                    loopbackAddr = addr;
                    break block14;
                }
            }
            if (loopbackIface == null) {
                try {
                    for (NetworkInterface iface : ifaces) {
                        if (!iface.isLoopback() || !(i = SocketUtils.addressesFromNetworkInterface(iface)).hasMoreElements()) continue;
                        loopbackIface = iface;
                        loopbackAddr = i.nextElement();
                        break;
                    }
                    if (loopbackIface == null) {
                        logger.warn("Failed to find the loopback interface");
                    }
                }
                catch (SocketException e) {
                    logger.warn("Failed to find the loopback interface", e);
                }
            }
            if (loopbackIface != null) {
                logger.debug("Loopback interface: {} ({}, {})", loopbackIface.getName(), loopbackIface.getDisplayName(), loopbackAddr.getHostAddress());
            } else if (loopbackAddr == null) {
                try {
                    if (NetworkInterface.getByInetAddress(LOCALHOST6) != null) {
                        logger.debug("Using hard-coded IPv6 localhost address: {}", (Object)localhost6);
                        loopbackAddr = localhost6;
                    }
                    break block25;
                }
                catch (Exception e) {}
                break block25;
                finally {
                    if (loopbackAddr == null) {
                        logger.debug("Using hard-coded IPv4 localhost address: {}", (Object)localhost4);
                        loopbackAddr = localhost4;
                    }
                }
            }
        }
        LOOPBACK_IF = loopbackIface;
        LOCALHOST = loopbackAddr;
        SOMAXCONN = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public Integer run() {
                int somaxconn;
                somaxconn = PlatformDependent.isWindows() ? 200 : 128;
                File file = new File("/proc/sys/net/core/somaxconn");
                BufferedReader in = null;
                try {
                    if (file.exists()) {
                        in = new BufferedReader(new FileReader(file));
                        somaxconn = Integer.parseInt(in.readLine());
                        if (logger.isDebugEnabled()) {
                            logger.debug("{}: {}", (Object)file, (Object)somaxconn);
                        }
                    } else {
                        Integer tmp = null;
                        if (SystemPropertyUtil.getBoolean("io.netty.net.somaxconn.trySysctl", false)) {
                            tmp = NetUtil.sysctlGetInt("kern.ipc.somaxconn");
                            if (tmp == null) {
                                tmp = NetUtil.sysctlGetInt("kern.ipc.soacceptqueue");
                                if (tmp != null) {
                                    somaxconn = tmp;
                                }
                            } else {
                                somaxconn = tmp;
                            }
                        }
                        if (tmp == null) {
                            logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", (Object)file, (Object)somaxconn);
                        }
                    }
                }
                catch (Exception e) {
                    logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", file, somaxconn, e);
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Exception e) {}
                    }
                }
                return somaxconn;
            }
        });
    }

}


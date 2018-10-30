/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.handler.codec.socks.SocksResponse;
import io.netty.handler.codec.socks.UnknownSocksRequest;
import io.netty.handler.codec.socks.UnknownSocksResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import java.nio.charset.Charset;

final class SocksCommonUtils {
    public static final SocksRequest UNKNOWN_SOCKS_REQUEST = new UnknownSocksRequest();
    public static final SocksResponse UNKNOWN_SOCKS_RESPONSE = new UnknownSocksResponse();
    private static final char ipv6hextetSeparator = ':';

    private SocksCommonUtils() {
    }

    public static String ipv6toStr(byte[] src) {
        assert (src.length == 16);
        StringBuilder sb = new StringBuilder(39);
        SocksCommonUtils.ipv6toStr(sb, src, 0, 8);
        return sb.toString();
    }

    private static void ipv6toStr(StringBuilder sb, byte[] src, int fromHextet, int toHextet) {
        int i;
        for (i = fromHextet; i < --toHextet; ++i) {
            SocksCommonUtils.appendHextet(sb, src, i);
            sb.append(':');
        }
        SocksCommonUtils.appendHextet(sb, src, i);
    }

    private static void appendHextet(StringBuilder sb, byte[] src, int i) {
        StringUtil.toHexString(sb, src, i << 1, 2);
    }

    static String readUsAscii(ByteBuf buffer, int length) {
        String s = buffer.toString(buffer.readerIndex(), length, CharsetUtil.US_ASCII);
        buffer.skipBytes(length);
        return s;
    }
}


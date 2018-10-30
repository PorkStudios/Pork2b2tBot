/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http2.CharSequenceMap;
import io.netty.handler.codec.http2.HpackHeaderField;
import io.netty.handler.codec.http2.HpackUtil;
import io.netty.util.AsciiString;
import java.util.Arrays;
import java.util.List;

final class HpackStaticTable {
    private static final List<HpackHeaderField> STATIC_TABLE = Arrays.asList(HpackStaticTable.newEmptyHeaderField(":authority"), HpackStaticTable.newHeaderField(":method", "GET"), HpackStaticTable.newHeaderField(":method", "POST"), HpackStaticTable.newHeaderField(":path", "/"), HpackStaticTable.newHeaderField(":path", "/index.html"), HpackStaticTable.newHeaderField(":scheme", "http"), HpackStaticTable.newHeaderField(":scheme", "https"), HpackStaticTable.newHeaderField(":status", "200"), HpackStaticTable.newHeaderField(":status", "204"), HpackStaticTable.newHeaderField(":status", "206"), HpackStaticTable.newHeaderField(":status", "304"), HpackStaticTable.newHeaderField(":status", "400"), HpackStaticTable.newHeaderField(":status", "404"), HpackStaticTable.newHeaderField(":status", "500"), HpackStaticTable.newEmptyHeaderField("accept-charset"), HpackStaticTable.newHeaderField("accept-encoding", "gzip, deflate"), HpackStaticTable.newEmptyHeaderField("accept-language"), HpackStaticTable.newEmptyHeaderField("accept-ranges"), HpackStaticTable.newEmptyHeaderField("accept"), HpackStaticTable.newEmptyHeaderField("access-control-allow-origin"), HpackStaticTable.newEmptyHeaderField("age"), HpackStaticTable.newEmptyHeaderField("allow"), HpackStaticTable.newEmptyHeaderField("authorization"), HpackStaticTable.newEmptyHeaderField("cache-control"), HpackStaticTable.newEmptyHeaderField("content-disposition"), HpackStaticTable.newEmptyHeaderField("content-encoding"), HpackStaticTable.newEmptyHeaderField("content-language"), HpackStaticTable.newEmptyHeaderField("content-length"), HpackStaticTable.newEmptyHeaderField("content-location"), HpackStaticTable.newEmptyHeaderField("content-range"), HpackStaticTable.newEmptyHeaderField("content-type"), HpackStaticTable.newEmptyHeaderField("cookie"), HpackStaticTable.newEmptyHeaderField("date"), HpackStaticTable.newEmptyHeaderField("etag"), HpackStaticTable.newEmptyHeaderField("expect"), HpackStaticTable.newEmptyHeaderField("expires"), HpackStaticTable.newEmptyHeaderField("from"), HpackStaticTable.newEmptyHeaderField("host"), HpackStaticTable.newEmptyHeaderField("if-match"), HpackStaticTable.newEmptyHeaderField("if-modified-since"), HpackStaticTable.newEmptyHeaderField("if-none-match"), HpackStaticTable.newEmptyHeaderField("if-range"), HpackStaticTable.newEmptyHeaderField("if-unmodified-since"), HpackStaticTable.newEmptyHeaderField("last-modified"), HpackStaticTable.newEmptyHeaderField("link"), HpackStaticTable.newEmptyHeaderField("location"), HpackStaticTable.newEmptyHeaderField("max-forwards"), HpackStaticTable.newEmptyHeaderField("proxy-authenticate"), HpackStaticTable.newEmptyHeaderField("proxy-authorization"), HpackStaticTable.newEmptyHeaderField("range"), HpackStaticTable.newEmptyHeaderField("referer"), HpackStaticTable.newEmptyHeaderField("refresh"), HpackStaticTable.newEmptyHeaderField("retry-after"), HpackStaticTable.newEmptyHeaderField("server"), HpackStaticTable.newEmptyHeaderField("set-cookie"), HpackStaticTable.newEmptyHeaderField("strict-transport-security"), HpackStaticTable.newEmptyHeaderField("transfer-encoding"), HpackStaticTable.newEmptyHeaderField("user-agent"), HpackStaticTable.newEmptyHeaderField("vary"), HpackStaticTable.newEmptyHeaderField("via"), HpackStaticTable.newEmptyHeaderField("www-authenticate"));
    private static final CharSequenceMap<Integer> STATIC_INDEX_BY_NAME = HpackStaticTable.createMap();
    static final int length = STATIC_TABLE.size();

    private static HpackHeaderField newEmptyHeaderField(String name) {
        return new HpackHeaderField(AsciiString.cached(name), AsciiString.EMPTY_STRING);
    }

    private static HpackHeaderField newHeaderField(String name, String value) {
        return new HpackHeaderField(AsciiString.cached(name), AsciiString.cached(value));
    }

    static HpackHeaderField getEntry(int index) {
        return STATIC_TABLE.get(index - 1);
    }

    static int getIndex(CharSequence name) {
        Integer index = (Integer)STATIC_INDEX_BY_NAME.get(name);
        if (index == null) {
            return -1;
        }
        return index;
    }

    static int getIndex(CharSequence name, CharSequence value) {
        int index = HpackStaticTable.getIndex(name);
        if (index == -1) {
            return -1;
        }
        while (index <= length) {
            HpackHeaderField entry = HpackStaticTable.getEntry(index);
            if (HpackUtil.equalsConstantTime(name, entry.name) == 0) break;
            if (HpackUtil.equalsConstantTime(value, entry.value) != 0) {
                return index;
            }
            ++index;
        }
        return -1;
    }

    private static CharSequenceMap<Integer> createMap() {
        int length = STATIC_TABLE.size();
        CharSequenceMap<Integer> ret = new CharSequenceMap<Integer>(true, UnsupportedValueConverter.instance(), length);
        for (int index = length; index > 0; --index) {
            HpackHeaderField entry = HpackStaticTable.getEntry(index);
            CharSequence name = entry.name;
            ret.set(name, Integer.valueOf(index));
        }
        return ret;
    }

    private HpackStaticTable() {
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;

final class HttpHeadersEncoder {
    private static final int COLON_AND_SPACE_SHORT = 14880;

    private HttpHeadersEncoder() {
    }

    static void encoderHeader(CharSequence name, CharSequence value, ByteBuf buf) {
        int nameLen = name.length();
        int valueLen = value.length();
        int entryLen = nameLen + valueLen + 4;
        buf.ensureWritable(entryLen);
        int offset = buf.writerIndex();
        HttpHeadersEncoder.writeAscii(buf, offset, name);
        ByteBufUtil.setShortBE(buf, offset += nameLen, 14880);
        HttpHeadersEncoder.writeAscii(buf, offset += 2, value);
        ByteBufUtil.setShortBE(buf, offset += valueLen, 3338);
        buf.writerIndex(offset += 2);
    }

    private static void writeAscii(ByteBuf buf, int offset, CharSequence value) {
        if (value instanceof AsciiString) {
            ByteBufUtil.copy((AsciiString)value, 0, buf, offset, value.length());
        } else {
            buf.setCharSequence(offset, value, CharsetUtil.US_ASCII);
        }
    }
}


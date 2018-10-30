/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;

public interface Http2HeadersDecoder {
    public Http2Headers decodeHeaders(int var1, ByteBuf var2) throws Http2Exception;

    public Configuration configuration();

    public static interface Configuration {
        public void maxHeaderTableSize(long var1) throws Http2Exception;

        public long maxHeaderTableSize();

        public void maxHeaderListSize(long var1, long var3) throws Http2Exception;

        public long maxHeaderListSize();

        public long maxHeaderListSizeGoAway();
    }

}


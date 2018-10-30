/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;

public interface Http2HeadersEncoder {
    public static final SensitivityDetector NEVER_SENSITIVE = new SensitivityDetector(){

        @Override
        public boolean isSensitive(CharSequence name, CharSequence value) {
            return false;
        }
    };
    public static final SensitivityDetector ALWAYS_SENSITIVE = new SensitivityDetector(){

        @Override
        public boolean isSensitive(CharSequence name, CharSequence value) {
            return true;
        }
    };

    public void encodeHeaders(int var1, Http2Headers var2, ByteBuf var3) throws Http2Exception;

    public Configuration configuration();

    public static interface SensitivityDetector {
        public boolean isSensitive(CharSequence var1, CharSequence var2);
    }

    public static interface Configuration {
        public void maxHeaderTableSize(long var1) throws Http2Exception;

        public long maxHeaderTableSize();

        public void maxHeaderListSize(long var1) throws Http2Exception;

        public long maxHeaderListSize();
    }

}


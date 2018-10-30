/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;

final class HttpPostBodyUtil {
    public static final int chunkSize = 8096;
    public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";

    private HttpPostBodyUtil() {
    }

    static int findNonWhitespace(String sb, int offset) {
        int result;
        for (result = offset; result < sb.length() && Character.isWhitespace(sb.charAt(result)); ++result) {
        }
        return result;
    }

    static int findEndOfString(String sb) {
        int result;
        for (result = sb.length(); result > 0 && Character.isWhitespace(sb.charAt(result - 1)); --result) {
        }
        return result;
    }

    static class SeekAheadOptimize {
        byte[] bytes;
        int readerIndex;
        int pos;
        int origPos;
        int limit;
        ByteBuf buffer;

        SeekAheadOptimize(ByteBuf buffer) {
            if (!buffer.hasArray()) {
                throw new IllegalArgumentException("buffer hasn't backing byte array");
            }
            this.buffer = buffer;
            this.bytes = buffer.array();
            this.readerIndex = buffer.readerIndex();
            this.origPos = this.pos = buffer.arrayOffset() + this.readerIndex;
            this.limit = buffer.arrayOffset() + buffer.writerIndex();
        }

        void setReadPosition(int minus) {
            this.pos -= minus;
            this.readerIndex = this.getReadPosition(this.pos);
            this.buffer.readerIndex(this.readerIndex);
        }

        int getReadPosition(int index) {
            return index - this.origPos + this.readerIndex;
        }
    }

    public static enum TransferEncodingMechanism {
        BIT7("7bit"),
        BIT8("8bit"),
        BINARY("binary");
        
        private final String value;

        private TransferEncodingMechanism(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

        public String toString() {
            return this.value;
        }
    }

}


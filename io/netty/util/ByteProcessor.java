/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

public interface ByteProcessor {
    public static final ByteProcessor FIND_NUL = new IndexOfProcessor(0);
    public static final ByteProcessor FIND_NON_NUL = new IndexNotOfProcessor(0);
    public static final ByteProcessor FIND_CR = new IndexOfProcessor(13);
    public static final ByteProcessor FIND_NON_CR = new IndexNotOfProcessor(13);
    public static final ByteProcessor FIND_LF = new IndexOfProcessor(10);
    public static final ByteProcessor FIND_NON_LF = new IndexNotOfProcessor(10);
    public static final ByteProcessor FIND_SEMI_COLON = new IndexOfProcessor(59);
    public static final ByteProcessor FIND_COMMA = new IndexOfProcessor(44);
    public static final ByteProcessor FIND_CRLF = new ByteProcessor(){

        @Override
        public boolean process(byte value) {
            return value != 13 && value != 10;
        }
    };
    public static final ByteProcessor FIND_NON_CRLF = new ByteProcessor(){

        @Override
        public boolean process(byte value) {
            return value == 13 || value == 10;
        }
    };
    public static final ByteProcessor FIND_LINEAR_WHITESPACE = new ByteProcessor(){

        @Override
        public boolean process(byte value) {
            return value != 32 && value != 9;
        }
    };
    public static final ByteProcessor FIND_NON_LINEAR_WHITESPACE = new ByteProcessor(){

        @Override
        public boolean process(byte value) {
            return value == 32 || value == 9;
        }
    };

    public boolean process(byte var1) throws Exception;

    public static class IndexNotOfProcessor
    implements ByteProcessor {
        private final byte byteToNotFind;

        public IndexNotOfProcessor(byte byteToNotFind) {
            this.byteToNotFind = byteToNotFind;
        }

        @Override
        public boolean process(byte value) {
            return value == this.byteToNotFind;
        }
    }

    public static class IndexOfProcessor
    implements ByteProcessor {
        private final byte byteToFind;

        public IndexOfProcessor(byte byteToFind) {
            this.byteToFind = byteToFind;
        }

        @Override
        public boolean process(byte value) {
            return value != this.byteToFind;
        }
    }

}


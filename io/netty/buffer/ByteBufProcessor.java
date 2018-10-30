/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.util.ByteProcessor;

@Deprecated
public interface ByteBufProcessor
extends ByteProcessor {
    @Deprecated
    public static final ByteBufProcessor FIND_NUL = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value != 0;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_NUL = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value == 0;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_CR = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value != 13;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_CR = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value == 13;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_LF = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value != 10;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_LF = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value == 10;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_CRLF = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value != 13 && value != 10;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_CRLF = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value == 13 || value == 10;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_LINEAR_WHITESPACE = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value != 32 && value != 9;
        }
    };
    @Deprecated
    public static final ByteBufProcessor FIND_NON_LINEAR_WHITESPACE = new ByteBufProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return value == 32 || value == 9;
        }
    };

}


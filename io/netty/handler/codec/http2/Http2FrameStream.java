/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Stream;

public interface Http2FrameStream {
    public static final Http2FrameStream CONNECTION_STREAM = new Http2FrameStream(){

        @Override
        public int id() {
            return 0;
        }

        @Override
        public Http2Stream.State state() {
            return Http2Stream.State.IDLE;
        }
    };

    public int id();

    public Http2Stream.State state();

}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Stream;

public interface StreamByteDistributor {
    public void updateStreamableBytes(StreamState var1);

    public void updateDependencyTree(int var1, int var2, short var3, boolean var4);

    public boolean distribute(int var1, Writer var2) throws Http2Exception;

    public static interface Writer {
        public void write(Http2Stream var1, int var2);
    }

    public static interface StreamState {
        public Http2Stream stream();

        public int pendingBytes();

        public boolean hasFrame();

        public int windowSize();
    }

}


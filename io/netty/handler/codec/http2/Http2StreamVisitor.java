/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Stream;

public interface Http2StreamVisitor {
    public boolean visit(Http2Stream var1) throws Http2Exception;
}


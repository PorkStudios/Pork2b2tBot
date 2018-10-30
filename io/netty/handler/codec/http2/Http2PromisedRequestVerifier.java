/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Headers;

public interface Http2PromisedRequestVerifier {
    public static final Http2PromisedRequestVerifier ALWAYS_VERIFY = new Http2PromisedRequestVerifier(){

        @Override
        public boolean isAuthoritative(ChannelHandlerContext ctx, Http2Headers headers) {
            return true;
        }

        @Override
        public boolean isCacheable(Http2Headers headers) {
            return true;
        }

        @Override
        public boolean isSafe(Http2Headers headers) {
            return true;
        }
    };

    public boolean isAuthoritative(ChannelHandlerContext var1, Http2Headers var2);

    public boolean isCacheable(Http2Headers var1);

    public boolean isSafe(Http2Headers var1);

}


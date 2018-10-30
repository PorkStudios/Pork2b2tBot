/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FlowController;
import io.netty.handler.codec.http2.Http2Stream;

public interface Http2RemoteFlowController
extends Http2FlowController {
    public ChannelHandlerContext channelHandlerContext();

    public void addFlowControlled(Http2Stream var1, FlowControlled var2);

    public boolean hasFlowControlled(Http2Stream var1);

    public void writePendingBytes() throws Http2Exception;

    public void listener(Listener var1);

    public boolean isWritable(Http2Stream var1);

    public void channelWritabilityChanged() throws Http2Exception;

    public void updateDependencyTree(int var1, int var2, short var3, boolean var4);

    public static interface Listener {
        public void writabilityChanged(Http2Stream var1);
    }

    public static interface FlowControlled {
        public int size();

        public void error(ChannelHandlerContext var1, Throwable var2);

        public void writeComplete();

        public void write(ChannelHandlerContext var1, int var2);

        public boolean merge(ChannelHandlerContext var1, FlowControlled var2);
    }

}


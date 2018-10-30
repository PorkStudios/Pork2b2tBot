/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.stomp.DefaultStompFrame;
import io.netty.handler.codec.stomp.LastStompContentSubframe;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompContentSubframe;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.handler.codec.stomp.StompHeadersSubframe;
import io.netty.handler.codec.stomp.StompSubframe;
import io.netty.util.AsciiString;

public class StompSubframeAggregator
extends MessageAggregator<StompSubframe, StompHeadersSubframe, StompContentSubframe, StompFrame> {
    public StompSubframeAggregator(int maxContentLength) {
        super(maxContentLength);
    }

    @Override
    protected boolean isStartMessage(StompSubframe msg) throws Exception {
        return msg instanceof StompHeadersSubframe;
    }

    @Override
    protected boolean isContentMessage(StompSubframe msg) throws Exception {
        return msg instanceof StompContentSubframe;
    }

    @Override
    protected boolean isLastContentMessage(StompContentSubframe msg) throws Exception {
        return msg instanceof LastStompContentSubframe;
    }

    @Override
    protected boolean isAggregated(StompSubframe msg) throws Exception {
        return msg instanceof StompFrame;
    }

    @Override
    protected boolean isContentLengthInvalid(StompHeadersSubframe start, int maxContentLength) {
        return (int)Math.min(Integer.MAX_VALUE, start.headers().getLong(StompHeaders.CONTENT_LENGTH, -1L)) > maxContentLength;
    }

    @Override
    protected Object newContinueResponse(StompHeadersSubframe start, int maxContentLength, ChannelPipeline pipeline) {
        return null;
    }

    @Override
    protected boolean closeAfterContinueResponse(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean ignoreContentAfterContinueResponse(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected StompFrame beginAggregation(StompHeadersSubframe start, ByteBuf content) throws Exception {
        DefaultStompFrame ret = new DefaultStompFrame(start.command(), content);
        ret.headers().set(start.headers());
        return ret;
    }
}


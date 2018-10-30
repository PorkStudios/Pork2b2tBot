/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.memcache.FullMemcacheMessage;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheContent;
import io.netty.handler.codec.memcache.MemcacheMessage;
import io.netty.handler.codec.memcache.MemcacheObject;

public abstract class AbstractMemcacheObjectAggregator<H extends MemcacheMessage>
extends MessageAggregator<MemcacheObject, H, MemcacheContent, FullMemcacheMessage> {
    protected AbstractMemcacheObjectAggregator(int maxContentLength) {
        super(maxContentLength);
    }

    @Override
    protected boolean isContentMessage(MemcacheObject msg) throws Exception {
        return msg instanceof MemcacheContent;
    }

    @Override
    protected boolean isLastContentMessage(MemcacheContent msg) throws Exception {
        return msg instanceof LastMemcacheContent;
    }

    @Override
    protected boolean isAggregated(MemcacheObject msg) throws Exception {
        return msg instanceof FullMemcacheMessage;
    }

    @Override
    protected boolean isContentLengthInvalid(H start, int maxContentLength) {
        return false;
    }

    @Override
    protected Object newContinueResponse(H start, int maxContentLength, ChannelPipeline pipeline) {
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
}


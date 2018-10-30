/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequestEncoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponseDecoder;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class BinaryMemcacheClientCodec
extends CombinedChannelDuplexHandler<BinaryMemcacheResponseDecoder, BinaryMemcacheRequestEncoder> {
    private final boolean failOnMissingResponse;
    private final AtomicLong requestResponseCounter = new AtomicLong();

    public BinaryMemcacheClientCodec() {
        this(8192);
    }

    public BinaryMemcacheClientCodec(int decodeChunkSize) {
        this(decodeChunkSize, false);
    }

    public BinaryMemcacheClientCodec(int decodeChunkSize, boolean failOnMissingResponse) {
        this.failOnMissingResponse = failOnMissingResponse;
        this.init(new Decoder(decodeChunkSize), new Encoder());
    }

    private final class Decoder
    extends BinaryMemcacheResponseDecoder {
        Decoder(int chunkSize) {
            super(chunkSize);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            int oldSize = out.size();
            super.decode(ctx, in, out);
            if (BinaryMemcacheClientCodec.this.failOnMissingResponse) {
                int size = out.size();
                for (int i = oldSize; i < size; ++i) {
                    Object msg = out.get(i);
                    if (!(msg instanceof LastMemcacheContent)) continue;
                    BinaryMemcacheClientCodec.this.requestResponseCounter.decrementAndGet();
                }
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            long missingResponses;
            super.channelInactive(ctx);
            if (BinaryMemcacheClientCodec.this.failOnMissingResponse && (missingResponses = BinaryMemcacheClientCodec.this.requestResponseCounter.get()) > 0L) {
                ctx.fireExceptionCaught(new PrematureChannelClosureException("channel gone inactive with " + missingResponses + " missing response(s)"));
            }
        }
    }

    private final class Encoder
    extends BinaryMemcacheRequestEncoder {
        private Encoder() {
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
            super.encode(ctx, msg, out);
            if (BinaryMemcacheClientCodec.this.failOnMissingResponse && msg instanceof LastMemcacheContent) {
                BinaryMemcacheClientCodec.this.requestResponseCounter.incrementAndGet();
            }
        }
    }

}


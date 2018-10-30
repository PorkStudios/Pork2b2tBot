/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache.binary;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheRequestDecoder;
import io.netty.handler.codec.memcache.binary.BinaryMemcacheResponseEncoder;

public class BinaryMemcacheServerCodec
extends CombinedChannelDuplexHandler<BinaryMemcacheRequestDecoder, BinaryMemcacheResponseEncoder> {
    public BinaryMemcacheServerCodec() {
        this(8192);
    }

    public BinaryMemcacheServerCodec(int decodeChunkSize) {
        super(new BinaryMemcacheRequestDecoder(decodeChunkSize), new BinaryMemcacheResponseEncoder());
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksCommonUtils;
import io.netty.handler.codec.socks.SocksInitRequest;
import io.netty.handler.codec.socks.SocksProtocolVersion;
import io.netty.handler.codec.socks.SocksRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SocksInitRequestDecoder
extends ReplayingDecoder<State> {
    public SocksInitRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        switch ((State)((Object)this.state())) {
            case CHECK_PROTOCOL_VERSION: {
                if (byteBuf.readByte() != SocksProtocolVersion.SOCKS5.byteValue()) {
                    out.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
                    break;
                }
                this.checkpoint(State.READ_AUTH_SCHEMES);
            }
            case READ_AUTH_SCHEMES: {
                List<SocksAuthScheme> authSchemes;
                int authSchemeNum = byteBuf.readByte();
                if (authSchemeNum > 0) {
                    authSchemes = new ArrayList(authSchemeNum);
                    for (int i = 0; i < authSchemeNum; ++i) {
                        authSchemes.add(SocksAuthScheme.valueOf(byteBuf.readByte()));
                    }
                } else {
                    authSchemes = Collections.emptyList();
                }
                out.add(new SocksInitRequest(authSchemes));
                break;
            }
            default: {
                throw new Error();
            }
        }
        ctx.pipeline().remove(this);
    }

    static enum State {
        CHECK_PROTOCOL_VERSION,
        READ_AUTH_SCHEMES;
        

        private State() {
        }
    }

}


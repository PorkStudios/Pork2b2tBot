/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socks.SocksAuthResponse;
import io.netty.handler.codec.socks.SocksAuthStatus;
import io.netty.handler.codec.socks.SocksCommonUtils;
import io.netty.handler.codec.socks.SocksResponse;
import io.netty.handler.codec.socks.SocksSubnegotiationVersion;
import java.util.List;

public class SocksAuthResponseDecoder
extends ReplayingDecoder<State> {
    public SocksAuthResponseDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        switch ((State)((Object)this.state())) {
            case CHECK_PROTOCOL_VERSION: {
                if (byteBuf.readByte() != SocksSubnegotiationVersion.AUTH_PASSWORD.byteValue()) {
                    out.add(SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE);
                    break;
                }
                this.checkpoint(State.READ_AUTH_RESPONSE);
            }
            case READ_AUTH_RESPONSE: {
                SocksAuthStatus authStatus = SocksAuthStatus.valueOf(byteBuf.readByte());
                out.add(new SocksAuthResponse(authStatus));
                break;
            }
            default: {
                throw new Error();
            }
        }
        channelHandlerContext.pipeline().remove(this);
    }

    static enum State {
        CHECK_PROTOCOL_VERSION,
        READ_AUTH_RESPONSE;
        

        private State() {
        }
    }

}


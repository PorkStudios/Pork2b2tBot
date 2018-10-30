/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socks.SocksAuthRequest;
import io.netty.handler.codec.socks.SocksCommonUtils;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.handler.codec.socks.SocksSubnegotiationVersion;
import java.util.List;

public class SocksAuthRequestDecoder
extends ReplayingDecoder<State> {
    private String username;

    public SocksAuthRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        switch ((State)((Object)this.state())) {
            byte fieldLength;
            case CHECK_PROTOCOL_VERSION: {
                if (byteBuf.readByte() != SocksSubnegotiationVersion.AUTH_PASSWORD.byteValue()) {
                    out.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
                    break;
                }
                this.checkpoint(State.READ_USERNAME);
            }
            case READ_USERNAME: {
                fieldLength = byteBuf.readByte();
                this.username = SocksCommonUtils.readUsAscii(byteBuf, fieldLength);
                this.checkpoint(State.READ_PASSWORD);
            }
            case READ_PASSWORD: {
                fieldLength = byteBuf.readByte();
                String password = SocksCommonUtils.readUsAscii(byteBuf, fieldLength);
                out.add(new SocksAuthRequest(this.username, password));
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
        READ_USERNAME,
        READ_PASSWORD;
        

        private State() {
        }
    }

}


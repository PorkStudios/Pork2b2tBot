/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdType;
import io.netty.handler.codec.socks.SocksCommonUtils;
import io.netty.handler.codec.socks.SocksProtocolVersion;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.util.NetUtil;
import java.util.List;

public class SocksCmdRequestDecoder
extends ReplayingDecoder<State> {
    private SocksCmdType cmdType;
    private SocksAddressType addressType;

    public SocksCmdRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        block0 : switch ((State)((Object)this.state())) {
            case CHECK_PROTOCOL_VERSION: {
                if (byteBuf.readByte() != SocksProtocolVersion.SOCKS5.byteValue()) {
                    out.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
                    break;
                }
                this.checkpoint(State.READ_CMD_HEADER);
            }
            case READ_CMD_HEADER: {
                this.cmdType = SocksCmdType.valueOf(byteBuf.readByte());
                byteBuf.skipBytes(1);
                this.addressType = SocksAddressType.valueOf(byteBuf.readByte());
                this.checkpoint(State.READ_CMD_ADDRESS);
            }
            case READ_CMD_ADDRESS: {
                switch (this.addressType) {
                    case IPv4: {
                        String host = NetUtil.intToIpAddress(byteBuf.readInt());
                        int port = byteBuf.readUnsignedShort();
                        out.add(new SocksCmdRequest(this.cmdType, this.addressType, host, port));
                        break block0;
                    }
                    case DOMAIN: {
                        byte fieldLength = byteBuf.readByte();
                        String host = SocksCommonUtils.readUsAscii(byteBuf, fieldLength);
                        int port = byteBuf.readUnsignedShort();
                        out.add(new SocksCmdRequest(this.cmdType, this.addressType, host, port));
                        break block0;
                    }
                    case IPv6: {
                        byte[] bytes = new byte[16];
                        byteBuf.readBytes(bytes);
                        String host = SocksCommonUtils.ipv6toStr(bytes);
                        int port = byteBuf.readUnsignedShort();
                        out.add(new SocksCmdRequest(this.cmdType, this.addressType, host, port));
                        break block0;
                    }
                    case UNKNOWN: {
                        out.add(SocksCommonUtils.UNKNOWN_SOCKS_REQUEST);
                        break block0;
                    }
                }
                throw new Error();
            }
            default: {
                throw new Error();
            }
        }
        ctx.pipeline().remove(this);
    }

    static enum State {
        CHECK_PROTOCOL_VERSION,
        READ_CMD_HEADER,
        READ_CMD_ADDRESS;
        

        private State() {
        }
    }

}


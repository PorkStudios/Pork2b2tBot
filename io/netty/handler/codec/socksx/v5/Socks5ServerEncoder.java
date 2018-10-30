/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.Socks5AddressEncoder;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5Message;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import io.netty.util.internal.StringUtil;

@ChannelHandler.Sharable
public class Socks5ServerEncoder
extends MessageToByteEncoder<Socks5Message> {
    public static final Socks5ServerEncoder DEFAULT = new Socks5ServerEncoder(Socks5AddressEncoder.DEFAULT);
    private final Socks5AddressEncoder addressEncoder;

    protected Socks5ServerEncoder() {
        this(Socks5AddressEncoder.DEFAULT);
    }

    public Socks5ServerEncoder(Socks5AddressEncoder addressEncoder) {
        if (addressEncoder == null) {
            throw new NullPointerException("addressEncoder");
        }
        this.addressEncoder = addressEncoder;
    }

    protected final Socks5AddressEncoder addressEncoder() {
        return this.addressEncoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Socks5Message msg, ByteBuf out) throws Exception {
        if (msg instanceof Socks5InitialResponse) {
            Socks5ServerEncoder.encodeAuthMethodResponse((Socks5InitialResponse)msg, out);
        } else if (msg instanceof Socks5PasswordAuthResponse) {
            Socks5ServerEncoder.encodePasswordAuthResponse((Socks5PasswordAuthResponse)msg, out);
        } else if (msg instanceof Socks5CommandResponse) {
            this.encodeCommandResponse((Socks5CommandResponse)msg, out);
        } else {
            throw new EncoderException("unsupported message type: " + StringUtil.simpleClassName(msg));
        }
    }

    private static void encodeAuthMethodResponse(Socks5InitialResponse msg, ByteBuf out) {
        out.writeByte(msg.version().byteValue());
        out.writeByte(msg.authMethod().byteValue());
    }

    private static void encodePasswordAuthResponse(Socks5PasswordAuthResponse msg, ByteBuf out) {
        out.writeByte(1);
        out.writeByte(msg.status().byteValue());
    }

    private void encodeCommandResponse(Socks5CommandResponse msg, ByteBuf out) throws Exception {
        out.writeByte(msg.version().byteValue());
        out.writeByte(msg.status().byteValue());
        out.writeByte(0);
        Socks5AddressType bndAddrType = msg.bndAddrType();
        out.writeByte(bndAddrType.byteValue());
        this.addressEncoder.encodeAddress(bndAddrType, msg.bndAddr(), out);
        out.writeShort(msg.bndPort());
    }
}


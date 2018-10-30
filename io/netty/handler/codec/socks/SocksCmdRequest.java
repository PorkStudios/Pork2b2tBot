/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.socks.SocksAddressType;
import io.netty.handler.codec.socks.SocksCmdType;
import io.netty.handler.codec.socks.SocksProtocolVersion;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.handler.codec.socks.SocksRequestType;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.net.IDN;
import java.nio.charset.Charset;

public final class SocksCmdRequest
extends SocksRequest {
    private final SocksCmdType cmdType;
    private final SocksAddressType addressType;
    private final String host;
    private final int port;

    public SocksCmdRequest(SocksCmdType cmdType, SocksAddressType addressType, String host, int port) {
        super(SocksRequestType.CMD);
        if (cmdType == null) {
            throw new NullPointerException("cmdType");
        }
        if (addressType == null) {
            throw new NullPointerException("addressType");
        }
        if (host == null) {
            throw new NullPointerException("host");
        }
        switch (addressType) {
            case IPv4: {
                if (NetUtil.isValidIpV4Address(host)) break;
                throw new IllegalArgumentException(host + " is not a valid IPv4 address");
            }
            case DOMAIN: {
                String asciiHost = IDN.toASCII(host);
                if (asciiHost.length() > 255) {
                    throw new IllegalArgumentException(host + " IDN: " + asciiHost + " exceeds 255 char limit");
                }
                host = asciiHost;
                break;
            }
            case IPv6: {
                if (NetUtil.isValidIpV6Address(host)) break;
                throw new IllegalArgumentException(host + " is not a valid IPv6 address");
            }
        }
        if (port <= 0 || port >= 65536) {
            throw new IllegalArgumentException("" + port + " is not in bounds 0 < x < 65536");
        }
        this.cmdType = cmdType;
        this.addressType = addressType;
        this.host = host;
        this.port = port;
    }

    public SocksCmdType cmdType() {
        return this.cmdType;
    }

    public SocksAddressType addressType() {
        return this.addressType;
    }

    public String host() {
        return this.addressType == SocksAddressType.DOMAIN ? IDN.toUnicode(this.host) : this.host;
    }

    public int port() {
        return this.port;
    }

    @Override
    public void encodeAsByteBuf(ByteBuf byteBuf) {
        byteBuf.writeByte(this.protocolVersion().byteValue());
        byteBuf.writeByte(this.cmdType.byteValue());
        byteBuf.writeByte(0);
        byteBuf.writeByte(this.addressType.byteValue());
        switch (this.addressType) {
            case IPv4: {
                byteBuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(this.host));
                byteBuf.writeShort(this.port);
                break;
            }
            case DOMAIN: {
                byteBuf.writeByte(this.host.length());
                byteBuf.writeCharSequence(this.host, CharsetUtil.US_ASCII);
                byteBuf.writeShort(this.port);
                break;
            }
            case IPv6: {
                byteBuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(this.host));
                byteBuf.writeShort(this.port);
            }
        }
    }

}


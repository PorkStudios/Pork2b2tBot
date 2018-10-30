/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.haproxy.HAProxyCommand;
import io.netty.handler.codec.haproxy.HAProxyProtocolException;
import io.netty.handler.codec.haproxy.HAProxyProtocolVersion;
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol;
import io.netty.handler.codec.haproxy.HAProxySSLTLV;
import io.netty.handler.codec.haproxy.HAProxyTLV;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class HAProxyMessage {
    private static final HAProxyMessage V1_UNKNOWN_MSG = new HAProxyMessage(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY, HAProxyProxiedProtocol.UNKNOWN, null, null, 0, 0);
    private static final HAProxyMessage V2_UNKNOWN_MSG = new HAProxyMessage(HAProxyProtocolVersion.V2, HAProxyCommand.PROXY, HAProxyProxiedProtocol.UNKNOWN, null, null, 0, 0);
    private static final HAProxyMessage V2_LOCAL_MSG = new HAProxyMessage(HAProxyProtocolVersion.V2, HAProxyCommand.LOCAL, HAProxyProxiedProtocol.UNKNOWN, null, null, 0, 0);
    private final HAProxyProtocolVersion protocolVersion;
    private final HAProxyCommand command;
    private final HAProxyProxiedProtocol proxiedProtocol;
    private final String sourceAddress;
    private final String destinationAddress;
    private final int sourcePort;
    private final int destinationPort;
    private final List<HAProxyTLV> tlvs;

    private HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, String sourcePort, String destinationPort) {
        this(protocolVersion, command, proxiedProtocol, sourceAddress, destinationAddress, HAProxyMessage.portStringToInt(sourcePort), HAProxyMessage.portStringToInt(destinationPort));
    }

    private HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, int sourcePort, int destinationPort) {
        this(protocolVersion, command, proxiedProtocol, sourceAddress, destinationAddress, sourcePort, destinationPort, Collections.emptyList());
    }

    private HAProxyMessage(HAProxyProtocolVersion protocolVersion, HAProxyCommand command, HAProxyProxiedProtocol proxiedProtocol, String sourceAddress, String destinationAddress, int sourcePort, int destinationPort, List<HAProxyTLV> tlvs) {
        if (proxiedProtocol == null) {
            throw new NullPointerException("proxiedProtocol");
        }
        HAProxyProxiedProtocol.AddressFamily addrFamily = proxiedProtocol.addressFamily();
        HAProxyMessage.checkAddress(sourceAddress, addrFamily);
        HAProxyMessage.checkAddress(destinationAddress, addrFamily);
        HAProxyMessage.checkPort(sourcePort);
        HAProxyMessage.checkPort(destinationPort);
        this.protocolVersion = protocolVersion;
        this.command = command;
        this.proxiedProtocol = proxiedProtocol;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.tlvs = Collections.unmodifiableList(tlvs);
    }

    static HAProxyMessage decodeHeader(ByteBuf header) {
        HAProxyProtocolVersion ver;
        String srcAddress;
        HAProxyCommand cmd;
        HAProxyProxiedProtocol protAndFam;
        String dstAddress;
        if (header == null) {
            throw new NullPointerException("header");
        }
        if (header.readableBytes() < 16) {
            throw new HAProxyProtocolException("incomplete header: " + header.readableBytes() + " bytes (expected: 16+ bytes)");
        }
        header.skipBytes(12);
        byte verCmdByte = header.readByte();
        try {
            ver = HAProxyProtocolVersion.valueOf(verCmdByte);
        }
        catch (IllegalArgumentException e) {
            throw new HAProxyProtocolException(e);
        }
        if (ver != HAProxyProtocolVersion.V2) {
            throw new HAProxyProtocolException("version 1 unsupported: 0x" + Integer.toHexString(verCmdByte));
        }
        try {
            cmd = HAProxyCommand.valueOf(verCmdByte);
        }
        catch (IllegalArgumentException e) {
            throw new HAProxyProtocolException(e);
        }
        if (cmd == HAProxyCommand.LOCAL) {
            return V2_LOCAL_MSG;
        }
        try {
            protAndFam = HAProxyProxiedProtocol.valueOf(header.readByte());
        }
        catch (IllegalArgumentException e) {
            throw new HAProxyProtocolException(e);
        }
        if (protAndFam == HAProxyProxiedProtocol.UNKNOWN) {
            return V2_UNKNOWN_MSG;
        }
        int addressInfoLen = header.readUnsignedShort();
        int srcPort = 0;
        int dstPort = 0;
        HAProxyProxiedProtocol.AddressFamily addressFamily = protAndFam.addressFamily();
        if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_UNIX) {
            if (addressInfoLen < 216 || header.readableBytes() < 216) {
                throw new HAProxyProtocolException("incomplete UNIX socket address information: " + Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 216+ bytes)");
            }
            int startIdx = header.readerIndex();
            int addressEnd = header.forEachByte(startIdx, 108, ByteProcessor.FIND_NUL);
            int addressLen = addressEnd == -1 ? 108 : addressEnd - startIdx;
            srcAddress = header.toString(startIdx, addressLen, CharsetUtil.US_ASCII);
            addressEnd = header.forEachByte(startIdx += 108, 108, ByteProcessor.FIND_NUL);
            addressLen = addressEnd == -1 ? 108 : addressEnd - startIdx;
            dstAddress = header.toString(startIdx, addressLen, CharsetUtil.US_ASCII);
            header.readerIndex(startIdx + 108);
        } else {
            int addressLen;
            if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_IPv4) {
                if (addressInfoLen < 12 || header.readableBytes() < 12) {
                    throw new HAProxyProtocolException("incomplete IPv4 address information: " + Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 12+ bytes)");
                }
                addressLen = 4;
            } else if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_IPv6) {
                if (addressInfoLen < 36 || header.readableBytes() < 36) {
                    throw new HAProxyProtocolException("incomplete IPv6 address information: " + Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 36+ bytes)");
                }
                addressLen = 16;
            } else {
                throw new HAProxyProtocolException("unable to parse address information (unknown address family: " + (Object)((Object)addressFamily) + ')');
            }
            srcAddress = HAProxyMessage.ipBytesToString(header, addressLen);
            dstAddress = HAProxyMessage.ipBytesToString(header, addressLen);
            srcPort = header.readUnsignedShort();
            dstPort = header.readUnsignedShort();
        }
        List<HAProxyTLV> tlvs = HAProxyMessage.readTlvs(header);
        return new HAProxyMessage(ver, cmd, protAndFam, srcAddress, dstAddress, srcPort, dstPort, tlvs);
    }

    private static List<HAProxyTLV> readTlvs(ByteBuf header) {
        HAProxyTLV haProxyTLV = HAProxyMessage.readNextTLV(header);
        if (haProxyTLV == null) {
            return Collections.emptyList();
        }
        ArrayList<HAProxyTLV> haProxyTLVs = new ArrayList<HAProxyTLV>(4);
        do {
            haProxyTLVs.add(haProxyTLV);
            if (!(haProxyTLV instanceof HAProxySSLTLV)) continue;
            haProxyTLVs.addAll(((HAProxySSLTLV)haProxyTLV).encapsulatedTLVs());
        } while ((haProxyTLV = HAProxyMessage.readNextTLV(header)) != null);
        return haProxyTLVs;
    }

    private static HAProxyTLV readNextTLV(ByteBuf header) {
        if (header.readableBytes() < 4) {
            return null;
        }
        byte typeAsByte = header.readByte();
        HAProxyTLV.Type type = HAProxyTLV.Type.typeForByteValue(typeAsByte);
        int length = header.readUnsignedShort();
        switch (type) {
            case PP2_TYPE_SSL: {
                ByteBuf rawContent = header.retainedSlice(header.readerIndex(), length);
                ByteBuf byteBuf = header.readSlice(length);
                byte client = byteBuf.readByte();
                int verify = byteBuf.readInt();
                if (byteBuf.readableBytes() >= 4) {
                    HAProxyTLV haProxyTLV;
                    ArrayList<HAProxyTLV> encapsulatedTlvs = new ArrayList<HAProxyTLV>(4);
                    while ((haProxyTLV = HAProxyMessage.readNextTLV(byteBuf)) != null) {
                        encapsulatedTlvs.add(haProxyTLV);
                        if (byteBuf.readableBytes() >= 4) continue;
                    }
                    return new HAProxySSLTLV(verify, client, encapsulatedTlvs, rawContent);
                }
                return new HAProxySSLTLV(verify, client, Collections.<HAProxyTLV>emptyList(), rawContent);
            }
            case PP2_TYPE_ALPN: 
            case PP2_TYPE_AUTHORITY: 
            case PP2_TYPE_SSL_VERSION: 
            case PP2_TYPE_SSL_CN: 
            case PP2_TYPE_NETNS: 
            case OTHER: {
                return new HAProxyTLV(type, typeAsByte, header.readRetainedSlice(length));
            }
        }
        return null;
    }

    static HAProxyMessage decodeHeader(String header) {
        HAProxyProxiedProtocol protAndFam;
        if (header == null) {
            throw new HAProxyProtocolException("header");
        }
        String[] parts = header.split(" ");
        int numParts = parts.length;
        if (numParts < 2) {
            throw new HAProxyProtocolException("invalid header: " + header + " (expected: 'PROXY' and proxied protocol values)");
        }
        if (!"PROXY".equals(parts[0])) {
            throw new HAProxyProtocolException("unknown identifier: " + parts[0]);
        }
        try {
            protAndFam = HAProxyProxiedProtocol.valueOf(parts[1]);
        }
        catch (IllegalArgumentException e) {
            throw new HAProxyProtocolException(e);
        }
        if (protAndFam != HAProxyProxiedProtocol.TCP4 && protAndFam != HAProxyProxiedProtocol.TCP6 && protAndFam != HAProxyProxiedProtocol.UNKNOWN) {
            throw new HAProxyProtocolException("unsupported v1 proxied protocol: " + parts[1]);
        }
        if (protAndFam == HAProxyProxiedProtocol.UNKNOWN) {
            return V1_UNKNOWN_MSG;
        }
        if (numParts != 6) {
            throw new HAProxyProtocolException("invalid TCP4/6 header: " + header + " (expected: 6 parts)");
        }
        return new HAProxyMessage(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY, protAndFam, parts[2], parts[3], parts[4], parts[5]);
    }

    private static String ipBytesToString(ByteBuf header, int addressLen) {
        StringBuilder sb = new StringBuilder();
        if (addressLen == 4) {
            sb.append(header.readByte() & 255);
            sb.append('.');
            sb.append(header.readByte() & 255);
            sb.append('.');
            sb.append(header.readByte() & 255);
            sb.append('.');
            sb.append(header.readByte() & 255);
        } else {
            sb.append(Integer.toHexString(header.readUnsignedShort()));
            sb.append(':');
            sb.append(Integer.toHexString(header.readUnsignedShort()));
            sb.append(':');
            sb.append(Integer.toHexString(header.readUnsignedShort()));
            sb.append(':');
            sb.append(Integer.toHexString(header.readUnsignedShort()));
            sb.append(':');
            sb.append(Integer.toHexString(header.readUnsignedShort()));
            sb.append(':');
            sb.append(Integer.toHexString(header.readUnsignedShort()));
            sb.append(':');
            sb.append(Integer.toHexString(header.readUnsignedShort()));
            sb.append(':');
            sb.append(Integer.toHexString(header.readUnsignedShort()));
        }
        return sb.toString();
    }

    private static int portStringToInt(String value) {
        int port;
        try {
            port = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new HAProxyProtocolException("invalid port: " + value, e);
        }
        if (port <= 0 || port > 65535) {
            throw new HAProxyProtocolException("invalid port: " + value + " (expected: 1 ~ 65535)");
        }
        return port;
    }

    private static void checkAddress(String address, HAProxyProxiedProtocol.AddressFamily addrFamily) {
        if (addrFamily == null) {
            throw new NullPointerException("addrFamily");
        }
        switch (addrFamily) {
            case AF_UNSPEC: {
                if (address != null) {
                    throw new HAProxyProtocolException("unable to validate an AF_UNSPEC address: " + address);
                }
                return;
            }
            case AF_UNIX: {
                return;
            }
        }
        if (address == null) {
            throw new NullPointerException("address");
        }
        switch (addrFamily) {
            case AF_IPv4: {
                if (NetUtil.isValidIpV4Address(address)) break;
                throw new HAProxyProtocolException("invalid IPv4 address: " + address);
            }
            case AF_IPv6: {
                if (NetUtil.isValidIpV6Address(address)) break;
                throw new HAProxyProtocolException("invalid IPv6 address: " + address);
            }
            default: {
                throw new Error();
            }
        }
    }

    private static void checkPort(int port) {
        if (port < 0 || port > 65535) {
            throw new HAProxyProtocolException("invalid port: " + port + " (expected: 1 ~ 65535)");
        }
    }

    public HAProxyProtocolVersion protocolVersion() {
        return this.protocolVersion;
    }

    public HAProxyCommand command() {
        return this.command;
    }

    public HAProxyProxiedProtocol proxiedProtocol() {
        return this.proxiedProtocol;
    }

    public String sourceAddress() {
        return this.sourceAddress;
    }

    public String destinationAddress() {
        return this.destinationAddress;
    }

    public int sourcePort() {
        return this.sourcePort;
    }

    public int destinationPort() {
        return this.destinationPort;
    }

    public List<HAProxyTLV> tlvs() {
        return this.tlvs;
    }

}


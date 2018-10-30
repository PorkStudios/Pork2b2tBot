/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.unix;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class NativeInetAddress {
    private static final byte[] IPV4_MAPPED_IPV6_PREFIX = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1};
    final byte[] address;
    final int scopeId;

    public static NativeInetAddress newInstance(InetAddress addr) {
        byte[] bytes = addr.getAddress();
        if (addr instanceof Inet6Address) {
            return new NativeInetAddress(bytes, ((Inet6Address)addr).getScopeId());
        }
        return new NativeInetAddress(NativeInetAddress.ipv4MappedIpv6Address(bytes));
    }

    public NativeInetAddress(byte[] address, int scopeId) {
        this.address = address;
        this.scopeId = scopeId;
    }

    public NativeInetAddress(byte[] address) {
        this(address, 0);
    }

    public byte[] address() {
        return this.address;
    }

    public int scopeId() {
        return this.scopeId;
    }

    public static byte[] ipv4MappedIpv6Address(byte[] ipv4) {
        byte[] address = new byte[16];
        System.arraycopy(IPV4_MAPPED_IPV6_PREFIX, 0, address, 0, IPV4_MAPPED_IPV6_PREFIX.length);
        System.arraycopy(ipv4, 0, address, 12, ipv4.length);
        return address;
    }

    public static InetSocketAddress address(byte[] addr, int offset, int len) {
        int port = NativeInetAddress.decodeInt(addr, offset + len - 4);
        try {
            InetAddress address;
            switch (len) {
                case 8: {
                    byte[] ipv4 = new byte[4];
                    System.arraycopy(addr, offset, ipv4, 0, 4);
                    address = InetAddress.getByAddress(ipv4);
                    break;
                }
                case 24: {
                    byte[] ipv6 = new byte[16];
                    System.arraycopy(addr, offset, ipv6, 0, 16);
                    int scopeId = NativeInetAddress.decodeInt(addr, offset + len - 8);
                    address = Inet6Address.getByAddress(null, ipv6, scopeId);
                    break;
                }
                default: {
                    throw new Error();
                }
            }
            return new InetSocketAddress(address, port);
        }
        catch (UnknownHostException e) {
            throw new Error("Should never happen", e);
        }
    }

    static int decodeInt(byte[] addr, int index) {
        return (addr[index] & 255) << 24 | (addr[index + 1] & 255) << 16 | (addr[index + 2] & 255) << 8 | addr[index + 3] & 255;
    }
}


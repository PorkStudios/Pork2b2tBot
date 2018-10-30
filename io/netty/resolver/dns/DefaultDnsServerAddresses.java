/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddresses;
import java.net.InetSocketAddress;

abstract class DefaultDnsServerAddresses
extends DnsServerAddresses {
    protected final InetSocketAddress[] addresses;
    private final String strVal;

    DefaultDnsServerAddresses(String type, InetSocketAddress[] addresses) {
        this.addresses = addresses;
        StringBuilder buf = new StringBuilder(type.length() + 2 + addresses.length * 16);
        buf.append(type).append('(');
        for (InetSocketAddress a : addresses) {
            buf.append(a).append(", ");
        }
        buf.setLength(buf.length() - 2);
        buf.append(')');
        this.strVal = buf.toString();
    }

    public String toString() {
        return this.strVal;
    }
}


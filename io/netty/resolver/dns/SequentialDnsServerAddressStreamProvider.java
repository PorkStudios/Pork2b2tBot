/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddresses;
import io.netty.resolver.dns.UniSequentialDnsServerAddressStreamProvider;
import java.net.InetSocketAddress;

public final class SequentialDnsServerAddressStreamProvider
extends UniSequentialDnsServerAddressStreamProvider {
    public /* varargs */ SequentialDnsServerAddressStreamProvider(InetSocketAddress ... addresses) {
        super(DnsServerAddresses.sequential(addresses));
    }

    public SequentialDnsServerAddressStreamProvider(Iterable<? extends InetSocketAddress> addresses) {
        super(DnsServerAddresses.sequential(addresses));
    }
}


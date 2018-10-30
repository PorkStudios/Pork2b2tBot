/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DefaultDnsServerAddresses;
import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.SequentialDnsServerAddressStream;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class RotationalDnsServerAddresses
extends DefaultDnsServerAddresses {
    private static final AtomicIntegerFieldUpdater<RotationalDnsServerAddresses> startIdxUpdater = AtomicIntegerFieldUpdater.newUpdater(RotationalDnsServerAddresses.class, "startIdx");
    private volatile int startIdx;

    RotationalDnsServerAddresses(InetSocketAddress[] addresses) {
        super("rotational", addresses);
    }

    @Override
    public DnsServerAddressStream stream() {
        int nextStartIdx;
        int curStartIdx;
        do {
            if ((nextStartIdx = (curStartIdx = this.startIdx) + 1) < this.addresses.length) continue;
            nextStartIdx = 0;
        } while (!startIdxUpdater.compareAndSet(this, curStartIdx, nextStartIdx));
        return new SequentialDnsServerAddressStream(this.addresses, curStartIdx);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddresses;
import java.net.InetSocketAddress;

final class SingletonDnsServerAddresses
extends DnsServerAddresses {
    private final InetSocketAddress address;
    private final DnsServerAddressStream stream = new DnsServerAddressStream(){

        @Override
        public InetSocketAddress next() {
            return SingletonDnsServerAddresses.this.address;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public DnsServerAddressStream duplicate() {
            return this;
        }

        public String toString() {
            return SingletonDnsServerAddresses.this.toString();
        }
    };

    SingletonDnsServerAddresses(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public DnsServerAddressStream stream() {
        return this.stream;
    }

    public String toString() {
        return "singleton(" + this.address + ")";
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider;
import io.netty.resolver.dns.DefaultDnsServerAddresses;
import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.RotationalDnsServerAddresses;
import io.netty.resolver.dns.SequentialDnsServerAddressStream;
import io.netty.resolver.dns.ShuffledDnsServerAddressStream;
import io.netty.resolver.dns.SingletonDnsServerAddresses;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DnsServerAddresses {
    @Deprecated
    public static List<InetSocketAddress> defaultAddressList() {
        return DefaultDnsServerAddressStreamProvider.defaultAddressList();
    }

    @Deprecated
    public static DnsServerAddresses defaultAddresses() {
        return DefaultDnsServerAddressStreamProvider.defaultAddresses();
    }

    public static DnsServerAddresses sequential(Iterable<? extends InetSocketAddress> addresses) {
        return DnsServerAddresses.sequential0(DnsServerAddresses.sanitize(addresses));
    }

    public static /* varargs */ DnsServerAddresses sequential(InetSocketAddress ... addresses) {
        return DnsServerAddresses.sequential0(DnsServerAddresses.sanitize(addresses));
    }

    private static /* varargs */ DnsServerAddresses sequential0(InetSocketAddress ... addresses) {
        if (addresses.length == 1) {
            return DnsServerAddresses.singleton(addresses[0]);
        }
        return new DefaultDnsServerAddresses("sequential", addresses){

            @Override
            public DnsServerAddressStream stream() {
                return new SequentialDnsServerAddressStream(this.addresses, 0);
            }
        };
    }

    public static DnsServerAddresses shuffled(Iterable<? extends InetSocketAddress> addresses) {
        return DnsServerAddresses.shuffled0(DnsServerAddresses.sanitize(addresses));
    }

    public static /* varargs */ DnsServerAddresses shuffled(InetSocketAddress ... addresses) {
        return DnsServerAddresses.shuffled0(DnsServerAddresses.sanitize(addresses));
    }

    private static DnsServerAddresses shuffled0(InetSocketAddress[] addresses) {
        if (addresses.length == 1) {
            return DnsServerAddresses.singleton(addresses[0]);
        }
        return new DefaultDnsServerAddresses("shuffled", addresses){

            @Override
            public DnsServerAddressStream stream() {
                return new ShuffledDnsServerAddressStream(this.addresses);
            }
        };
    }

    public static DnsServerAddresses rotational(Iterable<? extends InetSocketAddress> addresses) {
        return DnsServerAddresses.rotational0(DnsServerAddresses.sanitize(addresses));
    }

    public static /* varargs */ DnsServerAddresses rotational(InetSocketAddress ... addresses) {
        return DnsServerAddresses.rotational0(DnsServerAddresses.sanitize(addresses));
    }

    private static DnsServerAddresses rotational0(InetSocketAddress[] addresses) {
        if (addresses.length == 1) {
            return DnsServerAddresses.singleton(addresses[0]);
        }
        return new RotationalDnsServerAddresses(addresses);
    }

    public static DnsServerAddresses singleton(InetSocketAddress address) {
        if (address == null) {
            throw new NullPointerException("address");
        }
        if (address.isUnresolved()) {
            throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + address);
        }
        return new SingletonDnsServerAddresses(address);
    }

    private static InetSocketAddress[] sanitize(Iterable<? extends InetSocketAddress> addresses) {
        if (addresses == null) {
            throw new NullPointerException("addresses");
        }
        ArrayList<InetSocketAddress> list = addresses instanceof Collection ? new ArrayList(((Collection)addresses).size()) : new ArrayList<InetSocketAddress>(4);
        for (InetSocketAddress a : addresses) {
            if (a == null) break;
            if (a.isUnresolved()) {
                throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + a);
            }
            list.add(a);
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("empty addresses");
        }
        return list.toArray(new InetSocketAddress[list.size()]);
    }

    private static InetSocketAddress[] sanitize(InetSocketAddress[] addresses) {
        if (addresses == null) {
            throw new NullPointerException("addresses");
        }
        ArrayList<InetSocketAddress> list = new ArrayList<InetSocketAddress>(addresses.length);
        for (InetSocketAddress a : addresses) {
            if (a == null) break;
            if (a.isUnresolved()) {
                throw new IllegalArgumentException("cannot use an unresolved DNS server address: " + a);
            }
            list.add(a);
        }
        if (list.isEmpty()) {
            return DefaultDnsServerAddressStreamProvider.defaultAddressArray();
        }
        return list.toArray(new InetSocketAddress[list.size()]);
    }

    public abstract DnsServerAddressStream stream();

}


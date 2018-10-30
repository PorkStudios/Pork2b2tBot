/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider;
import io.netty.util.internal.PlatformDependent;

public final class DnsServerAddressStreamProviders {
    private static final DnsServerAddressStreamProvider DEFAULT_DNS_SERVER_ADDRESS_STREAM_PROVIDER = PlatformDependent.isWindows() ? DefaultDnsServerAddressStreamProvider.INSTANCE : UnixResolverDnsServerAddressStreamProvider.parseSilently();

    private DnsServerAddressStreamProviders() {
    }

    public static DnsServerAddressStreamProvider platformDefault() {
        return DEFAULT_DNS_SERVER_ADDRESS_STREAM_PROVIDER;
    }
}


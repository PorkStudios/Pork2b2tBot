/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.resolver.dns.DefaultDnsCache;
import io.netty.resolver.dns.DnsCache;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsQueryLifecycleObserverFactory;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddressStreamProviders;
import io.netty.resolver.dns.NoopDnsQueryLifecycleObserverFactory;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;

public final class DnsNameResolverBuilder {
    private final EventLoop eventLoop;
    private ChannelFactory<? extends DatagramChannel> channelFactory;
    private DnsCache resolveCache;
    private DnsCache authoritativeDnsServerCache;
    private Integer minTtl;
    private Integer maxTtl;
    private Integer negativeTtl;
    private long queryTimeoutMillis = 5000L;
    private ResolvedAddressTypes resolvedAddressTypes = DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
    private boolean recursionDesired = true;
    private int maxQueriesPerResolve = 16;
    private boolean traceEnabled;
    private int maxPayloadSize = 4096;
    private boolean optResourceEnabled = true;
    private HostsFileEntriesResolver hostsFileEntriesResolver = HostsFileEntriesResolver.DEFAULT;
    private DnsServerAddressStreamProvider dnsServerAddressStreamProvider = DnsServerAddressStreamProviders.platformDefault();
    private DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory = NoopDnsQueryLifecycleObserverFactory.INSTANCE;
    private String[] searchDomains;
    private int ndots = -1;
    private boolean decodeIdn = true;

    public DnsNameResolverBuilder(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    public DnsNameResolverBuilder channelFactory(ChannelFactory<? extends DatagramChannel> channelFactory) {
        this.channelFactory = channelFactory;
        return this;
    }

    public DnsNameResolverBuilder channelType(Class<? extends DatagramChannel> channelType) {
        return this.channelFactory(new ReflectiveChannelFactory<DatagramChannel>(channelType));
    }

    public DnsNameResolverBuilder resolveCache(DnsCache resolveCache) {
        this.resolveCache = resolveCache;
        return this;
    }

    public DnsNameResolverBuilder dnsQueryLifecycleObserverFactory(DnsQueryLifecycleObserverFactory lifecycleObserverFactory) {
        this.dnsQueryLifecycleObserverFactory = ObjectUtil.checkNotNull(lifecycleObserverFactory, "lifecycleObserverFactory");
        return this;
    }

    public DnsNameResolverBuilder authoritativeDnsServerCache(DnsCache authoritativeDnsServerCache) {
        this.authoritativeDnsServerCache = authoritativeDnsServerCache;
        return this;
    }

    public DnsNameResolverBuilder ttl(int minTtl, int maxTtl) {
        this.maxTtl = maxTtl;
        this.minTtl = minTtl;
        return this;
    }

    public DnsNameResolverBuilder negativeTtl(int negativeTtl) {
        this.negativeTtl = negativeTtl;
        return this;
    }

    public DnsNameResolverBuilder queryTimeoutMillis(long queryTimeoutMillis) {
        this.queryTimeoutMillis = queryTimeoutMillis;
        return this;
    }

    public static /* varargs */ ResolvedAddressTypes computeResolvedAddressTypes(InternetProtocolFamily ... internetProtocolFamilies) {
        if (internetProtocolFamilies == null || internetProtocolFamilies.length == 0) {
            return DnsNameResolver.DEFAULT_RESOLVE_ADDRESS_TYPES;
        }
        if (internetProtocolFamilies.length > 2) {
            throw new IllegalArgumentException("No more than 2 InternetProtocolFamilies");
        }
        switch (internetProtocolFamilies[0]) {
            case IPv4: {
                return internetProtocolFamilies.length >= 2 && internetProtocolFamilies[1] == InternetProtocolFamily.IPv6 ? ResolvedAddressTypes.IPV4_PREFERRED : ResolvedAddressTypes.IPV4_ONLY;
            }
            case IPv6: {
                return internetProtocolFamilies.length >= 2 && internetProtocolFamilies[1] == InternetProtocolFamily.IPv4 ? ResolvedAddressTypes.IPV6_PREFERRED : ResolvedAddressTypes.IPV6_ONLY;
            }
        }
        throw new IllegalArgumentException("Couldn't resolve ResolvedAddressTypes from InternetProtocolFamily array");
    }

    public DnsNameResolverBuilder resolvedAddressTypes(ResolvedAddressTypes resolvedAddressTypes) {
        this.resolvedAddressTypes = resolvedAddressTypes;
        return this;
    }

    public DnsNameResolverBuilder recursionDesired(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
        return this;
    }

    public DnsNameResolverBuilder maxQueriesPerResolve(int maxQueriesPerResolve) {
        this.maxQueriesPerResolve = maxQueriesPerResolve;
        return this;
    }

    public DnsNameResolverBuilder traceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
        return this;
    }

    public DnsNameResolverBuilder maxPayloadSize(int maxPayloadSize) {
        this.maxPayloadSize = maxPayloadSize;
        return this;
    }

    public DnsNameResolverBuilder optResourceEnabled(boolean optResourceEnabled) {
        this.optResourceEnabled = optResourceEnabled;
        return this;
    }

    public DnsNameResolverBuilder hostsFileEntriesResolver(HostsFileEntriesResolver hostsFileEntriesResolver) {
        this.hostsFileEntriesResolver = hostsFileEntriesResolver;
        return this;
    }

    public DnsNameResolverBuilder nameServerProvider(DnsServerAddressStreamProvider dnsServerAddressStreamProvider) {
        this.dnsServerAddressStreamProvider = ObjectUtil.checkNotNull(dnsServerAddressStreamProvider, "dnsServerAddressStreamProvider");
        return this;
    }

    public DnsNameResolverBuilder searchDomains(Iterable<String> searchDomains) {
        ObjectUtil.checkNotNull(searchDomains, "searchDomains");
        ArrayList<String> list = new ArrayList<String>(4);
        for (String f : searchDomains) {
            if (f == null) break;
            if (list.contains(f)) continue;
            list.add(f);
        }
        this.searchDomains = list.toArray(new String[list.size()]);
        return this;
    }

    public DnsNameResolverBuilder ndots(int ndots) {
        this.ndots = ndots;
        return this;
    }

    private DnsCache newCache() {
        return new DefaultDnsCache(ObjectUtil.intValue(this.minTtl, 0), ObjectUtil.intValue(this.maxTtl, Integer.MAX_VALUE), ObjectUtil.intValue(this.negativeTtl, 0));
    }

    public DnsNameResolverBuilder decodeIdn(boolean decodeIdn) {
        this.decodeIdn = decodeIdn;
        return this;
    }

    public DnsNameResolver build() {
        if (this.resolveCache != null && (this.minTtl != null || this.maxTtl != null || this.negativeTtl != null)) {
            throw new IllegalStateException("resolveCache and TTLs are mutually exclusive");
        }
        if (this.authoritativeDnsServerCache != null && (this.minTtl != null || this.maxTtl != null || this.negativeTtl != null)) {
            throw new IllegalStateException("authoritativeDnsServerCache and TTLs are mutually exclusive");
        }
        DnsCache resolveCache = this.resolveCache != null ? this.resolveCache : this.newCache();
        DnsCache authoritativeDnsServerCache = this.authoritativeDnsServerCache != null ? this.authoritativeDnsServerCache : this.newCache();
        return new DnsNameResolver(this.eventLoop, this.channelFactory, resolveCache, authoritativeDnsServerCache, this.dnsQueryLifecycleObserverFactory, this.queryTimeoutMillis, this.resolvedAddressTypes, this.recursionDesired, this.maxQueriesPerResolve, this.traceEnabled, this.maxPayloadSize, this.optResourceEnabled, this.hostsFileEntriesResolver, this.dnsServerAddressStreamProvider, this.searchDomains, this.ndots, this.decodeIdn);
    }

}


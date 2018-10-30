/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.InetSocketAddressResolver;
import io.netty.resolver.NameResolver;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.InflightNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class DnsAddressResolverGroup
extends AddressResolverGroup<InetSocketAddress> {
    private final ChannelFactory<? extends DatagramChannel> channelFactory;
    private final DnsServerAddressStreamProvider nameServerProvider;
    private final ConcurrentMap<String, Promise<InetAddress>> resolvesInProgress = PlatformDependent.newConcurrentHashMap();
    private final ConcurrentMap<String, Promise<List<InetAddress>>> resolveAllsInProgress = PlatformDependent.newConcurrentHashMap();

    public DnsAddressResolverGroup(Class<? extends DatagramChannel> channelType, DnsServerAddressStreamProvider nameServerProvider) {
        this(new ReflectiveChannelFactory<DatagramChannel>(channelType), nameServerProvider);
    }

    public DnsAddressResolverGroup(ChannelFactory<? extends DatagramChannel> channelFactory, DnsServerAddressStreamProvider nameServerProvider) {
        this.channelFactory = channelFactory;
        this.nameServerProvider = nameServerProvider;
    }

    @Override
    protected final AddressResolver<InetSocketAddress> newResolver(EventExecutor executor) throws Exception {
        if (!(executor instanceof EventLoop)) {
            throw new IllegalStateException("unsupported executor type: " + StringUtil.simpleClassName(executor) + " (expected: " + StringUtil.simpleClassName(EventLoop.class));
        }
        return this.newResolver((EventLoop)executor, this.channelFactory, this.nameServerProvider);
    }

    @Deprecated
    protected AddressResolver<InetSocketAddress> newResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, DnsServerAddressStreamProvider nameServerProvider) throws Exception {
        InflightNameResolver<InetAddress> resolver = new InflightNameResolver<InetAddress>(eventLoop, this.newNameResolver(eventLoop, channelFactory, nameServerProvider), this.resolvesInProgress, this.resolveAllsInProgress);
        return this.newAddressResolver(eventLoop, resolver);
    }

    protected NameResolver<InetAddress> newNameResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, DnsServerAddressStreamProvider nameServerProvider) throws Exception {
        return new DnsNameResolverBuilder(eventLoop).channelFactory(channelFactory).nameServerProvider(nameServerProvider).build();
    }

    protected AddressResolver<InetSocketAddress> newAddressResolver(EventLoop eventLoop, NameResolver<InetAddress> resolver) throws Exception {
        return new InetSocketAddressResolver(eventLoop, resolver);
    }
}


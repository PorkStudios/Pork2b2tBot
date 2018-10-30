/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.resolver.dns.DnsQueryLifecycleObserver;
import java.net.InetSocketAddress;
import java.util.List;

final class NoopDnsQueryLifecycleObserver
implements DnsQueryLifecycleObserver {
    static final NoopDnsQueryLifecycleObserver INSTANCE = new NoopDnsQueryLifecycleObserver();

    private NoopDnsQueryLifecycleObserver() {
    }

    @Override
    public void queryWritten(InetSocketAddress dnsServerAddress, ChannelFuture future) {
    }

    @Override
    public void queryCancelled(int queriesRemaining) {
    }

    @Override
    public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> nameServers) {
        return this;
    }

    @Override
    public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion cnameQuestion) {
        return this;
    }

    @Override
    public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode code) {
        return this;
    }

    @Override
    public void queryFailed(Throwable cause) {
    }

    @Override
    public void querySucceed() {
    }
}


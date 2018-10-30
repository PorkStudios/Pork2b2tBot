/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.resolver.dns.DnsQueryLifecycleObserver;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

public final class BiDnsQueryLifecycleObserver
implements DnsQueryLifecycleObserver {
    private final DnsQueryLifecycleObserver a;
    private final DnsQueryLifecycleObserver b;

    public BiDnsQueryLifecycleObserver(DnsQueryLifecycleObserver a, DnsQueryLifecycleObserver b) {
        this.a = ObjectUtil.checkNotNull(a, "a");
        this.b = ObjectUtil.checkNotNull(b, "b");
    }

    @Override
    public void queryWritten(InetSocketAddress dnsServerAddress, ChannelFuture future) {
        try {
            this.a.queryWritten(dnsServerAddress, future);
        }
        finally {
            this.b.queryWritten(dnsServerAddress, future);
        }
    }

    @Override
    public void queryCancelled(int queriesRemaining) {
        try {
            this.a.queryCancelled(queriesRemaining);
        }
        finally {
            this.b.queryCancelled(queriesRemaining);
        }
    }

    @Override
    public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> nameServers) {
        try {
            this.a.queryRedirected(nameServers);
        }
        finally {
            this.b.queryRedirected(nameServers);
        }
        return this;
    }

    @Override
    public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion cnameQuestion) {
        try {
            this.a.queryCNAMEd(cnameQuestion);
        }
        finally {
            this.b.queryCNAMEd(cnameQuestion);
        }
        return this;
    }

    @Override
    public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode code) {
        try {
            this.a.queryNoAnswer(code);
        }
        finally {
            this.b.queryNoAnswer(code);
        }
        return this;
    }

    @Override
    public void queryFailed(Throwable cause) {
        try {
            this.a.queryFailed(cause);
        }
        finally {
            this.b.queryFailed(cause);
        }
    }

    @Override
    public void querySucceed() {
        try {
            this.a.querySucceed();
        }
        finally {
            this.b.querySucceed();
        }
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import java.net.InetSocketAddress;
import java.util.List;

public interface DnsQueryLifecycleObserver {
    public void queryWritten(InetSocketAddress var1, ChannelFuture var2);

    public void queryCancelled(int var1);

    public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> var1);

    public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion var1);

    public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode var1);

    public void queryFailed(Throwable var1);

    public void querySucceed();
}


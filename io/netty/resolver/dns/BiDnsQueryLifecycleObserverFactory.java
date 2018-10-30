/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.resolver.dns.BiDnsQueryLifecycleObserver;
import io.netty.resolver.dns.DnsQueryLifecycleObserver;
import io.netty.resolver.dns.DnsQueryLifecycleObserverFactory;
import io.netty.util.internal.ObjectUtil;

public final class BiDnsQueryLifecycleObserverFactory
implements DnsQueryLifecycleObserverFactory {
    private final DnsQueryLifecycleObserverFactory a;
    private final DnsQueryLifecycleObserverFactory b;

    public BiDnsQueryLifecycleObserverFactory(DnsQueryLifecycleObserverFactory a, DnsQueryLifecycleObserverFactory b) {
        this.a = ObjectUtil.checkNotNull(a, "a");
        this.b = ObjectUtil.checkNotNull(b, "b");
    }

    @Override
    public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion question) {
        return new BiDnsQueryLifecycleObserver(this.a.newDnsQueryLifecycleObserver(question), this.b.newDnsQueryLifecycleObserver(question));
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.resolver.dns.DnsQueryLifecycleObserver;
import io.netty.resolver.dns.DnsQueryLifecycleObserverFactory;
import io.netty.resolver.dns.NoopDnsQueryLifecycleObserver;

public final class NoopDnsQueryLifecycleObserverFactory
implements DnsQueryLifecycleObserverFactory {
    public static final NoopDnsQueryLifecycleObserverFactory INSTANCE = new NoopDnsQueryLifecycleObserverFactory();

    private NoopDnsQueryLifecycleObserverFactory() {
    }

    @Override
    public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion question) {
        return NoopDnsQueryLifecycleObserver.INSTANCE;
    }
}


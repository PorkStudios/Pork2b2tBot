/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.resolver.dns.DnsQueryLifecycleObserver;

public interface DnsQueryLifecycleObserverFactory {
    public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion var1);
}


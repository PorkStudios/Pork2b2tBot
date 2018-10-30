/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.resolver.dns.DnsCache;
import io.netty.resolver.dns.DnsCacheEntry;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public final class NoopDnsCache
implements DnsCache {
    public static final NoopDnsCache INSTANCE = new NoopDnsCache();

    private NoopDnsCache() {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean clear(String hostname) {
        return false;
    }

    @Override
    public List<? extends DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
        return Collections.emptyList();
    }

    @Override
    public DnsCacheEntry cache(String hostname, DnsRecord[] additional, InetAddress address, long originalTtl, EventLoop loop) {
        return new NoopDnsCacheEntry(address);
    }

    @Override
    public DnsCacheEntry cache(String hostname, DnsRecord[] additional, Throwable cause, EventLoop loop) {
        return null;
    }

    public String toString() {
        return NoopDnsCache.class.getSimpleName();
    }

    private static final class NoopDnsCacheEntry
    implements DnsCacheEntry {
        private final InetAddress address;

        NoopDnsCacheEntry(InetAddress address) {
            this.address = address;
        }

        @Override
        public InetAddress address() {
            return this.address;
        }

        @Override
        public Throwable cause() {
            return null;
        }

        public String toString() {
            return this.address.toString();
        }
    }

}


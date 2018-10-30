/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.resolver.dns.DnsCache;
import io.netty.resolver.dns.DnsCacheEntry;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class DefaultDnsCache
implements DnsCache {
    private final ConcurrentMap<String, List<DefaultDnsCacheEntry>> resolveCache = PlatformDependent.newConcurrentHashMap();
    private final int minTtl;
    private final int maxTtl;
    private final int negativeTtl;

    public DefaultDnsCache() {
        this(0, Integer.MAX_VALUE, 0);
    }

    public DefaultDnsCache(int minTtl, int maxTtl, int negativeTtl) {
        this.minTtl = ObjectUtil.checkPositiveOrZero(minTtl, "minTtl");
        this.maxTtl = ObjectUtil.checkPositiveOrZero(maxTtl, "maxTtl");
        if (minTtl > maxTtl) {
            throw new IllegalArgumentException("minTtl: " + minTtl + ", maxTtl: " + maxTtl + " (expected: 0 <= minTtl <= maxTtl)");
        }
        this.negativeTtl = ObjectUtil.checkPositiveOrZero(negativeTtl, "negativeTtl");
    }

    public int minTtl() {
        return this.minTtl;
    }

    public int maxTtl() {
        return this.maxTtl;
    }

    public int negativeTtl() {
        return this.negativeTtl;
    }

    @Override
    public void clear() {
        Iterator<Map.Entry<String, List<DefaultDnsCacheEntry>>> i = this.resolveCache.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, List<DefaultDnsCacheEntry>> e = i.next();
            i.remove();
            DefaultDnsCache.cancelExpiration(e.getValue());
        }
    }

    @Override
    public boolean clear(String hostname) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        boolean removed = false;
        Iterator<Map.Entry<String, List<DefaultDnsCacheEntry>>> i = this.resolveCache.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, List<DefaultDnsCacheEntry>> e = i.next();
            if (!e.getKey().equals(hostname)) continue;
            i.remove();
            DefaultDnsCache.cancelExpiration(e.getValue());
            removed = true;
        }
        return removed;
    }

    private static boolean emptyAdditionals(DnsRecord[] additionals) {
        return additionals == null || additionals.length == 0;
    }

    @Override
    public List<? extends DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
        ObjectUtil.checkNotNull(hostname, "hostname");
        if (!DefaultDnsCache.emptyAdditionals(additionals)) {
            return null;
        }
        return this.resolveCache.get(hostname);
    }

    private List<DefaultDnsCacheEntry> cachedEntries(String hostname) {
        ArrayList newEntries;
        ArrayList oldEntries = this.resolveCache.get(hostname);
        ArrayList entries = oldEntries == null ? ((oldEntries = (ArrayList)this.resolveCache.putIfAbsent(hostname, newEntries = new ArrayList(8))) != null ? oldEntries : newEntries) : oldEntries;
        return entries;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop) {
        List<DefaultDnsCacheEntry> entries;
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(address, "address");
        ObjectUtil.checkNotNull(loop, "loop");
        DefaultDnsCacheEntry e = new DefaultDnsCacheEntry(hostname, address);
        if (this.maxTtl == 0 || !DefaultDnsCache.emptyAdditionals(additionals)) {
            return e;
        }
        int ttl = Math.max(this.minTtl, (int)Math.min((long)this.maxTtl, originalTtl));
        List<DefaultDnsCacheEntry> list = entries = this.cachedEntries(hostname);
        synchronized (list) {
            DefaultDnsCacheEntry firstEntry;
            if (!entries.isEmpty() && (firstEntry = entries.get(0)).cause() != null) {
                assert (entries.size() == 1);
                firstEntry.cancelExpiration();
                entries.clear();
            }
            entries.add(e);
        }
        this.scheduleCacheExpiration(entries, e, ttl, loop);
        return e;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop) {
        List<DefaultDnsCacheEntry> entries;
        ObjectUtil.checkNotNull(hostname, "hostname");
        ObjectUtil.checkNotNull(cause, "cause");
        ObjectUtil.checkNotNull(loop, "loop");
        DefaultDnsCacheEntry e = new DefaultDnsCacheEntry(hostname, cause);
        if (this.negativeTtl == 0 || !DefaultDnsCache.emptyAdditionals(additionals)) {
            return e;
        }
        List<DefaultDnsCacheEntry> list = entries = this.cachedEntries(hostname);
        synchronized (list) {
            int numEntries = entries.size();
            for (int i = 0; i < numEntries; ++i) {
                entries.get(i).cancelExpiration();
            }
            entries.clear();
            entries.add(e);
        }
        this.scheduleCacheExpiration(entries, e, this.negativeTtl, loop);
        return e;
    }

    private static void cancelExpiration(List<DefaultDnsCacheEntry> entries) {
        int numEntries = entries.size();
        for (int i = 0; i < numEntries; ++i) {
            entries.get(i).cancelExpiration();
        }
    }

    private void scheduleCacheExpiration(final List<DefaultDnsCacheEntry> entries, final DefaultDnsCacheEntry e, int ttl, EventLoop loop) {
        e.scheduleExpiration(loop, new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                List list = entries;
                synchronized (list) {
                    entries.remove(e);
                    if (entries.isEmpty()) {
                        DefaultDnsCache.this.resolveCache.remove(e.hostname());
                    }
                }
            }
        }, ttl, TimeUnit.SECONDS);
    }

    public String toString() {
        return "DefaultDnsCache(minTtl=" + this.minTtl + ", maxTtl=" + this.maxTtl + ", negativeTtl=" + this.negativeTtl + ", cached resolved hostname=" + this.resolveCache.size() + ")";
    }

    private static final class DefaultDnsCacheEntry
    implements DnsCacheEntry {
        private final String hostname;
        private final InetAddress address;
        private final Throwable cause;
        private volatile ScheduledFuture<?> expirationFuture;

        DefaultDnsCacheEntry(String hostname, InetAddress address) {
            this.hostname = ObjectUtil.checkNotNull(hostname, "hostname");
            this.address = ObjectUtil.checkNotNull(address, "address");
            this.cause = null;
        }

        DefaultDnsCacheEntry(String hostname, Throwable cause) {
            this.hostname = ObjectUtil.checkNotNull(hostname, "hostname");
            this.cause = ObjectUtil.checkNotNull(cause, "cause");
            this.address = null;
        }

        @Override
        public InetAddress address() {
            return this.address;
        }

        @Override
        public Throwable cause() {
            return this.cause;
        }

        String hostname() {
            return this.hostname;
        }

        void scheduleExpiration(EventLoop loop, Runnable task, long delay, TimeUnit unit) {
            assert (this.expirationFuture == null);
            this.expirationFuture = loop.schedule(task, delay, unit);
        }

        void cancelExpiration() {
            ScheduledFuture<?> expirationFuture = this.expirationFuture;
            if (expirationFuture != null) {
                expirationFuture.cancel(false);
            }
        }

        public String toString() {
            if (this.cause != null) {
                return this.hostname + '/' + this.cause;
            }
            return this.address.toString();
        }
    }

}


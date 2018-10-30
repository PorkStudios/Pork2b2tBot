/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.resolver.dns.DnsCacheEntry;
import java.net.InetAddress;
import java.util.List;

public interface DnsCache {
    public void clear();

    public boolean clear(String var1);

    public List<? extends DnsCacheEntry> get(String var1, DnsRecord[] var2);

    public DnsCacheEntry cache(String var1, DnsRecord[] var2, InetAddress var3, long var4, EventLoop var6);

    public DnsCacheEntry cache(String var1, DnsRecord[] var2, Throwable var3, EventLoop var4);
}


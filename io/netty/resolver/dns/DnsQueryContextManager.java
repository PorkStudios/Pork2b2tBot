/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.resolver.dns.DnsQueryContext;
import io.netty.util.NetUtil;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.PlatformDependent;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

final class DnsQueryContextManager {
    final Map<InetSocketAddress, IntObjectMap<DnsQueryContext>> map = new HashMap<InetSocketAddress, IntObjectMap<DnsQueryContext>>();

    DnsQueryContextManager() {
    }

    int add(DnsQueryContext qCtx) {
        IntObjectMap<DnsQueryContext> contexts = this.getOrCreateContextMap(qCtx.nameServerAddr());
        int id = PlatformDependent.threadLocalRandom().nextInt(65535) + 1;
        int maxTries = 131070;
        int tries = 0;
        IntObjectMap<DnsQueryContext> intObjectMap = contexts;
        synchronized (intObjectMap) {
            do {
                if (!contexts.containsKey(id)) {
                    contexts.put(id, qCtx);
                    return id;
                }
                id = id + 1 & 65535;
            } while (++tries < 131070);
            throw new IllegalStateException("query ID space exhausted: " + qCtx.question());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    DnsQueryContext get(InetSocketAddress nameServerAddr, int id) {
        DnsQueryContext qCtx;
        IntObjectMap<DnsQueryContext> contexts = this.getContextMap(nameServerAddr);
        if (contexts != null) {
            IntObjectMap<DnsQueryContext> intObjectMap = contexts;
            synchronized (intObjectMap) {
                qCtx = contexts.get(id);
            }
        } else {
            qCtx = null;
        }
        return qCtx;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    DnsQueryContext remove(InetSocketAddress nameServerAddr, int id) {
        IntObjectMap<DnsQueryContext> contexts = this.getContextMap(nameServerAddr);
        if (contexts == null) {
            return null;
        }
        IntObjectMap<DnsQueryContext> intObjectMap = contexts;
        synchronized (intObjectMap) {
            return contexts.remove(id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private IntObjectMap<DnsQueryContext> getContextMap(InetSocketAddress nameServerAddr) {
        Map<InetSocketAddress, IntObjectMap<DnsQueryContext>> map = this.map;
        synchronized (map) {
            return this.map.get(nameServerAddr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private IntObjectMap<DnsQueryContext> getOrCreateContextMap(InetSocketAddress nameServerAddr) {
        Map<InetSocketAddress, IntObjectMap<DnsQueryContext>> map = this.map;
        synchronized (map) {
            IntObjectMap<DnsQueryContext> contexts = this.map.get(nameServerAddr);
            if (contexts != null) {
                return contexts;
            }
            IntObjectHashMap<DnsQueryContext> newContexts = new IntObjectHashMap<DnsQueryContext>();
            InetAddress a = nameServerAddr.getAddress();
            int port = nameServerAddr.getPort();
            this.map.put(nameServerAddr, newContexts);
            if (a instanceof Inet4Address) {
                Inet4Address a4 = (Inet4Address)a;
                if (a4.isLoopbackAddress()) {
                    this.map.put(new InetSocketAddress(NetUtil.LOCALHOST6, port), newContexts);
                } else {
                    this.map.put(new InetSocketAddress(DnsQueryContextManager.toCompactAddress(a4), port), newContexts);
                }
            } else if (a instanceof Inet6Address) {
                Inet6Address a6 = (Inet6Address)a;
                if (a6.isLoopbackAddress()) {
                    this.map.put(new InetSocketAddress(NetUtil.LOCALHOST4, port), newContexts);
                } else if (a6.isIPv4CompatibleAddress()) {
                    this.map.put(new InetSocketAddress(DnsQueryContextManager.toIPv4Address(a6), port), newContexts);
                }
            }
            return newContexts;
        }
    }

    private static Inet6Address toCompactAddress(Inet4Address a4) {
        byte[] b4 = a4.getAddress();
        byte[] b6 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, b4[0], b4[1], b4[2], b4[3]};
        try {
            return (Inet6Address)InetAddress.getByAddress(b6);
        }
        catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    private static Inet4Address toIPv4Address(Inet6Address a6) {
        byte[] b6 = a6.getAddress();
        byte[] b4 = new byte[]{b6[12], b6[13], b6[14], b6[15]};
        try {
            return (Inet4Address)InetAddress.getByAddress(b4);
        }
        catch (UnknownHostException e) {
            throw new Error(e);
        }
    }
}


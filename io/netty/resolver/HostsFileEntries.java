/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class HostsFileEntries {
    static final HostsFileEntries EMPTY = new HostsFileEntries(Collections.<String, Inet4Address>emptyMap(), Collections.<String, Inet6Address>emptyMap());
    private final Map<String, Inet4Address> inet4Entries;
    private final Map<String, Inet6Address> inet6Entries;

    public HostsFileEntries(Map<String, Inet4Address> inet4Entries, Map<String, Inet6Address> inet6Entries) {
        this.inet4Entries = Collections.unmodifiableMap(new HashMap<String, Inet4Address>(inet4Entries));
        this.inet6Entries = Collections.unmodifiableMap(new HashMap<String, Inet6Address>(inet6Entries));
    }

    public Map<String, Inet4Address> inet4Entries() {
        return this.inet4Entries;
    }

    public Map<String, Inet6Address> inet6Entries() {
        return this.inet6Entries;
    }
}


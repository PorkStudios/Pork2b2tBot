/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.Mapping;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class DomainNameMapping<V>
implements Mapping<String, V> {
    final V defaultValue;
    private final Map<String, V> map;
    private final Map<String, V> unmodifiableMap;

    @Deprecated
    public DomainNameMapping(V defaultValue) {
        this(4, defaultValue);
    }

    @Deprecated
    public DomainNameMapping(int initialCapacity, V defaultValue) {
        this(new LinkedHashMap(initialCapacity), defaultValue);
    }

    DomainNameMapping(Map<String, V> map, V defaultValue) {
        this.defaultValue = ObjectUtil.checkNotNull(defaultValue, "defaultValue");
        this.map = map;
        this.unmodifiableMap = map != null ? Collections.unmodifiableMap(map) : null;
    }

    @Deprecated
    public DomainNameMapping<V> add(String hostname, V output) {
        this.map.put(DomainNameMapping.normalizeHostname(ObjectUtil.checkNotNull(hostname, "hostname")), ObjectUtil.checkNotNull(output, "output"));
        return this;
    }

    static boolean matches(String template, String hostName) {
        if (template.startsWith("*.")) {
            return template.regionMatches(2, hostName, 0, hostName.length()) || StringUtil.commonSuffixOfLength(hostName, template, template.length() - 1);
        }
        return template.equals(hostName);
    }

    static String normalizeHostname(String hostname) {
        if (DomainNameMapping.needsNormalization(hostname)) {
            hostname = IDN.toASCII(hostname, 1);
        }
        return hostname.toLowerCase(Locale.US);
    }

    private static boolean needsNormalization(String hostname) {
        int length = hostname.length();
        for (int i = 0; i < length; ++i) {
            char c = hostname.charAt(i);
            if (c <= '') continue;
            return true;
        }
        return false;
    }

    @Override
    public V map(String hostname) {
        if (hostname != null) {
            hostname = DomainNameMapping.normalizeHostname(hostname);
            for (Map.Entry<String, V> entry : this.map.entrySet()) {
                if (!DomainNameMapping.matches(entry.getKey(), hostname)) continue;
                return entry.getValue();
            }
        }
        return this.defaultValue;
    }

    public Map<String, V> asMap() {
        return this.unmodifiableMap;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(default: " + this.defaultValue + ", map: " + this.map + ')';
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.CipherSuiteFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SupportedCipherSuiteFilter
implements CipherSuiteFilter {
    public static final SupportedCipherSuiteFilter INSTANCE = new SupportedCipherSuiteFilter();

    private SupportedCipherSuiteFilter() {
    }

    @Override
    public String[] filterCipherSuites(Iterable<String> ciphers, List<String> defaultCiphers, Set<String> supportedCiphers) {
        ArrayList<String> newCiphers;
        if (defaultCiphers == null) {
            throw new NullPointerException("defaultCiphers");
        }
        if (supportedCiphers == null) {
            throw new NullPointerException("supportedCiphers");
        }
        if (ciphers == null) {
            newCiphers = new ArrayList<String>(defaultCiphers.size());
            ciphers = defaultCiphers;
        } else {
            newCiphers = new ArrayList(supportedCiphers.size());
        }
        for (String c : ciphers) {
            if (c == null) break;
            if (!supportedCiphers.contains(c)) continue;
            newCiphers.add(c);
        }
        return newCiphers.toArray(new String[newCiphers.size()]);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsRecordType;

public interface DnsRecord {
    public static final int CLASS_IN = 1;
    public static final int CLASS_CSNET = 2;
    public static final int CLASS_CHAOS = 3;
    public static final int CLASS_HESIOD = 4;
    public static final int CLASS_NONE = 254;
    public static final int CLASS_ANY = 255;

    public String name();

    public DnsRecordType type();

    public int dnsClass();

    public long timeToLive();
}


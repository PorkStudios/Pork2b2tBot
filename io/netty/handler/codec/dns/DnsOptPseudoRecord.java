/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsRecord;

public interface DnsOptPseudoRecord
extends DnsRecord {
    public int extendedRcode();

    public int version();

    public int flags();
}


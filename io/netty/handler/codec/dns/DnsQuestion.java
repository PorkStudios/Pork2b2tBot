/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsRecord;

public interface DnsQuestion
extends DnsRecord {
    @Override
    public long timeToLive();
}


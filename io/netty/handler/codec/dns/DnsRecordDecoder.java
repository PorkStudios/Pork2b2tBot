/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.dns.DefaultDnsRecordDecoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;

public interface DnsRecordDecoder {
    public static final DnsRecordDecoder DEFAULT = new DefaultDnsRecordDecoder();

    public DnsQuestion decodeQuestion(ByteBuf var1) throws Exception;

    public <T extends DnsRecord> T decodeRecord(ByteBuf var1) throws Exception;
}


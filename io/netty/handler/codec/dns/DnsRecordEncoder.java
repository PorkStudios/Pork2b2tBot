/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.dns.DefaultDnsRecordEncoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;

public interface DnsRecordEncoder {
    public static final DnsRecordEncoder DEFAULT = new DefaultDnsRecordEncoder();

    public void encodeQuestion(DnsQuestion var1, ByteBuf var2) throws Exception;

    public void encodeRecord(DnsRecord var1, ByteBuf var2) throws Exception;
}


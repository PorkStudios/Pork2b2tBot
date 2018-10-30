/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsSection;

public interface DnsQuery
extends DnsMessage {
    @Override
    public DnsQuery setId(int var1);

    @Override
    public DnsQuery setOpCode(DnsOpCode var1);

    @Override
    public DnsQuery setRecursionDesired(boolean var1);

    @Override
    public DnsQuery setZ(int var1);

    @Override
    public DnsQuery setRecord(DnsSection var1, DnsRecord var2);

    @Override
    public DnsQuery addRecord(DnsSection var1, DnsRecord var2);

    @Override
    public DnsQuery addRecord(DnsSection var1, int var2, DnsRecord var3);

    @Override
    public DnsQuery clear(DnsSection var1);

    @Override
    public DnsQuery clear();

    @Override
    public DnsQuery touch();

    @Override
    public DnsQuery touch(Object var1);

    @Override
    public DnsQuery retain();

    @Override
    public DnsQuery retain(int var1);
}


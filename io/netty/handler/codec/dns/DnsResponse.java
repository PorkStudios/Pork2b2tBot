/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;

public interface DnsResponse
extends DnsMessage {
    public boolean isAuthoritativeAnswer();

    public DnsResponse setAuthoritativeAnswer(boolean var1);

    public boolean isTruncated();

    public DnsResponse setTruncated(boolean var1);

    public boolean isRecursionAvailable();

    public DnsResponse setRecursionAvailable(boolean var1);

    public DnsResponseCode code();

    public DnsResponse setCode(DnsResponseCode var1);

    @Override
    public DnsResponse setId(int var1);

    @Override
    public DnsResponse setOpCode(DnsOpCode var1);

    @Override
    public DnsResponse setRecursionDesired(boolean var1);

    @Override
    public DnsResponse setZ(int var1);

    @Override
    public DnsResponse setRecord(DnsSection var1, DnsRecord var2);

    @Override
    public DnsResponse addRecord(DnsSection var1, DnsRecord var2);

    @Override
    public DnsResponse addRecord(DnsSection var1, int var2, DnsRecord var3);

    @Override
    public DnsResponse clear(DnsSection var1);

    @Override
    public DnsResponse clear();

    @Override
    public DnsResponse touch();

    @Override
    public DnsResponse touch(Object var1);

    @Override
    public DnsResponse retain();

    @Override
    public DnsResponse retain(int var1);
}


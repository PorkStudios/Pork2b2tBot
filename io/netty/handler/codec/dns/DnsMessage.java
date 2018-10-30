/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCounted;

public interface DnsMessage
extends ReferenceCounted {
    public int id();

    public DnsMessage setId(int var1);

    public DnsOpCode opCode();

    public DnsMessage setOpCode(DnsOpCode var1);

    public boolean isRecursionDesired();

    public DnsMessage setRecursionDesired(boolean var1);

    public int z();

    public DnsMessage setZ(int var1);

    public int count(DnsSection var1);

    public int count();

    public <T extends DnsRecord> T recordAt(DnsSection var1);

    public <T extends DnsRecord> T recordAt(DnsSection var1, int var2);

    public DnsMessage setRecord(DnsSection var1, DnsRecord var2);

    public <T extends DnsRecord> T setRecord(DnsSection var1, int var2, DnsRecord var3);

    public DnsMessage addRecord(DnsSection var1, DnsRecord var2);

    public DnsMessage addRecord(DnsSection var1, int var2, DnsRecord var3);

    public <T extends DnsRecord> T removeRecord(DnsSection var1, int var2);

    public DnsMessage clear(DnsSection var1);

    public DnsMessage clear();

    @Override
    public DnsMessage touch();

    @Override
    public DnsMessage touch(Object var1);

    @Override
    public DnsMessage retain();

    @Override
    public DnsMessage retain(int var1);
}


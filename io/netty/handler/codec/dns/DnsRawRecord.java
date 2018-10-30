/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.dns.DnsRecord;

public interface DnsRawRecord
extends DnsRecord,
ByteBufHolder {
    @Override
    public DnsRawRecord copy();

    @Override
    public DnsRawRecord duplicate();

    @Override
    public DnsRawRecord retainedDuplicate();

    @Override
    public DnsRawRecord replace(ByteBuf var1);

    @Override
    public DnsRawRecord retain();

    @Override
    public DnsRawRecord retain(int var1);

    @Override
    public DnsRawRecord touch();

    @Override
    public DnsRawRecord touch(Object var1);
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.dns.AbstractDnsRecord;
import io.netty.handler.codec.dns.DnsMessageUtil;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultDnsRawRecord
extends AbstractDnsRecord
implements DnsRawRecord {
    private final ByteBuf content;

    public DefaultDnsRawRecord(String name, DnsRecordType type, long timeToLive, ByteBuf content) {
        this(name, type, 1, timeToLive, content);
    }

    public DefaultDnsRawRecord(String name, DnsRecordType type, int dnsClass, long timeToLive, ByteBuf content) {
        super(name, type, dnsClass, timeToLive);
        this.content = ObjectUtil.checkNotNull(content, "content");
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public DnsRawRecord copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public DnsRawRecord duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public DnsRawRecord retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public DnsRawRecord replace(ByteBuf content) {
        return new DefaultDnsRawRecord(this.name(), this.type(), this.dnsClass(), this.timeToLive(), content);
    }

    @Override
    public int refCnt() {
        return this.content().refCnt();
    }

    @Override
    public DnsRawRecord retain() {
        this.content().retain();
        return this;
    }

    @Override
    public DnsRawRecord retain(int increment) {
        this.content().retain(increment);
        return this;
    }

    @Override
    public boolean release() {
        return this.content().release();
    }

    @Override
    public boolean release(int decrement) {
        return this.content().release(decrement);
    }

    @Override
    public DnsRawRecord touch() {
        this.content().touch();
        return this;
    }

    @Override
    public DnsRawRecord touch(Object hint) {
        this.content().touch(hint);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(64).append(StringUtil.simpleClassName(this)).append('(');
        DnsRecordType type = this.type();
        if (type != DnsRecordType.OPT) {
            buf.append(this.name().isEmpty() ? "<root>" : this.name()).append(' ').append(this.timeToLive()).append(' ');
            DnsMessageUtil.appendRecordClass(buf, this.dnsClass()).append(' ').append(type.name());
        } else {
            buf.append("OPT flags:").append(this.timeToLive()).append(" udp:").append(this.dnsClass());
        }
        buf.append(' ').append(this.content().readableBytes()).append("B)");
        return buf.toString();
    }
}


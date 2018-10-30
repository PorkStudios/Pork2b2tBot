/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.AbstractDnsMessage;
import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsMessageUtil;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCounted;

public class DefaultDnsQuery
extends AbstractDnsMessage
implements DnsQuery {
    public DefaultDnsQuery(int id) {
        super(id);
    }

    public DefaultDnsQuery(int id, DnsOpCode opCode) {
        super(id, opCode);
    }

    @Override
    public DnsQuery setId(int id) {
        return (DnsQuery)super.setId(id);
    }

    @Override
    public DnsQuery setOpCode(DnsOpCode opCode) {
        return (DnsQuery)super.setOpCode(opCode);
    }

    @Override
    public DnsQuery setRecursionDesired(boolean recursionDesired) {
        return (DnsQuery)super.setRecursionDesired(recursionDesired);
    }

    @Override
    public DnsQuery setZ(int z) {
        return (DnsQuery)super.setZ(z);
    }

    @Override
    public DnsQuery setRecord(DnsSection section, DnsRecord record) {
        return (DnsQuery)super.setRecord(section, record);
    }

    @Override
    public DnsQuery addRecord(DnsSection section, DnsRecord record) {
        return (DnsQuery)super.addRecord(section, record);
    }

    @Override
    public DnsQuery addRecord(DnsSection section, int index, DnsRecord record) {
        return (DnsQuery)super.addRecord(section, index, record);
    }

    @Override
    public DnsQuery clear(DnsSection section) {
        return (DnsQuery)super.clear(section);
    }

    @Override
    public DnsQuery clear() {
        return (DnsQuery)super.clear();
    }

    @Override
    public DnsQuery touch() {
        return (DnsQuery)super.touch();
    }

    @Override
    public DnsQuery touch(Object hint) {
        return (DnsQuery)super.touch(hint);
    }

    @Override
    public DnsQuery retain() {
        return (DnsQuery)super.retain();
    }

    @Override
    public DnsQuery retain(int increment) {
        return (DnsQuery)super.retain(increment);
    }

    public String toString() {
        return DnsMessageUtil.appendQuery(new StringBuilder(128), this).toString();
    }
}


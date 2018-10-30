/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.handler.codec.dns.DefaultDnsQuery;
import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCounted;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class DatagramDnsQuery
extends DefaultDnsQuery
implements AddressedEnvelope<DatagramDnsQuery, InetSocketAddress> {
    private final InetSocketAddress sender;
    private final InetSocketAddress recipient;

    public DatagramDnsQuery(InetSocketAddress sender, InetSocketAddress recipient, int id) {
        this(sender, recipient, id, DnsOpCode.QUERY);
    }

    public DatagramDnsQuery(InetSocketAddress sender, InetSocketAddress recipient, int id, DnsOpCode opCode) {
        super(id, opCode);
        if (recipient == null && sender == null) {
            throw new NullPointerException("recipient and sender");
        }
        this.sender = sender;
        this.recipient = recipient;
    }

    @Override
    public DatagramDnsQuery content() {
        return this;
    }

    @Override
    public InetSocketAddress sender() {
        return this.sender;
    }

    @Override
    public InetSocketAddress recipient() {
        return this.recipient;
    }

    @Override
    public DatagramDnsQuery setId(int id) {
        return (DatagramDnsQuery)super.setId(id);
    }

    @Override
    public DatagramDnsQuery setOpCode(DnsOpCode opCode) {
        return (DatagramDnsQuery)super.setOpCode(opCode);
    }

    @Override
    public DatagramDnsQuery setRecursionDesired(boolean recursionDesired) {
        return (DatagramDnsQuery)super.setRecursionDesired(recursionDesired);
    }

    @Override
    public DatagramDnsQuery setZ(int z) {
        return (DatagramDnsQuery)super.setZ(z);
    }

    @Override
    public DatagramDnsQuery setRecord(DnsSection section, DnsRecord record) {
        return (DatagramDnsQuery)super.setRecord(section, record);
    }

    @Override
    public DatagramDnsQuery addRecord(DnsSection section, DnsRecord record) {
        return (DatagramDnsQuery)super.addRecord(section, record);
    }

    @Override
    public DatagramDnsQuery addRecord(DnsSection section, int index, DnsRecord record) {
        return (DatagramDnsQuery)super.addRecord(section, index, record);
    }

    @Override
    public DatagramDnsQuery clear(DnsSection section) {
        return (DatagramDnsQuery)super.clear(section);
    }

    @Override
    public DatagramDnsQuery clear() {
        return (DatagramDnsQuery)super.clear();
    }

    @Override
    public DatagramDnsQuery touch() {
        return (DatagramDnsQuery)super.touch();
    }

    @Override
    public DatagramDnsQuery touch(Object hint) {
        return (DatagramDnsQuery)super.touch(hint);
    }

    @Override
    public DatagramDnsQuery retain() {
        return (DatagramDnsQuery)super.retain();
    }

    @Override
    public DatagramDnsQuery retain(int increment) {
        return (DatagramDnsQuery)super.retain(increment);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof AddressedEnvelope)) {
            return false;
        }
        AddressedEnvelope that = (AddressedEnvelope)obj;
        if (this.sender() == null ? that.sender() != null : !this.sender().equals(that.sender())) {
            return false;
        }
        if (this.recipient() == null ? that.recipient() != null : !this.recipient().equals(that.recipient())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        if (this.sender() != null) {
            hashCode = hashCode * 31 + this.sender().hashCode();
        }
        if (this.recipient() != null) {
            hashCode = hashCode * 31 + this.recipient().hashCode();
        }
        return hashCode;
    }
}


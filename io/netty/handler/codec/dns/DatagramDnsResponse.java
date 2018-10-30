/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.handler.codec.dns.DefaultDnsResponse;
import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCounted;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class DatagramDnsResponse
extends DefaultDnsResponse
implements AddressedEnvelope<DatagramDnsResponse, InetSocketAddress> {
    private final InetSocketAddress sender;
    private final InetSocketAddress recipient;

    public DatagramDnsResponse(InetSocketAddress sender, InetSocketAddress recipient, int id) {
        this(sender, recipient, id, DnsOpCode.QUERY, DnsResponseCode.NOERROR);
    }

    public DatagramDnsResponse(InetSocketAddress sender, InetSocketAddress recipient, int id, DnsOpCode opCode) {
        this(sender, recipient, id, opCode, DnsResponseCode.NOERROR);
    }

    public DatagramDnsResponse(InetSocketAddress sender, InetSocketAddress recipient, int id, DnsOpCode opCode, DnsResponseCode responseCode) {
        super(id, opCode, responseCode);
        if (recipient == null && sender == null) {
            throw new NullPointerException("recipient and sender");
        }
        this.sender = sender;
        this.recipient = recipient;
    }

    @Override
    public DatagramDnsResponse content() {
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
    public DatagramDnsResponse setAuthoritativeAnswer(boolean authoritativeAnswer) {
        return (DatagramDnsResponse)super.setAuthoritativeAnswer(authoritativeAnswer);
    }

    @Override
    public DatagramDnsResponse setTruncated(boolean truncated) {
        return (DatagramDnsResponse)super.setTruncated(truncated);
    }

    @Override
    public DatagramDnsResponse setRecursionAvailable(boolean recursionAvailable) {
        return (DatagramDnsResponse)super.setRecursionAvailable(recursionAvailable);
    }

    @Override
    public DatagramDnsResponse setCode(DnsResponseCode code) {
        return (DatagramDnsResponse)super.setCode(code);
    }

    @Override
    public DatagramDnsResponse setId(int id) {
        return (DatagramDnsResponse)super.setId(id);
    }

    @Override
    public DatagramDnsResponse setOpCode(DnsOpCode opCode) {
        return (DatagramDnsResponse)super.setOpCode(opCode);
    }

    @Override
    public DatagramDnsResponse setRecursionDesired(boolean recursionDesired) {
        return (DatagramDnsResponse)super.setRecursionDesired(recursionDesired);
    }

    @Override
    public DatagramDnsResponse setZ(int z) {
        return (DatagramDnsResponse)super.setZ(z);
    }

    @Override
    public DatagramDnsResponse setRecord(DnsSection section, DnsRecord record) {
        return (DatagramDnsResponse)super.setRecord(section, record);
    }

    @Override
    public DatagramDnsResponse addRecord(DnsSection section, DnsRecord record) {
        return (DatagramDnsResponse)super.addRecord(section, record);
    }

    @Override
    public DatagramDnsResponse addRecord(DnsSection section, int index, DnsRecord record) {
        return (DatagramDnsResponse)super.addRecord(section, index, record);
    }

    @Override
    public DatagramDnsResponse clear(DnsSection section) {
        return (DatagramDnsResponse)super.clear(section);
    }

    @Override
    public DatagramDnsResponse clear() {
        return (DatagramDnsResponse)super.clear();
    }

    @Override
    public DatagramDnsResponse touch() {
        return (DatagramDnsResponse)super.touch();
    }

    @Override
    public DatagramDnsResponse touch(Object hint) {
        return (DatagramDnsResponse)super.touch(hint);
    }

    @Override
    public DatagramDnsResponse retain() {
        return (DatagramDnsResponse)super.retain();
    }

    @Override
    public DatagramDnsResponse retain(int increment) {
        return (DatagramDnsResponse)super.retain(increment);
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


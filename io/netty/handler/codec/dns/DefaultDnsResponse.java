/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.AbstractDnsMessage;
import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsMessageUtil;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;

public class DefaultDnsResponse
extends AbstractDnsMessage
implements DnsResponse {
    private boolean authoritativeAnswer;
    private boolean truncated;
    private boolean recursionAvailable;
    private DnsResponseCode code;

    public DefaultDnsResponse(int id) {
        this(id, DnsOpCode.QUERY, DnsResponseCode.NOERROR);
    }

    public DefaultDnsResponse(int id, DnsOpCode opCode) {
        this(id, opCode, DnsResponseCode.NOERROR);
    }

    public DefaultDnsResponse(int id, DnsOpCode opCode, DnsResponseCode code) {
        super(id, opCode);
        this.setCode(code);
    }

    @Override
    public boolean isAuthoritativeAnswer() {
        return this.authoritativeAnswer;
    }

    @Override
    public DnsResponse setAuthoritativeAnswer(boolean authoritativeAnswer) {
        this.authoritativeAnswer = authoritativeAnswer;
        return this;
    }

    @Override
    public boolean isTruncated() {
        return this.truncated;
    }

    @Override
    public DnsResponse setTruncated(boolean truncated) {
        this.truncated = truncated;
        return this;
    }

    @Override
    public boolean isRecursionAvailable() {
        return this.recursionAvailable;
    }

    @Override
    public DnsResponse setRecursionAvailable(boolean recursionAvailable) {
        this.recursionAvailable = recursionAvailable;
        return this;
    }

    @Override
    public DnsResponseCode code() {
        return this.code;
    }

    @Override
    public DnsResponse setCode(DnsResponseCode code) {
        this.code = ObjectUtil.checkNotNull(code, "code");
        return this;
    }

    @Override
    public DnsResponse setId(int id) {
        return (DnsResponse)super.setId(id);
    }

    @Override
    public DnsResponse setOpCode(DnsOpCode opCode) {
        return (DnsResponse)super.setOpCode(opCode);
    }

    @Override
    public DnsResponse setRecursionDesired(boolean recursionDesired) {
        return (DnsResponse)super.setRecursionDesired(recursionDesired);
    }

    @Override
    public DnsResponse setZ(int z) {
        return (DnsResponse)super.setZ(z);
    }

    @Override
    public DnsResponse setRecord(DnsSection section, DnsRecord record) {
        return (DnsResponse)super.setRecord(section, record);
    }

    @Override
    public DnsResponse addRecord(DnsSection section, DnsRecord record) {
        return (DnsResponse)super.addRecord(section, record);
    }

    @Override
    public DnsResponse addRecord(DnsSection section, int index, DnsRecord record) {
        return (DnsResponse)super.addRecord(section, index, record);
    }

    @Override
    public DnsResponse clear(DnsSection section) {
        return (DnsResponse)super.clear(section);
    }

    @Override
    public DnsResponse clear() {
        return (DnsResponse)super.clear();
    }

    @Override
    public DnsResponse touch() {
        return (DnsResponse)super.touch();
    }

    @Override
    public DnsResponse touch(Object hint) {
        return (DnsResponse)super.touch(hint);
    }

    @Override
    public DnsResponse retain() {
        return (DnsResponse)super.retain();
    }

    @Override
    public DnsResponse retain(int increment) {
        return (DnsResponse)super.retain(increment);
    }

    public String toString() {
        return DnsMessageUtil.appendResponse(new StringBuilder(128), this).toString();
    }
}


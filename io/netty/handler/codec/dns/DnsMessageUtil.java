/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.handler.codec.dns.DnsMessage;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.internal.StringUtil;

final class DnsMessageUtil {
    static StringBuilder appendQuery(StringBuilder buf, DnsQuery query) {
        DnsMessageUtil.appendQueryHeader(buf, query);
        DnsMessageUtil.appendAllRecords(buf, query);
        return buf;
    }

    static StringBuilder appendResponse(StringBuilder buf, DnsResponse response) {
        DnsMessageUtil.appendResponseHeader(buf, response);
        DnsMessageUtil.appendAllRecords(buf, response);
        return buf;
    }

    static StringBuilder appendRecordClass(StringBuilder buf, int dnsClass) {
        String name;
        switch (dnsClass &= 65535) {
            case 1: {
                name = "IN";
                break;
            }
            case 2: {
                name = "CSNET";
                break;
            }
            case 3: {
                name = "CHAOS";
                break;
            }
            case 4: {
                name = "HESIOD";
                break;
            }
            case 254: {
                name = "NONE";
                break;
            }
            case 255: {
                name = "ANY";
                break;
            }
            default: {
                name = null;
            }
        }
        if (name != null) {
            buf.append(name);
        } else {
            buf.append("UNKNOWN(").append(dnsClass).append(')');
        }
        return buf;
    }

    private static void appendQueryHeader(StringBuilder buf, DnsQuery msg) {
        buf.append(StringUtil.simpleClassName(msg)).append('(');
        DnsMessageUtil.appendAddresses(buf, msg).append(msg.id()).append(", ").append(msg.opCode());
        if (msg.isRecursionDesired()) {
            buf.append(", RD");
        }
        if (msg.z() != 0) {
            buf.append(", Z: ").append(msg.z());
        }
        buf.append(')');
    }

    private static void appendResponseHeader(StringBuilder buf, DnsResponse msg) {
        buf.append(StringUtil.simpleClassName(msg)).append('(');
        DnsMessageUtil.appendAddresses(buf, msg).append(msg.id()).append(", ").append(msg.opCode()).append(", ").append(msg.code()).append(',');
        boolean hasComma = true;
        if (msg.isRecursionDesired()) {
            hasComma = false;
            buf.append(" RD");
        }
        if (msg.isAuthoritativeAnswer()) {
            hasComma = false;
            buf.append(" AA");
        }
        if (msg.isTruncated()) {
            hasComma = false;
            buf.append(" TC");
        }
        if (msg.isRecursionAvailable()) {
            hasComma = false;
            buf.append(" RA");
        }
        if (msg.z() != 0) {
            if (!hasComma) {
                buf.append(',');
            }
            buf.append(" Z: ").append(msg.z());
        }
        if (hasComma) {
            buf.setCharAt(buf.length() - 1, ')');
        } else {
            buf.append(')');
        }
    }

    private static StringBuilder appendAddresses(StringBuilder buf, DnsMessage msg) {
        if (!(msg instanceof AddressedEnvelope)) {
            return buf;
        }
        AddressedEnvelope envelope = (AddressedEnvelope)((Object)msg);
        Object addr = envelope.sender();
        if (addr != null) {
            buf.append("from: ").append(addr).append(", ");
        }
        if ((addr = envelope.recipient()) != null) {
            buf.append("to: ").append(addr).append(", ");
        }
        return buf;
    }

    private static void appendAllRecords(StringBuilder buf, DnsMessage msg) {
        DnsMessageUtil.appendRecords(buf, msg, DnsSection.QUESTION);
        DnsMessageUtil.appendRecords(buf, msg, DnsSection.ANSWER);
        DnsMessageUtil.appendRecords(buf, msg, DnsSection.AUTHORITY);
        DnsMessageUtil.appendRecords(buf, msg, DnsSection.ADDITIONAL);
    }

    private static void appendRecords(StringBuilder buf, DnsMessage message, DnsSection section) {
        int count = message.count(section);
        for (int i = 0; i < count; ++i) {
            buf.append(StringUtil.NEWLINE).append('\t').append(message.recordAt(section, i));
        }
    }

    private DnsMessageUtil() {
    }
}


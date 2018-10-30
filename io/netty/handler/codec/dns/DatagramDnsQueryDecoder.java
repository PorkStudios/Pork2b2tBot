/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordDecoder;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsQueryDecoder
extends MessageToMessageDecoder<DatagramPacket> {
    private final DnsRecordDecoder recordDecoder;

    public DatagramDnsQueryDecoder() {
        this(DnsRecordDecoder.DEFAULT);
    }

    public DatagramDnsQueryDecoder(DnsRecordDecoder recordDecoder) {
        this.recordDecoder = ObjectUtil.checkNotNull(recordDecoder, "recordDecoder");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
        ByteBuf buf = (ByteBuf)packet.content();
        DnsQuery query = DatagramDnsQueryDecoder.newQuery(packet, buf);
        boolean success = false;
        try {
            int questionCount = buf.readUnsignedShort();
            int answerCount = buf.readUnsignedShort();
            int authorityRecordCount = buf.readUnsignedShort();
            int additionalRecordCount = buf.readUnsignedShort();
            this.decodeQuestions(query, buf, questionCount);
            this.decodeRecords(query, DnsSection.ANSWER, buf, answerCount);
            this.decodeRecords(query, DnsSection.AUTHORITY, buf, authorityRecordCount);
            this.decodeRecords(query, DnsSection.ADDITIONAL, buf, additionalRecordCount);
            out.add(query);
            success = true;
        }
        finally {
            if (!success) {
                query.release();
            }
        }
    }

    private static DnsQuery newQuery(DatagramPacket packet, ByteBuf buf) {
        int id = buf.readUnsignedShort();
        int flags = buf.readUnsignedShort();
        if (flags >> 15 == 1) {
            throw new CorruptedFrameException("not a query");
        }
        DatagramDnsQuery query = new DatagramDnsQuery((InetSocketAddress)packet.sender(), (InetSocketAddress)packet.recipient(), id, DnsOpCode.valueOf((byte)(flags >> 11 & 15)));
        query.setRecursionDesired((flags >> 8 & 1) == 1);
        query.setZ(flags >> 4 & 7);
        return query;
    }

    private void decodeQuestions(DnsQuery query, ByteBuf buf, int questionCount) throws Exception {
        for (int i = questionCount; i > 0; --i) {
            query.addRecord(DnsSection.QUESTION, this.recordDecoder.decodeQuestion(buf));
        }
    }

    private void decodeRecords(DnsQuery query, DnsSection section, ByteBuf buf, int count) throws Exception {
        Object r;
        for (int i = count; i > 0 && (r = this.recordDecoder.decodeRecord(buf)) != null; --i) {
            query.addRecord(section, (DnsRecord)r);
        }
    }
}


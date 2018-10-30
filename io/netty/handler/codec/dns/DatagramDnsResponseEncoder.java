/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.dns.DnsOpCode;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordEncoder;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsResponseEncoder
extends MessageToMessageEncoder<AddressedEnvelope<DnsResponse, InetSocketAddress>> {
    private final DnsRecordEncoder recordEncoder;

    public DatagramDnsResponseEncoder() {
        this(DnsRecordEncoder.DEFAULT);
    }

    public DatagramDnsResponseEncoder(DnsRecordEncoder recordEncoder) {
        this.recordEncoder = ObjectUtil.checkNotNull(recordEncoder, "recordEncoder");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<DnsResponse, InetSocketAddress> in, List<Object> out) throws Exception {
        ByteBuf buf;
        InetSocketAddress recipient;
        recipient = in.recipient();
        DnsResponse response = in.content();
        buf = this.allocateBuffer(ctx, in);
        boolean success = false;
        try {
            DatagramDnsResponseEncoder.encodeHeader(response, buf);
            this.encodeQuestions(response, buf);
            this.encodeRecords(response, DnsSection.ANSWER, buf);
            this.encodeRecords(response, DnsSection.AUTHORITY, buf);
            this.encodeRecords(response, DnsSection.ADDITIONAL, buf);
            success = true;
        }
        finally {
            if (!success) {
                buf.release();
            }
        }
        out.add(new DatagramPacket(buf, recipient, null));
    }

    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, AddressedEnvelope<DnsResponse, InetSocketAddress> msg) throws Exception {
        return ctx.alloc().ioBuffer(1024);
    }

    private static void encodeHeader(DnsResponse response, ByteBuf buf) {
        buf.writeShort(response.id());
        int flags = 32768;
        flags |= (response.opCode().byteValue() & 255) << 11;
        if (response.isAuthoritativeAnswer()) {
            flags |= 1024;
        }
        if (response.isTruncated()) {
            flags |= 512;
        }
        if (response.isRecursionDesired()) {
            flags |= 256;
        }
        if (response.isRecursionAvailable()) {
            flags |= 128;
        }
        flags |= response.z() << 4;
        buf.writeShort(flags |= response.code().intValue());
        buf.writeShort(response.count(DnsSection.QUESTION));
        buf.writeShort(response.count(DnsSection.ANSWER));
        buf.writeShort(response.count(DnsSection.AUTHORITY));
        buf.writeShort(response.count(DnsSection.ADDITIONAL));
    }

    private void encodeQuestions(DnsResponse response, ByteBuf buf) throws Exception {
        int count = response.count(DnsSection.QUESTION);
        for (int i = 0; i < count; ++i) {
            this.recordEncoder.encodeQuestion((DnsQuestion)response.recordAt(DnsSection.QUESTION, i), buf);
        }
    }

    private void encodeRecords(DnsResponse response, DnsSection section, ByteBuf buf) throws Exception {
        int count = response.count(section);
        for (int i = 0; i < count; ++i) {
            this.recordEncoder.encodeRecord((DnsRecord)response.recordAt(section, i), buf);
        }
    }
}


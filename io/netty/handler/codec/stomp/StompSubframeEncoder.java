/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.AsciiHeadersEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.stomp.LastStompContentSubframe;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompContentSubframe;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.handler.codec.stomp.StompHeadersSubframe;
import io.netty.handler.codec.stomp.StompSubframe;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class StompSubframeEncoder
extends MessageToMessageEncoder<StompSubframe> {
    @Override
    protected void encode(ChannelHandlerContext ctx, StompSubframe msg, List<Object> out) throws Exception {
        if (msg instanceof StompFrame) {
            StompFrame frame = (StompFrame)msg;
            ByteBuf frameBuf = StompSubframeEncoder.encodeFrame(frame, ctx);
            out.add(frameBuf);
            ByteBuf contentBuf = StompSubframeEncoder.encodeContent(frame, ctx);
            out.add(contentBuf);
        } else if (msg instanceof StompHeadersSubframe) {
            StompHeadersSubframe frame = (StompHeadersSubframe)msg;
            ByteBuf buf = StompSubframeEncoder.encodeFrame(frame, ctx);
            out.add(buf);
        } else if (msg instanceof StompContentSubframe) {
            StompContentSubframe stompContentSubframe = (StompContentSubframe)msg;
            ByteBuf buf = StompSubframeEncoder.encodeContent(stompContentSubframe, ctx);
            out.add(buf);
        }
    }

    private static ByteBuf encodeContent(StompContentSubframe content, ChannelHandlerContext ctx) {
        if (content instanceof LastStompContentSubframe) {
            ByteBuf buf = ctx.alloc().buffer(content.content().readableBytes() + 1);
            buf.writeBytes(content.content());
            buf.writeByte(0);
            return buf;
        }
        return content.content().retain();
    }

    private static ByteBuf encodeFrame(StompHeadersSubframe frame, ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeCharSequence(frame.command().toString(), CharsetUtil.US_ASCII);
        buf.writeByte(10);
        AsciiHeadersEncoder headersEncoder = new AsciiHeadersEncoder(buf, AsciiHeadersEncoder.SeparatorType.COLON, AsciiHeadersEncoder.NewlineType.LF);
        for (Map.Entry<CharSequence, CharSequence> entry : frame.headers()) {
            headersEncoder.encode(entry);
        }
        buf.writeByte(10);
        return buf;
    }
}


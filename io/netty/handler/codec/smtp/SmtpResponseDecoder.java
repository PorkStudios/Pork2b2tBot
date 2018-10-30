/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SmtpResponseDecoder
extends LineBasedFrameDecoder {
    private List<CharSequence> details;

    public SmtpResponseDecoder(int maxLineLength) {
        super(maxLineLength);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected SmtpResponse decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf frame = (ByteBuf)super.decode(ctx, buffer);
        if (frame == null) {
            return null;
        }
        try {
            List<CharSequence> details222;
            int readable = frame.readableBytes();
            int readerIndex = frame.readerIndex();
            if (readable < 3) {
                throw SmtpResponseDecoder.newDecoderException(buffer, readerIndex, readable);
            }
            int code = SmtpResponseDecoder.parseCode(frame);
            byte separator = frame.readByte();
            String detail = frame.isReadable() ? frame.toString(CharsetUtil.US_ASCII) : null;
            details222 = this.details;
            switch (separator) {
                case 32: {
                    void details222;
                    this.details = null;
                    if (details222 != null) {
                        if (detail != null) {
                            details222.add(detail);
                        }
                    } else if (detail == null) {
                        List details222 = Collections.emptyList();
                    } else {
                        List<String> details222 = Collections.singletonList(detail);
                    }
                    DefaultSmtpResponse defaultSmtpResponse = new DefaultSmtpResponse(code, (List<CharSequence>)details222);
                    return defaultSmtpResponse;
                }
                case 45: {
                    if (detail == null) return null;
                    {
                        void details222;
                        if (details222 == null) {
                            ArrayList<CharSequence> details222 = new ArrayList<CharSequence>(4);
                            this.details = details222;
                        }
                        details222.add(detail);
                        return null;
                    }
                }
                default: {
                    throw SmtpResponseDecoder.newDecoderException(buffer, readerIndex, readable);
                }
            }
        }
        finally {
            frame.release();
        }
    }

    private static DecoderException newDecoderException(ByteBuf buffer, int readerIndex, int readable) {
        return new DecoderException("Received invalid line: '" + buffer.toString(readerIndex, readable, CharsetUtil.US_ASCII) + '\'');
    }

    private static int parseCode(ByteBuf buffer) {
        int first = SmtpResponseDecoder.parseNumber(buffer.readByte()) * 100;
        int second = SmtpResponseDecoder.parseNumber(buffer.readByte()) * 10;
        int third = SmtpResponseDecoder.parseNumber(buffer.readByte());
        return first + second + third;
    }

    private static int parseNumber(byte b) {
        return Character.digit((char)b, 10);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.xml;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class XmlFrameDecoder
extends ByteToMessageDecoder {
    private final int maxFrameLength;

    public XmlFrameDecoder(int maxFrameLength) {
        if (maxFrameLength < 1) {
            throw new IllegalArgumentException("maxFrameLength must be a positive int");
        }
        this.maxFrameLength = maxFrameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        boolean openingBracketFound = false;
        boolean atLeastOneXmlElementFound = false;
        boolean inCDATASection = false;
        long openBracketsCount = 0L;
        int length = 0;
        int leadingWhiteSpaceCount = 0;
        int bufferLength = in.writerIndex();
        if (bufferLength > this.maxFrameLength) {
            in.skipBytes(in.readableBytes());
            this.fail(bufferLength);
            return;
        }
        block0 : for (int i = in.readerIndex(); i < bufferLength; ++i) {
            byte readByte = in.getByte(i);
            if (!openingBracketFound && Character.isWhitespace(readByte)) {
                ++leadingWhiteSpaceCount;
                continue;
            }
            if (!openingBracketFound && readByte != 60) {
                XmlFrameDecoder.fail(ctx);
                in.skipBytes(in.readableBytes());
                return;
            }
            if (!inCDATASection && readByte == 60) {
                openingBracketFound = true;
                if (i >= bufferLength - 1) continue;
                byte peekAheadByte = in.getByte(i + 1);
                if (peekAheadByte == 47) {
                    for (int peekFurtherAheadIndex = i + 2; peekFurtherAheadIndex <= bufferLength - 1; ++peekFurtherAheadIndex) {
                        if (in.getByte(peekFurtherAheadIndex) != 62) continue;
                        --openBracketsCount;
                        continue block0;
                    }
                    continue;
                }
                if (XmlFrameDecoder.isValidStartCharForXmlElement(peekAheadByte)) {
                    atLeastOneXmlElementFound = true;
                    ++openBracketsCount;
                    continue;
                }
                if (peekAheadByte == 33) {
                    if (XmlFrameDecoder.isCommentBlockStart(in, i)) {
                        ++openBracketsCount;
                        continue;
                    }
                    if (!XmlFrameDecoder.isCDATABlockStart(in, i)) continue;
                    ++openBracketsCount;
                    inCDATASection = true;
                    continue;
                }
                if (peekAheadByte != 63) continue;
                ++openBracketsCount;
                continue;
            }
            if (!inCDATASection && readByte == 47) {
                if (i >= bufferLength - 1 || in.getByte(i + 1) != 62) continue;
                --openBracketsCount;
                continue;
            }
            if (readByte != 62) continue;
            length = i + 1;
            if (i - 1 > -1) {
                byte peekBehindByte = in.getByte(i - 1);
                if (!inCDATASection) {
                    if (peekBehindByte == 63) {
                        --openBracketsCount;
                    } else if (peekBehindByte == 45 && i - 2 > -1 && in.getByte(i - 2) == 45) {
                        --openBracketsCount;
                    }
                } else if (peekBehindByte == 93 && i - 2 > -1 && in.getByte(i - 2) == 93) {
                    --openBracketsCount;
                    inCDATASection = false;
                }
            }
            if (atLeastOneXmlElementFound && openBracketsCount == 0L) break;
        }
        int readerIndex = in.readerIndex();
        int xmlElementLength = length - readerIndex;
        if (openBracketsCount == 0L && xmlElementLength > 0) {
            if (readerIndex + xmlElementLength >= bufferLength) {
                xmlElementLength = in.readableBytes();
            }
            ByteBuf frame = XmlFrameDecoder.extractFrame(in, readerIndex + leadingWhiteSpaceCount, xmlElementLength - leadingWhiteSpaceCount);
            in.skipBytes(xmlElementLength);
            out.add(frame);
        }
    }

    private void fail(long frameLength) {
        if (frameLength > 0L) {
            throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
        }
        throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding");
    }

    private static void fail(ChannelHandlerContext ctx) {
        ctx.fireExceptionCaught(new CorruptedFrameException("frame contains content before the xml starts"));
    }

    private static ByteBuf extractFrame(ByteBuf buffer, int index, int length) {
        return buffer.copy(index, length);
    }

    private static boolean isValidStartCharForXmlElement(byte b) {
        return b >= 97 && b <= 122 || b >= 65 && b <= 90 || b == 58 || b == 95;
    }

    private static boolean isCommentBlockStart(ByteBuf in, int i) {
        return i < in.writerIndex() - 3 && in.getByte(i + 2) == 45 && in.getByte(i + 3) == 45;
    }

    private static boolean isCDATABlockStart(ByteBuf in, int i) {
        return i < in.writerIndex() - 8 && in.getByte(i + 2) == 91 && in.getByte(i + 3) == 67 && in.getByte(i + 4) == 68 && in.getByte(i + 5) == 65 && in.getByte(i + 6) == 84 && in.getByte(i + 7) == 65 && in.getByte(i + 8) == 91;
    }
}


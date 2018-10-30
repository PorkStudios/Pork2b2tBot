/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Locale;

public final class ByteBufUtil {
    private static final InternalLogger logger;
    private static final FastThreadLocal<CharBuffer> CHAR_BUFFERS;
    private static final byte WRITE_UTF_UNKNOWN = 63;
    private static final int MAX_CHAR_BUFFER_SIZE;
    private static final int THREAD_LOCAL_BUFFER_SIZE;
    private static final int MAX_BYTES_PER_CHAR_UTF8;
    static final ByteBufAllocator DEFAULT_ALLOCATOR;
    private static final ByteProcessor FIND_NON_ASCII;

    public static String hexDump(ByteBuf buffer) {
        return ByteBufUtil.hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
    }

    public static String hexDump(ByteBuf buffer, int fromIndex, int length) {
        return HexUtil.hexDump(buffer, fromIndex, length);
    }

    public static String hexDump(byte[] array) {
        return ByteBufUtil.hexDump(array, 0, array.length);
    }

    public static String hexDump(byte[] array, int fromIndex, int length) {
        return HexUtil.hexDump(array, fromIndex, length);
    }

    public static byte decodeHexByte(CharSequence s, int pos) {
        return StringUtil.decodeHexByte(s, pos);
    }

    public static byte[] decodeHexDump(CharSequence hexDump) {
        return StringUtil.decodeHexDump(hexDump, 0, hexDump.length());
    }

    public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) {
        return StringUtil.decodeHexDump(hexDump, fromIndex, length);
    }

    public static boolean ensureWritableSuccess(int ensureWritableResult) {
        return ensureWritableResult == 0 || ensureWritableResult == 2;
    }

    public static int hashCode(ByteBuf buffer) {
        int i;
        int aLen = buffer.readableBytes();
        int intCount = aLen >>> 2;
        int byteCount = aLen & 3;
        int hashCode = 1;
        int arrayIndex = buffer.readerIndex();
        if (buffer.order() == ByteOrder.BIG_ENDIAN) {
            for (i = intCount; i > 0; --i) {
                hashCode = 31 * hashCode + buffer.getInt(arrayIndex);
                arrayIndex += 4;
            }
        } else {
            for (i = intCount; i > 0; --i) {
                hashCode = 31 * hashCode + ByteBufUtil.swapInt(buffer.getInt(arrayIndex));
                arrayIndex += 4;
            }
        }
        for (i = byteCount; i > 0; --i) {
            hashCode = 31 * hashCode + buffer.getByte(arrayIndex++);
        }
        if (hashCode == 0) {
            hashCode = 1;
        }
        return hashCode;
    }

    public static int indexOf(ByteBuf needle, ByteBuf haystack) {
        int attempts = haystack.readableBytes() - needle.readableBytes() + 1;
        for (int i = 0; i < attempts; ++i) {
            if (!ByteBufUtil.equals(needle, needle.readerIndex(), haystack, haystack.readerIndex() + i, needle.readableBytes())) continue;
            return haystack.readerIndex() + i;
        }
        return -1;
    }

    public static boolean equals(ByteBuf a, int aStartIndex, ByteBuf b, int bStartIndex, int length) {
        int i;
        if (aStartIndex < 0 || bStartIndex < 0 || length < 0) {
            throw new IllegalArgumentException("All indexes and lengths must be non-negative");
        }
        if (a.writerIndex() - length < aStartIndex || b.writerIndex() - length < bStartIndex) {
            return false;
        }
        int longCount = length >>> 3;
        int byteCount = length & 7;
        if (a.order() == b.order()) {
            for (i = longCount; i > 0; --i) {
                if (a.getLong(aStartIndex) != b.getLong(bStartIndex)) {
                    return false;
                }
                aStartIndex += 8;
                bStartIndex += 8;
            }
        } else {
            for (i = longCount; i > 0; --i) {
                if (a.getLong(aStartIndex) != ByteBufUtil.swapLong(b.getLong(bStartIndex))) {
                    return false;
                }
                aStartIndex += 8;
                bStartIndex += 8;
            }
        }
        for (i = byteCount; i > 0; --i) {
            if (a.getByte(aStartIndex) != b.getByte(bStartIndex)) {
                return false;
            }
            ++aStartIndex;
            ++bStartIndex;
        }
        return true;
    }

    public static boolean equals(ByteBuf bufferA, ByteBuf bufferB) {
        int aLen = bufferA.readableBytes();
        if (aLen != bufferB.readableBytes()) {
            return false;
        }
        return ByteBufUtil.equals(bufferA, bufferA.readerIndex(), bufferB, bufferB.readerIndex(), aLen);
    }

    public static int compare(ByteBuf bufferA, ByteBuf bufferB) {
        int aLen = bufferA.readableBytes();
        int bLen = bufferB.readableBytes();
        int minLength = Math.min(aLen, bLen);
        int uintCount = minLength >>> 2;
        int byteCount = minLength & 3;
        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();
        if (uintCount > 0) {
            long res;
            boolean bufferAIsBigEndian = bufferA.order() == ByteOrder.BIG_ENDIAN;
            int uintCountIncrement = uintCount << 2;
            if (bufferA.order() == bufferB.order()) {
                res = bufferAIsBigEndian ? ByteBufUtil.compareUintBigEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement) : ByteBufUtil.compareUintLittleEndian(bufferA, bufferB, aIndex, bIndex, uintCountIncrement);
            } else {
                long l = res = bufferAIsBigEndian ? ByteBufUtil.compareUintBigEndianA(bufferA, bufferB, aIndex, bIndex, uintCountIncrement) : ByteBufUtil.compareUintBigEndianB(bufferA, bufferB, aIndex, bIndex, uintCountIncrement);
            }
            if (res != 0L) {
                return (int)Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, res));
            }
            aIndex += uintCountIncrement;
            bIndex += uintCountIncrement;
        }
        int aEnd = aIndex + byteCount;
        while (aIndex < aEnd) {
            int comp = bufferA.getUnsignedByte(aIndex) - bufferB.getUnsignedByte(bIndex);
            if (comp != 0) {
                return comp;
            }
            ++aIndex;
            ++bIndex;
        }
        return aLen - bLen;
    }

    private static long compareUintBigEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedInt(bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintLittleEndian(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedIntLE(aIndex) - bufferB.getUnsignedIntLE(bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintBigEndianA(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedInt(aIndex) - bufferB.getUnsignedIntLE(bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    private static long compareUintBigEndianB(ByteBuf bufferA, ByteBuf bufferB, int aIndex, int bIndex, int uintCountIncrement) {
        int aEnd = aIndex + uintCountIncrement;
        while (aIndex < aEnd) {
            long comp = bufferA.getUnsignedIntLE(aIndex) - bufferB.getUnsignedInt(bIndex);
            if (comp != 0L) {
                return comp;
            }
            aIndex += 4;
            bIndex += 4;
        }
        return 0L;
    }

    public static int indexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if (fromIndex <= toIndex) {
            return ByteBufUtil.firstIndexOf(buffer, fromIndex, toIndex, value);
        }
        return ByteBufUtil.lastIndexOf(buffer, fromIndex, toIndex, value);
    }

    public static short swapShort(short value) {
        return Short.reverseBytes(value);
    }

    public static int swapMedium(int value) {
        int swapped = value << 16 & 16711680 | value & 65280 | value >>> 16 & 255;
        if ((swapped & 8388608) != 0) {
            swapped |= -16777216;
        }
        return swapped;
    }

    public static int swapInt(int value) {
        return Integer.reverseBytes(value);
    }

    public static long swapLong(long value) {
        return Long.reverseBytes(value);
    }

    public static ByteBuf writeShortBE(ByteBuf buf, int shortValue) {
        return buf.order() == ByteOrder.BIG_ENDIAN ? buf.writeShort(shortValue) : buf.writeShortLE(shortValue);
    }

    public static ByteBuf setShortBE(ByteBuf buf, int index, int shortValue) {
        return buf.order() == ByteOrder.BIG_ENDIAN ? buf.setShort(index, shortValue) : buf.setShortLE(index, shortValue);
    }

    public static ByteBuf writeMediumBE(ByteBuf buf, int mediumValue) {
        return buf.order() == ByteOrder.BIG_ENDIAN ? buf.writeMedium(mediumValue) : buf.writeMediumLE(mediumValue);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuf readBytes(ByteBufAllocator alloc, ByteBuf buffer, int length) {
        boolean release = true;
        ByteBuf dst = alloc.buffer(length);
        try {
            buffer.readBytes(dst);
            release = false;
            ByteBuf byteBuf = dst;
            return byteBuf;
        }
        finally {
            if (release) {
                dst.release();
            }
        }
    }

    private static int firstIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if ((fromIndex = Math.max(fromIndex, 0)) >= toIndex || buffer.capacity() == 0) {
            return -1;
        }
        return buffer.forEachByte(fromIndex, toIndex - fromIndex, new ByteProcessor.IndexOfProcessor(value));
    }

    private static int lastIndexOf(ByteBuf buffer, int fromIndex, int toIndex, byte value) {
        if ((fromIndex = Math.min(fromIndex, buffer.capacity())) < 0 || buffer.capacity() == 0) {
            return -1;
        }
        return buffer.forEachByteDesc(toIndex, fromIndex - toIndex, new ByteProcessor.IndexOfProcessor(value));
    }

    public static ByteBuf writeUtf8(ByteBufAllocator alloc, CharSequence seq) {
        ByteBuf buf = alloc.buffer(ByteBufUtil.utf8MaxBytes(seq));
        ByteBufUtil.writeUtf8(buf, seq);
        return buf;
    }

    public static int writeUtf8(ByteBuf buf, CharSequence seq) {
        do {
            if (buf instanceof AbstractByteBuf) {
                AbstractByteBuf byteBuf = (AbstractByteBuf)buf;
                byteBuf.ensureWritable0(ByteBufUtil.utf8MaxBytes(seq));
                int written = ByteBufUtil.writeUtf8(byteBuf, byteBuf.writerIndex, seq, seq.length());
                byteBuf.writerIndex += written;
                return written;
            }
            if (!(buf instanceof WrappedByteBuf)) break;
            buf = buf.unwrap();
        } while (true);
        byte[] bytes = seq.toString().getBytes(CharsetUtil.UTF_8);
        buf.writeBytes(bytes);
        return bytes.length;
    }

    static int writeUtf8(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) {
        int oldWriterIndex = writerIndex;
        for (int i = 0; i < len; ++i) {
            char c = seq.charAt(i);
            if (c < '') {
                buffer._setByte(writerIndex++, (byte)c);
                continue;
            }
            if (c < '\u0800') {
                buffer._setByte(writerIndex++, (byte)(192 | c >> 6));
                buffer._setByte(writerIndex++, (byte)(128 | c & 63));
                continue;
            }
            if (StringUtil.isSurrogate(c)) {
                char c2;
                if (!Character.isHighSurrogate(c)) {
                    buffer._setByte(writerIndex++, 63);
                    continue;
                }
                try {
                    c2 = seq.charAt(++i);
                }
                catch (IndexOutOfBoundsException e) {
                    buffer._setByte(writerIndex++, 63);
                    break;
                }
                if (!Character.isLowSurrogate(c2)) {
                    buffer._setByte(writerIndex++, 63);
                    buffer._setByte(writerIndex++, Character.isHighSurrogate(c2) ? 63 : (int)c2);
                    continue;
                }
                int codePoint = Character.toCodePoint(c, c2);
                buffer._setByte(writerIndex++, (byte)(240 | codePoint >> 18));
                buffer._setByte(writerIndex++, (byte)(128 | codePoint >> 12 & 63));
                buffer._setByte(writerIndex++, (byte)(128 | codePoint >> 6 & 63));
                buffer._setByte(writerIndex++, (byte)(128 | codePoint & 63));
                continue;
            }
            buffer._setByte(writerIndex++, (byte)(224 | c >> 12));
            buffer._setByte(writerIndex++, (byte)(128 | c >> 6 & 63));
            buffer._setByte(writerIndex++, (byte)(128 | c & 63));
        }
        return writerIndex - oldWriterIndex;
    }

    public static int utf8MaxBytes(CharSequence seq) {
        return seq.length() * MAX_BYTES_PER_CHAR_UTF8;
    }

    public static ByteBuf writeAscii(ByteBufAllocator alloc, CharSequence seq) {
        ByteBuf buf = alloc.buffer(seq.length());
        ByteBufUtil.writeAscii(buf, seq);
        return buf;
    }

    public static int writeAscii(ByteBuf buf, CharSequence seq) {
        int len = seq.length();
        if (!(seq instanceof AsciiString)) {
            do {
                if (buf instanceof AbstractByteBuf) {
                    AbstractByteBuf byteBuf = (AbstractByteBuf)buf;
                    byteBuf.ensureWritable0(len);
                    int written = ByteBufUtil.writeAscii(byteBuf, byteBuf.writerIndex, seq, len);
                    byteBuf.writerIndex += written;
                    return written;
                }
                if (!(buf instanceof WrappedByteBuf)) break;
                buf = buf.unwrap();
            } while (true);
            byte[] bytes = seq.toString().getBytes(CharsetUtil.US_ASCII);
            buf.writeBytes(bytes);
            return bytes.length;
        }
        AsciiString asciiString = (AsciiString)seq;
        buf.writeBytes(asciiString.array(), asciiString.arrayOffset(), len);
        return len;
    }

    static int writeAscii(AbstractByteBuf buffer, int writerIndex, CharSequence seq, int len) {
        for (int i = 0; i < len; ++i) {
            buffer._setByte(writerIndex++, AsciiString.c2b(seq.charAt(i)));
        }
        return len;
    }

    public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset) {
        return ByteBufUtil.encodeString0(alloc, false, src, charset, 0);
    }

    public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset, int extraCapacity) {
        return ByteBufUtil.encodeString0(alloc, false, src, charset, extraCapacity);
    }

    static ByteBuf encodeString0(ByteBufAllocator alloc, boolean enforceHeap, CharBuffer src, Charset charset, int extraCapacity) {
        CharsetEncoder encoder = CharsetUtil.encoder(charset);
        int length = (int)((double)src.remaining() * (double)encoder.maxBytesPerChar()) + extraCapacity;
        boolean release = true;
        ByteBuf dst = enforceHeap ? alloc.heapBuffer(length) : alloc.buffer(length);
        try {
            ByteBuffer dstBuf = dst.internalNioBuffer(dst.readerIndex(), length);
            int pos = dstBuf.position();
            CoderResult cr = encoder.encode(src, dstBuf, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            if (!(cr = encoder.flush(dstBuf)).isUnderflow()) {
                cr.throwException();
            }
            dst.writerIndex(dst.writerIndex() + dstBuf.position() - pos);
            release = false;
            ByteBuf byteBuf = dst;
            return byteBuf;
        }
        catch (CharacterCodingException x) {
            throw new IllegalStateException(x);
        }
        finally {
            if (release) {
                dst.release();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String decodeString(ByteBuf src, int readerIndex, int len, Charset charset) {
        CharBuffer dst;
        if (len == 0) {
            return "";
        }
        CharsetDecoder decoder = CharsetUtil.decoder(charset);
        int maxLength = (int)((double)len * (double)decoder.maxCharsPerByte());
        dst = CHAR_BUFFERS.get();
        if (dst.length() < maxLength) {
            dst = CharBuffer.allocate(maxLength);
            if (maxLength <= MAX_CHAR_BUFFER_SIZE) {
                CHAR_BUFFERS.set(dst);
            }
        } else {
            dst.clear();
        }
        if (src.nioBufferCount() == 1) {
            ByteBufUtil.decodeString(decoder, src.internalNioBuffer(readerIndex, len), dst);
        } else {
            ByteBuf buffer = src.alloc().heapBuffer(len);
            try {
                buffer.writeBytes(src, readerIndex, len);
                ByteBufUtil.decodeString(decoder, buffer.internalNioBuffer(buffer.readerIndex(), len), dst);
            }
            finally {
                buffer.release();
            }
        }
        return dst.flip().toString();
    }

    private static void decodeString(CharsetDecoder decoder, ByteBuffer src, CharBuffer dst) {
        try {
            CoderResult cr = decoder.decode(src, dst, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            if (!(cr = decoder.flush(dst)).isUnderflow()) {
                cr.throwException();
            }
        }
        catch (CharacterCodingException x) {
            throw new IllegalStateException(x);
        }
    }

    public static ByteBuf threadLocalDirectBuffer() {
        if (THREAD_LOCAL_BUFFER_SIZE <= 0) {
            return null;
        }
        if (PlatformDependent.hasUnsafe()) {
            return ThreadLocalUnsafeDirectByteBuf.newInstance();
        }
        return ThreadLocalDirectByteBuf.newInstance();
    }

    public static byte[] getBytes(ByteBuf buf) {
        return ByteBufUtil.getBytes(buf, buf.readerIndex(), buf.readableBytes());
    }

    public static byte[] getBytes(ByteBuf buf, int start, int length) {
        return ByteBufUtil.getBytes(buf, start, length, true);
    }

    public static byte[] getBytes(ByteBuf buf, int start, int length, boolean copy) {
        if (MathUtil.isOutOfBounds(start, length, buf.capacity())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= buf.capacity(" + buf.capacity() + ')');
        }
        if (buf.hasArray()) {
            if (copy || start != 0 || length != buf.capacity()) {
                int baseOffset = buf.arrayOffset() + start;
                return Arrays.copyOfRange(buf.array(), baseOffset, baseOffset + length);
            }
            return buf.array();
        }
        byte[] v = new byte[length];
        buf.getBytes(start, v);
        return v;
    }

    public static void copy(AsciiString src, ByteBuf dst) {
        ByteBufUtil.copy(src, 0, dst, src.length());
    }

    public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int dstIdx, int length) {
        if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');
        }
        ObjectUtil.checkNotNull(dst, "dst").setBytes(dstIdx, src.array(), srcIdx + src.arrayOffset(), length);
    }

    public static void copy(AsciiString src, int srcIdx, ByteBuf dst, int length) {
        if (MathUtil.isOutOfBounds(srcIdx, length, src.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + src.length() + ')');
        }
        ObjectUtil.checkNotNull(dst, "dst").writeBytes(src.array(), srcIdx + src.arrayOffset(), length);
    }

    public static String prettyHexDump(ByteBuf buffer) {
        return ByteBufUtil.prettyHexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
    }

    public static String prettyHexDump(ByteBuf buffer, int offset, int length) {
        return HexUtil.prettyHexDump(buffer, offset, length);
    }

    public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf) {
        ByteBufUtil.appendPrettyHexDump(dump, buf, buf.readerIndex(), buf.readableBytes());
    }

    public static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf, int offset, int length) {
        HexUtil.appendPrettyHexDump(dump, buf, offset, length);
    }

    public static boolean isText(ByteBuf buf, Charset charset) {
        return ByteBufUtil.isText(buf, buf.readerIndex(), buf.readableBytes(), charset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isText(ByteBuf buf, int index, int length, Charset charset) {
        ObjectUtil.checkNotNull(buf, "buf");
        ObjectUtil.checkNotNull(charset, "charset");
        int maxIndex = buf.readerIndex() + buf.readableBytes();
        if (index < 0 || length < 0 || index > maxIndex - length) {
            throw new IndexOutOfBoundsException("index: " + index + " length: " + length);
        }
        if (charset.equals(CharsetUtil.UTF_8)) {
            return ByteBufUtil.isUtf8(buf, index, length);
        }
        if (charset.equals(CharsetUtil.US_ASCII)) {
            return ByteBufUtil.isAscii(buf, index, length);
        }
        CharsetDecoder decoder = CharsetUtil.decoder(charset, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
        try {
            if (buf.nioBufferCount() == 1) {
                decoder.decode(buf.internalNioBuffer(index, length));
            } else {
                ByteBuf heapBuffer = buf.alloc().heapBuffer(length);
                try {
                    heapBuffer.writeBytes(buf, index, length);
                    decoder.decode(heapBuffer.internalNioBuffer(heapBuffer.readerIndex(), length));
                }
                finally {
                    heapBuffer.release();
                }
            }
            return true;
        }
        catch (CharacterCodingException ignore) {
            return false;
        }
    }

    private static boolean isAscii(ByteBuf buf, int index, int length) {
        return buf.forEachByte(index, length, FIND_NON_ASCII) == -1;
    }

    private static boolean isUtf8(ByteBuf buf, int index, int length) {
        int endIndex = index + length;
        while (index < endIndex) {
            byte b1;
            byte b2;
            byte b3;
            if (((b1 = buf.getByte(index++)) & 128) == 0) continue;
            if ((b1 & 224) == 192) {
                if (index >= endIndex) {
                    return false;
                }
                if (((b2 = buf.getByte(index++)) & 192) != 128) {
                    return false;
                }
                if ((b1 & 255) >= 194) continue;
                return false;
            }
            if ((b1 & 240) == 224) {
                if (index > endIndex - 2) {
                    return false;
                }
                b2 = buf.getByte(index++);
                b3 = buf.getByte(index++);
                if ((b2 & 192) != 128 || (b3 & 192) != 128) {
                    return false;
                }
                if ((b1 & 15) == 0 && (b2 & 255) < 160) {
                    return false;
                }
                if ((b1 & 15) != 13 || (b2 & 255) <= 159) continue;
                return false;
            }
            if ((b1 & 248) == 240) {
                if (index > endIndex - 3) {
                    return false;
                }
                b2 = buf.getByte(index++);
                b3 = buf.getByte(index++);
                byte b4 = buf.getByte(index++);
                if ((b2 & 192) != 128 || (b3 & 192) != 128 || (b4 & 192) != 128) {
                    return false;
                }
                if ((b1 & 255) <= 244 && ((b1 & 255) != 240 || (b2 & 255) >= 144) && ((b1 & 255) != 244 || (b2 & 255) <= 143)) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    private ByteBufUtil() {
    }

    static {
        AbstractByteBufAllocator alloc;
        logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
        CHAR_BUFFERS = new FastThreadLocal<CharBuffer>(){

            @Override
            protected CharBuffer initialValue() throws Exception {
                return CharBuffer.allocate(1024);
            }
        };
        MAX_BYTES_PER_CHAR_UTF8 = (int)CharsetUtil.encoder(CharsetUtil.UTF_8).maxBytesPerChar();
        String allocType = SystemPropertyUtil.get("io.netty.allocator.type", PlatformDependent.isAndroid() ? "unpooled" : "pooled");
        if ("unpooled".equals(allocType = allocType.toLowerCase(Locale.US).trim())) {
            alloc = UnpooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: {}", (Object)allocType);
        } else if ("pooled".equals(allocType)) {
            alloc = PooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: {}", (Object)allocType);
        } else {
            alloc = PooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", (Object)allocType);
        }
        DEFAULT_ALLOCATOR = alloc;
        THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalDirectBufferSize", 65536);
        logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", (Object)THREAD_LOCAL_BUFFER_SIZE);
        MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.maxThreadLocalCharBufferSize", 16384);
        logger.debug("-Dio.netty.maxThreadLocalCharBufferSize: {}", (Object)MAX_CHAR_BUFFER_SIZE);
        FIND_NON_ASCII = new ByteProcessor(){

            @Override
            public boolean process(byte value) {
                return value >= 0;
            }
        };
    }

    static final class ThreadLocalDirectByteBuf
    extends UnpooledDirectByteBuf {
        private static final Recycler<ThreadLocalDirectByteBuf> RECYCLER = new Recycler<ThreadLocalDirectByteBuf>(){

            @Override
            protected ThreadLocalDirectByteBuf newObject(Recycler.Handle<ThreadLocalDirectByteBuf> handle) {
                return new ThreadLocalDirectByteBuf(handle);
            }
        };
        private final Recycler.Handle<ThreadLocalDirectByteBuf> handle;

        static ThreadLocalDirectByteBuf newInstance() {
            ThreadLocalDirectByteBuf buf = RECYCLER.get();
            buf.setRefCnt(1);
            return buf;
        }

        private ThreadLocalDirectByteBuf(Recycler.Handle<ThreadLocalDirectByteBuf> handle) {
            super((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
            this.handle = handle;
        }

        @Override
        protected void deallocate() {
            if (this.capacity() > THREAD_LOCAL_BUFFER_SIZE) {
                super.deallocate();
            } else {
                this.clear();
                this.handle.recycle(this);
            }
        }

    }

    static final class ThreadLocalUnsafeDirectByteBuf
    extends UnpooledUnsafeDirectByteBuf {
        private static final Recycler<ThreadLocalUnsafeDirectByteBuf> RECYCLER = new Recycler<ThreadLocalUnsafeDirectByteBuf>(){

            @Override
            protected ThreadLocalUnsafeDirectByteBuf newObject(Recycler.Handle<ThreadLocalUnsafeDirectByteBuf> handle) {
                return new ThreadLocalUnsafeDirectByteBuf(handle);
            }
        };
        private final Recycler.Handle<ThreadLocalUnsafeDirectByteBuf> handle;

        static ThreadLocalUnsafeDirectByteBuf newInstance() {
            ThreadLocalUnsafeDirectByteBuf buf = RECYCLER.get();
            buf.setRefCnt(1);
            return buf;
        }

        private ThreadLocalUnsafeDirectByteBuf(Recycler.Handle<ThreadLocalUnsafeDirectByteBuf> handle) {
            super((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
            this.handle = handle;
        }

        @Override
        protected void deallocate() {
            if (this.capacity() > THREAD_LOCAL_BUFFER_SIZE) {
                super.deallocate();
            } else {
                this.clear();
                this.handle.recycle(this);
            }
        }

    }

    private static final class HexUtil {
        private static final char[] BYTE2CHAR;
        private static final char[] HEXDUMP_TABLE;
        private static final String[] HEXPADDING;
        private static final String[] HEXDUMP_ROWPREFIXES;
        private static final String[] BYTE2HEX;
        private static final String[] BYTEPADDING;

        private HexUtil() {
        }

        private static String hexDump(ByteBuf buffer, int fromIndex, int length) {
            if (length < 0) {
                throw new IllegalArgumentException("length: " + length);
            }
            if (length == 0) {
                return "";
            }
            int endIndex = fromIndex + length;
            char[] buf = new char[length << 1];
            int srcIdx = fromIndex;
            int dstIdx = 0;
            while (srcIdx < endIndex) {
                System.arraycopy(HEXDUMP_TABLE, buffer.getUnsignedByte(srcIdx) << 1, buf, dstIdx, 2);
                ++srcIdx;
                dstIdx += 2;
            }
            return new String(buf);
        }

        private static String hexDump(byte[] array, int fromIndex, int length) {
            if (length < 0) {
                throw new IllegalArgumentException("length: " + length);
            }
            if (length == 0) {
                return "";
            }
            int endIndex = fromIndex + length;
            char[] buf = new char[length << 1];
            int srcIdx = fromIndex;
            int dstIdx = 0;
            while (srcIdx < endIndex) {
                System.arraycopy(HEXDUMP_TABLE, (array[srcIdx] & 255) << 1, buf, dstIdx, 2);
                ++srcIdx;
                dstIdx += 2;
            }
            return new String(buf);
        }

        private static String prettyHexDump(ByteBuf buffer, int offset, int length) {
            if (length == 0) {
                return "";
            }
            int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
            StringBuilder buf = new StringBuilder(rows * 80);
            HexUtil.appendPrettyHexDump(buf, buffer, offset, length);
            return buf.toString();
        }

        private static void appendPrettyHexDump(StringBuilder dump, ByteBuf buf, int offset, int length) {
            if (MathUtil.isOutOfBounds(offset, length, buf.capacity())) {
                throw new IndexOutOfBoundsException("expected: 0 <= offset(" + offset + ") <= offset + length(" + length + ") <= buf.capacity(" + buf.capacity() + ')');
            }
            if (length == 0) {
                return;
            }
            dump.append("         +-------------------------------------------------+" + StringUtil.NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
            int startIndex = offset;
            int fullRows = length >>> 4;
            int remainder = length & 15;
            for (int row = 0; row < fullRows; ++row) {
                int j;
                int rowStartIndex = (row << 4) + startIndex;
                HexUtil.appendHexDumpRowPrefix(dump, row, rowStartIndex);
                int rowEndIndex = rowStartIndex + 16;
                for (j = rowStartIndex; j < rowEndIndex; ++j) {
                    dump.append(BYTE2HEX[buf.getUnsignedByte(j)]);
                }
                dump.append(" |");
                for (j = rowStartIndex; j < rowEndIndex; ++j) {
                    dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
                }
                dump.append('|');
            }
            if (remainder != 0) {
                int j;
                int rowStartIndex = (fullRows << 4) + startIndex;
                HexUtil.appendHexDumpRowPrefix(dump, fullRows, rowStartIndex);
                int rowEndIndex = rowStartIndex + remainder;
                for (j = rowStartIndex; j < rowEndIndex; ++j) {
                    dump.append(BYTE2HEX[buf.getUnsignedByte(j)]);
                }
                dump.append(HEXPADDING[remainder]);
                dump.append(" |");
                for (j = rowStartIndex; j < rowEndIndex; ++j) {
                    dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
                }
                dump.append(BYTEPADDING[remainder]);
                dump.append('|');
            }
            dump.append(StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
        }

        private static void appendHexDumpRowPrefix(StringBuilder dump, int row, int rowStartIndex) {
            if (row < HEXDUMP_ROWPREFIXES.length) {
                dump.append(HEXDUMP_ROWPREFIXES[row]);
            } else {
                dump.append(StringUtil.NEWLINE);
                dump.append(Long.toHexString((long)rowStartIndex & 0xFFFFFFFFL | 0x100000000L));
                dump.setCharAt(dump.length() - 9, '|');
                dump.append('|');
            }
        }

        static {
            int j;
            StringBuilder buf;
            int i;
            int padding;
            BYTE2CHAR = new char[256];
            HEXDUMP_TABLE = new char[1024];
            HEXPADDING = new String[16];
            HEXDUMP_ROWPREFIXES = new String[4096];
            BYTE2HEX = new String[256];
            BYTEPADDING = new String[16];
            char[] DIGITS = "0123456789abcdef".toCharArray();
            for (i = 0; i < 256; ++i) {
                HexUtil.HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 15];
                HexUtil.HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 15];
            }
            for (i = 0; i < HEXPADDING.length; ++i) {
                padding = HEXPADDING.length - i;
                buf = new StringBuilder(padding * 3);
                for (j = 0; j < padding; ++j) {
                    buf.append("   ");
                }
                HexUtil.HEXPADDING[i] = buf.toString();
            }
            for (i = 0; i < HEXDUMP_ROWPREFIXES.length; ++i) {
                StringBuilder buf2 = new StringBuilder(12);
                buf2.append(StringUtil.NEWLINE);
                buf2.append(Long.toHexString((long)(i << 4) & 0xFFFFFFFFL | 0x100000000L));
                buf2.setCharAt(buf2.length() - 9, '|');
                buf2.append('|');
                HexUtil.HEXDUMP_ROWPREFIXES[i] = buf2.toString();
            }
            for (i = 0; i < BYTE2HEX.length; ++i) {
                HexUtil.BYTE2HEX[i] = "" + ' ' + StringUtil.byteToHexStringPadded(i);
            }
            for (i = 0; i < BYTEPADDING.length; ++i) {
                padding = BYTEPADDING.length - i;
                buf = new StringBuilder(padding);
                for (j = 0; j < padding; ++j) {
                    buf.append(' ');
                }
                HexUtil.BYTEPADDING[i] = buf.toString();
            }
            for (i = 0; i < BYTE2CHAR.length; ++i) {
                HexUtil.BYTE2CHAR[i] = i <= 31 || i >= 127 ? 46 : (char)i;
            }
        }
    }

}


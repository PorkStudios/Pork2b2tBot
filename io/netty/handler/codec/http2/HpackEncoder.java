/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.HpackHeaderField;
import io.netty.handler.codec.http2.HpackHuffmanEncoder;
import io.netty.handler.codec.http2.HpackStaticTable;
import io.netty.handler.codec.http2.HpackUtil;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersEncoder;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

final class HpackEncoder {
    private final HeaderEntry[] headerFields;
    private final HeaderEntry head = new HeaderEntry(-1, AsciiString.EMPTY_STRING, AsciiString.EMPTY_STRING, Integer.MAX_VALUE, null);
    private final HpackHuffmanEncoder hpackHuffmanEncoder = new HpackHuffmanEncoder();
    private final byte hashMask;
    private final boolean ignoreMaxHeaderListSize;
    private long size;
    private long maxHeaderTableSize;
    private long maxHeaderListSize;

    HpackEncoder() {
        this(false);
    }

    public HpackEncoder(boolean ignoreMaxHeaderListSize) {
        this(ignoreMaxHeaderListSize, 16);
    }

    public HpackEncoder(boolean ignoreMaxHeaderListSize, int arraySizeHint) {
        this.ignoreMaxHeaderListSize = ignoreMaxHeaderListSize;
        this.maxHeaderTableSize = 4096L;
        this.maxHeaderListSize = 8192L;
        this.headerFields = new HeaderEntry[io.netty.util.internal.MathUtil.findNextPositivePowerOfTwo(java.lang.Math.max(2, java.lang.Math.min(arraySizeHint, 128)))];
        this.hashMask = (byte)(this.headerFields.length - 1);
        this.head.before = this.head.after = this.head;
    }

    public void encodeHeaders(int streamId, ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector) throws Http2Exception {
        if (this.ignoreMaxHeaderListSize) {
            this.encodeHeadersIgnoreMaxHeaderListSize(out, headers, sensitivityDetector);
        } else {
            this.encodeHeadersEnforceMaxHeaderListSize(streamId, out, headers, sensitivityDetector);
        }
    }

    private void encodeHeadersEnforceMaxHeaderListSize(int streamId, ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector) throws Http2Exception {
        long headerSize = 0L;
        for (Map.Entry<CharSequence, CharSequence> header : headers) {
            CharSequence value;
            CharSequence name = header.getKey();
            if ((headerSize += HpackHeaderField.sizeOf(name, value = header.getValue())) <= this.maxHeaderListSize) continue;
            Http2CodecUtil.headerListSizeExceeded(streamId, this.maxHeaderListSize, false);
        }
        this.encodeHeadersIgnoreMaxHeaderListSize(out, headers, sensitivityDetector);
    }

    private void encodeHeadersIgnoreMaxHeaderListSize(ByteBuf out, Http2Headers headers, Http2HeadersEncoder.SensitivityDetector sensitivityDetector) throws Http2Exception {
        for (Map.Entry<CharSequence, CharSequence> header : headers) {
            CharSequence name = header.getKey();
            CharSequence value = header.getValue();
            this.encodeHeader(out, name, value, sensitivityDetector.isSensitive(name, value), HpackHeaderField.sizeOf(name, value));
        }
    }

    private void encodeHeader(ByteBuf out, CharSequence name, CharSequence value, boolean sensitive, long headerSize) {
        if (sensitive) {
            int nameIndex = this.getNameIndex(name);
            this.encodeLiteral(out, name, value, HpackUtil.IndexType.NEVER, nameIndex);
            return;
        }
        if (this.maxHeaderTableSize == 0L) {
            int staticTableIndex = HpackStaticTable.getIndex(name, value);
            if (staticTableIndex == -1) {
                int nameIndex = HpackStaticTable.getIndex(name);
                this.encodeLiteral(out, name, value, HpackUtil.IndexType.NONE, nameIndex);
            } else {
                HpackEncoder.encodeInteger(out, 128, 7, staticTableIndex);
            }
            return;
        }
        if (headerSize > this.maxHeaderTableSize) {
            int nameIndex = this.getNameIndex(name);
            this.encodeLiteral(out, name, value, HpackUtil.IndexType.NONE, nameIndex);
            return;
        }
        HeaderEntry headerField = this.getEntry(name, value);
        if (headerField != null) {
            int index = this.getIndex(headerField.index) + HpackStaticTable.length;
            HpackEncoder.encodeInteger(out, 128, 7, index);
        } else {
            int staticTableIndex = HpackStaticTable.getIndex(name, value);
            if (staticTableIndex != -1) {
                HpackEncoder.encodeInteger(out, 128, 7, staticTableIndex);
            } else {
                this.ensureCapacity(headerSize);
                this.encodeLiteral(out, name, value, HpackUtil.IndexType.INCREMENTAL, this.getNameIndex(name));
                this.add(name, value, headerSize);
            }
        }
    }

    public void setMaxHeaderTableSize(ByteBuf out, long maxHeaderTableSize) throws Http2Exception {
        if (maxHeaderTableSize < 0L || maxHeaderTableSize > 0xFFFFFFFFL) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header Table Size must be >= %d and <= %d but was %d", 0L, 0xFFFFFFFFL, maxHeaderTableSize);
        }
        if (this.maxHeaderTableSize == maxHeaderTableSize) {
            return;
        }
        this.maxHeaderTableSize = maxHeaderTableSize;
        this.ensureCapacity(0L);
        HpackEncoder.encodeInteger(out, 32, 5, maxHeaderTableSize);
    }

    public long getMaxHeaderTableSize() {
        return this.maxHeaderTableSize;
    }

    public void setMaxHeaderListSize(long maxHeaderListSize) throws Http2Exception {
        if (maxHeaderListSize < 0L || maxHeaderListSize > 0xFFFFFFFFL) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header List Size must be >= %d and <= %d but was %d", 0L, 0xFFFFFFFFL, maxHeaderListSize);
        }
        this.maxHeaderListSize = maxHeaderListSize;
    }

    public long getMaxHeaderListSize() {
        return this.maxHeaderListSize;
    }

    private static void encodeInteger(ByteBuf out, int mask, int n, int i) {
        HpackEncoder.encodeInteger(out, mask, n, (long)i);
    }

    private static void encodeInteger(ByteBuf out, int mask, int n, long i) {
        assert (n >= 0 && n <= 8);
        int nbits = 255 >>> 8 - n;
        if (i < (long)nbits) {
            out.writeByte((int)((long)mask | i));
        } else {
            out.writeByte(mask | nbits);
            long length = i - (long)nbits;
            while ((length & -128L) != 0L) {
                out.writeByte((int)(length & 127L | 128L));
                length >>>= 7;
            }
            out.writeByte((int)length);
        }
    }

    private void encodeStringLiteral(ByteBuf out, CharSequence string) {
        int huffmanLength = this.hpackHuffmanEncoder.getEncodedLength(string);
        if (huffmanLength < string.length()) {
            HpackEncoder.encodeInteger(out, 128, 7, huffmanLength);
            this.hpackHuffmanEncoder.encode(out, string);
        } else {
            HpackEncoder.encodeInteger(out, 0, 7, string.length());
            if (string instanceof AsciiString) {
                AsciiString asciiString = (AsciiString)string;
                out.writeBytes(asciiString.array(), asciiString.arrayOffset(), asciiString.length());
            } else {
                out.writeCharSequence(string, CharsetUtil.ISO_8859_1);
            }
        }
    }

    private void encodeLiteral(ByteBuf out, CharSequence name, CharSequence value, HpackUtil.IndexType indexType, int nameIndex) {
        boolean nameIndexValid = nameIndex != -1;
        switch (indexType) {
            case INCREMENTAL: {
                HpackEncoder.encodeInteger(out, 64, 6, nameIndexValid ? nameIndex : 0);
                break;
            }
            case NONE: {
                HpackEncoder.encodeInteger(out, 0, 4, nameIndexValid ? nameIndex : 0);
                break;
            }
            case NEVER: {
                HpackEncoder.encodeInteger(out, 16, 4, nameIndexValid ? nameIndex : 0);
                break;
            }
            default: {
                throw new Error("should not reach here");
            }
        }
        if (!nameIndexValid) {
            this.encodeStringLiteral(out, name);
        }
        this.encodeStringLiteral(out, value);
    }

    private int getNameIndex(CharSequence name) {
        int index = HpackStaticTable.getIndex(name);
        if (index == -1 && (index = this.getIndex(name)) >= 0) {
            index += HpackStaticTable.length;
        }
        return index;
    }

    private void ensureCapacity(long headerSize) {
        int index;
        while (this.maxHeaderTableSize - this.size < headerSize && (index = this.length()) != 0) {
            this.remove();
        }
    }

    int length() {
        return this.size == 0L ? 0 : this.head.after.index - this.head.before.index + 1;
    }

    long size() {
        return this.size;
    }

    HpackHeaderField getHeaderField(int index) {
        HeaderEntry entry = this.head;
        while (index-- >= 0) {
            entry = entry.before;
        }
        return entry;
    }

    private HeaderEntry getEntry(CharSequence name, CharSequence value) {
        if (this.length() == 0 || name == null || value == null) {
            return null;
        }
        int h = AsciiString.hashCode(name);
        int i = this.index(h);
        HeaderEntry e = this.headerFields[i];
        while (e != null) {
            if (e.hash == h && (HpackUtil.equalsConstantTime(name, e.name) & HpackUtil.equalsConstantTime(value, e.value)) != 0) {
                return e;
            }
            e = e.next;
        }
        return null;
    }

    private int getIndex(CharSequence name) {
        if (this.length() == 0 || name == null) {
            return -1;
        }
        int h = AsciiString.hashCode(name);
        int i = this.index(h);
        HeaderEntry e = this.headerFields[i];
        while (e != null) {
            if (e.hash == h && HpackUtil.equalsConstantTime(name, e.name) != 0) {
                return this.getIndex(e.index);
            }
            e = e.next;
        }
        return -1;
    }

    private int getIndex(int index) {
        return index == -1 ? -1 : index - this.head.before.index + 1;
    }

    private void add(CharSequence name, CharSequence value, long headerSize) {
        HeaderEntry e;
        if (headerSize > this.maxHeaderTableSize) {
            this.clear();
            return;
        }
        while (this.maxHeaderTableSize - this.size < headerSize) {
            this.remove();
        }
        int h = AsciiString.hashCode(name);
        int i = this.index(h);
        HeaderEntry old = this.headerFields[i];
        this.headerFields[i] = e = new HeaderEntry(h, name, value, this.head.before.index - 1, old);
        e.addBefore(this.head);
        this.size += headerSize;
    }

    private HpackHeaderField remove() {
        HeaderEntry prev;
        if (this.size == 0L) {
            return null;
        }
        HeaderEntry eldest = this.head.after;
        int h = eldest.hash;
        int i = this.index(h);
        HeaderEntry e = prev = this.headerFields[i];
        while (e != null) {
            HeaderEntry next = e.next;
            if (e == eldest) {
                if (prev == eldest) {
                    this.headerFields[i] = next;
                } else {
                    prev.next = next;
                }
                eldest.remove();
                this.size -= (long)eldest.size();
                return eldest;
            }
            prev = e;
            e = next;
        }
        return null;
    }

    private void clear() {
        Arrays.fill(this.headerFields, null);
        this.head.before = this.head.after = this.head;
        this.size = 0L;
    }

    private int index(int h) {
        return h & this.hashMask;
    }

    private static final class HeaderEntry
    extends HpackHeaderField {
        HeaderEntry before;
        HeaderEntry after;
        HeaderEntry next;
        int hash;
        int index;

        HeaderEntry(int hash, CharSequence name, CharSequence value, int index, HeaderEntry next) {
            super(name, value);
            this.index = index;
            this.hash = hash;
            this.next = next;
        }

        private void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
            this.before = null;
            this.after = null;
            this.next = null;
        }

        private void addBefore(HeaderEntry existingEntry) {
            this.after = existingEntry;
            this.before = existingEntry.before;
            this.before.after = this;
            this.after.before = this;
        }
    }

}


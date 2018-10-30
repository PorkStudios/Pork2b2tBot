/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public final class AsciiString
implements CharSequence,
Comparable<CharSequence> {
    public static final AsciiString EMPTY_STRING = AsciiString.cached("");
    private static final char MAX_CHAR_VALUE = '\u00ff';
    public static final int INDEX_NOT_FOUND = -1;
    private final byte[] value;
    private final int offset;
    private final int length;
    private int hash;
    private String string;
    public static final HashingStrategy<CharSequence> CASE_INSENSITIVE_HASHER = new HashingStrategy<CharSequence>(){

        @Override
        public int hashCode(CharSequence o) {
            return AsciiString.hashCode(o);
        }

        @Override
        public boolean equals(CharSequence a, CharSequence b) {
            return AsciiString.contentEqualsIgnoreCase(a, b);
        }
    };
    public static final HashingStrategy<CharSequence> CASE_SENSITIVE_HASHER = new HashingStrategy<CharSequence>(){

        @Override
        public int hashCode(CharSequence o) {
            return AsciiString.hashCode(o);
        }

        @Override
        public boolean equals(CharSequence a, CharSequence b) {
            return AsciiString.contentEquals(a, b);
        }
    };

    public AsciiString(byte[] value) {
        this(value, true);
    }

    public AsciiString(byte[] value, boolean copy) {
        this(value, 0, value.length, copy);
    }

    public AsciiString(byte[] value, int start, int length, boolean copy) {
        if (copy) {
            this.value = Arrays.copyOfRange(value, start, start + length);
            this.offset = 0;
        } else {
            if (MathUtil.isOutOfBounds(start, length, value.length)) {
                throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length + ')');
            }
            this.value = value;
            this.offset = start;
        }
        this.length = length;
    }

    public AsciiString(ByteBuffer value) {
        this(value, true);
    }

    public AsciiString(ByteBuffer value, boolean copy) {
        this(value, value.position(), value.remaining(), copy);
    }

    public AsciiString(ByteBuffer value, int start, int length, boolean copy) {
        if (MathUtil.isOutOfBounds(start, length, value.capacity())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.capacity(" + value.capacity() + ')');
        }
        if (value.hasArray()) {
            if (copy) {
                int bufferOffset = value.arrayOffset() + start;
                this.value = Arrays.copyOfRange(value.array(), bufferOffset, bufferOffset + length);
                this.offset = 0;
            } else {
                this.value = value.array();
                this.offset = start;
            }
        } else {
            this.value = new byte[length];
            int oldPos = value.position();
            value.get(this.value, 0, length);
            value.position(oldPos);
            this.offset = 0;
        }
        this.length = length;
    }

    public AsciiString(char[] value) {
        this(value, 0, value.length);
    }

    public AsciiString(char[] value, int start, int length) {
        if (MathUtil.isOutOfBounds(start, length, value.length)) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length + ')');
        }
        this.value = new byte[length];
        int i = 0;
        int j = start;
        while (i < length) {
            this.value[i] = AsciiString.c2b(value[j]);
            ++i;
            ++j;
        }
        this.offset = 0;
        this.length = length;
    }

    public AsciiString(char[] value, Charset charset) {
        this(value, charset, 0, value.length);
    }

    public AsciiString(char[] value, Charset charset, int start, int length) {
        CharBuffer cbuf = CharBuffer.wrap(value, start, length);
        CharsetEncoder encoder = CharsetUtil.encoder(charset);
        ByteBuffer nativeBuffer = ByteBuffer.allocate((int)(encoder.maxBytesPerChar() * (float)length));
        encoder.encode(cbuf, nativeBuffer, true);
        int bufferOffset = nativeBuffer.arrayOffset();
        this.value = Arrays.copyOfRange(nativeBuffer.array(), bufferOffset, bufferOffset + nativeBuffer.position());
        this.offset = 0;
        this.length = this.value.length;
    }

    public AsciiString(CharSequence value) {
        this(value, 0, value.length());
    }

    public AsciiString(CharSequence value, int start, int length) {
        if (MathUtil.isOutOfBounds(start, length, value.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length() + ')');
        }
        this.value = new byte[length];
        int i = 0;
        int j = start;
        while (i < length) {
            this.value[i] = AsciiString.c2b(value.charAt(j));
            ++i;
            ++j;
        }
        this.offset = 0;
        this.length = length;
    }

    public AsciiString(CharSequence value, Charset charset) {
        this(value, charset, 0, value.length());
    }

    public AsciiString(CharSequence value, Charset charset, int start, int length) {
        CharBuffer cbuf = CharBuffer.wrap(value, start, start + length);
        CharsetEncoder encoder = CharsetUtil.encoder(charset);
        ByteBuffer nativeBuffer = ByteBuffer.allocate((int)(encoder.maxBytesPerChar() * (float)length));
        encoder.encode(cbuf, nativeBuffer, true);
        int offset = nativeBuffer.arrayOffset();
        this.value = Arrays.copyOfRange(nativeBuffer.array(), offset, offset + nativeBuffer.position());
        this.offset = 0;
        this.length = this.value.length;
    }

    public int forEachByte(ByteProcessor visitor) throws Exception {
        return this.forEachByte0(0, this.length(), visitor);
    }

    public int forEachByte(int index, int length, ByteProcessor visitor) throws Exception {
        if (MathUtil.isOutOfBounds(index, length, this.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= index(" + index + ") <= start + length(" + length + ") <= length(" + this.length() + ')');
        }
        return this.forEachByte0(index, length, visitor);
    }

    private int forEachByte0(int index, int length, ByteProcessor visitor) throws Exception {
        int len = this.offset + index + length;
        for (int i = this.offset + index; i < len; ++i) {
            if (visitor.process(this.value[i])) continue;
            return i - this.offset;
        }
        return -1;
    }

    public int forEachByteDesc(ByteProcessor visitor) throws Exception {
        return this.forEachByteDesc0(0, this.length(), visitor);
    }

    public int forEachByteDesc(int index, int length, ByteProcessor visitor) throws Exception {
        if (MathUtil.isOutOfBounds(index, length, this.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= index(" + index + ") <= start + length(" + length + ") <= length(" + this.length() + ')');
        }
        return this.forEachByteDesc0(index, length, visitor);
    }

    private int forEachByteDesc0(int index, int length, ByteProcessor visitor) throws Exception {
        int end = this.offset + index;
        for (int i = this.offset + index + length - 1; i >= end; --i) {
            if (visitor.process(this.value[i])) continue;
            return i - this.offset;
        }
        return -1;
    }

    public byte byteAt(int index) {
        if (index < 0 || index >= this.length) {
            throw new IndexOutOfBoundsException("index: " + index + " must be in the range [0," + this.length + ")");
        }
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.getByte(this.value, index + this.offset);
        }
        return this.value[index + this.offset];
    }

    public boolean isEmpty() {
        return this.length == 0;
    }

    @Override
    public int length() {
        return this.length;
    }

    public void arrayChanged() {
        this.string = null;
        this.hash = 0;
    }

    public byte[] array() {
        return this.value;
    }

    public int arrayOffset() {
        return this.offset;
    }

    public boolean isEntireArrayUsed() {
        return this.offset == 0 && this.length == this.value.length;
    }

    public byte[] toByteArray() {
        return this.toByteArray(0, this.length());
    }

    public byte[] toByteArray(int start, int end) {
        return Arrays.copyOfRange(this.value, start + this.offset, end + this.offset);
    }

    public void copy(int srcIdx, byte[] dst, int dstIdx, int length) {
        if (MathUtil.isOutOfBounds(srcIdx, length, this.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')');
        }
        System.arraycopy(this.value, srcIdx + this.offset, ObjectUtil.checkNotNull(dst, "dst"), dstIdx, length);
    }

    @Override
    public char charAt(int index) {
        return AsciiString.b2c(this.byteAt(index));
    }

    public boolean contains(CharSequence cs) {
        return this.indexOf(cs) >= 0;
    }

    @Override
    public int compareTo(CharSequence string) {
        if (this == string) {
            return 0;
        }
        int length1 = this.length();
        int length2 = string.length();
        int minLength = Math.min(length1, length2);
        int i = 0;
        int j = this.arrayOffset();
        while (i < minLength) {
            int result = AsciiString.b2c(this.value[j]) - string.charAt(i);
            if (result != 0) {
                return result;
            }
            ++i;
            ++j;
        }
        return length1 - length2;
    }

    public AsciiString concat(CharSequence string) {
        int thisLen = this.length();
        int thatLen = string.length();
        if (thatLen == 0) {
            return this;
        }
        if (string.getClass() == AsciiString.class) {
            AsciiString that = (AsciiString)string;
            if (this.isEmpty()) {
                return that;
            }
            byte[] newValue = new byte[thisLen + thatLen];
            System.arraycopy(this.value, this.arrayOffset(), newValue, 0, thisLen);
            System.arraycopy(that.value, that.arrayOffset(), newValue, thisLen, thatLen);
            return new AsciiString(newValue, false);
        }
        if (this.isEmpty()) {
            return new AsciiString(string);
        }
        byte[] newValue = new byte[thisLen + thatLen];
        System.arraycopy(this.value, this.arrayOffset(), newValue, 0, thisLen);
        int i = thisLen;
        int j = 0;
        while (i < newValue.length) {
            newValue[i] = AsciiString.c2b(string.charAt(j));
            ++i;
            ++j;
        }
        return new AsciiString(newValue, false);
    }

    public boolean endsWith(CharSequence suffix) {
        int suffixLen = suffix.length();
        return this.regionMatches(this.length() - suffixLen, suffix, 0, suffixLen);
    }

    public boolean contentEqualsIgnoreCase(CharSequence string) {
        if (string == null || string.length() != this.length()) {
            return false;
        }
        if (string.getClass() == AsciiString.class) {
            AsciiString rhs = (AsciiString)string;
            int i = this.arrayOffset();
            int j = rhs.arrayOffset();
            while (i < this.length()) {
                if (!AsciiString.equalsIgnoreCase(this.value[i], rhs.value[j])) {
                    return false;
                }
                ++i;
                ++j;
            }
            return true;
        }
        int i = this.arrayOffset();
        int j = 0;
        while (i < this.length()) {
            if (!AsciiString.equalsIgnoreCase(AsciiString.b2c(this.value[i]), string.charAt(j))) {
                return false;
            }
            ++i;
            ++j;
        }
        return true;
    }

    public char[] toCharArray() {
        return this.toCharArray(0, this.length());
    }

    public char[] toCharArray(int start, int end) {
        int length = end - start;
        if (length == 0) {
            return EmptyArrays.EMPTY_CHARS;
        }
        if (MathUtil.isOutOfBounds(start, length, this.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')');
        }
        char[] buffer = new char[length];
        int i = 0;
        int j = start + this.arrayOffset();
        while (i < length) {
            buffer[i] = AsciiString.b2c(this.value[j]);
            ++i;
            ++j;
        }
        return buffer;
    }

    public void copy(int srcIdx, char[] dst, int dstIdx, int length) {
        if (dst == null) {
            throw new NullPointerException("dst");
        }
        if (MathUtil.isOutOfBounds(srcIdx, length, this.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')');
        }
        int dstEnd = dstIdx + length;
        int i = dstIdx;
        int j = srcIdx + this.arrayOffset();
        while (i < dstEnd) {
            dst[i] = AsciiString.b2c(this.value[j]);
            ++i;
            ++j;
        }
    }

    public AsciiString subSequence(int start) {
        return this.subSequence(start, this.length());
    }

    @Override
    public AsciiString subSequence(int start, int end) {
        return this.subSequence(start, end, true);
    }

    public AsciiString subSequence(int start, int end, boolean copy) {
        if (MathUtil.isOutOfBounds(start, end - start, this.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= end (" + end + ") <= length(" + this.length() + ')');
        }
        if (start == 0 && end == this.length()) {
            return this;
        }
        if (end == start) {
            return EMPTY_STRING;
        }
        return new AsciiString(this.value, start + this.offset, end - start, copy);
    }

    public int indexOf(CharSequence string) {
        return this.indexOf(string, 0);
    }

    public int indexOf(CharSequence subString, int start) {
        if (start < 0) {
            start = 0;
        }
        int thisLen = this.length();
        int subCount = subString.length();
        if (subCount <= 0) {
            return start < thisLen ? start : thisLen;
        }
        if (subCount > thisLen - start) {
            return -1;
        }
        char firstChar = subString.charAt(0);
        if (firstChar > '\u00ff') {
            return -1;
        }
        ByteProcessor.IndexOfProcessor IndexOfVisitor = new ByteProcessor.IndexOfProcessor((byte)firstChar);
        try {
            do {
                int i;
                if ((i = this.forEachByte(start, thisLen - start, IndexOfVisitor)) == -1 || subCount + i > thisLen) {
                    return -1;
                }
                int o1 = i;
                int o2 = 0;
                while (++o2 < subCount && AsciiString.b2c(this.value[++o1 + this.arrayOffset()]) == subString.charAt(o2)) {
                }
                if (o2 == subCount) {
                    return i;
                }
                start = i + 1;
            } while (true);
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    public int indexOf(char ch, int start) {
        if (start < 0) {
            start = 0;
        }
        int thisLen = this.length();
        if (ch > '\u00ff') {
            return -1;
        }
        try {
            return this.forEachByte(start, thisLen - start, new ByteProcessor.IndexOfProcessor((byte)ch));
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    public int lastIndexOf(CharSequence string) {
        return this.lastIndexOf(string, this.length());
    }

    public int lastIndexOf(CharSequence subString, int start) {
        int thisLen = this.length();
        int subCount = subString.length();
        if (subCount > thisLen || start < 0) {
            return -1;
        }
        if (subCount <= 0) {
            return start < thisLen ? start : thisLen;
        }
        start = Math.min(start, thisLen - subCount);
        char firstChar = subString.charAt(0);
        if (firstChar > '\u00ff') {
            return -1;
        }
        ByteProcessor.IndexOfProcessor IndexOfVisitor = new ByteProcessor.IndexOfProcessor((byte)firstChar);
        try {
            do {
                int i;
                if ((i = this.forEachByteDesc(start, thisLen - start, IndexOfVisitor)) == -1) {
                    return -1;
                }
                int o1 = i;
                int o2 = 0;
                while (++o2 < subCount && AsciiString.b2c(this.value[++o1 + this.arrayOffset()]) == subString.charAt(o2)) {
                }
                if (o2 == subCount) {
                    return i;
                }
                start = i - 1;
            } while (true);
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    public boolean regionMatches(int thisStart, CharSequence string, int start, int length) {
        if (string == null) {
            throw new NullPointerException("string");
        }
        if (start < 0 || string.length() - start < length) {
            return false;
        }
        int thisLen = this.length();
        if (thisStart < 0 || thisLen - thisStart < length) {
            return false;
        }
        if (length <= 0) {
            return true;
        }
        int thatEnd = start + length;
        int i = start;
        int j = thisStart + this.arrayOffset();
        while (i < thatEnd) {
            if (AsciiString.b2c(this.value[j]) != string.charAt(i)) {
                return false;
            }
            ++i;
            ++j;
        }
        return true;
    }

    public boolean regionMatches(boolean ignoreCase, int thisStart, CharSequence string, int start, int length) {
        if (!ignoreCase) {
            return this.regionMatches(thisStart, string, start, length);
        }
        if (string == null) {
            throw new NullPointerException("string");
        }
        int thisLen = this.length();
        if (thisStart < 0 || length > thisLen - thisStart) {
            return false;
        }
        if (start < 0 || length > string.length() - start) {
            return false;
        }
        int thisEnd = (thisStart += this.arrayOffset()) + length;
        while (thisStart < thisEnd) {
            if (AsciiString.equalsIgnoreCase(AsciiString.b2c(this.value[thisStart++]), string.charAt(start++))) continue;
            return false;
        }
        return true;
    }

    public AsciiString replace(char oldChar, char newChar) {
        int index;
        if (oldChar > '\u00ff') {
            return this;
        }
        byte oldCharByte = AsciiString.c2b(oldChar);
        try {
            index = this.forEachByte(new ByteProcessor.IndexOfProcessor(oldCharByte));
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
            return this;
        }
        if (index == -1) {
            return this;
        }
        byte newCharByte = AsciiString.c2b(newChar);
        byte[] buffer = new byte[this.length()];
        int i = 0;
        int j = this.arrayOffset();
        while (i < buffer.length) {
            byte b = this.value[j];
            if (b == oldCharByte) {
                b = newCharByte;
            }
            buffer[i] = b;
            ++i;
            ++j;
        }
        return new AsciiString(buffer, false);
    }

    public boolean startsWith(CharSequence prefix) {
        return this.startsWith(prefix, 0);
    }

    public boolean startsWith(CharSequence prefix, int start) {
        return this.regionMatches(start, prefix, 0, prefix.length());
    }

    public AsciiString toLowerCase() {
        int i;
        boolean lowercased = true;
        int len = this.length() + this.arrayOffset();
        for (i = this.arrayOffset(); i < len; ++i) {
            byte b = this.value[i];
            if (b < 65 || b > 90) continue;
            lowercased = false;
            break;
        }
        if (lowercased) {
            return this;
        }
        byte[] newValue = new byte[this.length()];
        i = 0;
        int j = this.arrayOffset();
        while (i < newValue.length) {
            newValue[i] = AsciiString.toLowerCase(this.value[j]);
            ++i;
            ++j;
        }
        return new AsciiString(newValue, false);
    }

    public AsciiString toUpperCase() {
        int i;
        boolean uppercased = true;
        int len = this.length() + this.arrayOffset();
        for (i = this.arrayOffset(); i < len; ++i) {
            byte b = this.value[i];
            if (b < 97 || b > 122) continue;
            uppercased = false;
            break;
        }
        if (uppercased) {
            return this;
        }
        byte[] newValue = new byte[this.length()];
        i = 0;
        int j = this.arrayOffset();
        while (i < newValue.length) {
            newValue[i] = AsciiString.toUpperCase(this.value[j]);
            ++i;
            ++j;
        }
        return new AsciiString(newValue, false);
    }

    public static CharSequence trim(CharSequence c) {
        int last;
        int start;
        if (c.getClass() == AsciiString.class) {
            return ((AsciiString)c).trim();
        }
        if (c instanceof String) {
            return ((String)c).trim();
        }
        int end = last = c.length() - 1;
        for (start = 0; start <= end && c.charAt(start) <= ' '; ++start) {
        }
        while (end >= start && c.charAt(end) <= ' ') {
            --end;
        }
        if (start == 0 && end == last) {
            return c;
        }
        return c.subSequence(start, end);
    }

    public AsciiString trim() {
        int last;
        int start;
        int end = last = this.arrayOffset() + this.length() - 1;
        for (start = this.arrayOffset(); start <= end && this.value[start] <= 32; ++start) {
        }
        while (end >= start && this.value[end] <= 32) {
            --end;
        }
        if (start == 0 && end == last) {
            return this;
        }
        return new AsciiString(this.value, start, end - start + 1, false);
    }

    public boolean contentEquals(CharSequence a) {
        if (a == null || a.length() != this.length()) {
            return false;
        }
        if (a.getClass() == AsciiString.class) {
            return this.equals(a);
        }
        int i = this.arrayOffset();
        for (int j = 0; j < a.length(); ++j) {
            if (AsciiString.b2c(this.value[i]) != a.charAt(j)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public boolean matches(String expr) {
        return Pattern.matches(expr, this);
    }

    public AsciiString[] split(String expr, int max) {
        return AsciiString.toAsciiStringArray(Pattern.compile(expr).split(this, max));
    }

    public AsciiString[] split(char delim) {
        int i;
        ArrayList<AsciiString> res = InternalThreadLocalMap.get().arrayList();
        int start = 0;
        int length = this.length();
        for (i = start; i < length; ++i) {
            if (this.charAt(i) != delim) continue;
            if (start == i) {
                res.add(EMPTY_STRING);
            } else {
                res.add(new AsciiString(this.value, start + this.arrayOffset(), i - start, false));
            }
            start = i + 1;
        }
        if (start == 0) {
            res.add(this);
        } else if (start != length) {
            res.add(new AsciiString(this.value, start + this.arrayOffset(), length - start, false));
        } else {
            for (i = res.size() - 1; i >= 0 && ((AsciiString)res.get(i)).isEmpty(); --i) {
                res.remove(i);
            }
        }
        return res.toArray(new AsciiString[res.size()]);
    }

    public int hashCode() {
        int h = this.hash;
        if (h == 0) {
            this.hash = h = PlatformDependent.hashCodeAscii(this.value, this.offset, this.length);
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != AsciiString.class) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AsciiString other = (AsciiString)obj;
        return this.length() == other.length() && this.hashCode() == other.hashCode() && PlatformDependent.equals(this.array(), this.arrayOffset(), other.array(), other.arrayOffset(), this.length());
    }

    @Override
    public String toString() {
        String cache = this.string;
        if (cache == null) {
            this.string = cache = this.toString(0);
        }
        return cache;
    }

    public String toString(int start) {
        return this.toString(start, this.length());
    }

    public String toString(int start, int end) {
        int length = end - start;
        if (length == 0) {
            return "";
        }
        if (MathUtil.isOutOfBounds(start, length, this.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')');
        }
        String str = new String(this.value, 0, start + this.offset, length);
        return str;
    }

    public boolean parseBoolean() {
        return this.length >= 1 && this.value[this.offset] != 0;
    }

    public char parseChar() {
        return this.parseChar(0);
    }

    public char parseChar(int start) {
        if (start + 1 >= this.length()) {
            throw new IndexOutOfBoundsException("2 bytes required to convert to character. index " + start + " would go out of bounds.");
        }
        int startWithOffset = start + this.offset;
        return (char)(AsciiString.b2c(this.value[startWithOffset]) << 8 | AsciiString.b2c(this.value[startWithOffset + 1]));
    }

    public short parseShort() {
        return this.parseShort(0, this.length(), 10);
    }

    public short parseShort(int radix) {
        return this.parseShort(0, this.length(), radix);
    }

    public short parseShort(int start, int end) {
        return this.parseShort(start, end, 10);
    }

    public short parseShort(int start, int end, int radix) {
        int intValue = this.parseInt(start, end, radix);
        short result = (short)intValue;
        if (result != intValue) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
        }
        return result;
    }

    public int parseInt() {
        return this.parseInt(0, this.length(), 10);
    }

    public int parseInt(int radix) {
        return this.parseInt(0, this.length(), radix);
    }

    public int parseInt(int start, int end) {
        return this.parseInt(start, end, 10);
    }

    public int parseInt(int start, int end, int radix) {
        boolean negative;
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException();
        }
        if (start == end) {
            throw new NumberFormatException();
        }
        int i = start;
        boolean bl = negative = this.byteAt(i) == 45;
        if (negative && ++i == end) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
        }
        return this.parseInt(i, end, radix, negative);
    }

    private int parseInt(int start, int end, int radix, boolean negative) {
        int max = Integer.MIN_VALUE / radix;
        int result = 0;
        int currOffset = start;
        while (currOffset < end) {
            int digit;
            if ((digit = Character.digit((char)(this.value[currOffset++ + this.offset] & 255), radix)) == -1) {
                throw new NumberFormatException(this.subSequence(start, end, false).toString());
            }
            if (max > result) {
                throw new NumberFormatException(this.subSequence(start, end, false).toString());
            }
            int next = result * radix - digit;
            if (next > result) {
                throw new NumberFormatException(this.subSequence(start, end, false).toString());
            }
            result = next;
        }
        if (!negative && (result = - result) < 0) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
        }
        return result;
    }

    public long parseLong() {
        return this.parseLong(0, this.length(), 10);
    }

    public long parseLong(int radix) {
        return this.parseLong(0, this.length(), radix);
    }

    public long parseLong(int start, int end) {
        return this.parseLong(start, end, 10);
    }

    public long parseLong(int start, int end, int radix) {
        boolean negative;
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException();
        }
        if (start == end) {
            throw new NumberFormatException();
        }
        int i = start;
        boolean bl = negative = this.byteAt(i) == 45;
        if (negative && ++i == end) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
        }
        return this.parseLong(i, end, radix, negative);
    }

    private long parseLong(int start, int end, int radix, boolean negative) {
        long max = Long.MIN_VALUE / (long)radix;
        long result = 0L;
        int currOffset = start;
        while (currOffset < end) {
            int digit;
            if ((digit = Character.digit((char)(this.value[currOffset++ + this.offset] & 255), radix)) == -1) {
                throw new NumberFormatException(this.subSequence(start, end, false).toString());
            }
            if (max > result) {
                throw new NumberFormatException(this.subSequence(start, end, false).toString());
            }
            long next = result * (long)radix - (long)digit;
            if (next > result) {
                throw new NumberFormatException(this.subSequence(start, end, false).toString());
            }
            result = next;
        }
        if (!negative && (result = - result) < 0L) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
        }
        return result;
    }

    public float parseFloat() {
        return this.parseFloat(0, this.length());
    }

    public float parseFloat(int start, int end) {
        return Float.parseFloat(this.toString(start, end));
    }

    public double parseDouble() {
        return this.parseDouble(0, this.length());
    }

    public double parseDouble(int start, int end) {
        return Double.parseDouble(this.toString(start, end));
    }

    public static AsciiString of(CharSequence string) {
        return string.getClass() == AsciiString.class ? (AsciiString)string : new AsciiString(string);
    }

    public static AsciiString cached(String string) {
        AsciiString asciiString = new AsciiString(string);
        asciiString.string = string;
        return asciiString;
    }

    public static int hashCode(CharSequence value) {
        if (value == null) {
            return 0;
        }
        if (value.getClass() == AsciiString.class) {
            return value.hashCode();
        }
        return PlatformDependent.hashCodeAscii(value);
    }

    public static boolean contains(CharSequence a, CharSequence b) {
        return AsciiString.contains(a, b, DefaultCharEqualityComparator.INSTANCE);
    }

    public static boolean containsIgnoreCase(CharSequence a, CharSequence b) {
        return AsciiString.contains(a, b, AsciiCaseInsensitiveCharEqualityComparator.INSTANCE);
    }

    public static boolean contentEqualsIgnoreCase(CharSequence a, CharSequence b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.getClass() == AsciiString.class) {
            return ((AsciiString)a).contentEqualsIgnoreCase(b);
        }
        if (b.getClass() == AsciiString.class) {
            return ((AsciiString)b).contentEqualsIgnoreCase(a);
        }
        if (a.length() != b.length()) {
            return false;
        }
        int i = 0;
        int j = 0;
        while (i < a.length()) {
            if (!AsciiString.equalsIgnoreCase(a.charAt(i), b.charAt(j))) {
                return false;
            }
            ++i;
            ++j;
        }
        return true;
    }

    public static boolean containsContentEqualsIgnoreCase(Collection<CharSequence> collection, CharSequence value) {
        for (CharSequence v : collection) {
            if (!AsciiString.contentEqualsIgnoreCase(value, v)) continue;
            return true;
        }
        return false;
    }

    public static boolean containsAllContentEqualsIgnoreCase(Collection<CharSequence> a, Collection<CharSequence> b) {
        for (CharSequence v : b) {
            if (AsciiString.containsContentEqualsIgnoreCase(a, v)) continue;
            return false;
        }
        return true;
    }

    public static boolean contentEquals(CharSequence a, CharSequence b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.getClass() == AsciiString.class) {
            return ((AsciiString)a).contentEquals(b);
        }
        if (b.getClass() == AsciiString.class) {
            return ((AsciiString)b).contentEquals(a);
        }
        if (a.length() != b.length()) {
            return false;
        }
        for (int i = 0; i < a.length(); ++i) {
            if (a.charAt(i) == b.charAt(i)) continue;
            return false;
        }
        return true;
    }

    private static AsciiString[] toAsciiStringArray(String[] jdkResult) {
        AsciiString[] res = new AsciiString[jdkResult.length];
        for (int i = 0; i < jdkResult.length; ++i) {
            res[i] = new AsciiString(jdkResult[i]);
        }
        return res;
    }

    private static boolean contains(CharSequence a, CharSequence b, CharEqualityComparator cmp) {
        if (a == null || b == null || a.length() < b.length()) {
            return false;
        }
        if (b.length() == 0) {
            return true;
        }
        int bStart = 0;
        for (int i = 0; i < a.length(); ++i) {
            if (cmp.equals(b.charAt(bStart), a.charAt(i))) {
                if (++bStart != b.length()) continue;
                return true;
            }
            if (a.length() - i < b.length()) {
                return false;
            }
            bStart = 0;
        }
        return false;
    }

    private static boolean regionMatchesCharSequences(CharSequence cs, int csStart, CharSequence string, int start, int length, CharEqualityComparator charEqualityComparator) {
        if (csStart < 0 || length > cs.length() - csStart) {
            return false;
        }
        if (start < 0 || length > string.length() - start) {
            return false;
        }
        int csIndex = csStart;
        int csEnd = csIndex + length;
        int stringIndex = start;
        while (csIndex < csEnd) {
            char c1;
            char c2;
            if (charEqualityComparator.equals(c1 = cs.charAt(csIndex++), c2 = string.charAt(stringIndex++))) continue;
            return false;
        }
        return true;
    }

    public static boolean regionMatches(CharSequence cs, boolean ignoreCase, int csStart, CharSequence string, int start, int length) {
        if (cs == null || string == null) {
            return false;
        }
        if (cs instanceof String && string instanceof String) {
            return ((String)cs).regionMatches(ignoreCase, csStart, (String)string, start, length);
        }
        if (cs instanceof AsciiString) {
            return ((AsciiString)cs).regionMatches(ignoreCase, csStart, string, start, length);
        }
        return AsciiString.regionMatchesCharSequences(cs, csStart, string, start, length, ignoreCase ? GeneralCaseInsensitiveCharEqualityComparator.INSTANCE : DefaultCharEqualityComparator.INSTANCE);
    }

    public static boolean regionMatchesAscii(CharSequence cs, boolean ignoreCase, int csStart, CharSequence string, int start, int length) {
        if (cs == null || string == null) {
            return false;
        }
        if (!ignoreCase && cs instanceof String && string instanceof String) {
            return ((String)cs).regionMatches(false, csStart, (String)string, start, length);
        }
        if (cs instanceof AsciiString) {
            return ((AsciiString)cs).regionMatches(ignoreCase, csStart, string, start, length);
        }
        return AsciiString.regionMatchesCharSequences(cs, csStart, string, start, length, ignoreCase ? AsciiCaseInsensitiveCharEqualityComparator.INSTANCE : DefaultCharEqualityComparator.INSTANCE);
    }

    public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        int searchStrLen = searchStr.length();
        int endLimit = str.length() - searchStrLen + 1;
        if (startPos > endLimit) {
            return -1;
        }
        if (searchStrLen == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; ++i) {
            if (!AsciiString.regionMatches(str, true, i, searchStr, 0, searchStrLen)) continue;
            return i;
        }
        return -1;
    }

    public static int indexOfIgnoreCaseAscii(CharSequence str, CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        int searchStrLen = searchStr.length();
        int endLimit = str.length() - searchStrLen + 1;
        if (startPos > endLimit) {
            return -1;
        }
        if (searchStrLen == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; ++i) {
            if (!AsciiString.regionMatchesAscii(str, true, i, searchStr, 0, searchStrLen)) continue;
            return i;
        }
        return -1;
    }

    public static int indexOf(CharSequence cs, char searchChar, int start) {
        if (cs instanceof String) {
            return ((String)cs).indexOf(searchChar, start);
        }
        if (cs instanceof AsciiString) {
            return ((AsciiString)cs).indexOf(searchChar, start);
        }
        if (cs == null) {
            return -1;
        }
        int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        for (int i = start; i < sz; ++i) {
            if (cs.charAt(i) != searchChar) continue;
            return i;
        }
        return -1;
    }

    private static boolean equalsIgnoreCase(byte a, byte b) {
        return a == b || AsciiString.toLowerCase(a) == AsciiString.toLowerCase(b);
    }

    private static boolean equalsIgnoreCase(char a, char b) {
        return a == b || AsciiString.toLowerCase(a) == AsciiString.toLowerCase(b);
    }

    private static byte toLowerCase(byte b) {
        return AsciiString.isUpperCase(b) ? (byte)(b + 32) : b;
    }

    private static char toLowerCase(char c) {
        return AsciiString.isUpperCase(c) ? (char)(c + 32) : c;
    }

    private static byte toUpperCase(byte b) {
        return AsciiString.isLowerCase(b) ? (byte)(b - 32) : b;
    }

    private static boolean isLowerCase(byte value) {
        return value >= 97 && value <= 122;
    }

    public static boolean isUpperCase(byte value) {
        return value >= 65 && value <= 90;
    }

    public static boolean isUpperCase(char value) {
        return value >= 'A' && value <= 'Z';
    }

    public static byte c2b(char c) {
        return (byte)(c > '\u00ff' ? 63 : (int)c);
    }

    public static char b2c(byte b) {
        return (char)(b & 255);
    }

    private static final class GeneralCaseInsensitiveCharEqualityComparator
    implements CharEqualityComparator {
        static final GeneralCaseInsensitiveCharEqualityComparator INSTANCE = new GeneralCaseInsensitiveCharEqualityComparator();

        private GeneralCaseInsensitiveCharEqualityComparator() {
        }

        @Override
        public boolean equals(char a, char b) {
            return Character.toUpperCase(a) == Character.toUpperCase(b) || Character.toLowerCase(a) == Character.toLowerCase(b);
        }
    }

    private static final class AsciiCaseInsensitiveCharEqualityComparator
    implements CharEqualityComparator {
        static final AsciiCaseInsensitiveCharEqualityComparator INSTANCE = new AsciiCaseInsensitiveCharEqualityComparator();

        private AsciiCaseInsensitiveCharEqualityComparator() {
        }

        @Override
        public boolean equals(char a, char b) {
            return AsciiString.equalsIgnoreCase(a, b);
        }
    }

    private static final class DefaultCharEqualityComparator
    implements CharEqualityComparator {
        static final DefaultCharEqualityComparator INSTANCE = new DefaultCharEqualityComparator();

        private DefaultCharEqualityComparator() {
        }

        @Override
        public boolean equals(char a, char b) {
            return a == b;
        }
    }

    private static interface CharEqualityComparator {
        public boolean equals(char var1, char var2);
    }

}


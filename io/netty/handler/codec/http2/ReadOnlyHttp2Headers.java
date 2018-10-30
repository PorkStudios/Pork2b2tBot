/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import io.netty.util.internal.EmptyArrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class ReadOnlyHttp2Headers
implements Http2Headers {
    private static final byte PSEUDO_HEADER_TOKEN = 58;
    private final AsciiString[] pseudoHeaders;
    private final AsciiString[] otherHeaders;

    public static /* varargs */ ReadOnlyHttp2Headers trailers(boolean validateHeaders, AsciiString ... otherHeaders) {
        return new ReadOnlyHttp2Headers(validateHeaders, EmptyArrays.EMPTY_ASCII_STRINGS, otherHeaders);
    }

    public static /* varargs */ ReadOnlyHttp2Headers clientHeaders(boolean validateHeaders, AsciiString method, AsciiString path, AsciiString scheme, AsciiString authority, AsciiString ... otherHeaders) {
        return new ReadOnlyHttp2Headers(validateHeaders, new AsciiString[]{Http2Headers.PseudoHeaderName.METHOD.value(), method, Http2Headers.PseudoHeaderName.PATH.value(), path, Http2Headers.PseudoHeaderName.SCHEME.value(), scheme, Http2Headers.PseudoHeaderName.AUTHORITY.value(), authority}, otherHeaders);
    }

    public static /* varargs */ ReadOnlyHttp2Headers serverHeaders(boolean validateHeaders, AsciiString status, AsciiString ... otherHeaders) {
        return new ReadOnlyHttp2Headers(validateHeaders, new AsciiString[]{Http2Headers.PseudoHeaderName.STATUS.value(), status}, otherHeaders);
    }

    private /* varargs */ ReadOnlyHttp2Headers(boolean validateHeaders, AsciiString[] pseudoHeaders, AsciiString ... otherHeaders) {
        assert ((pseudoHeaders.length & 1) == 0);
        if ((otherHeaders.length & 1) != 0) {
            throw ReadOnlyHttp2Headers.newInvalidArraySizeException();
        }
        if (validateHeaders) {
            ReadOnlyHttp2Headers.validateHeaders(pseudoHeaders, otherHeaders);
        }
        this.pseudoHeaders = pseudoHeaders;
        this.otherHeaders = otherHeaders;
    }

    private static IllegalArgumentException newInvalidArraySizeException() {
        return new IllegalArgumentException("pseudoHeaders and otherHeaders must be arrays of [name, value] pairs");
    }

    private static /* varargs */ void validateHeaders(AsciiString[] pseudoHeaders, AsciiString ... otherHeaders) {
        for (int i = 1; i < pseudoHeaders.length; i += 2) {
            if (pseudoHeaders[i] != null) continue;
            throw new IllegalArgumentException("pseudoHeaders value at index " + i + " is null");
        }
        boolean seenNonPseudoHeader = false;
        int otherHeadersEnd = otherHeaders.length - 1;
        for (int i = 0; i < otherHeadersEnd; i += 2) {
            AsciiString name = otherHeaders[i];
            DefaultHttp2Headers.HTTP2_NAME_VALIDATOR.validateName(name);
            if (!seenNonPseudoHeader && !name.isEmpty() && name.byteAt(0) != 58) {
                seenNonPseudoHeader = true;
            } else if (seenNonPseudoHeader && !name.isEmpty() && name.byteAt(0) == 58) {
                throw new IllegalArgumentException("otherHeaders name at index " + i + " is a pseudo header that appears after non-pseudo headers.");
            }
            if (otherHeaders[i + 1] != null) continue;
            throw new IllegalArgumentException("otherHeaders value at index " + (i + 1) + " is null");
        }
    }

    private AsciiString get0(CharSequence name) {
        int nameHash = AsciiString.hashCode(name);
        int pseudoHeadersEnd = this.pseudoHeaders.length - 1;
        for (int i = 0; i < pseudoHeadersEnd; i += 2) {
            AsciiString roName = this.pseudoHeaders[i];
            if (roName.hashCode() != nameHash || !roName.contentEqualsIgnoreCase(name)) continue;
            return this.pseudoHeaders[i + 1];
        }
        int otherHeadersEnd = this.otherHeaders.length - 1;
        for (int i = 0; i < otherHeadersEnd; i += 2) {
            AsciiString roName = this.otherHeaders[i];
            if (roName.hashCode() != nameHash || !roName.contentEqualsIgnoreCase(name)) continue;
            return this.otherHeaders[i + 1];
        }
        return null;
    }

    @Override
    public CharSequence get(CharSequence name) {
        return this.get0(name);
    }

    @Override
    public CharSequence get(CharSequence name, CharSequence defaultValue) {
        CharSequence value = this.get(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public CharSequence getAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public CharSequence getAndRemove(CharSequence name, CharSequence defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public List<CharSequence> getAll(CharSequence name) {
        int nameHash = AsciiString.hashCode(name);
        ArrayList<CharSequence> values = new ArrayList<CharSequence>();
        int pseudoHeadersEnd = this.pseudoHeaders.length - 1;
        for (int i = 0; i < pseudoHeadersEnd; i += 2) {
            AsciiString roName = this.pseudoHeaders[i];
            if (roName.hashCode() != nameHash || !roName.contentEqualsIgnoreCase(name)) continue;
            values.add(this.pseudoHeaders[i + 1]);
        }
        int otherHeadersEnd = this.otherHeaders.length - 1;
        for (int i = 0; i < otherHeadersEnd; i += 2) {
            AsciiString roName = this.otherHeaders[i];
            if (roName.hashCode() != nameHash || !roName.contentEqualsIgnoreCase(name)) continue;
            values.add(this.otherHeaders[i + 1]);
        }
        return values;
    }

    @Override
    public List<CharSequence> getAllAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Boolean getBoolean(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Boolean.valueOf(CharSequenceValueConverter.INSTANCE.convertToBoolean(value)) : null;
    }

    @Override
    public boolean getBoolean(CharSequence name, boolean defaultValue) {
        Boolean value = this.getBoolean(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Byte getByte(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Byte.valueOf(CharSequenceValueConverter.INSTANCE.convertToByte(value)) : null;
    }

    @Override
    public byte getByte(CharSequence name, byte defaultValue) {
        Byte value = this.getByte(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Character getChar(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Character.valueOf(CharSequenceValueConverter.INSTANCE.convertToChar(value)) : null;
    }

    @Override
    public char getChar(CharSequence name, char defaultValue) {
        Character value = this.getChar(name);
        return value != null ? value.charValue() : defaultValue;
    }

    @Override
    public Short getShort(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Short.valueOf(CharSequenceValueConverter.INSTANCE.convertToShort(value)) : null;
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        Short value = this.getShort(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Integer getInt(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Integer.valueOf(CharSequenceValueConverter.INSTANCE.convertToInt(value)) : null;
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        Integer value = this.getInt(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Long getLong(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Long.valueOf(CharSequenceValueConverter.INSTANCE.convertToLong(value)) : null;
    }

    @Override
    public long getLong(CharSequence name, long defaultValue) {
        Long value = this.getLong(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Float getFloat(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Float.valueOf(CharSequenceValueConverter.INSTANCE.convertToFloat(value)) : null;
    }

    @Override
    public float getFloat(CharSequence name, float defaultValue) {
        Float value = this.getFloat(name);
        return value != null ? value.floatValue() : defaultValue;
    }

    @Override
    public Double getDouble(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Double.valueOf(CharSequenceValueConverter.INSTANCE.convertToDouble(value)) : null;
    }

    @Override
    public double getDouble(CharSequence name, double defaultValue) {
        Double value = this.getDouble(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        AsciiString value = this.get0(name);
        return value != null ? Long.valueOf(CharSequenceValueConverter.INSTANCE.convertToTimeMillis(value)) : null;
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        Long value = this.getTimeMillis(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Boolean getBooleanAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean getBooleanAndRemove(CharSequence name, boolean defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Byte getByteAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public byte getByteAndRemove(CharSequence name, byte defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Character getCharAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public char getCharAndRemove(CharSequence name, char defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Short getShortAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public short getShortAndRemove(CharSequence name, short defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Integer getIntAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public int getIntAndRemove(CharSequence name, int defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Long getLongAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public long getLongAndRemove(CharSequence name, long defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Float getFloatAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public float getFloatAndRemove(CharSequence name, float defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Double getDoubleAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public double getDoubleAndRemove(CharSequence name, double defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Long getTimeMillisAndRemove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public long getTimeMillisAndRemove(CharSequence name, long defaultValue) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean contains(CharSequence name) {
        return this.get(name) != null;
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value) {
        int nameHash = AsciiString.hashCode(name);
        int valueHash = AsciiString.hashCode(value);
        int pseudoHeadersEnd = this.pseudoHeaders.length - 1;
        for (int i = 0; i < pseudoHeadersEnd; i += 2) {
            AsciiString roName = this.pseudoHeaders[i];
            AsciiString roValue = this.pseudoHeaders[i + 1];
            if (roName.hashCode() != nameHash || roValue.hashCode() != valueHash || !roName.contentEqualsIgnoreCase(name) || !roValue.contentEqualsIgnoreCase(value)) continue;
            return true;
        }
        int otherHeadersEnd = this.otherHeaders.length - 1;
        for (int i = 0; i < otherHeadersEnd; i += 2) {
            AsciiString roName = this.otherHeaders[i];
            AsciiString roValue = this.otherHeaders[i + 1];
            if (roName.hashCode() != nameHash || roValue.hashCode() != valueHash || !roName.contentEqualsIgnoreCase(name) || !roValue.contentEqualsIgnoreCase(value)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsObject(CharSequence name, Object value) {
        if (value instanceof CharSequence) {
            return this.contains(name, (CharSequence)value);
        }
        return this.contains(name, value.toString());
    }

    @Override
    public boolean containsBoolean(CharSequence name, boolean value) {
        return this.contains(name, String.valueOf(value));
    }

    @Override
    public boolean containsByte(CharSequence name, byte value) {
        return this.contains(name, String.valueOf(value));
    }

    @Override
    public boolean containsChar(CharSequence name, char value) {
        return this.contains(name, String.valueOf(value));
    }

    @Override
    public boolean containsShort(CharSequence name, short value) {
        return this.contains(name, String.valueOf(value));
    }

    @Override
    public boolean containsInt(CharSequence name, int value) {
        return this.contains(name, String.valueOf(value));
    }

    @Override
    public boolean containsLong(CharSequence name, long value) {
        return this.contains(name, String.valueOf(value));
    }

    @Override
    public boolean containsFloat(CharSequence name, float value) {
        return false;
    }

    @Override
    public boolean containsDouble(CharSequence name, double value) {
        return this.contains(name, String.valueOf(value));
    }

    @Override
    public boolean containsTimeMillis(CharSequence name, long value) {
        return this.contains(name, String.valueOf(value));
    }

    @Override
    public int size() {
        return this.pseudoHeaders.length + this.otherHeaders.length >>> 1;
    }

    @Override
    public boolean isEmpty() {
        return this.pseudoHeaders.length == 0 && this.otherHeaders.length == 0;
    }

    @Override
    public Set<CharSequence> names() {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet<CharSequence> names = new LinkedHashSet<CharSequence>(this.size());
        int pseudoHeadersEnd = this.pseudoHeaders.length - 1;
        for (int i = 0; i < pseudoHeadersEnd; i += 2) {
            names.add(this.pseudoHeaders[i]);
        }
        int otherHeadersEnd = this.otherHeaders.length - 1;
        for (int i = 0; i < otherHeadersEnd; i += 2) {
            names.add(this.otherHeaders[i]);
        }
        return names;
    }

    @Override
    public Http2Headers add(CharSequence name, CharSequence value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers add(CharSequence name, Iterable<? extends CharSequence> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public /* varargs */ Http2Headers add(CharSequence name, CharSequence ... values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addObject(CharSequence name, Object value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addObject(CharSequence name, Iterable<?> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public /* varargs */ Http2Headers addObject(CharSequence name, Object ... values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addBoolean(CharSequence name, boolean value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addByte(CharSequence name, byte value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addChar(CharSequence name, char value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addShort(CharSequence name, short value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addInt(CharSequence name, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addLong(CharSequence name, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addFloat(CharSequence name, float value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addDouble(CharSequence name, double value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers addTimeMillis(CharSequence name, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers add(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers set(CharSequence name, CharSequence value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers set(CharSequence name, Iterable<? extends CharSequence> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public /* varargs */ Http2Headers set(CharSequence name, CharSequence ... values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setObject(CharSequence name, Object value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setObject(CharSequence name, Iterable<?> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public /* varargs */ Http2Headers setObject(CharSequence name, Object ... values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setBoolean(CharSequence name, boolean value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setByte(CharSequence name, byte value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setChar(CharSequence name, char value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setShort(CharSequence name, short value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setInt(CharSequence name, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setLong(CharSequence name, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setFloat(CharSequence name, float value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setDouble(CharSequence name, double value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setTimeMillis(CharSequence name, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers set(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers setAll(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean remove(CharSequence name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers clear() {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iterator() {
        return new ReadOnlyIterator();
    }

    @Override
    public Iterator<CharSequence> valueIterator(CharSequence name) {
        return new ReadOnlyValueIterator(name);
    }

    @Override
    public Http2Headers method(CharSequence value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers scheme(CharSequence value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers authority(CharSequence value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers path(CharSequence value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Http2Headers status(CharSequence value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public CharSequence method() {
        return this.get(Http2Headers.PseudoHeaderName.METHOD.value());
    }

    @Override
    public CharSequence scheme() {
        return this.get(Http2Headers.PseudoHeaderName.SCHEME.value());
    }

    @Override
    public CharSequence authority() {
        return this.get(Http2Headers.PseudoHeaderName.AUTHORITY.value());
    }

    @Override
    public CharSequence path() {
        return this.get(Http2Headers.PseudoHeaderName.PATH.value());
    }

    @Override
    public CharSequence status() {
        return this.get(Http2Headers.PseudoHeaderName.STATUS.value());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName()).append('[');
        String separator = "";
        for (Map.Entry<CharSequence, CharSequence> entry : this) {
            builder.append(separator);
            builder.append(entry.getKey()).append(": ").append(entry.getValue());
            separator = ", ";
        }
        return builder.append(']').toString();
    }

    private final class ReadOnlyIterator
    implements Map.Entry<CharSequence, CharSequence>,
    Iterator<Map.Entry<CharSequence, CharSequence>> {
        private int i;
        private AsciiString[] current;
        private AsciiString key;
        private AsciiString value;

        private ReadOnlyIterator() {
            this.current = ReadOnlyHttp2Headers.this.pseudoHeaders.length != 0 ? ReadOnlyHttp2Headers.this.pseudoHeaders : ReadOnlyHttp2Headers.this.otherHeaders;
        }

        @Override
        public boolean hasNext() {
            return this.i != this.current.length;
        }

        @Override
        public Map.Entry<CharSequence, CharSequence> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.key = this.current[this.i];
            this.value = this.current[this.i + 1];
            this.i += 2;
            if (this.i == this.current.length && this.current == ReadOnlyHttp2Headers.this.pseudoHeaders) {
                this.current = ReadOnlyHttp2Headers.this.otherHeaders;
                this.i = 0;
            }
            return this;
        }

        @Override
        public CharSequence getKey() {
            return this.key;
        }

        @Override
        public CharSequence getValue() {
            return this.value;
        }

        @Override
        public CharSequence setValue(CharSequence value) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("read only");
        }

        public String toString() {
            return this.key.toString() + '=' + this.value.toString();
        }
    }

    private final class ReadOnlyValueIterator
    implements Iterator<CharSequence> {
        private int i;
        private final int nameHash;
        private final CharSequence name;
        private AsciiString[] current;
        private AsciiString next;

        ReadOnlyValueIterator(CharSequence name) {
            this.current = ReadOnlyHttp2Headers.this.pseudoHeaders.length != 0 ? ReadOnlyHttp2Headers.this.pseudoHeaders : ReadOnlyHttp2Headers.this.otherHeaders;
            this.nameHash = AsciiString.hashCode(name);
            this.name = name;
            this.calculateNext();
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public CharSequence next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            AsciiString current = this.next;
            this.calculateNext();
            return current;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("read only");
        }

        private void calculateNext() {
            while (this.i < this.current.length) {
                AsciiString roName = this.current[this.i];
                if (roName.hashCode() == this.nameHash && roName.contentEqualsIgnoreCase(this.name)) {
                    this.next = this.current[this.i + 1];
                    this.i += 2;
                    return;
                }
                this.i += 2;
            }
            if (this.i >= this.current.length && this.current == ReadOnlyHttp2Headers.this.pseudoHeaders) {
                this.i = 0;
                this.current = ReadOnlyHttp2Headers.this.otherHeaders;
                this.calculateNext();
            } else {
                this.next = null;
            }
        }
    }

}


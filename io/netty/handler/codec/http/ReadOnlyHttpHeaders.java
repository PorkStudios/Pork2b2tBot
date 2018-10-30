/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AsciiString;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class ReadOnlyHttpHeaders
extends HttpHeaders {
    private final CharSequence[] nameValuePairs;

    public /* varargs */ ReadOnlyHttpHeaders(boolean validateHeaders, CharSequence ... nameValuePairs) {
        if ((nameValuePairs.length & 1) != 0) {
            throw ReadOnlyHttpHeaders.newInvalidArraySizeException();
        }
        if (validateHeaders) {
            ReadOnlyHttpHeaders.validateHeaders(nameValuePairs);
        }
        this.nameValuePairs = nameValuePairs;
    }

    private static IllegalArgumentException newInvalidArraySizeException() {
        return new IllegalArgumentException("nameValuePairs must be arrays of [name, value] pairs");
    }

    private static /* varargs */ void validateHeaders(CharSequence ... keyValuePairs) {
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            DefaultHttpHeaders.HttpNameValidator.validateName(keyValuePairs[i]);
        }
    }

    private CharSequence get0(CharSequence name) {
        int nameHash = AsciiString.hashCode(name);
        for (int i = 0; i < this.nameValuePairs.length; i += 2) {
            CharSequence roName = this.nameValuePairs[i];
            if (AsciiString.hashCode(roName) != nameHash || !AsciiString.contentEqualsIgnoreCase(roName, name)) continue;
            return this.nameValuePairs[i + 1];
        }
        return null;
    }

    @Override
    public String get(String name) {
        CharSequence value = this.get0(name);
        return value == null ? null : value.toString();
    }

    @Override
    public Integer getInt(CharSequence name) {
        CharSequence value = this.get0(name);
        return value == null ? null : Integer.valueOf(CharSequenceValueConverter.INSTANCE.convertToInt(value));
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        CharSequence value = this.get0(name);
        return value == null ? defaultValue : CharSequenceValueConverter.INSTANCE.convertToInt(value);
    }

    @Override
    public Short getShort(CharSequence name) {
        CharSequence value = this.get0(name);
        return value == null ? null : Short.valueOf(CharSequenceValueConverter.INSTANCE.convertToShort(value));
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        CharSequence value = this.get0(name);
        return value == null ? defaultValue : CharSequenceValueConverter.INSTANCE.convertToShort(value);
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        CharSequence value = this.get0(name);
        return value == null ? null : Long.valueOf(CharSequenceValueConverter.INSTANCE.convertToTimeMillis(value));
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        CharSequence value = this.get0(name);
        return value == null ? defaultValue : CharSequenceValueConverter.INSTANCE.convertToTimeMillis(value);
    }

    @Override
    public List<String> getAll(String name) {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        int nameHash = AsciiString.hashCode(name);
        ArrayList<String> values = new ArrayList<String>(4);
        for (int i = 0; i < this.nameValuePairs.length; i += 2) {
            CharSequence roName = this.nameValuePairs[i];
            if (AsciiString.hashCode(roName) != nameHash || !AsciiString.contentEqualsIgnoreCase(roName, name)) continue;
            values.add(this.nameValuePairs[i + 1].toString());
        }
        return values;
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Map.Entry<String, String>> entries = new ArrayList<Map.Entry<String, String>>(this.size());
        for (int i = 0; i < this.nameValuePairs.length; i += 2) {
            entries.add(new AbstractMap.SimpleImmutableEntry<String, String>(this.nameValuePairs[i].toString(), this.nameValuePairs[i + 1].toString()));
        }
        return entries;
    }

    @Override
    public boolean contains(String name) {
        return this.get0(name) != null;
    }

    @Override
    public boolean contains(String name, String value, boolean ignoreCase) {
        return this.containsValue(name, value, ignoreCase);
    }

    @Override
    public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
        if (ignoreCase) {
            for (int i = 0; i < this.nameValuePairs.length; i += 2) {
                if (!AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[i], name) || !AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[i + 1], value)) continue;
                return true;
            }
        } else {
            for (int i = 0; i < this.nameValuePairs.length; i += 2) {
                if (!AsciiString.contentEqualsIgnoreCase(this.nameValuePairs[i], name) || !AsciiString.contentEquals(this.nameValuePairs[i + 1], value)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<String> valueStringIterator(CharSequence name) {
        return new ReadOnlyStringValueIterator(name);
    }

    public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
        return new ReadOnlyValueIterator(name);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return new ReadOnlyStringIterator();
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        return new ReadOnlyIterator();
    }

    @Override
    public boolean isEmpty() {
        return this.nameValuePairs.length == 0;
    }

    @Override
    public int size() {
        return this.nameValuePairs.length >>> 1;
    }

    @Override
    public Set<String> names() {
        if (this.isEmpty()) {
            return Collections.emptySet();
        }
        LinkedHashSet<String> names = new LinkedHashSet<String>(this.size());
        for (int i = 0; i < this.nameValuePairs.length; i += 2) {
            names.add(this.nameValuePairs[i].toString());
        }
        return names;
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders add(String name, Iterable<?> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders addInt(CharSequence name, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders addShort(CharSequence name, short value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders setInt(CharSequence name, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders setShort(CharSequence name, short value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders remove(String name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders clear() {
        throw new UnsupportedOperationException("read only");
    }

    private final class ReadOnlyValueIterator
    implements Iterator<CharSequence> {
        private final CharSequence name;
        private final int nameHash;
        private int nextNameIndex;

        ReadOnlyValueIterator(CharSequence name) {
            this.name = name;
            this.nameHash = AsciiString.hashCode(name);
            this.nextNameIndex = this.findNextValue();
        }

        @Override
        public boolean hasNext() {
            return this.nextNameIndex != -1;
        }

        @Override
        public CharSequence next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            CharSequence value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1];
            this.nextNameIndex = this.findNextValue();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("read only");
        }

        private int findNextValue() {
            for (int i = this.nextNameIndex; i < ReadOnlyHttpHeaders.this.nameValuePairs.length; i += 2) {
                CharSequence roName = ReadOnlyHttpHeaders.this.nameValuePairs[i];
                if (this.nameHash != AsciiString.hashCode(roName) || !AsciiString.contentEqualsIgnoreCase(this.name, roName)) continue;
                return i;
            }
            return -1;
        }
    }

    private final class ReadOnlyStringValueIterator
    implements Iterator<String> {
        private final CharSequence name;
        private final int nameHash;
        private int nextNameIndex;

        ReadOnlyStringValueIterator(CharSequence name) {
            this.name = name;
            this.nameHash = AsciiString.hashCode(name);
            this.nextNameIndex = this.findNextValue();
        }

        @Override
        public boolean hasNext() {
            return this.nextNameIndex != -1;
        }

        @Override
        public String next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            String value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1].toString();
            this.nextNameIndex = this.findNextValue();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("read only");
        }

        private int findNextValue() {
            for (int i = this.nextNameIndex; i < ReadOnlyHttpHeaders.this.nameValuePairs.length; i += 2) {
                CharSequence roName = ReadOnlyHttpHeaders.this.nameValuePairs[i];
                if (this.nameHash != AsciiString.hashCode(roName) || !AsciiString.contentEqualsIgnoreCase(this.name, roName)) continue;
                return i;
            }
            return -1;
        }
    }

    private final class ReadOnlyStringIterator
    implements Map.Entry<String, String>,
    Iterator<Map.Entry<String, String>> {
        private String key;
        private String value;
        private int nextNameIndex;

        private ReadOnlyStringIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.nextNameIndex != ReadOnlyHttpHeaders.this.nameValuePairs.length;
        }

        @Override
        public Map.Entry<String, String> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.key = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex].toString();
            this.value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1].toString();
            this.nextNameIndex += 2;
            return this;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public String setValue(String value) {
            throw new UnsupportedOperationException("read only");
        }

        public String toString() {
            return this.key + '=' + this.value;
        }
    }

    private final class ReadOnlyIterator
    implements Map.Entry<CharSequence, CharSequence>,
    Iterator<Map.Entry<CharSequence, CharSequence>> {
        private CharSequence key;
        private CharSequence value;
        private int nextNameIndex;

        private ReadOnlyIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.nextNameIndex != ReadOnlyHttpHeaders.this.nameValuePairs.length;
        }

        @Override
        public Map.Entry<CharSequence, CharSequence> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.key = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex];
            this.value = ReadOnlyHttpHeaders.this.nameValuePairs[this.nextNameIndex + 1];
            this.nextNameIndex += 2;
            return this;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("read only");
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

        public String toString() {
            return this.key.toString() + '=' + this.value.toString();
        }
    }

}


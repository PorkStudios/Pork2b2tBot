/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.DefaultHeadersImpl;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.PlatformDependent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultHttpHeaders
extends HttpHeaders {
    private static final int HIGHEST_INVALID_VALUE_CHAR_MASK = -16;
    private static final ByteProcessor HEADER_NAME_VALIDATOR = new ByteProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            DefaultHttpHeaders.validateHeaderNameElement(value);
            return true;
        }
    };
    static final DefaultHeaders.NameValidator<CharSequence> HttpNameValidator = new DefaultHeaders.NameValidator<CharSequence>(){

        @Override
        public void validateName(CharSequence name) {
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException("empty headers are not allowed [" + name + "]");
            }
            if (name instanceof AsciiString) {
                try {
                    ((AsciiString)name).forEachByte(HEADER_NAME_VALIDATOR);
                }
                catch (Exception e) {
                    PlatformDependent.throwException(e);
                }
            } else {
                for (int index = 0; index < name.length(); ++index) {
                    DefaultHttpHeaders.validateHeaderNameElement(name.charAt(index));
                }
            }
        }
    };
    private final DefaultHeaders<CharSequence, CharSequence, ?> headers;

    public DefaultHttpHeaders() {
        this(true);
    }

    public DefaultHttpHeaders(boolean validate) {
        this(validate, DefaultHttpHeaders.nameValidator(validate));
    }

    protected DefaultHttpHeaders(boolean validate, DefaultHeaders.NameValidator<CharSequence> nameValidator) {
        this(new DefaultHeadersImpl<CharSequence, CharSequence>(AsciiString.CASE_INSENSITIVE_HASHER, DefaultHttpHeaders.valueConverter(validate), nameValidator));
    }

    protected DefaultHttpHeaders(DefaultHeaders<CharSequence, CharSequence, ?> headers) {
        this.headers = headers;
    }

    @Override
    public HttpHeaders add(HttpHeaders headers) {
        if (headers instanceof DefaultHttpHeaders) {
            this.headers.add(((DefaultHttpHeaders)headers).headers);
            return this;
        }
        return super.add(headers);
    }

    @Override
    public HttpHeaders set(HttpHeaders headers) {
        if (headers instanceof DefaultHttpHeaders) {
            this.headers.set(((DefaultHttpHeaders)headers).headers);
            return this;
        }
        return super.set(headers);
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        this.headers.addObject((CharSequence)name, value);
        return this;
    }

    @Override
    public HttpHeaders add(CharSequence name, Object value) {
        this.headers.addObject(name, value);
        return this;
    }

    @Override
    public HttpHeaders add(String name, Iterable<?> values) {
        this.headers.addObject((CharSequence)name, values);
        return this;
    }

    @Override
    public HttpHeaders add(CharSequence name, Iterable<?> values) {
        this.headers.addObject(name, values);
        return this;
    }

    @Override
    public HttpHeaders addInt(CharSequence name, int value) {
        this.headers.addInt(name, value);
        return this;
    }

    @Override
    public HttpHeaders addShort(CharSequence name, short value) {
        this.headers.addShort(name, value);
        return this;
    }

    @Override
    public HttpHeaders remove(String name) {
        this.headers.remove(name);
        return this;
    }

    @Override
    public HttpHeaders remove(CharSequence name) {
        this.headers.remove(name);
        return this;
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        this.headers.setObject((CharSequence)name, value);
        return this;
    }

    @Override
    public HttpHeaders set(CharSequence name, Object value) {
        this.headers.setObject(name, value);
        return this;
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> values) {
        this.headers.setObject((CharSequence)name, values);
        return this;
    }

    @Override
    public HttpHeaders set(CharSequence name, Iterable<?> values) {
        this.headers.setObject(name, values);
        return this;
    }

    @Override
    public HttpHeaders setInt(CharSequence name, int value) {
        this.headers.setInt(name, value);
        return this;
    }

    @Override
    public HttpHeaders setShort(CharSequence name, short value) {
        this.headers.setShort(name, value);
        return this;
    }

    @Override
    public HttpHeaders clear() {
        this.headers.clear();
        return this;
    }

    @Override
    public String get(String name) {
        return this.get((CharSequence)name);
    }

    @Override
    public String get(CharSequence name) {
        return HeadersUtils.getAsString(this.headers, name);
    }

    @Override
    public Integer getInt(CharSequence name) {
        return this.headers.getInt(name);
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        return this.headers.getInt(name, defaultValue);
    }

    @Override
    public Short getShort(CharSequence name) {
        return this.headers.getShort(name);
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        return this.headers.getShort(name, defaultValue);
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        return this.headers.getTimeMillis(name);
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        return this.headers.getTimeMillis(name, defaultValue);
    }

    @Override
    public List<String> getAll(String name) {
        return this.getAll((CharSequence)name);
    }

    @Override
    public List<String> getAll(CharSequence name) {
        return HeadersUtils.getAllAsString(this.headers, name);
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Map.Entry<String, String>> entriesConverted = new ArrayList<Map.Entry<String, String>>(this.headers.size());
        for (Map.Entry<String, String> entry : this) {
            entriesConverted.add(entry);
        }
        return entriesConverted;
    }

    @Deprecated
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return HeadersUtils.iteratorAsString(this.headers);
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        return this.headers.iterator();
    }

    @Override
    public Iterator<String> valueStringIterator(CharSequence name) {
        final Iterator<CharSequence> itr = this.valueCharSequenceIterator(name);
        return new Iterator<String>(){

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public String next() {
                return ((CharSequence)itr.next()).toString();
            }

            @Override
            public void remove() {
                itr.remove();
            }
        };
    }

    public Iterator<CharSequence> valueCharSequenceIterator(CharSequence name) {
        return this.headers.valueIterator(name);
    }

    @Override
    public boolean contains(String name) {
        return this.contains((CharSequence)name);
    }

    @Override
    public boolean contains(CharSequence name) {
        return this.headers.contains(name);
    }

    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public boolean contains(String name, String value, boolean ignoreCase) {
        return this.contains((CharSequence)name, (CharSequence)value, ignoreCase);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        return this.headers.contains(name, value, ignoreCase ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public Set<String> names() {
        return HeadersUtils.namesAsString(this.headers);
    }

    public boolean equals(Object o) {
        return o instanceof DefaultHttpHeaders && this.headers.equals(((DefaultHttpHeaders)o).headers, AsciiString.CASE_SENSITIVE_HASHER);
    }

    public int hashCode() {
        return this.headers.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
    }

    private static void validateHeaderNameElement(byte value) {
        switch (value) {
            case 0: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 32: 
            case 44: 
            case 58: 
            case 59: 
            case 61: {
                throw new IllegalArgumentException("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + value);
            }
        }
        if (value < 0) {
            throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + value);
        }
    }

    private static void validateHeaderNameElement(char value) {
        switch (value) {
            case '\u0000': 
            case '\t': 
            case '\n': 
            case '\u000b': 
            case '\f': 
            case '\r': 
            case ' ': 
            case ',': 
            case ':': 
            case ';': 
            case '=': {
                throw new IllegalArgumentException("a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + value);
            }
        }
        if (value > '') {
            throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + value);
        }
    }

    static ValueConverter<CharSequence> valueConverter(boolean validate) {
        return validate ? HeaderValueConverterAndValidator.INSTANCE : HeaderValueConverter.INSTANCE;
    }

    static DefaultHeaders.NameValidator<CharSequence> nameValidator(boolean validate) {
        return validate ? HttpNameValidator : DefaultHeaders.NameValidator.NOT_NULL;
    }

    private static final class HeaderValueConverterAndValidator
    extends HeaderValueConverter {
        static final HeaderValueConverterAndValidator INSTANCE = new HeaderValueConverterAndValidator();

        private HeaderValueConverterAndValidator() {
            super();
        }

        @Override
        public CharSequence convertObject(Object value) {
            CharSequence seq = super.convertObject(value);
            int state = 0;
            for (int index = 0; index < seq.length(); ++index) {
                state = HeaderValueConverterAndValidator.validateValueChar(seq, state, seq.charAt(index));
            }
            if (state != 0) {
                throw new IllegalArgumentException("a header value must not end with '\\r' or '\\n':" + seq);
            }
            return seq;
        }

        private static int validateValueChar(CharSequence seq, int state, char character) {
            if ((character & -16) == 0) {
                switch (character) {
                    case '\u0000': {
                        throw new IllegalArgumentException("a header value contains a prohibited character '\u0000': " + seq);
                    }
                    case '\u000b': {
                        throw new IllegalArgumentException("a header value contains a prohibited character '\\v': " + seq);
                    }
                    case '\f': {
                        throw new IllegalArgumentException("a header value contains a prohibited character '\\f': " + seq);
                    }
                }
            }
            switch (state) {
                case 0: {
                    switch (character) {
                        case '\r': {
                            return 1;
                        }
                        case '\n': {
                            return 2;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (character) {
                        case '\n': {
                            return 2;
                        }
                    }
                    throw new IllegalArgumentException("only '\\n' is allowed after '\\r': " + seq);
                }
                case 2: {
                    switch (character) {
                        case '\t': 
                        case ' ': {
                            return 0;
                        }
                    }
                    throw new IllegalArgumentException("only ' ' and '\\t' are allowed after '\\n': " + seq);
                }
            }
            return state;
        }
    }

    private static class HeaderValueConverter
    extends CharSequenceValueConverter {
        static final HeaderValueConverter INSTANCE = new HeaderValueConverter();

        private HeaderValueConverter() {
        }

        @Override
        public CharSequence convertObject(Object value) {
            if (value instanceof CharSequence) {
                return (CharSequence)value;
            }
            if (value instanceof Date) {
                return DateFormatter.format((Date)value);
            }
            if (value instanceof Calendar) {
                return DateFormatter.format(((Calendar)value).getTime());
            }
            return value.toString();
        }
    }

}


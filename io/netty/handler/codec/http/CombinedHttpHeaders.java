/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.StringUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CombinedHttpHeaders
extends DefaultHttpHeaders {
    public CombinedHttpHeaders(boolean validate) {
        super(new CombinedHttpHeadersImpl(AsciiString.CASE_INSENSITIVE_HASHER, CombinedHttpHeaders.valueConverter(validate), CombinedHttpHeaders.nameValidator(validate)));
    }

    @Override
    public boolean containsValue(CharSequence name, CharSequence value, boolean ignoreCase) {
        return super.containsValue(name, StringUtil.trimOws(value), ignoreCase);
    }

    private static final class CombinedHttpHeadersImpl
    extends DefaultHeaders<CharSequence, CharSequence, CombinedHttpHeadersImpl> {
        private static final int VALUE_LENGTH_ESTIMATE = 10;
        private CsvValueEscaper<Object> objectEscaper;
        private CsvValueEscaper<CharSequence> charSequenceEscaper;

        private CsvValueEscaper<Object> objectEscaper() {
            if (this.objectEscaper == null) {
                this.objectEscaper = new CsvValueEscaper<Object>(){

                    @Override
                    public CharSequence escape(Object value) {
                        return StringUtil.escapeCsv((CharSequence)CombinedHttpHeadersImpl.this.valueConverter().convertObject(value), true);
                    }
                };
            }
            return this.objectEscaper;
        }

        private CsvValueEscaper<CharSequence> charSequenceEscaper() {
            if (this.charSequenceEscaper == null) {
                this.charSequenceEscaper = new CsvValueEscaper<CharSequence>(){

                    @Override
                    public CharSequence escape(CharSequence value) {
                        return StringUtil.escapeCsv(value, true);
                    }
                };
            }
            return this.charSequenceEscaper;
        }

        public CombinedHttpHeadersImpl(HashingStrategy<CharSequence> nameHashingStrategy, ValueConverter<CharSequence> valueConverter, DefaultHeaders.NameValidator<CharSequence> nameValidator) {
            super(nameHashingStrategy, valueConverter, nameValidator);
        }

        @Override
        public Iterator<CharSequence> valueIterator(CharSequence name) {
            Iterator<CharSequence> itr = super.valueIterator(name);
            if (!itr.hasNext()) {
                return itr;
            }
            Iterator<CharSequence> unescapedItr = StringUtil.unescapeCsvFields(itr.next()).iterator();
            if (itr.hasNext()) {
                throw new IllegalStateException("CombinedHttpHeaders should only have one value");
            }
            return unescapedItr;
        }

        @Override
        public List<CharSequence> getAll(CharSequence name) {
            List<CharSequence> values = super.getAll(name);
            if (values.isEmpty()) {
                return values;
            }
            if (values.size() != 1) {
                throw new IllegalStateException("CombinedHttpHeaders should only have one value");
            }
            return StringUtil.unescapeCsvFields(values.get(0));
        }

        @Override
        public CombinedHttpHeadersImpl add(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
            if (headers == this) {
                throw new IllegalArgumentException("can't add to itself.");
            }
            if (headers instanceof CombinedHttpHeadersImpl) {
                if (this.isEmpty()) {
                    this.addImpl(headers);
                } else {
                    for (Map.Entry<CharSequence, CharSequence> header : headers) {
                        this.addEscapedValue(header.getKey(), header.getValue());
                    }
                }
            } else {
                for (Map.Entry<CharSequence, CharSequence> header : headers) {
                    this.add(header.getKey(), header.getValue());
                }
            }
            return this;
        }

        @Override
        public CombinedHttpHeadersImpl set(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
            if (headers == this) {
                return this;
            }
            this.clear();
            return this.add((Headers)headers);
        }

        @Override
        public CombinedHttpHeadersImpl setAll(Headers<? extends CharSequence, ? extends CharSequence, ?> headers) {
            if (headers == this) {
                return this;
            }
            for (CharSequence key : headers.names()) {
                this.remove(key);
            }
            return this.add(headers);
        }

        @Override
        public CombinedHttpHeadersImpl add(CharSequence name, CharSequence value) {
            return this.addEscapedValue(name, this.charSequenceEscaper().escape(value));
        }

        @Override
        public /* varargs */ CombinedHttpHeadersImpl add(CharSequence name, CharSequence ... values) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(this.charSequenceEscaper(), values));
        }

        @Override
        public CombinedHttpHeadersImpl add(CharSequence name, Iterable<? extends CharSequence> values) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(this.charSequenceEscaper(), values));
        }

        @Override
        public CombinedHttpHeadersImpl addObject(CharSequence name, Object value) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(this.objectEscaper(), value));
        }

        @Override
        public CombinedHttpHeadersImpl addObject(CharSequence name, Iterable<?> values) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(this.objectEscaper(), values));
        }

        @Override
        public /* varargs */ CombinedHttpHeadersImpl addObject(CharSequence name, Object ... values) {
            return this.addEscapedValue(name, CombinedHttpHeadersImpl.commaSeparate(this.objectEscaper(), values));
        }

        @Override
        public /* varargs */ CombinedHttpHeadersImpl set(CharSequence name, CharSequence ... values) {
            super.set(name, CombinedHttpHeadersImpl.commaSeparate(this.charSequenceEscaper(), values));
            return this;
        }

        @Override
        public CombinedHttpHeadersImpl set(CharSequence name, Iterable<? extends CharSequence> values) {
            super.set(name, CombinedHttpHeadersImpl.commaSeparate(this.charSequenceEscaper(), values));
            return this;
        }

        @Override
        public CombinedHttpHeadersImpl setObject(CharSequence name, Object value) {
            super.set(name, CombinedHttpHeadersImpl.commaSeparate(this.objectEscaper(), value));
            return this;
        }

        @Override
        public /* varargs */ CombinedHttpHeadersImpl setObject(CharSequence name, Object ... values) {
            super.set(name, CombinedHttpHeadersImpl.commaSeparate(this.objectEscaper(), values));
            return this;
        }

        @Override
        public CombinedHttpHeadersImpl setObject(CharSequence name, Iterable<?> values) {
            super.set(name, CombinedHttpHeadersImpl.commaSeparate(this.objectEscaper(), values));
            return this;
        }

        private CombinedHttpHeadersImpl addEscapedValue(CharSequence name, CharSequence escapedValue) {
            CharSequence currentValue = (CharSequence)super.get(name);
            if (currentValue == null) {
                super.add(name, escapedValue);
            } else {
                super.set(name, CombinedHttpHeadersImpl.commaSeparateEscapedValues(currentValue, escapedValue));
            }
            return this;
        }

        private static /* varargs */ <T> CharSequence commaSeparate(CsvValueEscaper<T> escaper, T ... values) {
            StringBuilder sb = new StringBuilder(values.length * 10);
            if (values.length > 0) {
                int end = values.length - 1;
                for (int i = 0; i < end; ++i) {
                    sb.append(escaper.escape(values[i])).append(',');
                }
                sb.append(escaper.escape(values[end]));
            }
            return sb;
        }

        private static <T> CharSequence commaSeparate(CsvValueEscaper<T> escaper, Iterable<? extends T> values) {
            StringBuilder sb = values instanceof Collection ? new StringBuilder(((Collection)values).size() * 10) : new StringBuilder();
            Iterator<T> iterator = values.iterator();
            if (iterator.hasNext()) {
                T next = iterator.next();
                while (iterator.hasNext()) {
                    sb.append(escaper.escape(next)).append(',');
                    next = iterator.next();
                }
                sb.append(escaper.escape(next));
            }
            return sb;
        }

        private static CharSequence commaSeparateEscapedValues(CharSequence currentValue, CharSequence value) {
            return new StringBuilder(currentValue.length() + 1 + value.length()).append(currentValue).append(',').append(value);
        }

        private static interface CsvValueEscaper<T> {
            public CharSequence escape(T var1);
        }

    }

}


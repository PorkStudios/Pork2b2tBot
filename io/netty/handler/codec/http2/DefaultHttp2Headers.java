/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.PlatformDependent;
import java.util.Iterator;

public class DefaultHttp2Headers
extends DefaultHeaders<CharSequence, CharSequence, Http2Headers>
implements Http2Headers {
    private static final ByteProcessor HTTP2_NAME_VALIDATOR_PROCESSOR = new ByteProcessor(){

        @Override
        public boolean process(byte value) throws Exception {
            return !AsciiString.isUpperCase(value);
        }
    };
    static final DefaultHeaders.NameValidator<CharSequence> HTTP2_NAME_VALIDATOR = new DefaultHeaders.NameValidator<CharSequence>(){

        @Override
        public void validateName(CharSequence name) {
            if (name == null || name.length() == 0) {
                PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "empty headers are not allowed [%s]", name));
            }
            if (name instanceof AsciiString) {
                int index;
                try {
                    index = ((AsciiString)name).forEachByte(HTTP2_NAME_VALIDATOR_PROCESSOR);
                }
                catch (Http2Exception e) {
                    PlatformDependent.throwException(e);
                    return;
                }
                catch (Throwable t) {
                    PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, t, "unexpected error. invalid header name [%s]", name));
                    return;
                }
                if (index != -1) {
                    PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "invalid header name [%s]", name));
                }
            } else {
                for (int i = 0; i < name.length(); ++i) {
                    if (!AsciiString.isUpperCase(name.charAt(i))) continue;
                    PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "invalid header name [%s]", name));
                }
            }
        }
    };
    private DefaultHeaders.HeaderEntry<CharSequence, CharSequence> firstNonPseudo;

    public DefaultHttp2Headers() {
        this(true);
    }

    public DefaultHttp2Headers(boolean validate) {
        super(AsciiString.CASE_SENSITIVE_HASHER, CharSequenceValueConverter.INSTANCE, validate ? HTTP2_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL);
        this.firstNonPseudo = this.head;
    }

    public DefaultHttp2Headers(boolean validate, int arraySizeHint) {
        super(AsciiString.CASE_SENSITIVE_HASHER, CharSequenceValueConverter.INSTANCE, validate ? HTTP2_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL, arraySizeHint);
        this.firstNonPseudo = this.head;
    }

    @Override
    public Http2Headers clear() {
        this.firstNonPseudo = this.head;
        return (Http2Headers)super.clear();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Http2Headers && this.equals((Http2Headers)o, AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public int hashCode() {
        return this.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
    }

    @Override
    public Http2Headers method(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.METHOD.value(), value);
        return this;
    }

    @Override
    public Http2Headers scheme(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.SCHEME.value(), value);
        return this;
    }

    @Override
    public Http2Headers authority(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.AUTHORITY.value(), value);
        return this;
    }

    @Override
    public Http2Headers path(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.PATH.value(), value);
        return this;
    }

    @Override
    public Http2Headers status(CharSequence value) {
        this.set(Http2Headers.PseudoHeaderName.STATUS.value(), value);
        return this;
    }

    @Override
    public CharSequence method() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.METHOD.value());
    }

    @Override
    public CharSequence scheme() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.SCHEME.value());
    }

    @Override
    public CharSequence authority() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.AUTHORITY.value());
    }

    @Override
    public CharSequence path() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.PATH.value());
    }

    @Override
    public CharSequence status() {
        return (CharSequence)this.get(Http2Headers.PseudoHeaderName.STATUS.value());
    }

    @Override
    protected final DefaultHeaders.HeaderEntry<CharSequence, CharSequence> newHeaderEntry(int h, CharSequence name, CharSequence value, DefaultHeaders.HeaderEntry<CharSequence, CharSequence> next) {
        return new Http2HeaderEntry(h, name, value, next);
    }

    private final class Http2HeaderEntry
    extends DefaultHeaders.HeaderEntry<CharSequence, CharSequence> {
        protected Http2HeaderEntry(int hash, CharSequence key, CharSequence value, DefaultHeaders.HeaderEntry<CharSequence, CharSequence> next) {
            super(hash, key);
            this.value = value;
            this.next = next;
            if (key.length() != 0 && key.charAt(0) == ':') {
                this.after = DefaultHttp2Headers.this.firstNonPseudo;
                this.before = DefaultHttp2Headers.this.firstNonPseudo.before();
            } else {
                this.after = DefaultHttp2Headers.this.head;
                this.before = DefaultHttp2Headers.this.head.before();
                if (DefaultHttp2Headers.this.firstNonPseudo == DefaultHttp2Headers.this.head) {
                    DefaultHttp2Headers.this.firstNonPseudo = this;
                }
            }
            this.pointNeighborsToThis();
        }

        @Override
        protected void remove() {
            if (this == DefaultHttp2Headers.this.firstNonPseudo) {
                DefaultHttp2Headers.this.firstNonPseudo = DefaultHttp2Headers.this.firstNonPseudo.after();
            }
            super.remove();
        }
    }

}


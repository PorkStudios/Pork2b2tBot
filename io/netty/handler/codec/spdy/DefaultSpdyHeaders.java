/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefaultSpdyHeaders
extends DefaultHeaders<CharSequence, CharSequence, SpdyHeaders>
implements SpdyHeaders {
    private static final DefaultHeaders.NameValidator<CharSequence> SpdyNameValidator = new DefaultHeaders.NameValidator<CharSequence>(){

        @Override
        public void validateName(CharSequence name) {
            SpdyCodecUtil.validateHeaderName(name);
        }
    };

    public DefaultSpdyHeaders() {
        this(true);
    }

    public DefaultSpdyHeaders(boolean validate) {
        super(AsciiString.CASE_INSENSITIVE_HASHER, validate ? HeaderValueConverterAndValidator.INSTANCE : CharSequenceValueConverter.INSTANCE, validate ? SpdyNameValidator : DefaultHeaders.NameValidator.NOT_NULL);
    }

    @Override
    public String getAsString(CharSequence name) {
        return HeadersUtils.getAsString(this, name);
    }

    @Override
    public List<String> getAllAsString(CharSequence name) {
        return HeadersUtils.getAllAsString(this, name);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iteratorAsString() {
        return HeadersUtils.iteratorAsString(this);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value) {
        return this.contains(name, value, false);
    }

    @Override
    public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
        return this.contains(name, value, ignoreCase ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
    }

    private static final class HeaderValueConverterAndValidator
    extends CharSequenceValueConverter {
        public static final HeaderValueConverterAndValidator INSTANCE = new HeaderValueConverterAndValidator();

        private HeaderValueConverterAndValidator() {
        }

        @Override
        public CharSequence convertObject(Object value) {
            CharSequence seq = super.convertObject(value);
            SpdyCodecUtil.validateHeaderValue(seq);
            return seq;
        }
    }

}


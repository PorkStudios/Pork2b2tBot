/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;

public final class CharSequenceMap<V>
extends DefaultHeaders<CharSequence, V, CharSequenceMap<V>> {
    public CharSequenceMap() {
        this(true);
    }

    public CharSequenceMap(boolean caseSensitive) {
        this(caseSensitive, UnsupportedValueConverter.instance());
    }

    public CharSequenceMap(boolean caseSensitive, ValueConverter<V> valueConverter) {
        super(caseSensitive ? AsciiString.CASE_SENSITIVE_HASHER : AsciiString.CASE_INSENSITIVE_HASHER, valueConverter);
    }

    public CharSequenceMap(boolean caseSensitive, ValueConverter<V> valueConverter, int arraySizeHint) {
        super(caseSensitive ? AsciiString.CASE_SENSITIVE_HASHER : AsciiString.CASE_INSENSITIVE_HASHER, valueConverter, DefaultHeaders.NameValidator.NOT_NULL, arraySizeHint);
    }
}


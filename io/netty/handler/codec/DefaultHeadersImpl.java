/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.HashingStrategy;

public final class DefaultHeadersImpl<K, V>
extends DefaultHeaders<K, V, DefaultHeadersImpl<K, V>> {
    public DefaultHeadersImpl(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, DefaultHeaders.NameValidator<K> nameValidator) {
        super(nameHashingStrategy, valueConverter, nameValidator);
    }
}


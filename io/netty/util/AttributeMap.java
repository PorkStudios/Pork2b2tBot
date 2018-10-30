/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public interface AttributeMap {
    public <T> Attribute<T> attr(AttributeKey<T> var1);

    public <T> boolean hasAttr(AttributeKey<T> var1);
}


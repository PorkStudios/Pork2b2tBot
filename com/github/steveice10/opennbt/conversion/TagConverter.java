/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion;

import com.github.steveice10.opennbt.tag.builtin.Tag;

public interface TagConverter<T extends Tag, V> {
    public V convert(T var1);

    public T convert(String var1, V var2);
}


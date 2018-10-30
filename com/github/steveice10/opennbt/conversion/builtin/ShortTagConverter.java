/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;

public class ShortTagConverter
implements TagConverter<ShortTag, Short> {
    @Override
    public Short convert(ShortTag tag) {
        return tag.getValue();
    }

    @Override
    public ShortTag convert(String name, Short value) {
        return new ShortTag(name, value);
    }
}


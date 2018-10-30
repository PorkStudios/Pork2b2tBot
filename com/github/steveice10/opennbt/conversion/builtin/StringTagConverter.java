/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;

public class StringTagConverter
implements TagConverter<StringTag, String> {
    @Override
    public String convert(StringTag tag) {
        return tag.getValue();
    }

    @Override
    public StringTag convert(String name, String value) {
        return new StringTag(name, value);
    }
}


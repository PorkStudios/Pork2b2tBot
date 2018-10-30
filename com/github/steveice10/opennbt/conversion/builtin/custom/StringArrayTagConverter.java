/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin.custom;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.custom.StringArrayTag;

public class StringArrayTagConverter
implements TagConverter<StringArrayTag, String[]> {
    @Override
    public String[] convert(StringArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public StringArrayTag convert(String name, String[] value) {
        return new StringArrayTag(name, value);
    }
}


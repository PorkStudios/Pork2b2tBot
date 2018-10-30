/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin.custom;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.custom.LongArrayTag;

public class LongArrayTagConverter
implements TagConverter<LongArrayTag, long[]> {
    @Override
    public long[] convert(LongArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public LongArrayTag convert(String name, long[] value) {
        return new LongArrayTag(name, value);
    }
}


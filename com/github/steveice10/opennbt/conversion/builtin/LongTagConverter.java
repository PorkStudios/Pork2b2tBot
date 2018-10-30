/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;

public class LongTagConverter
implements TagConverter<LongTag, Long> {
    @Override
    public Long convert(LongTag tag) {
        return tag.getValue();
    }

    @Override
    public LongTag convert(String name, Long value) {
        return new LongTag(name, value);
    }
}


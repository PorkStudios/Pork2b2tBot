/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;

public class DoubleTagConverter
implements TagConverter<DoubleTag, Double> {
    @Override
    public Double convert(DoubleTag tag) {
        return tag.getValue();
    }

    @Override
    public DoubleTag convert(String name, Double value) {
        return new DoubleTag(name, value);
    }
}


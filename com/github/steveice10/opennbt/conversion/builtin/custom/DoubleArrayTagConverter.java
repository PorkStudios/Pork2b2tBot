/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin.custom;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.custom.DoubleArrayTag;

public class DoubleArrayTagConverter
implements TagConverter<DoubleArrayTag, double[]> {
    @Override
    public double[] convert(DoubleArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public DoubleArrayTag convert(String name, double[] value) {
        return new DoubleArrayTag(name, value);
    }
}


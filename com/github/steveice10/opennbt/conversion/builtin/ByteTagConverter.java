/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;

public class ByteTagConverter
implements TagConverter<ByteTag, Byte> {
    @Override
    public Byte convert(ByteTag tag) {
        return tag.getValue();
    }

    @Override
    public ByteTag convert(String name, Byte value) {
        return new ByteTag(name, value);
    }
}


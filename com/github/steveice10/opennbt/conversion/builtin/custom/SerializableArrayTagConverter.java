/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin.custom;

import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.custom.SerializableArrayTag;
import java.io.Serializable;

public class SerializableArrayTagConverter
implements TagConverter<SerializableArrayTag, Serializable[]> {
    @Override
    public Serializable[] convert(SerializableArrayTag tag) {
        return tag.getValue();
    }

    @Override
    public SerializableArrayTag convert(String name, Serializable[] value) {
        return new SerializableArrayTag(name, value);
    }
}


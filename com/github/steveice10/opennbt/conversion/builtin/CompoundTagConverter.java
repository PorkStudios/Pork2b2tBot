/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.ConverterRegistry;
import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompoundTagConverter
implements TagConverter<CompoundTag, Map> {
    @Override
    public Map convert(CompoundTag tag) {
        HashMap ret = new HashMap();
        Object tags = tag.getValue();
        for (String name : tags.keySet()) {
            Tag t = (Tag)tags.get(name);
            ret.put(t.getName(), ConverterRegistry.convertToValue(t));
        }
        return ret;
    }

    @Override
    public CompoundTag convert(String name, Map value) {
        HashMap<String, Tag> tags = new HashMap<String, Tag>();
        for (Object na : value.keySet()) {
            String n = (String)na;
            tags.put(n, (Tag)ConverterRegistry.convertToTag(n, value.get(n)));
        }
        return new CompoundTag(name, tags);
    }
}


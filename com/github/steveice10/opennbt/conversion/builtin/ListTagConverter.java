/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion.builtin;

import com.github.steveice10.opennbt.conversion.ConverterRegistry;
import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListTagConverter
implements TagConverter<ListTag, List> {
    @Override
    public List convert(ListTag tag) {
        ArrayList ret = new ArrayList();
        Object tags = tag.getValue();
        Iterator i$ = tags.iterator();
        while (i$.hasNext()) {
            Tag t = (Tag)i$.next();
            ret.add(ConverterRegistry.convertToValue(t));
        }
        return ret;
    }

    @Override
    public ListTag convert(String name, List value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Cannot convert ListTag with size of 0.");
        }
        ArrayList<Tag> tags = new ArrayList<Tag>();
        for (Object o : value) {
            tags.add((Tag)ConverterRegistry.convertToTag("", o));
        }
        return new ListTag(name, tags);
    }
}


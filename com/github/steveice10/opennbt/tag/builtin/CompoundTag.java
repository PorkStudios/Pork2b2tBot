/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CompoundTag
extends Tag
implements Iterable<Tag> {
    private Map<String, Tag> value;

    public CompoundTag(String name) {
        this(name, new LinkedHashMap<String, Tag>());
    }

    public CompoundTag(String name, Map<String, Tag> value) {
        super(name);
        this.value = new LinkedHashMap<String, Tag>(value);
    }

    @Override
    public Map<String, Tag> getValue() {
        return new LinkedHashMap<String, Tag>(this.value);
    }

    public void setValue(Map<String, Tag> value) {
        this.value = new LinkedHashMap<String, Tag>(value);
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    public boolean contains(String tagName) {
        return this.value.containsKey(tagName);
    }

    public <T extends Tag> T get(String tagName) {
        return (T)this.value.get(tagName);
    }

    public <T extends Tag> T put(T tag) {
        return (T)this.value.put(tag.getName(), (Tag)tag);
    }

    public <T extends Tag> T remove(String tagName) {
        return (T)this.value.remove(tagName);
    }

    public Set<String> keySet() {
        return this.value.keySet();
    }

    public Collection<Tag> values() {
        return this.value.values();
    }

    public int size() {
        return this.value.size();
    }

    public void clear() {
        this.value.clear();
    }

    @Override
    public Iterator<Tag> iterator() {
        return this.values().iterator();
    }

    @Override
    public void read(DataInput in) throws IOException {
        ArrayList<Tag> tags = new ArrayList<Tag>();
        try {
            Tag tag;
            while ((tag = NBTIO.readTag(in)) != null) {
                tags.add(tag);
            }
        }
        catch (EOFException e) {
            throw new IOException("Closing EndTag was not found!");
        }
        for (Tag tag : tags) {
            this.put(tag);
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        for (Tag tag : this.value.values()) {
            NBTIO.writeTag(out, tag);
        }
        out.writeByte(0);
    }

    @Override
    public CompoundTag clone() {
        LinkedHashMap<String, Tag> newMap = new LinkedHashMap<String, Tag>();
        for (Map.Entry<String, Tag> entry : this.value.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().clone());
        }
        return new CompoundTag(this.getName(), newMap);
    }
}


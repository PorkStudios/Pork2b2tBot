/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.IntArrayTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class CompoundTag
extends Tag
implements Cloneable {
    private final Map<String, Tag> tags = new HashMap<String, Tag>();

    public CompoundTag() {
        super("");
    }

    public CompoundTag(String name) {
        super(name);
    }

    @Override
    public void write(NBTOutputStream dos) throws IOException {
        for (Tag tag : this.tags.values()) {
            Tag.writeNamedTag(tag, dos);
        }
        dos.writeByte(0);
    }

    @Override
    public void load(NBTInputStream dis) throws IOException {
        Tag tag;
        this.tags.clear();
        while ((tag = Tag.readNamedTag(dis)).getId() != 0) {
            this.tags.put(tag.getName(), tag);
        }
    }

    public Collection<Tag> getAllTags() {
        return this.tags.values();
    }

    @Override
    public byte getId() {
        return 10;
    }

    public CompoundTag put(String name, Tag tag) {
        this.tags.put(name, tag.setName(name));
        return this;
    }

    public CompoundTag putByte(String name, byte value) {
        this.tags.put(name, new ByteTag(name, value));
        return this;
    }

    public CompoundTag putShort(String name, short value) {
        this.tags.put(name, new ShortTag(name, value));
        return this;
    }

    public CompoundTag putInt(String name, int value) {
        this.tags.put(name, new IntTag(name, value));
        return this;
    }

    public CompoundTag putLong(String name, long value) {
        this.tags.put(name, new LongTag(name, value));
        return this;
    }

    public CompoundTag putFloat(String name, float value) {
        this.tags.put(name, new FloatTag(name, value));
        return this;
    }

    public CompoundTag putDouble(String name, double value) {
        this.tags.put(name, new DoubleTag(name, value));
        return this;
    }

    public CompoundTag putString(String name, String value) {
        this.tags.put(name, new StringTag(name, value));
        return this;
    }

    public CompoundTag putByteArray(String name, byte[] value) {
        this.tags.put(name, new ByteArrayTag(name, value));
        return this;
    }

    public CompoundTag putIntArray(String name, int[] value) {
        this.tags.put(name, new IntArrayTag(name, value));
        return this;
    }

    public CompoundTag putList(ListTag<? extends Tag> listTag) {
        this.tags.put(listTag.getName(), listTag);
        return this;
    }

    public CompoundTag putCompound(String name, CompoundTag value) {
        this.tags.put(name, value.setName(name));
        return this;
    }

    public CompoundTag putBoolean(String string, boolean val) {
        this.putByte(string, val ? (byte)1 : 0);
        return this;
    }

    public Tag get(String name) {
        return this.tags.get(name);
    }

    public boolean contains(String name) {
        return this.tags.containsKey(name);
    }

    public CompoundTag remove(String name) {
        this.tags.remove(name);
        return this;
    }

    public byte getByte(String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return ((NumberTag)this.tags.get(name)).getData().byteValue();
    }

    public short getShort(String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return ((NumberTag)this.tags.get(name)).getData().shortValue();
    }

    public int getInt(String name) {
        if (!this.tags.containsKey(name)) {
            return 0;
        }
        return ((NumberTag)this.tags.get(name)).getData().intValue();
    }

    public long getLong(String name) {
        if (!this.tags.containsKey(name)) {
            return 0L;
        }
        return ((NumberTag)this.tags.get(name)).getData().longValue();
    }

    public float getFloat(String name) {
        if (!this.tags.containsKey(name)) {
            return 0.0f;
        }
        return ((NumberTag)this.tags.get(name)).getData().floatValue();
    }

    public double getDouble(String name) {
        if (!this.tags.containsKey(name)) {
            return 0.0;
        }
        return ((NumberTag)this.tags.get(name)).getData().doubleValue();
    }

    public String getString(String name) {
        if (!this.tags.containsKey(name)) {
            return "";
        }
        Tag tag = this.tags.get(name);
        if (tag instanceof NumberTag) {
            return String.valueOf(((NumberTag)tag).getData());
        }
        return ((StringTag)tag).data;
    }

    public byte[] getByteArray(String name) {
        if (!this.tags.containsKey(name)) {
            return new byte[0];
        }
        return ((ByteArrayTag)this.tags.get((Object)name)).data;
    }

    public int[] getIntArray(String name) {
        if (!this.tags.containsKey(name)) {
            return new int[0];
        }
        return ((IntArrayTag)this.tags.get((Object)name)).data;
    }

    public CompoundTag getCompound(String name) {
        if (!this.tags.containsKey(name)) {
            return new CompoundTag(name);
        }
        return (CompoundTag)this.tags.get(name);
    }

    public ListTag<? extends Tag> getList(String name) {
        if (!this.tags.containsKey(name)) {
            return new ListTag(name);
        }
        return (ListTag)this.tags.get(name);
    }

    public <T extends Tag> ListTag<T> getList(String name, Class<T> type) {
        if (this.tags.containsKey(name)) {
            return (ListTag)this.tags.get(name);
        }
        return new ListTag(name);
    }

    public Map<String, Tag> getTags() {
        return new HashMap<String, Tag>(this.tags);
    }

    public boolean getBoolean(String name) {
        return this.getByte(name) != 0;
    }

    @Override
    public String toString() {
        return "CompoundTag " + this.getName() + " (" + this.tags.size() + " entries)";
    }

    @Override
    public void print(String prefix, PrintStream out) {
        super.print(prefix, out);
        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix = prefix + "   ";
        for (Tag tag : this.tags.values()) {
            tag.print(prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    @Override
    public CompoundTag copy() {
        CompoundTag tag = new CompoundTag(this.getName());
        for (String key : this.tags.keySet()) {
            tag.put(key, this.tags.get(key).copy());
        }
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            CompoundTag o = (CompoundTag)obj;
            return this.tags.entrySet().equals(o.tags.entrySet());
        }
        return false;
    }

    public boolean exist(String name) {
        return this.tags.containsKey(name);
    }

    public CompoundTag clone() {
        CompoundTag nbt = new CompoundTag();
        this.getTags().forEach((key, value) -> nbt.put((String)key, value.copy()));
        return nbt;
    }
}


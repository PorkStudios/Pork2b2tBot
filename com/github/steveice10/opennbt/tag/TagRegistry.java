/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag;

import com.github.steveice10.opennbt.tag.TagCreateException;
import com.github.steveice10.opennbt.tag.TagRegisterException;
import com.github.steveice10.opennbt.tag.builtin.ByteArrayTag;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.DoubleTag;
import com.github.steveice10.opennbt.tag.builtin.FloatTag;
import com.github.steveice10.opennbt.tag.builtin.IntArrayTag;
import com.github.steveice10.opennbt.tag.builtin.IntTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.LongTag;
import com.github.steveice10.opennbt.tag.builtin.ShortTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.opennbt.tag.builtin.custom.DoubleArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.FloatArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.LongArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.SerializableArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.SerializableTag;
import com.github.steveice10.opennbt.tag.builtin.custom.ShortArrayTag;
import com.github.steveice10.opennbt.tag.builtin.custom.StringArrayTag;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class TagRegistry {
    private static final Map<Integer, Class<? extends Tag>> idToTag = new HashMap<Integer, Class<? extends Tag>>();
    private static final Map<Class<? extends Tag>, Integer> tagToId = new HashMap<Class<? extends Tag>, Integer>();

    public static void register(int id, Class<? extends Tag> tag) throws TagRegisterException {
        if (idToTag.containsKey(id)) {
            throw new TagRegisterException("Tag ID \"" + id + "\" is already in use.");
        }
        if (tagToId.containsKey(tag)) {
            throw new TagRegisterException("Tag \"" + tag.getSimpleName() + "\" is already registered.");
        }
        idToTag.put(id, tag);
        tagToId.put(tag, id);
    }

    public static Class<? extends Tag> getClassFor(int id) {
        if (!idToTag.containsKey(id)) {
            return null;
        }
        return idToTag.get(id);
    }

    public static int getIdFor(Class<? extends Tag> clazz) {
        if (!tagToId.containsKey(clazz)) {
            return -1;
        }
        return tagToId.get(clazz);
    }

    public static Tag createInstance(int id, String tagName) throws TagCreateException {
        Class<? extends Tag> clazz = idToTag.get(id);
        if (clazz == null) {
            throw new TagCreateException("Could not find tag with ID \"" + id + "\".");
        }
        try {
            Constructor<? extends Tag> constructor = clazz.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(tagName);
        }
        catch (Exception e) {
            throw new TagCreateException("Failed to create instance of tag \"" + clazz.getSimpleName() + "\".", e);
        }
    }

    static {
        TagRegistry.register(1, ByteTag.class);
        TagRegistry.register(2, ShortTag.class);
        TagRegistry.register(3, IntTag.class);
        TagRegistry.register(4, LongTag.class);
        TagRegistry.register(5, FloatTag.class);
        TagRegistry.register(6, DoubleTag.class);
        TagRegistry.register(7, ByteArrayTag.class);
        TagRegistry.register(8, StringTag.class);
        TagRegistry.register(9, ListTag.class);
        TagRegistry.register(10, CompoundTag.class);
        TagRegistry.register(11, IntArrayTag.class);
        TagRegistry.register(60, DoubleArrayTag.class);
        TagRegistry.register(61, FloatArrayTag.class);
        TagRegistry.register(62, LongArrayTag.class);
        TagRegistry.register(63, SerializableArrayTag.class);
        TagRegistry.register(64, SerializableTag.class);
        TagRegistry.register(65, ShortArrayTag.class);
        TagRegistry.register(66, StringArrayTag.class);
    }
}


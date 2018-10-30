/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.conversion;

import com.github.steveice10.opennbt.conversion.ConversionException;
import com.github.steveice10.opennbt.conversion.ConverterRegisterException;
import com.github.steveice10.opennbt.conversion.TagConverter;
import com.github.steveice10.opennbt.conversion.builtin.ByteArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.ByteTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.CompoundTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.DoubleTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.FloatTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.IntArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.IntTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.ListTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.LongTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.ShortTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.StringTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.DoubleArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.FloatArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.LongArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.SerializableArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.SerializableTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.ShortArrayTagConverter;
import com.github.steveice10.opennbt.conversion.builtin.custom.StringArrayTagConverter;
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
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConverterRegistry {
    private static final Map<Class<? extends Tag>, TagConverter<? extends Tag, ?>> tagToConverter = new HashMap();
    private static final Map<Class<?>, TagConverter<? extends Tag, ?>> typeToConverter = new HashMap();

    public static <T extends Tag, V> void register(Class<T> tag, Class<V> type, TagConverter<T, V> converter) throws ConverterRegisterException {
        if (tagToConverter.containsKey(tag)) {
            throw new TagRegisterException("Type conversion to tag " + tag.getName() + " is already registered.");
        }
        if (typeToConverter.containsKey(type)) {
            throw new TagRegisterException("Tag conversion to type " + type.getName() + " is already registered.");
        }
        tagToConverter.put(tag, converter);
        typeToConverter.put(type, converter);
    }

    public static <T extends Tag, V> V convertToValue(T tag) throws ConversionException {
        if (tag == null || tag.getValue() == null) {
            return null;
        }
        if (!tagToConverter.containsKey(tag.getClass())) {
            throw new ConversionException("Tag type " + tag.getClass().getName() + " has no converter.");
        }
        TagConverter<Tag, ?> converter = tagToConverter.get(tag.getClass());
        return (V)converter.convert((Tag)tag);
    }

    public static <V, T extends Tag> T convertToTag(String name, V value) throws ConversionException {
        if (value == null) {
            return null;
        }
        TagConverter<Tag, ?> converter = typeToConverter.get(value.getClass());
        if (converter == null) {
            for (Class<?> clazz : ConverterRegistry.getAllClasses(value.getClass())) {
                if (!typeToConverter.containsKey(clazz)) continue;
                try {
                    converter = typeToConverter.get(clazz);
                    break;
                }
                catch (ClassCastException e) {
                }
            }
        }
        if (converter == null) {
            throw new ConversionException("Value type " + value.getClass().getName() + " has no converter.");
        }
        return (T)converter.convert(name, value);
    }

    private static Set<Class<?>> getAllClasses(Class<?> clazz) {
        LinkedHashSet ret = new LinkedHashSet();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            ret.add(c);
            ret.addAll(ConverterRegistry.getAllSuperInterfaces(c));
        }
        if (ret.contains(Serializable.class)) {
            ret.remove(Serializable.class);
            ret.add(Serializable.class);
        }
        return ret;
    }

    private static Set<Class<?>> getAllSuperInterfaces(Class<?> clazz) {
        HashSet ret = new HashSet();
        for (Class<?> c : clazz.getInterfaces()) {
            ret.add(c);
            ret.addAll(ConverterRegistry.getAllSuperInterfaces(c));
        }
        return ret;
    }

    static {
        ConverterRegistry.register(ByteTag.class, Byte.class, new ByteTagConverter());
        ConverterRegistry.register(ShortTag.class, Short.class, new ShortTagConverter());
        ConverterRegistry.register(IntTag.class, Integer.class, new IntTagConverter());
        ConverterRegistry.register(LongTag.class, Long.class, new LongTagConverter());
        ConverterRegistry.register(FloatTag.class, Float.class, new FloatTagConverter());
        ConverterRegistry.register(DoubleTag.class, Double.class, new DoubleTagConverter());
        ConverterRegistry.register(ByteArrayTag.class, byte[].class, new ByteArrayTagConverter());
        ConverterRegistry.register(StringTag.class, String.class, new StringTagConverter());
        ConverterRegistry.register(ListTag.class, List.class, new ListTagConverter());
        ConverterRegistry.register(CompoundTag.class, Map.class, new CompoundTagConverter());
        ConverterRegistry.register(IntArrayTag.class, int[].class, new IntArrayTagConverter());
        ConverterRegistry.register(DoubleArrayTag.class, double[].class, new DoubleArrayTagConverter());
        ConverterRegistry.register(FloatArrayTag.class, float[].class, new FloatArrayTagConverter());
        ConverterRegistry.register(LongArrayTag.class, long[].class, new LongArrayTagConverter());
        ConverterRegistry.register(SerializableArrayTag.class, Serializable[].class, new SerializableArrayTagConverter());
        ConverterRegistry.register(SerializableTag.class, Serializable.class, new SerializableTagConverter());
        ConverterRegistry.register(ShortArrayTag.class, short[].class, new ShortArrayTagConverter());
        ConverterRegistry.register(StringArrayTag.class, String[].class, new StringArrayTagConverter());
    }
}


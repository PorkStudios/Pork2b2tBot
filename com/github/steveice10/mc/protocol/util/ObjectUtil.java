/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectUtil {
    public static /* varargs */ int hashCode(Object ... objects) {
        return Arrays.deepHashCode(objects);
    }

    public static String toString(Object o) {
        if (o == null) {
            return "null";
        }
        try {
            StringBuilder builder = new StringBuilder(o.getClass().getSimpleName()).append('(');
            List<Field> allDeclaredFields = ObjectUtil.getAllDeclaredFields(o.getClass());
            for (int i = 0; i < allDeclaredFields.size(); ++i) {
                if (i > 0) {
                    builder.append(", ");
                }
                Field field = allDeclaredFields.get(i);
                field.setAccessible(true);
                builder.append(field.getName()).append('=').append(ObjectUtil.memberToString(field.get(o)));
            }
            return builder.append(')').toString();
        }
        catch (Throwable e) {
            return o.getClass().getSimpleName() + '@' + Integer.toHexString(o.hashCode()) + '(' + e.toString() + ')';
        }
    }

    public static String memberToString(Object o) {
        if (o == null) {
            return "null";
        }
        if (o.getClass().isArray()) {
            int length = Array.getLength(o);
            if (length > 20) {
                return o.getClass().getSimpleName() + "(length=" + length + ')';
            }
            StringBuilder builder = new StringBuilder("[");
            for (int i = 0; i < length; ++i) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(ObjectUtil.memberToString(Array.get(o, i)));
            }
            return builder.append(']').toString();
        }
        return o.toString();
    }

    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<Field>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}


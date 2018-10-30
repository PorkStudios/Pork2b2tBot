/*
 * Decompiled with CFR 0_132.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class JsonAdapterAnnotationTypeAdapterFactory
implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;

    public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
        this.constructorConstructor = constructorConstructor;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
        JsonAdapter annotation = targetType.getRawType().getAnnotation(JsonAdapter.class);
        if (annotation == null) {
            return null;
        }
        return JsonAdapterAnnotationTypeAdapterFactory.getTypeAdapter(this.constructorConstructor, gson, targetType, annotation);
    }

    static TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson, TypeToken<?> fieldType, JsonAdapter annotation) {
        Class<?> value = annotation.value();
        if (TypeAdapter.class.isAssignableFrom(value)) {
            Class<?> typeAdapter = value;
            return (TypeAdapter)constructorConstructor.get(TypeToken.get(typeAdapter)).construct();
        }
        if (TypeAdapterFactory.class.isAssignableFrom(value)) {
            Class<?> typeAdapterFactory = value;
            return ((TypeAdapterFactory)constructorConstructor.get(TypeToken.get(typeAdapterFactory)).construct()).create(gson, fieldType);
        }
        throw new IllegalArgumentException("@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
    }
}


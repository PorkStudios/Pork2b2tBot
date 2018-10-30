/*
 * Decompiled with CFR 0_132.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SqlDateTypeAdapter
extends TypeAdapter<java.sql.Date> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == java.sql.Date.class ? new SqlDateTypeAdapter() : null;
        }
    };
    private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

    @Override
    public synchronized java.sql.Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        try {
            long utilDate = this.format.parse(in.nextString()).getTime();
            return new java.sql.Date(utilDate);
        }
        catch (ParseException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public synchronized void write(JsonWriter out, java.sql.Date value) throws IOException {
        out.value(value == null ? null : this.format.format(value));
    }

}


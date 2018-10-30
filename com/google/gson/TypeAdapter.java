/*
 * Decompiled with CFR 0_132.
 */
package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TypeAdapter<T> {
    public abstract void write(JsonWriter var1, T var2) throws IOException;

    public final void toJson(Writer out, T value) throws IOException {
        JsonWriter writer = new JsonWriter(out);
        this.write(writer, value);
    }

    public final TypeAdapter<T> nullSafe() {
        return new TypeAdapter<T>(){

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    TypeAdapter.this.write(out, value);
                }
            }

            @Override
            public T read(JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                }
                return TypeAdapter.this.read(reader);
            }
        };
    }

    public final String toJson(T value) throws IOException {
        StringWriter stringWriter = new StringWriter();
        this.toJson(stringWriter, value);
        return stringWriter.toString();
    }

    public final JsonElement toJsonTree(T value) {
        try {
            JsonTreeWriter jsonWriter = new JsonTreeWriter();
            this.write(jsonWriter, value);
            return jsonWriter.get();
        }
        catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    public abstract T read(JsonReader var1) throws IOException;

    public final T fromJson(Reader in) throws IOException {
        JsonReader reader = new JsonReader(in);
        return this.read(reader);
    }

    public final T fromJson(String json) throws IOException {
        return this.fromJson(new StringReader(json));
    }

    public final T fromJsonTree(JsonElement jsonTree) {
        try {
            JsonTreeReader jsonReader = new JsonTreeReader(jsonTree);
            return this.read(jsonReader);
        }
        catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

}


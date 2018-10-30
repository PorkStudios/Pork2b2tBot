/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public interface IConfigTranslator {
    public void encode(JsonObject var1);

    public void decode(String var1, JsonObject var2);

    public String name();

    default public int getInt(JsonObject object, String name, int def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().intValue();
        }
        return def;
    }

    default public short getShort(JsonObject object, String name, short def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().shortValue();
        }
        return def;
    }

    default public byte getByte(JsonObject object, String name, byte def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().byteValue();
        }
        return def;
    }

    default public long getLong(JsonObject object, String name, long def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().longValue();
        }
        return def;
    }

    default public float getFloat(JsonObject object, String name, float def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().floatValue();
        }
        return def;
    }

    default public double getDouble(JsonObject object, String name, double def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsJsonPrimitive().getAsNumber().doubleValue();
        }
        return def;
    }

    default public boolean getBoolean(JsonObject object, String name, boolean def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return element.getAsJsonPrimitive().getAsBoolean();
        }
        return def;
    }

    default public String getString(JsonObject object, String name, String def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return element.getAsJsonPrimitive().getAsString();
        }
        return def;
    }

    default public JsonArray getArray(JsonObject object, String name, JsonArray def) {
        JsonElement element;
        if (object.has(name) && (element = object.get(name)).isJsonArray()) {
            return element.getAsJsonArray();
        }
        return def;
    }
}


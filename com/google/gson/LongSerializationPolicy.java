/*
 * Decompiled with CFR 0_132.
 */
package com.google.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum LongSerializationPolicy {
    DEFAULT{

        public JsonElement serialize(Long value) {
            return new JsonPrimitive(value);
        }
    }
    ,
    STRING{

        public JsonElement serialize(Long value) {
            return new JsonPrimitive(String.valueOf(value));
        }
    };
    

    private LongSerializationPolicy() {
    }

    public abstract JsonElement serialize(Long var1);

}


/*
 * Decompiled with CFR 0_132.
 */
package com.google.gson.internal;

import com.google.gson.stream.JsonReader;
import java.io.IOException;

public abstract class JsonReaderInternalAccess {
    public static JsonReaderInternalAccess INSTANCE;

    public abstract void promoteNameToValue(JsonReader var1) throws IOException;
}


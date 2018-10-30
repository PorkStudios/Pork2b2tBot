/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import net.daporkchop.porklib.config.IConfigTranslator;

public class NullConfigTranslator
implements IConfigTranslator {
    public static final IConfigTranslator INSTANCE = new NullConfigTranslator();

    private NullConfigTranslator() {
    }

    @Override
    public void encode(JsonObject json) {
    }

    @Override
    public void decode(String fieldName, JsonObject json) {
        System.out.println("[Warning] Config element with name " + fieldName + "is being ignored, discarding " + json.entrySet().size() + " values!");
    }

    @Override
    public String name() {
        return null;
    }

    public boolean getState() {
        return false;
    }

    public String getPackageName() {
        return null;
    }
}


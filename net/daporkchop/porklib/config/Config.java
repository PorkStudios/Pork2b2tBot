/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import net.daporkchop.porklib.config.IConfigTranslator;
import net.daporkchop.porklib.config.NullConfigTranslator;
import org.apache.commons.io.IOUtils;

public abstract class Config {
    private static Hashtable<String, IConfigTranslator> translators = new Hashtable<K, V>();

    protected abstract void registerTranslators();

    public static void registerConfigTranslator(IConfigTranslator element) {
        translators.put(element.name(), element);
    }

    public static void loadConfig(String configJson) {
        System.out.println("Loading config!");
        System.out.println(configJson);
        JsonObject object = new JsonParser().parse(configJson).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            translators.getOrDefault(entry.getKey(), NullConfigTranslator.INSTANCE).decode(entry.getKey(), entry.getValue().getAsJsonObject());
        }
    }

    public static String export() {
        JsonObject object = new JsonObject();
        for (IConfigTranslator translator : translators.values()) {
            JsonObject elementObj = new JsonObject();
            translator.encode(elementObj);
            object.add(translator.name(), elementObj);
        }
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
    }

    public void prepareLoad(File file) {
        File folder = file.getParentFile();
        folder.mkdirs();
        try {
            String config = "{}";
            if (file.getAbsoluteFile().exists()) {
                config = IOUtils.toString(new FileInputStream(file));
            }
            Config.loadConfig(config);
            String newConf = Config.export();
            if (!newConf.equals(config)) {
                file.delete();
                IOUtils.write(newConf, (OutputStream)new FileOutputStream(file));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}


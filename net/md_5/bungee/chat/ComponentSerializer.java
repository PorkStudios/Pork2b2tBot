/*
 * Decompiled with CFR 0_132.
 */
package net.md_5.bungee.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import java.util.HashSet;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.ScoreComponentSerializer;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;

public class ComponentSerializer
implements JsonDeserializer<BaseComponent> {
    private static final JsonParser JSON_PARSER = new JsonParser();
    private static final Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)BaseComponent.class), new ComponentSerializer()).registerTypeAdapter((Type)((Object)TextComponent.class), new TextComponentSerializer()).registerTypeAdapter((Type)((Object)TranslatableComponent.class), new TranslatableComponentSerializer()).registerTypeAdapter((Type)((Object)KeybindComponent.class), new KeybindComponentSerializer()).registerTypeAdapter((Type)((Object)ScoreComponent.class), new ScoreComponentSerializer()).registerTypeAdapter((Type)((Object)SelectorComponent.class), new SelectorComponentSerializer()).create();
    public static final ThreadLocal<HashSet<BaseComponent>> serializedComponents = new ThreadLocal();

    public static BaseComponent[] parse(String json) {
        JsonElement jsonElement = JSON_PARSER.parse(json);
        if (jsonElement.isJsonArray()) {
            return gson.fromJson(jsonElement, BaseComponent[].class);
        }
        return new BaseComponent[]{gson.fromJson(jsonElement, BaseComponent.class)};
    }

    public static String toString(BaseComponent component) {
        return gson.toJson(component);
    }

    public static /* varargs */ String toString(BaseComponent ... components) {
        if (components.length == 1) {
            return gson.toJson(components[0]);
        }
        return gson.toJson(new TextComponent(components));
    }

    @Override
    public BaseComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return new TextComponent(json.getAsString());
        }
        JsonObject object = json.getAsJsonObject();
        if (object.has("translate")) {
            return (BaseComponent)context.deserialize(json, (Type)((Object)TranslatableComponent.class));
        }
        if (object.has("keybind")) {
            return (BaseComponent)context.deserialize(json, (Type)((Object)KeybindComponent.class));
        }
        if (object.has("score")) {
            return (BaseComponent)context.deserialize(json, (Type)((Object)ScoreComponent.class));
        }
        if (object.has("selector")) {
            return (BaseComponent)context.deserialize(json, (Type)((Object)SelectorComponent.class));
        }
        return (BaseComponent)context.deserialize(json, (Type)((Object)TextComponent.class));
    }
}


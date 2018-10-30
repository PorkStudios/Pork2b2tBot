/*
 * Decompiled with CFR 0_132.
 */
package net.md_5.bungee.chat;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.chat.ComponentSerializer;

public class BaseComponentSerializer {
    protected void deserialize(JsonObject object, BaseComponent component, JsonDeserializationContext context) {
        JsonObject event;
        if (object.has("color")) {
            component.setColor(ChatColor.valueOf(object.get("color").getAsString().toUpperCase(Locale.ROOT)));
        }
        if (object.has("bold")) {
            component.setBold(object.get("bold").getAsBoolean());
        }
        if (object.has("italic")) {
            component.setItalic(object.get("italic").getAsBoolean());
        }
        if (object.has("underlined")) {
            component.setUnderlined(object.get("underlined").getAsBoolean());
        }
        if (object.has("strikethrough")) {
            component.setStrikethrough(object.get("strikethrough").getAsBoolean());
        }
        if (object.has("obfuscated")) {
            component.setObfuscated(object.get("obfuscated").getAsBoolean());
        }
        if (object.has("insertion")) {
            component.setInsertion(object.get("insertion").getAsString());
        }
        if (object.has("extra")) {
            component.setExtra(Arrays.asList((Object[])context.deserialize(object.get("extra"), (Type)((Object)BaseComponent[].class))));
        }
        if (object.has("clickEvent")) {
            event = object.getAsJsonObject("clickEvent");
            component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(event.get("action").getAsString().toUpperCase(Locale.ROOT)), event.get("value").getAsString()));
        }
        if (object.has("hoverEvent")) {
            event = object.getAsJsonObject("hoverEvent");
            BaseComponent[] res = event.get("value").isJsonArray() ? (BaseComponent[])context.deserialize(event.get("value"), (Type)((Object)BaseComponent[].class)) : new BaseComponent[]{(BaseComponent)context.deserialize(event.get("value"), (Type)((Object)BaseComponent.class))};
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(event.get("action").getAsString().toUpperCase(Locale.ROOT)), res));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void serialize(JsonObject object, BaseComponent component, JsonSerializationContext context) {
        boolean first = false;
        if (ComponentSerializer.serializedComponents.get() == null) {
            first = true;
            ComponentSerializer.serializedComponents.set(new HashSet());
        }
        try {
            Preconditions.checkArgument(!ComponentSerializer.serializedComponents.get().contains(component), "Component loop");
            ComponentSerializer.serializedComponents.get().add(component);
            if (component.getColorRaw() != null) {
                object.addProperty("color", component.getColorRaw().getName());
            }
            if (component.isBoldRaw() != null) {
                object.addProperty("bold", component.isBoldRaw());
            }
            if (component.isItalicRaw() != null) {
                object.addProperty("italic", component.isItalicRaw());
            }
            if (component.isUnderlinedRaw() != null) {
                object.addProperty("underlined", component.isUnderlinedRaw());
            }
            if (component.isStrikethroughRaw() != null) {
                object.addProperty("strikethrough", component.isStrikethroughRaw());
            }
            if (component.isObfuscatedRaw() != null) {
                object.addProperty("obfuscated", component.isObfuscatedRaw());
            }
            if (component.getInsertion() != null) {
                object.addProperty("insertion", component.getInsertion());
            }
            if (component.getExtra() != null) {
                object.add("extra", context.serialize(component.getExtra()));
            }
            if (component.getClickEvent() != null) {
                JsonObject clickEvent = new JsonObject();
                clickEvent.addProperty("action", component.getClickEvent().getAction().toString().toLowerCase(Locale.ROOT));
                clickEvent.addProperty("value", component.getClickEvent().getValue());
                object.add("clickEvent", clickEvent);
            }
            if (component.getHoverEvent() != null) {
                JsonObject hoverEvent = new JsonObject();
                hoverEvent.addProperty("action", component.getHoverEvent().getAction().toString().toLowerCase(Locale.ROOT));
                hoverEvent.add("value", context.serialize(component.getHoverEvent().getValue()));
                object.add("hoverEvent", hoverEvent);
            }
        }
        finally {
            ComponentSerializer.serializedComponents.get().remove(component);
            if (first) {
                ComponentSerializer.serializedComponents.set(null);
            }
        }
    }
}


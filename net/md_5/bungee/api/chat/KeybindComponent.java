/*
 * Decompiled with CFR 0_132.
 */
package net.md_5.bungee.api.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public final class KeybindComponent
extends BaseComponent {
    private String keybind;

    public KeybindComponent(KeybindComponent original) {
        super(original);
        this.setKeybind(original.getKeybind());
    }

    public KeybindComponent(String keybind) {
        this.setKeybind(keybind);
    }

    @Override
    public BaseComponent duplicate() {
        return new KeybindComponent(this);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        builder.append(this.getKeybind());
        super.toPlainText(builder);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        builder.append((Object)this.getColor());
        if (this.isBold()) {
            builder.append((Object)ChatColor.BOLD);
        }
        if (this.isItalic()) {
            builder.append((Object)ChatColor.ITALIC);
        }
        if (this.isUnderlined()) {
            builder.append((Object)ChatColor.UNDERLINE);
        }
        if (this.isStrikethrough()) {
            builder.append((Object)ChatColor.STRIKETHROUGH);
        }
        if (this.isObfuscated()) {
            builder.append((Object)ChatColor.MAGIC);
        }
        builder.append(this.getKeybind());
        super.toLegacyText(builder);
    }

    public String getKeybind() {
        return this.keybind;
    }

    public void setKeybind(String keybind) {
        this.keybind = keybind;
    }

    @Override
    public String toString() {
        return "KeybindComponent(keybind=" + this.getKeybind() + ")";
    }

    public KeybindComponent() {
    }
}


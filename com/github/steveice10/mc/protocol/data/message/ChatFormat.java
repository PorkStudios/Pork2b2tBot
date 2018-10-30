/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.message;

public enum ChatFormat {
    BOLD,
    UNDERLINED,
    STRIKETHROUGH,
    ITALIC,
    OBFUSCATED;
    

    private ChatFormat() {
    }

    public static ChatFormat byName(String name) {
        name = name.toLowerCase();
        for (ChatFormat format : ChatFormat.values()) {
            if (!format.toString().equals(name)) continue;
            return format;
        }
        return null;
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}


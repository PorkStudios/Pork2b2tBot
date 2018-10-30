/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.message;

public enum ChatColor {
    BLACK,
    DARK_BLUE,
    DARK_GREEN,
    DARK_AQUA,
    DARK_RED,
    DARK_PURPLE,
    GOLD,
    GRAY,
    DARK_GRAY,
    BLUE,
    GREEN,
    AQUA,
    RED,
    LIGHT_PURPLE,
    YELLOW,
    WHITE,
    RESET;
    

    private ChatColor() {
    }

    public static ChatColor byName(String name) {
        name = name.toLowerCase();
        for (ChatColor color : ChatColor.values()) {
            if (!color.toString().equals(name)) continue;
            return color;
        }
        return null;
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}


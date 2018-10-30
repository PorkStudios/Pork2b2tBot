/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.message;

public enum HoverAction {
    SHOW_TEXT,
    SHOW_ITEM,
    SHOW_ACHIEVEMENT,
    SHOW_ENTITY;
    

    private HoverAction() {
    }

    public static HoverAction byName(String name) {
        name = name.toLowerCase();
        for (HoverAction action : HoverAction.values()) {
            if (!action.toString().equals(name)) continue;
            return action;
        }
        return null;
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}


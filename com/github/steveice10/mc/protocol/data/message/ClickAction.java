/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.message;

public enum ClickAction {
    RUN_COMMAND,
    SUGGEST_COMMAND,
    OPEN_URL,
    OPEN_FILE;
    

    private ClickAction() {
    }

    public static ClickAction byName(String name) {
        name = name.toLowerCase();
        for (ClickAction action : ClickAction.values()) {
            if (!action.toString().equals(name)) continue;
            return action;
        }
        return null;
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}


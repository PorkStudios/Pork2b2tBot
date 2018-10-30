/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.text;

public enum ChatType {
    CHAT(0),
    SYSTEM(1),
    GAME_INFO(2);
    
    private final byte id;

    private ChatType(byte id) {
        this.id = id;
    }

    public static ChatType byId(byte idIn) {
        for (ChatType chattype : ChatType.values()) {
            if (idIn != chattype.id) continue;
            return chattype;
        }
        return CHAT;
    }

    public byte getId() {
        return this.id;
    }
}


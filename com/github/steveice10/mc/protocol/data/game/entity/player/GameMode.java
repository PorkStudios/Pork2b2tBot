/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.game.entity.player;

import com.github.steveice10.mc.protocol.data.game.world.notify.ClientNotificationValue;

public enum GameMode implements ClientNotificationValue
{
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;
    

    private GameMode() {
    }
}


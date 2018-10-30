/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.web;

import java.io.Serializable;

public class PlayData
implements Serializable {
    public final String UUID;
    public String name;
    public int[] playTimeByDay = new int[30];
    public int[] playTimeByHour = new int[720];
    public long lastPlayed;

    public PlayData(String UUID2, String name) {
        int i;
        this.UUID = UUID2;
        this.name = name;
        this.lastPlayed = System.currentTimeMillis();
        for (i = 0; i < this.playTimeByDay.length; ++i) {
            this.playTimeByDay[i] = 0;
        }
        for (i = 0; i < this.playTimeByHour.length; ++i) {
            this.playTimeByHour[i] = 0;
        }
    }
}


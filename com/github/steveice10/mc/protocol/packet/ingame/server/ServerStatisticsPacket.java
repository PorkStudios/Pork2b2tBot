/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server;

import com.github.steveice10.mc.protocol.data.game.statistic.Statistic;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;
import java.util.Map;

public class ServerStatisticsPacket
extends MinecraftPacket {
    public static final String CRAFT_ITEM_PREFIX = "stat.craftItem.";
    public static final String BREAK_BLOCK_PREFIX = "stat.mineBlock.";
    public static final String USE_ITEM_PREFIX = "stat.useItem.";
    public static final String BREAK_ITEM_PREFIX = "stat.breakItem.";
    public static final String KILL_ENTITY_PREFIX = "stat.killEntity.";
    public static final String KILLED_BY_ENTITY_PREFIX = "stat.entityKilledBy.";
    public static final String DROP_ITEM_PREFIX = "stat.drop.";
    public static final String PICKUP_ITEM_PREFIX = "stat.pickup.";
    private byte[] in;

    public ServerStatisticsPacket() {
    }

    public ServerStatisticsPacket(Map<Statistic, Integer> statistics) {
    }

    public Map<Statistic, Integer> getStatistics() {
        return null;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.in = in.readBytes(in.available());
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeBytes(this.in);
    }
}


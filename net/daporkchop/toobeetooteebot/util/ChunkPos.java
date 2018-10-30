/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.util;

public class ChunkPos {
    public int x;
    public int z;
    public long hash;

    public ChunkPos(long hash) {
        this.hash = hash;
        this.x = ChunkPos.getXFromHash(hash);
        this.z = ChunkPos.getZFromHash(hash);
    }

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
        this.hash = ChunkPos.getChunkHashFromXZ(x, z);
    }

    public static long getChunkHashFromXZ(int x, int z) {
        return (long)x << 32 | (long)z & 0xFFFFFFFFL;
    }

    public static ChunkPos getPosFromHash(long hash) {
        return new ChunkPos(ChunkPos.getXFromHash(hash), ChunkPos.getZFromHash(hash));
    }

    public static int getXFromHash(long hash) {
        return (int)(hash >> 32);
    }

    public static int getZFromHash(long hash) {
        return (int)hash;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.game.chunk;

import com.github.steveice10.mc.protocol.data.game.chunk.FlexibleStorage;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.mc.protocol.util.ObjectUtil;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BlockStorage {
    public static final BlockState AIR = new BlockState(0, 0);
    public int bitsPerEntry;
    public List<BlockState> states;
    public FlexibleStorage storage;

    public BlockStorage() {
        this.bitsPerEntry = 4;
        this.states = new ArrayList<BlockState>();
        this.states.add(AIR);
        this.storage = new FlexibleStorage(this.bitsPerEntry, 4096);
    }

    public BlockStorage(NetInput in) throws IOException {
        this.bitsPerEntry = in.readUnsignedByte();
        this.states = new ArrayList<BlockState>();
        int stateCount = in.readVarInt();
        for (int i = 0; i < stateCount; ++i) {
            this.states.add(NetUtil.readBlockState(in));
        }
        this.storage = new FlexibleStorage(this.bitsPerEntry, in.readLongs(in.readVarInt()));
    }

    public static int index(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    public static BlockState rawToState(int raw) {
        return new BlockState(raw >> 4, raw & 15);
    }

    public static int stateToRaw(BlockState state) {
        return state.getId() << 4 | state.getData() & 15;
    }

    public void write(NetOutput out) throws IOException {
        out.writeByte(this.bitsPerEntry);
        out.writeVarInt(this.states.size());
        for (BlockState state : this.states) {
            NetUtil.writeBlockState(out, state);
        }
        long[] data = this.storage.getData();
        out.writeVarInt(data.length);
        out.writeLongs(data);
    }

    public int getBitsPerEntry() {
        return this.bitsPerEntry;
    }

    public List<BlockState> getStates() {
        return Collections.unmodifiableList(this.states);
    }

    public FlexibleStorage getStorage() {
        return this.storage;
    }

    public BlockState get(int x, int y, int z) {
        int id = this.storage.get(BlockStorage.index(x, y, z));
        return this.bitsPerEntry <= 8 ? (id >= 0 && id < this.states.size() ? this.states.get(id) : AIR) : BlockStorage.rawToState(id);
    }

    public void set(int x, int y, int z, BlockState state) {
        int id;
        int n = id = this.bitsPerEntry <= 8 ? this.states.indexOf(state) : BlockStorage.stateToRaw(state);
        if (id == -1) {
            this.states.add(state);
            if (this.states.size() > 1 << this.bitsPerEntry) {
                ++this.bitsPerEntry;
                List<BlockState> oldStates = this.states;
                if (this.bitsPerEntry > 8) {
                    oldStates = new ArrayList<BlockState>(this.states);
                    this.states.clear();
                    this.bitsPerEntry = 13;
                }
                FlexibleStorage oldStorage = this.storage;
                this.storage = new FlexibleStorage(this.bitsPerEntry, this.storage.getSize());
                for (int index = 0; index < this.storage.getSize(); ++index) {
                    this.storage.set(index, this.bitsPerEntry <= 8 ? oldStorage.get(index) : BlockStorage.stateToRaw(oldStates.get(index)));
                }
            }
            id = this.bitsPerEntry <= 8 ? this.states.indexOf(state) : BlockStorage.stateToRaw(state);
        }
        this.storage.set(BlockStorage.index(x, y, z), id);
    }

    public boolean isEmpty() {
        for (int index = 0; index < this.storage.getSize(); ++index) {
            if (this.storage.get(index) == 0) continue;
            return false;
        }
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockStorage)) {
            return false;
        }
        BlockStorage that = (BlockStorage)o;
        return this.bitsPerEntry == that.bitsPerEntry && Objects.equals(this.states, that.states) && Objects.equals(this.storage, that.storage);
    }

    public int hashCode() {
        return ObjectUtil.hashCode(this.bitsPerEntry, this.states, this.storage);
    }

    public String toString() {
        return ObjectUtil.toString(this);
    }
}


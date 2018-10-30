/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.game.world.block;

import com.github.steveice10.mc.protocol.util.ObjectUtil;

public class BlockState {
    public int id;
    public int data;

    public BlockState(int id, int data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return this.id;
    }

    public int getData() {
        return this.data;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockState)) {
            return false;
        }
        BlockState that = (BlockState)o;
        return this.id == that.id && this.data == that.data;
    }

    public int hashCode() {
        return ObjectUtil.hashCode(this.id, this.data);
    }

    public String toString() {
        return ObjectUtil.toString(this);
    }
}


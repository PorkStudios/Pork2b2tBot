/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;

public class IntTag
extends NumberTag<Integer> {
    public int data;

    @Override
    public Integer getData() {
        return this.data;
    }

    @Override
    public void setData(Integer data) {
        this.data = data == null ? 0 : data;
    }

    public IntTag(String name) {
        super(name);
    }

    public IntTag(String name, int data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeInt(this.data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        this.data = dis.readInt();
    }

    @Override
    public byte getId() {
        return 3;
    }

    @Override
    public String toString() {
        return "IntTag" + this.getName() + "(data: " + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new IntTag(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            IntTag o = (IntTag)obj;
            return this.data == o.data;
        }
        return false;
    }
}


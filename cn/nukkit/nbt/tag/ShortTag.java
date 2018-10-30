/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;

public class ShortTag
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

    public ShortTag(String name) {
        super(name);
    }

    public ShortTag(String name, int data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeShort(this.data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        this.data = dis.readUnsignedShort();
    }

    @Override
    public byte getId() {
        return 2;
    }

    @Override
    public String toString() {
        return "" + this.data;
    }

    @Override
    public Tag copy() {
        return new ShortTag(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ShortTag o = (ShortTag)obj;
            return this.data == o.data;
        }
        return false;
    }
}


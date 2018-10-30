/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;

public class ByteTag
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

    public ByteTag(String name) {
        super(name);
    }

    public ByteTag(String name, int data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeByte(this.data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        this.data = dis.readByte();
    }

    @Override
    public byte getId() {
        return 1;
    }

    @Override
    public String toString() {
        String hex = Integer.toHexString(this.data);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return "ByteTag " + this.getName() + " (data: 0x" + hex + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteTag byteTag = (ByteTag)obj;
            return this.data == byteTag.data;
        }
        return false;
    }

    @Override
    public Tag copy() {
        return new ByteTag(this.getName(), this.data);
    }
}


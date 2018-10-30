/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;

public class StringTag
extends Tag {
    public String data;

    public StringTag(String name) {
        super(name);
    }

    public StringTag(String name, String data) {
        super(name);
        this.data = data;
        if (data == null) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeUTF(this.data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        this.data = dis.readUTF();
    }

    @Override
    public byte getId() {
        return 8;
    }

    @Override
    public String toString() {
        return "StringTag " + this.getName() + " (data: " + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new StringTag(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            StringTag o = (StringTag)obj;
            return this.data == null && o.data == null || this.data != null && this.data.equals(o.data);
        }
        return false;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;
import java.util.Arrays;

public class IntArrayTag
extends Tag {
    public int[] data;

    public IntArrayTag(String name) {
        super(name);
    }

    public IntArrayTag(String name, int[] data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeInt(this.data.length);
        for (int aData : this.data) {
            dos.writeInt(aData);
        }
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        int length = dis.readInt();
        this.data = new int[length];
        for (int i = 0; i < length; ++i) {
            this.data[i] = dis.readInt();
        }
    }

    @Override
    public byte getId() {
        return 11;
    }

    @Override
    public String toString() {
        return "IntArrayTag " + this.getName() + " [" + this.data.length + " bytes]";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            IntArrayTag intArrayTag = (IntArrayTag)obj;
            return this.data == null && intArrayTag.data == null || this.data != null && Arrays.equals(this.data, intArrayTag.data);
        }
        return false;
    }

    @Override
    public Tag copy() {
        int[] cp = new int[this.data.length];
        System.arraycopy(this.data, 0, cp, 0, this.data.length);
        return new IntArrayTag(this.getName(), cp);
    }
}


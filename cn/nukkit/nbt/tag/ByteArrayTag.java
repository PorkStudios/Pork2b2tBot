/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class ByteArrayTag
extends Tag {
    public byte[] data;

    public ByteArrayTag(String name) {
        super(name);
    }

    public ByteArrayTag(String name, byte[] data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        if (this.data == null) {
            dos.writeInt(0);
            return;
        }
        dos.writeInt(this.data.length);
        dos.write(this.data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        int length = dis.readInt();
        this.data = new byte[length];
        dis.readFully(this.data);
    }

    @Override
    public byte getId() {
        return 7;
    }

    @Override
    public String toString() {
        return "ByteArrayTag " + this.getName() + " (data: 0x" + new BigInteger(this.data).toString(16) + " [" + this.data.length + " bytes])";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteArrayTag byteArrayTag = (ByteArrayTag)obj;
            return this.data == null && byteArrayTag.data == null || this.data != null && Arrays.equals(this.data, byteArrayTag.data);
        }
        return false;
    }

    @Override
    public Tag copy() {
        byte[] cp = new byte[this.data.length];
        System.arraycopy(this.data, 0, cp, 0, this.data.length);
        return new ByteArrayTag(this.getName(), cp);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;

public class FloatTag
extends NumberTag<Float> {
    public float data;

    @Override
    public Float getData() {
        return Float.valueOf(this.data);
    }

    @Override
    public void setData(Float data) {
        this.data = data == null ? 0.0f : data.floatValue();
    }

    public FloatTag(String name) {
        super(name);
    }

    public FloatTag(String name, float data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeFloat(this.data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        this.data = dis.readFloat();
    }

    @Override
    public byte getId() {
        return 5;
    }

    @Override
    public String toString() {
        return "FloatTag " + this.getName() + " (data: " + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new FloatTag(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            FloatTag o = (FloatTag)obj;
            return this.data == o.data;
        }
        return false;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;

public class LongTag
extends NumberTag<Long> {
    public long data;

    @Override
    public Long getData() {
        return this.data;
    }

    @Override
    public void setData(Long data) {
        this.data = data == null ? 0L : data;
    }

    public LongTag(String name) {
        super(name);
    }

    public LongTag(String name, long data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeLong(this.data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        this.data = dis.readLong();
    }

    @Override
    public byte getId() {
        return 4;
    }

    @Override
    public String toString() {
        return "LongTag" + this.getName() + " (data:" + this.data + ")";
    }

    @Override
    public Tag copy() {
        return new LongTag(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            LongTag o = (LongTag)obj;
            return this.data == o.data;
        }
        return false;
    }
}


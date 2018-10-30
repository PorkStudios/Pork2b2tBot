/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin.custom;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortArrayTag
extends Tag {
    private short[] value;

    public ShortArrayTag(String name) {
        this(name, new short[0]);
    }

    public ShortArrayTag(String name, short[] value) {
        super(name);
        this.value = value;
    }

    public short[] getValue() {
        return (short[])this.value.clone();
    }

    public void setValue(short[] value) {
        if (value == null) {
            return;
        }
        this.value = (short[])value.clone();
    }

    public short getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, short value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new short[in.readInt()];
        for (int index = 0; index < this.value.length; ++index) {
            this.value[index] = in.readShort();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; ++index) {
            out.writeShort(this.value[index]);
        }
    }

    @Override
    public ShortArrayTag clone() {
        return new ShortArrayTag(this.getName(), this.getValue());
    }
}


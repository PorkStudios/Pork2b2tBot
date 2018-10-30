/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntArrayTag
extends Tag {
    private int[] value;

    public IntArrayTag(String name) {
        this(name, new int[0]);
    }

    public IntArrayTag(String name, int[] value) {
        super(name);
        this.value = value;
    }

    public int[] getValue() {
        return (int[])this.value.clone();
    }

    public void setValue(int[] value) {
        if (value == null) {
            return;
        }
        this.value = (int[])value.clone();
    }

    public int getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, int value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new int[in.readInt()];
        for (int index = 0; index < this.value.length; ++index) {
            this.value[index] = in.readInt();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; ++index) {
            out.writeInt(this.value[index]);
        }
    }

    @Override
    public IntArrayTag clone() {
        return new IntArrayTag(this.getName(), this.getValue());
    }
}


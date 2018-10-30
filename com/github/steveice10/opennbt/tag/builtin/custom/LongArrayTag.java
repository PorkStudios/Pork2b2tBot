/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin.custom;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongArrayTag
extends Tag {
    private long[] value;

    public LongArrayTag(String name) {
        this(name, new long[0]);
    }

    public LongArrayTag(String name, long[] value) {
        super(name);
        this.value = value;
    }

    public long[] getValue() {
        return (long[])this.value.clone();
    }

    public void setValue(long[] value) {
        if (value == null) {
            return;
        }
        this.value = (long[])value.clone();
    }

    public long getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, long value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new long[in.readInt()];
        for (int index = 0; index < this.value.length; ++index) {
            this.value[index] = in.readLong();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; ++index) {
            out.writeLong(this.value[index]);
        }
    }

    @Override
    public LongArrayTag clone() {
        return new LongArrayTag(this.getName(), this.getValue());
    }
}


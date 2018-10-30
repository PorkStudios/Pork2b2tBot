/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteArrayTag
extends Tag {
    private byte[] value;

    public ByteArrayTag(String name) {
        this(name, new byte[0]);
    }

    public ByteArrayTag(String name, byte[] value) {
        super(name);
        this.value = value;
    }

    public byte[] getValue() {
        return (byte[])this.value.clone();
    }

    public void setValue(byte[] value) {
        if (value == null) {
            return;
        }
        this.value = (byte[])value.clone();
    }

    public byte getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, byte value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new byte[in.readInt()];
        in.readFully(this.value);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        out.write(this.value);
    }

    @Override
    public ByteArrayTag clone() {
        return new ByteArrayTag(this.getName(), this.getValue());
    }
}


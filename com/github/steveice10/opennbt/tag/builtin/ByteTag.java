/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag
extends Tag {
    private byte value;

    public ByteTag(String name) {
        this(name, 0);
    }

    public ByteTag(String name, byte value) {
        super(name);
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return this.value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readByte();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(this.value);
    }

    @Override
    public ByteTag clone() {
        return new ByteTag(this.getName(), this.getValue());
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag
extends Tag {
    private short value;

    public ShortTag(String name) {
        this(name, 0);
    }

    public ShortTag(String name, short value) {
        super(name);
        this.value = value;
    }

    @Override
    public Short getValue() {
        return this.value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readShort();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeShort(this.value);
    }

    @Override
    public ShortTag clone() {
        return new ShortTag(this.getName(), this.getValue());
    }
}


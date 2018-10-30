/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag
extends Tag {
    private double value;

    public DoubleTag(String name) {
        this(name, 0.0);
    }

    public DoubleTag(String name, double value) {
        super(name);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readDouble();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(this.value);
    }

    @Override
    public DoubleTag clone() {
        return new DoubleTag(this.getName(), this.getValue());
    }
}


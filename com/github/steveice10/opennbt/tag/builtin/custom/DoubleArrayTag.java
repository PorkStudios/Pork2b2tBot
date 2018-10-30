/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin.custom;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleArrayTag
extends Tag {
    private double[] value;

    public DoubleArrayTag(String name) {
        this(name, new double[0]);
    }

    public DoubleArrayTag(String name, double[] value) {
        super(name);
        this.value = value;
    }

    public double[] getValue() {
        return (double[])this.value.clone();
    }

    public void setValue(double[] value) {
        if (value == null) {
            return;
        }
        this.value = (double[])value.clone();
    }

    public double getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, double value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new double[in.readInt()];
        for (int index = 0; index < this.value.length; ++index) {
            this.value[index] = in.readDouble();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; ++index) {
            out.writeDouble(this.value[index]);
        }
    }

    @Override
    public DoubleArrayTag clone() {
        return new DoubleArrayTag(this.getName(), this.getValue());
    }
}


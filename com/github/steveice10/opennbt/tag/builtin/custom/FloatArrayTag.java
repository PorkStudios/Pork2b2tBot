/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin.custom;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatArrayTag
extends Tag {
    private float[] value;

    public FloatArrayTag(String name) {
        this(name, new float[0]);
    }

    public FloatArrayTag(String name, float[] value) {
        super(name);
        this.value = value;
    }

    public float[] getValue() {
        return (float[])this.value.clone();
    }

    public void setValue(float[] value) {
        if (value == null) {
            return;
        }
        this.value = (float[])value.clone();
    }

    public float getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, float value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new float[in.readInt()];
        for (int index = 0; index < this.value.length; ++index) {
            this.value[index] = in.readFloat();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; ++index) {
            out.writeFloat(this.value[index]);
        }
    }

    @Override
    public FloatArrayTag clone() {
        return new FloatArrayTag(this.getName(), this.getValue());
    }
}


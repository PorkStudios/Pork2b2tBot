/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatTag
extends Tag {
    private float value;

    public FloatTag(String name) {
        this(name, 0.0f);
    }

    public FloatTag(String name, float value) {
        super(name);
        this.value = value;
    }

    @Override
    public Float getValue() {
        return Float.valueOf(this.value);
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readFloat();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeFloat(this.value);
    }

    @Override
    public FloatTag clone() {
        return new FloatTag(this.getName(), this.getValue().floatValue());
    }
}


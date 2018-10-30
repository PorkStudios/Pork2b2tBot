/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin.custom;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringArrayTag
extends Tag {
    private String[] value;

    public StringArrayTag(String name) {
        this(name, new String[0]);
    }

    public StringArrayTag(String name, String[] value) {
        super(name);
        this.value = value;
    }

    public String[] getValue() {
        return (String[])this.value.clone();
    }

    public void setValue(String[] value) {
        if (value == null) {
            return;
        }
        this.value = (String[])value.clone();
    }

    public String getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, String value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new String[in.readInt()];
        for (int index = 0; index < this.value.length; ++index) {
            this.value[index] = in.readUTF();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (int index = 0; index < this.value.length; ++index) {
            out.writeUTF(this.value[index]);
        }
    }

    @Override
    public StringArrayTag clone() {
        return new StringArrayTag(this.getName(), this.getValue());
    }
}


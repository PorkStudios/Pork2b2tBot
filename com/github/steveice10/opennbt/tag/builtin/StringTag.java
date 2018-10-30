/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringTag
extends Tag {
    private String value;

    public StringTag(String name) {
        this(name, "");
    }

    public StringTag(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readUTF();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.value);
    }

    @Override
    public StringTag clone() {
        return new StringTag(this.getName(), this.getValue());
    }
}


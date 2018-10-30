/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;

public abstract class Tag
implements Cloneable {
    private String name;

    public Tag(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public abstract Object getValue();

    public abstract void read(DataInput var1) throws IOException;

    public abstract void write(DataOutput var1) throws IOException;

    public abstract Tag clone();

    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag tag = (Tag)obj;
        if (!this.getName().equals(tag.getName())) {
            return false;
        }
        if (this.getValue() == null) {
            return tag.getValue() == null;
        }
        if (tag.getValue() == null) {
            return false;
        }
        if (this.getValue().getClass().isArray() && tag.getValue().getClass().isArray()) {
            int length = Array.getLength(this.getValue());
            if (Array.getLength(tag.getValue()) != length) {
                return false;
            }
            for (int index = 0; index < length; ++index) {
                Object o = Array.get(this.getValue(), index);
                Object other = Array.get(tag.getValue(), index);
                if ((o != null || other == null) && (o == null || o.equals(other))) continue;
                return false;
            }
            return true;
        }
        return this.getValue().equals(tag.getValue());
    }

    public String toString() {
        String name = this.getName() != null && !this.getName().equals("") ? "(" + this.getName() + ")" : "";
        String value = "";
        if (this.getValue() != null) {
            value = this.getValue().toString();
            if (this.getValue().getClass().isArray()) {
                StringBuilder build = new StringBuilder();
                build.append("[");
                for (int index = 0; index < Array.getLength(this.getValue()); ++index) {
                    if (index > 0) {
                        build.append(", ");
                    }
                    build.append(Array.get(this.getValue(), index));
                }
                build.append("]");
                value = build.toString();
            }
        }
        return this.getClass().getSimpleName() + name + " { " + value + " }";
    }
}


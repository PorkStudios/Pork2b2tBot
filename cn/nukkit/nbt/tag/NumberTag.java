/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.tag.Tag;

public abstract class NumberTag<T extends Number>
extends Tag {
    protected NumberTag(String name) {
        super(name);
    }

    public abstract T getData();

    public abstract void setData(T var1);
}


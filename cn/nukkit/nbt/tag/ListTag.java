/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.tag;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.Tag;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListTag<T extends Tag>
extends Tag {
    private List<T> list = new ArrayList<T>();
    public byte type;

    public ListTag() {
        super("");
    }

    public ListTag(String name) {
        super(name);
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        this.type = this.list.size() > 0 ? ((Tag)this.list.get(0)).getId() : (byte)1;
        dos.writeByte(this.type);
        dos.writeInt(this.list.size());
        for (Tag aList : this.list) {
            aList.write(dos);
        }
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        this.type = dis.readByte();
        int size = dis.readInt();
        this.list = new ArrayList<T>();
        for (int i = 0; i < size; ++i) {
            Tag tag = Tag.newTag(this.type, null);
            tag.load(dis);
            this.list.add(tag);
        }
    }

    @Override
    public byte getId() {
        return 9;
    }

    @Override
    public String toString() {
        return "ListTag " + this.getName() + " [" + this.list.size() + " entries of type " + Tag.getTagName(this.type) + "]";
    }

    @Override
    public void print(String prefix, PrintStream out) {
        super.print(prefix, out);
        out.println(prefix + "{");
        String orgPrefix = prefix;
        prefix = prefix + "   ";
        for (Tag aList : this.list) {
            aList.print(prefix, out);
        }
        out.println(orgPrefix + "}");
    }

    public ListTag<T> add(T tag) {
        this.type = tag.getId();
        this.list.add(tag);
        return this;
    }

    public ListTag<T> add(int index, T tag) {
        this.type = tag.getId();
        if (index >= this.list.size()) {
            this.list.add(index, tag);
        } else {
            this.list.set(index, tag);
        }
        return this;
    }

    public T get(int index) {
        return (T)((Tag)this.list.get(index));
    }

    public List<T> getAll() {
        return new ArrayList<T>(this.list);
    }

    public void setAll(List<T> tags) {
        this.list = new ArrayList<T>(tags);
    }

    public void remove(T tag) {
        this.list.remove(tag);
    }

    public void remove(int index) {
        this.list.remove(index);
    }

    public void removeAll(Collection<T> tags) {
        this.list.remove(tags);
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public Tag copy() {
        ListTag<T> res = new ListTag<T>(this.getName());
        res.type = this.type;
        for (Tag t : this.list) {
            Tag copy = t.copy();
            res.list.add(copy);
        }
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ListTag o = (ListTag)obj;
            if (this.type == o.type) {
                return this.list.equals(o.list);
            }
        }
        return false;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.list.TIntList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TIntListDecorator
extends AbstractList<Integer>
implements List<Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TIntList list;

    public TIntListDecorator() {
    }

    public TIntListDecorator(TIntList list) {
        this.list = list;
    }

    public TIntList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Integer get(int index) {
        int value = this.list.get(index);
        if (value == this.list.getNoEntryValue()) {
            return null;
        }
        return value;
    }

    @Override
    public Integer set(int index, Integer value) {
        int previous_value = this.list.set(index, value);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return previous_value;
    }

    @Override
    public void add(int index, Integer value) {
        this.list.insert(index, value);
    }

    @Override
    public Integer remove(int index) {
        int previous_value = this.list.removeAt(index);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return previous_value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TIntList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this.list);
    }
}


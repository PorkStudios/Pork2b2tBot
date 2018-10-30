/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.list.TShortList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TShortListDecorator
extends AbstractList<Short>
implements List<Short>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TShortList list;

    public TShortListDecorator() {
    }

    public TShortListDecorator(TShortList list) {
        this.list = list;
    }

    public TShortList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Short get(int index) {
        short value = this.list.get(index);
        if (value == this.list.getNoEntryValue()) {
            return null;
        }
        return value;
    }

    @Override
    public Short set(int index, Short value) {
        short previous_value = this.list.set(index, value);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return previous_value;
    }

    @Override
    public void add(int index, Short value) {
        this.list.insert(index, value);
    }

    @Override
    public Short remove(int index) {
        short previous_value = this.list.removeAt(index);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return previous_value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TShortList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this.list);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.list.TByteList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TByteListDecorator
extends AbstractList<Byte>
implements List<Byte>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TByteList list;

    public TByteListDecorator() {
    }

    public TByteListDecorator(TByteList list) {
        this.list = list;
    }

    public TByteList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Byte get(int index) {
        byte value = this.list.get(index);
        if (value == this.list.getNoEntryValue()) {
            return null;
        }
        return value;
    }

    @Override
    public Byte set(int index, Byte value) {
        byte previous_value = this.list.set(index, value);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return previous_value;
    }

    @Override
    public void add(int index, Byte value) {
        this.list.insert(index, value);
    }

    @Override
    public Byte remove(int index) {
        byte previous_value = this.list.removeAt(index);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return previous_value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TByteList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this.list);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.list.TFloatList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TFloatListDecorator
extends AbstractList<Float>
implements List<Float>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatList list;

    public TFloatListDecorator() {
    }

    public TFloatListDecorator(TFloatList list) {
        this.list = list;
    }

    public TFloatList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Float get(int index) {
        float value = this.list.get(index);
        if (value == this.list.getNoEntryValue()) {
            return null;
        }
        return Float.valueOf(value);
    }

    @Override
    public Float set(int index, Float value) {
        float previous_value = this.list.set(index, value.floatValue());
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return Float.valueOf(previous_value);
    }

    @Override
    public void add(int index, Float value) {
        this.list.insert(index, value.floatValue());
    }

    @Override
    public Float remove(int index) {
        float previous_value = this.list.removeAt(index);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return Float.valueOf(previous_value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TFloatList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this.list);
    }
}


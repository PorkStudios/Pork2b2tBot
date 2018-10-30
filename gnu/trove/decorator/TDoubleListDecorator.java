/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.list.TDoubleList;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TDoubleListDecorator
extends AbstractList<Double>
implements List<Double>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TDoubleList list;

    public TDoubleListDecorator() {
    }

    public TDoubleListDecorator(TDoubleList list) {
        this.list = list;
    }

    public TDoubleList getList() {
        return this.list;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Double get(int index) {
        double value = this.list.get(index);
        if (value == this.list.getNoEntryValue()) {
            return null;
        }
        return value;
    }

    @Override
    public Double set(int index, Double value) {
        double previous_value = this.list.set(index, value);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return previous_value;
    }

    @Override
    public void add(int index, Double value) {
        this.list.insert(index, value);
    }

    @Override
    public Double remove(int index) {
        double previous_value = this.list.removeAt(index);
        if (previous_value == this.list.getNoEntryValue()) {
            return null;
        }
        return previous_value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.list = (TDoubleList)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this.list);
    }
}


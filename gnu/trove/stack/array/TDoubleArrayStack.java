/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.stack.array;

import gnu.trove.TDoubleCollection;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.stack.TDoubleStack;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TDoubleArrayStack
implements TDoubleStack,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TDoubleArrayList _list;
    public static final int DEFAULT_CAPACITY = 10;

    public TDoubleArrayStack() {
        this(10);
    }

    public TDoubleArrayStack(int capacity) {
        this._list = new TDoubleArrayList(capacity);
    }

    public TDoubleArrayStack(int capacity, double no_entry_value) {
        this._list = new TDoubleArrayList(capacity, no_entry_value);
    }

    public TDoubleArrayStack(TDoubleStack stack) {
        if (!(stack instanceof TDoubleArrayStack)) {
            throw new UnsupportedOperationException("Only support TDoubleArrayStack");
        }
        TDoubleArrayStack array_stack = (TDoubleArrayStack)stack;
        this._list = new TDoubleArrayList(array_stack._list);
    }

    public double getNoEntryValue() {
        return this._list.getNoEntryValue();
    }

    public void push(double val) {
        this._list.add(val);
    }

    public double pop() {
        return this._list.removeAt(this._list.size() - 1);
    }

    public double peek() {
        return this._list.get(this._list.size() - 1);
    }

    public int size() {
        return this._list.size();
    }

    public void clear() {
        this._list.clear();
    }

    public double[] toArray() {
        double[] retval = this._list.toArray();
        this.reverse(retval, 0, this.size());
        return retval;
    }

    public void toArray(double[] dest) {
        int size = this.size();
        int start = size - dest.length;
        if (start < 0) {
            start = 0;
        }
        int length = Math.min(size, dest.length);
        this._list.toArray(dest, start, length);
        this.reverse(dest, 0, length);
        if (dest.length > size) {
            dest[size] = this._list.getNoEntryValue();
        }
    }

    private void reverse(double[] dest, int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException("from cannot be greater than to");
        }
        int i = from;
        for (int j = to - 1; i < j; ++i, --j) {
            this.swap(dest, i, j);
        }
    }

    private void swap(double[] dest, int i, int j) {
        double tmp = dest[i];
        dest[i] = dest[j];
        dest[j] = tmp;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("{");
        for (int i = this._list.size() - 1; i > 0; --i) {
            buf.append(this._list.get(i));
            buf.append(", ");
        }
        if (this.size() > 0) {
            buf.append(this._list.get(0));
        }
        buf.append("}");
        return buf.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TDoubleArrayStack that = (TDoubleArrayStack)o;
        return this._list.equals(that._list);
    }

    public int hashCode() {
        return this._list.hashCode();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._list);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._list = (TDoubleArrayList)in.readObject();
    }
}


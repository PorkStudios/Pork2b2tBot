/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TDoubleSetDecorator
extends AbstractSet<Double>
implements Set<Double>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected TDoubleSet _set;

    public TDoubleSetDecorator() {
    }

    public TDoubleSetDecorator(TDoubleSet set) {
        this._set = set;
    }

    public TDoubleSet getSet() {
        return this._set;
    }

    @Override
    public boolean add(Double value) {
        return value != null && this._set.add(value);
    }

    @Override
    public boolean equals(Object other) {
        if (this._set.equals(other)) {
            return true;
        }
        if (other instanceof Set) {
            Set that = (Set)other;
            if (that.size() != this._set.size()) {
                return false;
            }
            Iterator it = that.iterator();
            int i = that.size();
            while (i-- > 0) {
                Object val = it.next();
                if (val instanceof Double) {
                    double v = (Double)val;
                    if (this._set.contains(v)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this._set.clear();
    }

    @Override
    public boolean remove(Object value) {
        return value instanceof Double && this._set.remove((Double)value);
    }

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>(){
            private final TDoubleIterator it;
            {
                this.it = TDoubleSetDecorator.this._set.iterator();
            }

            @Override
            public Double next() {
                return this.it.next();
            }

            @Override
            public boolean hasNext() {
                return this.it.hasNext();
            }

            @Override
            public void remove() {
                this.it.remove();
            }
        };
    }

    @Override
    public int size() {
        return this._set.size();
    }

    @Override
    public boolean isEmpty() {
        return this._set.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Double)) {
            return false;
        }
        return this._set.contains((Double)o);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._set = (TDoubleSet)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._set);
    }

}


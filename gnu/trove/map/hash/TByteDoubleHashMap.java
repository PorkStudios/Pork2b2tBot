/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.procedure.TByteDoubleProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TByteDoubleHashMap
extends TByteDoubleHash
implements TByteDoubleMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient double[] _values;

    public TByteDoubleHashMap() {
    }

    public TByteDoubleHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TByteDoubleHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TByteDoubleHashMap(int initialCapacity, float loadFactor, byte noEntryKey, double noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TByteDoubleHashMap(byte[] keys, double[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TByteDoubleHashMap(TByteDoubleMap map) {
        super(map.size());
        if (map instanceof TByteDoubleHashMap) {
            TByteDoubleHashMap hashmap = (TByteDoubleHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
                Arrays.fill(this._set, this.no_entry_key);
            }
            if (this.no_entry_value != 0.0) {
                Arrays.fill(this._values, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.putAll(map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new double[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        byte[] oldKeys = this._set;
        double[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new byte[newCapacity];
        this._values = new double[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            byte o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public double put(byte key, double value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public double putIfAbsent(byte key, double value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private double doPut(byte key, double value, int index) {
        double previous = this.no_entry_value;
        boolean isNewMapping = true;
        if (index < 0) {
            index = - index - 1;
            previous = this._values[index];
            isNewMapping = false;
        }
        this._values[index] = value;
        if (isNewMapping) {
            this.postInsertHook(this.consumeFreeSlot);
        }
        return previous;
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Double> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Byte, ? extends Double> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TByteDoubleMap map) {
        this.ensureCapacity(map.size());
        TByteDoubleIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public double get(byte key) {
        int index = this.index(key);
        return index < 0 ? this.no_entry_value : this._values[index];
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
        Arrays.fill(this._states, 0, this._states.length, (byte)0);
    }

    @Override
    public boolean isEmpty() {
        return 0 == this._size;
    }

    @Override
    public double remove(byte key) {
        double prev = this.no_entry_value;
        int index = this.index(key);
        if (index >= 0) {
            prev = this._values[index];
            this.removeAt(index);
        }
        return prev;
    }

    @Override
    protected void removeAt(int index) {
        this._values[index] = this.no_entry_value;
        super.removeAt(index);
    }

    @Override
    public TByteSet keySet() {
        return new TKeyView();
    }

    @Override
    public byte[] keys() {
        byte[] keys = new byte[this.size()];
        byte[] k = this._set;
        byte[] states = this._states;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            keys[j++] = k[i];
        }
        return keys;
    }

    @Override
    public byte[] keys(byte[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new byte[size];
        }
        byte[] keys = this._set;
        byte[] states = this._states;
        int i = keys.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            array[j++] = keys[i];
        }
        return array;
    }

    @Override
    public TDoubleCollection valueCollection() {
        return new TValueView();
    }

    @Override
    public double[] values() {
        double[] vals = new double[this.size()];
        double[] v = this._values;
        byte[] states = this._states;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            vals[j++] = v[i];
        }
        return vals;
    }

    @Override
    public double[] values(double[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new double[size];
        }
        double[] v = this._values;
        byte[] states = this._states;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            array[j++] = v[i];
        }
        return array;
    }

    @Override
    public boolean containsValue(double val) {
        byte[] states = this._states;
        double[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (states[i] != 1 || val != vals[i]) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(byte key) {
        return this.contains(key);
    }

    @Override
    public TByteDoubleIterator iterator() {
        return new TByteDoubleHashIterator(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        byte[] states = this._states;
        double[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TByteDoubleProcedure procedure) {
        byte[] states = this._states;
        byte[] keys = this._set;
        double[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        byte[] states = this._states;
        double[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute(values[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TByteDoubleProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        byte[] keys = this._set;
        double[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
                this.removeAt(i);
                modified = true;
            }
        }
        finally {
            this.reenableAutoCompaction(true);
        }
        return modified;
    }

    @Override
    public boolean increment(byte key) {
        return this.adjustValue(key, 1.0);
    }

    @Override
    public boolean adjustValue(byte key, double amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        double[] arrd = this._values;
        int n = index;
        arrd[n] = arrd[n] + amount;
        return true;
    }

    @Override
    public double adjustOrPutValue(byte key, double adjust_amount, double put_amount) {
        double newValue;
        boolean isNewMapping;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            double[] arrd = this._values;
            int n = index;
            double d = arrd[n] + adjust_amount;
            arrd[n] = d;
            newValue = d;
            isNewMapping = false;
        } else {
            newValue = this._values[index] = put_amount;
            isNewMapping = true;
        }
        byte previousState = this._states[index];
        if (isNewMapping) {
            this.postInsertHook(this.consumeFreeSlot);
        }
        return newValue;
    }

    public boolean equals(Object other) {
        if (!(other instanceof TByteDoubleMap)) {
            return false;
        }
        TByteDoubleMap that = (TByteDoubleMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        double[] values = this._values;
        byte[] states = this._states;
        double this_no_entry_value = this.getNoEntryValue();
        double that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            double this_value;
            byte key;
            double that_value;
            if (states[i] != 1 || (this_value = values[i]) == (that_value = that.get(key = this._set[i])) || this_value == this_no_entry_value || that_value == that_no_entry_value) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hashcode = 0;
        byte[] states = this._states;
        int i = this._values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            hashcode += HashFunctions.hash(this._set[i]) ^ HashFunctions.hash(this._values[i]);
        }
        return hashcode;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TByteDoubleProcedure(){
            private boolean first = true;

            public boolean execute(byte key, double value) {
                if (this.first) {
                    this.first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(key);
                buf.append("=");
                buf.append(value);
                return true;
            }
        });
        buf.append("}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        super.writeExternal(out);
        out.writeInt(this._size);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeByte(this._set[i]);
            out.writeDouble(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            byte key = in.readByte();
            double val = in.readDouble();
            this.put(key, val);
        }
    }

    class TByteDoubleHashIterator
    extends THashPrimitiveIterator
    implements TByteDoubleIterator {
        TByteDoubleHashIterator(TByteDoubleHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public byte key() {
            return TByteDoubleHashMap.this._set[this._index];
        }

        public double value() {
            return TByteDoubleHashMap.this._values[this._index];
        }

        public double setValue(double val) {
            double old = this.value();
            TByteDoubleHashMap.this._values[this._index] = val;
            return old;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            }
            try {
                this._hash.tempDisableAutoCompaction();
                TByteDoubleHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TByteDoubleValueHashIterator
    extends THashPrimitiveIterator
    implements TDoubleIterator {
        TByteDoubleValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public double next() {
            this.moveToNextIndex();
            return TByteDoubleHashMap.this._values[this._index];
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            }
            try {
                this._hash.tempDisableAutoCompaction();
                TByteDoubleHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TByteDoubleKeyHashIterator
    extends THashPrimitiveIterator
    implements TByteIterator {
        TByteDoubleKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public byte next() {
            this.moveToNextIndex();
            return TByteDoubleHashMap.this._set[this._index];
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void remove() {
            if (this._expectedSize != this._hash.size()) {
                throw new ConcurrentModificationException();
            }
            try {
                this._hash.tempDisableAutoCompaction();
                TByteDoubleHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class TValueView
    implements TDoubleCollection {
        protected TValueView() {
        }

        @Override
        public TDoubleIterator iterator() {
            return new TByteDoubleValueHashIterator(TByteDoubleHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TByteDoubleHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TByteDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(double entry) {
            return TByteDoubleHashMap.this.containsValue(entry);
        }

        @Override
        public double[] toArray() {
            return TByteDoubleHashMap.this.values();
        }

        @Override
        public double[] toArray(double[] dest) {
            return TByteDoubleHashMap.this.values(dest);
        }

        @Override
        public boolean add(double entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double entry) {
            double[] values = TByteDoubleHashMap.this._values;
            byte[] set = TByteDoubleHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == 0 || set[i] == 2 || entry != values[i]) continue;
                TByteDoubleHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Double) {
                    double ele = (Double)element;
                    if (TByteDoubleHashMap.this.containsValue(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TDoubleCollection collection) {
            TDoubleIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TByteDoubleHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(double[] array) {
            for (double element : array) {
                if (TByteDoubleHashMap.this.containsValue(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Double> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TDoubleCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(double[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TDoubleIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TDoubleCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TDoubleIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(double[] array) {
            boolean changed = false;
            Arrays.sort(array);
            double[] values = TByteDoubleHashMap.this._values;
            byte[] states = TByteDoubleHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TByteDoubleHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                double c;
                if (!(element instanceof Double) || !this.remove(c = ((Double)element).doubleValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TDoubleCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TDoubleIterator iter = collection.iterator();
            while (iter.hasNext()) {
                double element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(double[] array) {
            boolean changed = false;
            int i = array.length;
            while (i-- > 0) {
                if (!this.remove(array[i])) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public void clear() {
            TByteDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure procedure) {
            return TByteDoubleHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TByteDoubleHashMap.this.forEachValue(new TDoubleProcedure(){
                private boolean first = true;

                public boolean execute(double value) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        buf.append(", ");
                    }
                    buf.append(value);
                    return true;
                }
            });
            buf.append("}");
            return buf.toString();
        }

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class TKeyView
    implements TByteSet {
        protected TKeyView() {
        }

        @Override
        public TByteIterator iterator() {
            return new TByteDoubleKeyHashIterator(TByteDoubleHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteDoubleHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteDoubleHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteDoubleHashMap.this._size;
        }

        @Override
        public boolean contains(byte entry) {
            return TByteDoubleHashMap.this.contains(entry);
        }

        @Override
        public byte[] toArray() {
            return TByteDoubleHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] dest) {
            return TByteDoubleHashMap.this.keys(dest);
        }

        @Override
        public boolean add(byte entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte entry) {
            return TByteDoubleHashMap.this.no_entry_value != TByteDoubleHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Byte) {
                    byte ele = (Byte)element;
                    if (TByteDoubleHashMap.this.containsKey(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TByteCollection collection) {
            TByteIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TByteDoubleHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(byte[] array) {
            for (byte element : array) {
                if (TByteDoubleHashMap.this.contains(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Byte> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TByteCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(byte[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TByteIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TByteCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TByteIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(byte[] array) {
            boolean changed = false;
            Arrays.sort(array);
            byte[] set = TByteDoubleHashMap.this._set;
            byte[] states = TByteDoubleHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TByteDoubleHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                byte c;
                if (!(element instanceof Byte) || !this.remove(c = ((Byte)element).byteValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TByteCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TByteIterator iter = collection.iterator();
            while (iter.hasNext()) {
                byte element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(byte[] array) {
            boolean changed = false;
            int i = array.length;
            while (i-- > 0) {
                if (!this.remove(array[i])) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public void clear() {
            TByteDoubleHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure procedure) {
            return TByteDoubleHashMap.this.forEachKey(procedure);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TByteSet)) {
                return false;
            }
            TByteSet that = (TByteSet)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = TByteDoubleHashMap.this._states.length;
            while (i-- > 0) {
                if (TByteDoubleHashMap.this._states[i] != 1 || that.contains(TByteDoubleHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TByteDoubleHashMap.this._states.length;
            while (i-- > 0) {
                if (TByteDoubleHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TByteDoubleHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TByteDoubleHashMap.this.forEachKey(new TByteProcedure(){
                private boolean first = true;

                public boolean execute(byte key) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        buf.append(", ");
                    }
                    buf.append(key);
                    return true;
                }
            });
            buf.append("}");
            return buf.toString();
        }

    }

}


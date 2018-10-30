/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
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
public class TDoubleByteHashMap
extends TDoubleByteHash
implements TDoubleByteMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient byte[] _values;

    public TDoubleByteHashMap() {
    }

    public TDoubleByteHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TDoubleByteHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TDoubleByteHashMap(int initialCapacity, float loadFactor, double noEntryKey, byte noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TDoubleByteHashMap(double[] keys, byte[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TDoubleByteHashMap(TDoubleByteMap map) {
        super(map.size());
        if (map instanceof TDoubleByteHashMap) {
            TDoubleByteHashMap hashmap = (TDoubleByteHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0.0) {
                Arrays.fill(this._set, this.no_entry_key);
            }
            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.putAll(map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new byte[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        double[] oldKeys = this._set;
        byte[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new double[newCapacity];
        this._values = new byte[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            double o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public byte put(double key, byte value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public byte putIfAbsent(double key, byte value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private byte doPut(double key, byte value, int index) {
        byte previous = this.no_entry_value;
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
    public void putAll(Map<? extends Double, ? extends Byte> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Double, ? extends Byte> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TDoubleByteMap map) {
        this.ensureCapacity(map.size());
        TDoubleByteIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public byte get(double key) {
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
    public byte remove(double key) {
        byte prev = this.no_entry_value;
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
    public TDoubleSet keySet() {
        return new TKeyView();
    }

    @Override
    public double[] keys() {
        double[] keys = new double[this.size()];
        double[] k = this._set;
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
    public double[] keys(double[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new double[size];
        }
        double[] keys = this._set;
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
    public TByteCollection valueCollection() {
        return new TValueView();
    }

    @Override
    public byte[] values() {
        byte[] vals = new byte[this.size()];
        byte[] v = this._values;
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
    public byte[] values(byte[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new byte[size];
        }
        byte[] v = this._values;
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
    public boolean containsValue(byte val) {
        byte[] states = this._states;
        byte[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (states[i] != 1 || val != vals[i]) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(double key) {
        return this.contains(key);
    }

    @Override
    public TDoubleByteIterator iterator() {
        return new TDoubleByteHashIterator(this);
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure procedure) {
        byte[] states = this._states;
        byte[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TDoubleByteProcedure procedure) {
        byte[] states = this._states;
        double[] keys = this._set;
        byte[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void transformValues(TByteFunction function) {
        byte[] states = this._states;
        byte[] values = this._values;
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
    public boolean retainEntries(TDoubleByteProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        double[] keys = this._set;
        byte[] values = this._values;
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
    public boolean increment(double key) {
        return this.adjustValue(key, (byte)1);
    }

    @Override
    public boolean adjustValue(double key, byte amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        byte[] arrby = this._values;
        int n = index;
        arrby[n] = (byte)(arrby[n] + amount);
        return true;
    }

    @Override
    public byte adjustOrPutValue(double key, byte adjust_amount, byte put_amount) {
        byte newValue;
        boolean isNewMapping;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            byte[] arrby = this._values;
            int n = index;
            byte by = (byte)(arrby[n] + adjust_amount);
            arrby[n] = by;
            newValue = by;
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
        if (!(other instanceof TDoubleByteMap)) {
            return false;
        }
        TDoubleByteMap that = (TDoubleByteMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        byte[] values = this._values;
        byte[] states = this._states;
        byte this_no_entry_value = this.getNoEntryValue();
        byte that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            byte that_value;
            byte this_value;
            double key;
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
        this.forEachEntry(new TDoubleByteProcedure(){
            private boolean first = true;

            public boolean execute(double key, byte value) {
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
            out.writeDouble(this._set[i]);
            out.writeByte(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            double key = in.readDouble();
            byte val = in.readByte();
            this.put(key, val);
        }
    }

    class TDoubleByteHashIterator
    extends THashPrimitiveIterator
    implements TDoubleByteIterator {
        TDoubleByteHashIterator(TDoubleByteHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public double key() {
            return TDoubleByteHashMap.this._set[this._index];
        }

        public byte value() {
            return TDoubleByteHashMap.this._values[this._index];
        }

        public byte setValue(byte val) {
            byte old = this.value();
            TDoubleByteHashMap.this._values[this._index] = val;
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
                TDoubleByteHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TDoubleByteValueHashIterator
    extends THashPrimitiveIterator
    implements TByteIterator {
        TDoubleByteValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public byte next() {
            this.moveToNextIndex();
            return TDoubleByteHashMap.this._values[this._index];
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
                TDoubleByteHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TDoubleByteKeyHashIterator
    extends THashPrimitiveIterator
    implements TDoubleIterator {
        TDoubleByteKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public double next() {
            this.moveToNextIndex();
            return TDoubleByteHashMap.this._set[this._index];
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
                TDoubleByteHashMap.this.removeAt(this._index);
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
    implements TByteCollection {
        protected TValueView() {
        }

        @Override
        public TByteIterator iterator() {
            return new TDoubleByteValueHashIterator(TDoubleByteHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TDoubleByteHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TDoubleByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleByteHashMap.this._size;
        }

        @Override
        public boolean contains(byte entry) {
            return TDoubleByteHashMap.this.containsValue(entry);
        }

        @Override
        public byte[] toArray() {
            return TDoubleByteHashMap.this.values();
        }

        @Override
        public byte[] toArray(byte[] dest) {
            return TDoubleByteHashMap.this.values(dest);
        }

        @Override
        public boolean add(byte entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte entry) {
            byte[] values = TDoubleByteHashMap.this._values;
            double[] set = TDoubleByteHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == 0.0 || set[i] == 2.0 || entry != values[i]) continue;
                TDoubleByteHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Byte) {
                    byte ele = (Byte)element;
                    if (TDoubleByteHashMap.this.containsValue(ele)) continue;
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
                if (TDoubleByteHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(byte[] array) {
            for (byte element : array) {
                if (TDoubleByteHashMap.this.containsValue(element)) continue;
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
            byte[] values = TDoubleByteHashMap.this._values;
            byte[] states = TDoubleByteHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TDoubleByteHashMap.this.removeAt(i);
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
            TDoubleByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure procedure) {
            return TDoubleByteHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TDoubleByteHashMap.this.forEachValue(new TByteProcedure(){
                private boolean first = true;

                public boolean execute(byte value) {
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
    implements TDoubleSet {
        protected TKeyView() {
        }

        @Override
        public TDoubleIterator iterator() {
            return new TDoubleByteKeyHashIterator(TDoubleByteHashMap.this);
        }

        @Override
        public double getNoEntryValue() {
            return TDoubleByteHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TDoubleByteHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TDoubleByteHashMap.this._size;
        }

        @Override
        public boolean contains(double entry) {
            return TDoubleByteHashMap.this.contains(entry);
        }

        @Override
        public double[] toArray() {
            return TDoubleByteHashMap.this.keys();
        }

        @Override
        public double[] toArray(double[] dest) {
            return TDoubleByteHashMap.this.keys(dest);
        }

        @Override
        public boolean add(double entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double entry) {
            return TDoubleByteHashMap.this.no_entry_value != TDoubleByteHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Double) {
                    double ele = (Double)element;
                    if (TDoubleByteHashMap.this.containsKey(ele)) continue;
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
                if (TDoubleByteHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(double[] array) {
            for (double element : array) {
                if (TDoubleByteHashMap.this.contains(element)) continue;
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
            double[] set = TDoubleByteHashMap.this._set;
            byte[] states = TDoubleByteHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TDoubleByteHashMap.this.removeAt(i);
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
            TDoubleByteHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure procedure) {
            return TDoubleByteHashMap.this.forEachKey(procedure);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TDoubleSet)) {
                return false;
            }
            TDoubleSet that = (TDoubleSet)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = TDoubleByteHashMap.this._states.length;
            while (i-- > 0) {
                if (TDoubleByteHashMap.this._states[i] != 1 || that.contains(TDoubleByteHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TDoubleByteHashMap.this._states.length;
            while (i-- > 0) {
                if (TDoubleByteHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TDoubleByteHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TDoubleByteHashMap.this.forEachKey(new TDoubleProcedure(){
                private boolean first = true;

                public boolean execute(double key) {
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


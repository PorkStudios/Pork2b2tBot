/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortShortHash;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.procedure.TShortShortProcedure;
import gnu.trove.set.TShortSet;
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
public class TShortShortHashMap
extends TShortShortHash
implements TShortShortMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient short[] _values;

    public TShortShortHashMap() {
    }

    public TShortShortHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TShortShortHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TShortShortHashMap(int initialCapacity, float loadFactor, short noEntryKey, short noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TShortShortHashMap(short[] keys, short[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TShortShortHashMap(TShortShortMap map) {
        super(map.size());
        if (map instanceof TShortShortHashMap) {
            TShortShortHashMap hashmap = (TShortShortHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
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
        this._values = new short[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        short[] oldKeys = this._set;
        short[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new short[newCapacity];
        this._values = new short[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            short o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public short put(short key, short value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public short putIfAbsent(short key, short value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private short doPut(short key, short value, int index) {
        short previous = this.no_entry_value;
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
    public void putAll(Map<? extends Short, ? extends Short> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Short, ? extends Short> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TShortShortMap map) {
        this.ensureCapacity(map.size());
        TShortShortIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public short get(short key) {
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
    public short remove(short key) {
        short prev = this.no_entry_value;
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
    public TShortSet keySet() {
        return new TKeyView();
    }

    @Override
    public short[] keys() {
        short[] keys = new short[this.size()];
        short[] k = this._set;
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
    public short[] keys(short[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new short[size];
        }
        short[] keys = this._set;
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
    public TShortCollection valueCollection() {
        return new TValueView();
    }

    @Override
    public short[] values() {
        short[] vals = new short[this.size()];
        short[] v = this._values;
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
    public short[] values(short[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new short[size];
        }
        short[] v = this._values;
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
    public boolean containsValue(short val) {
        byte[] states = this._states;
        short[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (states[i] != 1 || val != vals[i]) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(short key) {
        return this.contains(key);
    }

    @Override
    public TShortShortIterator iterator() {
        return new TShortShortHashIterator(this);
    }

    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        byte[] states = this._states;
        short[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TShortShortProcedure procedure) {
        byte[] states = this._states;
        short[] keys = this._set;
        short[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void transformValues(TShortFunction function) {
        byte[] states = this._states;
        short[] values = this._values;
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
    public boolean retainEntries(TShortShortProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        short[] keys = this._set;
        short[] values = this._values;
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
    public boolean increment(short key) {
        return this.adjustValue(key, (short)1);
    }

    @Override
    public boolean adjustValue(short key, short amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        short[] arrs = this._values;
        int n = index;
        arrs[n] = (short)(arrs[n] + amount);
        return true;
    }

    @Override
    public short adjustOrPutValue(short key, short adjust_amount, short put_amount) {
        short newValue;
        boolean isNewMapping;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            short[] arrs = this._values;
            int n = index;
            short s = (short)(arrs[n] + adjust_amount);
            arrs[n] = s;
            newValue = s;
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
        if (!(other instanceof TShortShortMap)) {
            return false;
        }
        TShortShortMap that = (TShortShortMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        short[] values = this._values;
        byte[] states = this._states;
        short this_no_entry_value = this.getNoEntryValue();
        short that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            short that_value;
            short key;
            short this_value;
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
        this.forEachEntry(new TShortShortProcedure(){
            private boolean first = true;

            public boolean execute(short key, short value) {
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
            out.writeShort(this._set[i]);
            out.writeShort(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            short key = in.readShort();
            short val = in.readShort();
            this.put(key, val);
        }
    }

    class TShortShortHashIterator
    extends THashPrimitiveIterator
    implements TShortShortIterator {
        TShortShortHashIterator(TShortShortHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public short key() {
            return TShortShortHashMap.this._set[this._index];
        }

        public short value() {
            return TShortShortHashMap.this._values[this._index];
        }

        public short setValue(short val) {
            short old = this.value();
            TShortShortHashMap.this._values[this._index] = val;
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
                TShortShortHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TShortShortValueHashIterator
    extends THashPrimitiveIterator
    implements TShortIterator {
        TShortShortValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public short next() {
            this.moveToNextIndex();
            return TShortShortHashMap.this._values[this._index];
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
                TShortShortHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TShortShortKeyHashIterator
    extends THashPrimitiveIterator
    implements TShortIterator {
        TShortShortKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public short next() {
            this.moveToNextIndex();
            return TShortShortHashMap.this._set[this._index];
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
                TShortShortHashMap.this.removeAt(this._index);
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
    implements TShortCollection {
        protected TValueView() {
        }

        @Override
        public TShortIterator iterator() {
            return new TShortShortValueHashIterator(TShortShortHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TShortShortHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TShortShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortShortHashMap.this._size;
        }

        @Override
        public boolean contains(short entry) {
            return TShortShortHashMap.this.containsValue(entry);
        }

        @Override
        public short[] toArray() {
            return TShortShortHashMap.this.values();
        }

        @Override
        public short[] toArray(short[] dest) {
            return TShortShortHashMap.this.values(dest);
        }

        @Override
        public boolean add(short entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short entry) {
            short[] values = TShortShortHashMap.this._values;
            short[] set = TShortShortHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == 0 || set[i] == 2 || entry != values[i]) continue;
                TShortShortHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Short) {
                    short ele = (Short)element;
                    if (TShortShortHashMap.this.containsValue(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TShortCollection collection) {
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TShortShortHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(short[] array) {
            for (short element : array) {
                if (TShortShortHashMap.this.containsValue(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Short> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TShortCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(short[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TShortCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(short[] array) {
            boolean changed = false;
            Arrays.sort(array);
            short[] values = TShortShortHashMap.this._values;
            byte[] states = TShortShortHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TShortShortHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                short c;
                if (!(element instanceof Short) || !this.remove(c = ((Short)element).shortValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TShortCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                short element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(short[] array) {
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
            TShortShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure procedure) {
            return TShortShortHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TShortShortHashMap.this.forEachValue(new TShortProcedure(){
                private boolean first = true;

                public boolean execute(short value) {
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
    implements TShortSet {
        protected TKeyView() {
        }

        @Override
        public TShortIterator iterator() {
            return new TShortShortKeyHashIterator(TShortShortHashMap.this);
        }

        @Override
        public short getNoEntryValue() {
            return TShortShortHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TShortShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TShortShortHashMap.this._size;
        }

        @Override
        public boolean contains(short entry) {
            return TShortShortHashMap.this.contains(entry);
        }

        @Override
        public short[] toArray() {
            return TShortShortHashMap.this.keys();
        }

        @Override
        public short[] toArray(short[] dest) {
            return TShortShortHashMap.this.keys(dest);
        }

        @Override
        public boolean add(short entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short entry) {
            return TShortShortHashMap.this.no_entry_value != TShortShortHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Short) {
                    short ele = (Short)element;
                    if (TShortShortHashMap.this.containsKey(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TShortCollection collection) {
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TShortShortHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(short[] array) {
            for (short element : array) {
                if (TShortShortHashMap.this.contains(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Short> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TShortCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(short[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TShortCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(short[] array) {
            boolean changed = false;
            Arrays.sort(array);
            short[] set = TShortShortHashMap.this._set;
            byte[] states = TShortShortHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TShortShortHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                short c;
                if (!(element instanceof Short) || !this.remove(c = ((Short)element).shortValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TShortCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                short element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(short[] array) {
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
            TShortShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure procedure) {
            return TShortShortHashMap.this.forEachKey(procedure);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TShortSet)) {
                return false;
            }
            TShortSet that = (TShortSet)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = TShortShortHashMap.this._states.length;
            while (i-- > 0) {
                if (TShortShortHashMap.this._states[i] != 1 || that.contains(TShortShortHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TShortShortHashMap.this._states.length;
            while (i-- > 0) {
                if (TShortShortHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TShortShortHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TShortShortHashMap.this.forEachKey(new TShortProcedure(){
                private boolean first = true;

                public boolean execute(short key) {
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


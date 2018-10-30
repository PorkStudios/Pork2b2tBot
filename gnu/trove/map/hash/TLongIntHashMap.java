/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongIntHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
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
public class TLongIntHashMap
extends TLongIntHash
implements TLongIntMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TLongIntHashMap() {
    }

    public TLongIntHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TLongIntHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TLongIntHashMap(int initialCapacity, float loadFactor, long noEntryKey, int noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TLongIntHashMap(long[] keys, int[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TLongIntHashMap(TLongIntMap map) {
        super(map.size());
        if (map instanceof TLongIntHashMap) {
            TLongIntHashMap hashmap = (TLongIntHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
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
        this._values = new int[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        long[] oldKeys = this._set;
        int[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._values = new int[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            long o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public int put(long key, int value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public int putIfAbsent(long key, int value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private int doPut(long key, int value, int index) {
        int previous = this.no_entry_value;
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
    public void putAll(Map<? extends Long, ? extends Integer> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Long, ? extends Integer> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TLongIntMap map) {
        this.ensureCapacity(map.size());
        TLongIntIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public int get(long key) {
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
    public int remove(long key) {
        int prev = this.no_entry_value;
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
    public TLongSet keySet() {
        return new TKeyView();
    }

    @Override
    public long[] keys() {
        long[] keys = new long[this.size()];
        long[] k = this._set;
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
    public long[] keys(long[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new long[size];
        }
        long[] keys = this._set;
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
    public TIntCollection valueCollection() {
        return new TValueView();
    }

    @Override
    public int[] values() {
        int[] vals = new int[this.size()];
        int[] v = this._values;
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
    public int[] values(int[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new int[size];
        }
        int[] v = this._values;
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
    public boolean containsValue(int val) {
        byte[] states = this._states;
        int[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (states[i] != 1 || val != vals[i]) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(long key) {
        return this.contains(key);
    }

    @Override
    public TLongIntIterator iterator() {
        return new TLongIntHashIterator(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        byte[] states = this._states;
        int[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TLongIntProcedure procedure) {
        byte[] states = this._states;
        long[] keys = this._set;
        int[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void transformValues(TIntFunction function) {
        byte[] states = this._states;
        int[] values = this._values;
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
    public boolean retainEntries(TLongIntProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        long[] keys = this._set;
        int[] values = this._values;
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
    public boolean increment(long key) {
        return this.adjustValue(key, 1);
    }

    @Override
    public boolean adjustValue(long key, int amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        int[] arrn = this._values;
        int n = index;
        arrn[n] = arrn[n] + amount;
        return true;
    }

    @Override
    public int adjustOrPutValue(long key, int adjust_amount, int put_amount) {
        boolean isNewMapping;
        int newValue;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            int[] arrn = this._values;
            int n = index;
            int n2 = arrn[n] + adjust_amount;
            arrn[n] = n2;
            newValue = n2;
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
        if (!(other instanceof TLongIntMap)) {
            return false;
        }
        TLongIntMap that = (TLongIntMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        int[] values = this._values;
        byte[] states = this._states;
        int this_no_entry_value = this.getNoEntryValue();
        int that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            int this_value;
            long key;
            int that_value;
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
        this.forEachEntry(new TLongIntProcedure(){
            private boolean first = true;

            public boolean execute(long key, int value) {
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
            out.writeLong(this._set[i]);
            out.writeInt(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            long key = in.readLong();
            int val = in.readInt();
            this.put(key, val);
        }
    }

    class TLongIntHashIterator
    extends THashPrimitiveIterator
    implements TLongIntIterator {
        TLongIntHashIterator(TLongIntHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public long key() {
            return TLongIntHashMap.this._set[this._index];
        }

        public int value() {
            return TLongIntHashMap.this._values[this._index];
        }

        public int setValue(int val) {
            int old = this.value();
            TLongIntHashMap.this._values[this._index] = val;
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
                TLongIntHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TLongIntValueHashIterator
    extends THashPrimitiveIterator
    implements TIntIterator {
        TLongIntValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public int next() {
            this.moveToNextIndex();
            return TLongIntHashMap.this._values[this._index];
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
                TLongIntHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TLongIntKeyHashIterator
    extends THashPrimitiveIterator
    implements TLongIterator {
        TLongIntKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public long next() {
            this.moveToNextIndex();
            return TLongIntHashMap.this._set[this._index];
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
                TLongIntHashMap.this.removeAt(this._index);
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
    implements TIntCollection {
        protected TValueView() {
        }

        @Override
        public TIntIterator iterator() {
            return new TLongIntValueHashIterator(TLongIntHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TLongIntHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TLongIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongIntHashMap.this._size;
        }

        @Override
        public boolean contains(int entry) {
            return TLongIntHashMap.this.containsValue(entry);
        }

        @Override
        public int[] toArray() {
            return TLongIntHashMap.this.values();
        }

        @Override
        public int[] toArray(int[] dest) {
            return TLongIntHashMap.this.values(dest);
        }

        @Override
        public boolean add(int entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int entry) {
            int[] values = TLongIntHashMap.this._values;
            long[] set = TLongIntHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == 0L || set[i] == 2L || entry != values[i]) continue;
                TLongIntHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Integer) {
                    int ele = (Integer)element;
                    if (TLongIntHashMap.this.containsValue(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TIntCollection collection) {
            TIntIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TLongIntHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(int[] array) {
            for (int element : array) {
                if (TLongIntHashMap.this.containsValue(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Integer> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TIntCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TIntIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TIntCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TIntIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(int[] array) {
            boolean changed = false;
            Arrays.sort(array);
            int[] values = TLongIntHashMap.this._values;
            byte[] states = TLongIntHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TLongIntHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                int c;
                if (!(element instanceof Integer) || !this.remove(c = ((Integer)element).intValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TIntCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TIntIterator iter = collection.iterator();
            while (iter.hasNext()) {
                int element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(int[] array) {
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
            TLongIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure procedure) {
            return TLongIntHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TLongIntHashMap.this.forEachValue(new TIntProcedure(){
                private boolean first = true;

                public boolean execute(int value) {
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
    implements TLongSet {
        protected TKeyView() {
        }

        @Override
        public TLongIterator iterator() {
            return new TLongIntKeyHashIterator(TLongIntHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongIntHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongIntHashMap.this._size;
        }

        @Override
        public boolean contains(long entry) {
            return TLongIntHashMap.this.contains(entry);
        }

        @Override
        public long[] toArray() {
            return TLongIntHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] dest) {
            return TLongIntHashMap.this.keys(dest);
        }

        @Override
        public boolean add(long entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long entry) {
            return TLongIntHashMap.this.no_entry_value != TLongIntHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Long) {
                    long ele = (Long)element;
                    if (TLongIntHashMap.this.containsKey(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TLongCollection collection) {
            TLongIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TLongIntHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(long[] array) {
            for (long element : array) {
                if (TLongIntHashMap.this.contains(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Long> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TLongCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TLongIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TLongCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TLongIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(long[] array) {
            boolean changed = false;
            Arrays.sort(array);
            long[] set = TLongIntHashMap.this._set;
            byte[] states = TLongIntHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TLongIntHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                long c;
                if (!(element instanceof Long) || !this.remove(c = ((Long)element).longValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TLongCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TLongIterator iter = collection.iterator();
            while (iter.hasNext()) {
                long element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(long[] array) {
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
            TLongIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure procedure) {
            return TLongIntHashMap.this.forEachKey(procedure);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TLongSet)) {
                return false;
            }
            TLongSet that = (TLongSet)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = TLongIntHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongIntHashMap.this._states[i] != 1 || that.contains(TLongIntHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TLongIntHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongIntHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TLongIntHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TLongIntHashMap.this.forEachKey(new TLongProcedure(){
                private boolean first = true;

                public boolean execute(long key) {
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


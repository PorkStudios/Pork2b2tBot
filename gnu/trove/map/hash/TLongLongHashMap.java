/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongLongHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
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
public class TLongLongHashMap
extends TLongLongHash
implements TLongLongMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient long[] _values;

    public TLongLongHashMap() {
    }

    public TLongLongHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TLongLongHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TLongLongHashMap(int initialCapacity, float loadFactor, long noEntryKey, long noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TLongLongHashMap(long[] keys, long[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TLongLongHashMap(TLongLongMap map) {
        super(map.size());
        if (map instanceof TLongLongHashMap) {
            TLongLongHashMap hashmap = (TLongLongHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
                Arrays.fill(this._set, this.no_entry_key);
            }
            if (this.no_entry_value != 0L) {
                Arrays.fill(this._values, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.putAll(map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new long[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        long[] oldKeys = this._set;
        long[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._values = new long[newCapacity];
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
    public long put(long key, long value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public long putIfAbsent(long key, long value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private long doPut(long key, long value, int index) {
        long previous = this.no_entry_value;
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
    public void putAll(Map<? extends Long, ? extends Long> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Long, ? extends Long> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TLongLongMap map) {
        this.ensureCapacity(map.size());
        TLongLongIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public long get(long key) {
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
    public long remove(long key) {
        long prev = this.no_entry_value;
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
    public TLongCollection valueCollection() {
        return new TValueView();
    }

    @Override
    public long[] values() {
        long[] vals = new long[this.size()];
        long[] v = this._values;
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
    public long[] values(long[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new long[size];
        }
        long[] v = this._values;
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
    public boolean containsValue(long val) {
        byte[] states = this._states;
        long[] vals = this._values;
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
    public TLongLongIterator iterator() {
        return new TLongLongHashIterator(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure procedure) {
        byte[] states = this._states;
        long[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TLongLongProcedure procedure) {
        byte[] states = this._states;
        long[] keys = this._set;
        long[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void transformValues(TLongFunction function) {
        byte[] states = this._states;
        long[] values = this._values;
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
    public boolean retainEntries(TLongLongProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        long[] keys = this._set;
        long[] values = this._values;
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
        return this.adjustValue(key, 1L);
    }

    @Override
    public boolean adjustValue(long key, long amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        long[] arrl = this._values;
        int n = index;
        arrl[n] = arrl[n] + amount;
        return true;
    }

    @Override
    public long adjustOrPutValue(long key, long adjust_amount, long put_amount) {
        long newValue;
        boolean isNewMapping;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            long[] arrl = this._values;
            int n = index;
            long l = arrl[n] + adjust_amount;
            arrl[n] = l;
            newValue = l;
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
        if (!(other instanceof TLongLongMap)) {
            return false;
        }
        TLongLongMap that = (TLongLongMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        long[] values = this._values;
        byte[] states = this._states;
        long this_no_entry_value = this.getNoEntryValue();
        long that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            long key;
            long this_value;
            long that_value;
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
        this.forEachEntry(new TLongLongProcedure(){
            private boolean first = true;

            public boolean execute(long key, long value) {
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
            out.writeLong(this._values[i]);
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
            long val = in.readLong();
            this.put(key, val);
        }
    }

    class TLongLongHashIterator
    extends THashPrimitiveIterator
    implements TLongLongIterator {
        TLongLongHashIterator(TLongLongHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public long key() {
            return TLongLongHashMap.this._set[this._index];
        }

        public long value() {
            return TLongLongHashMap.this._values[this._index];
        }

        public long setValue(long val) {
            long old = this.value();
            TLongLongHashMap.this._values[this._index] = val;
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
                TLongLongHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TLongLongValueHashIterator
    extends THashPrimitiveIterator
    implements TLongIterator {
        TLongLongValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public long next() {
            this.moveToNextIndex();
            return TLongLongHashMap.this._values[this._index];
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
                TLongLongHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TLongLongKeyHashIterator
    extends THashPrimitiveIterator
    implements TLongIterator {
        TLongLongKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public long next() {
            this.moveToNextIndex();
            return TLongLongHashMap.this._set[this._index];
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
                TLongLongHashMap.this.removeAt(this._index);
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
    implements TLongCollection {
        protected TValueView() {
        }

        @Override
        public TLongIterator iterator() {
            return new TLongLongValueHashIterator(TLongLongHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongLongHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TLongLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongLongHashMap.this._size;
        }

        @Override
        public boolean contains(long entry) {
            return TLongLongHashMap.this.containsValue(entry);
        }

        @Override
        public long[] toArray() {
            return TLongLongHashMap.this.values();
        }

        @Override
        public long[] toArray(long[] dest) {
            return TLongLongHashMap.this.values(dest);
        }

        @Override
        public boolean add(long entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long entry) {
            long[] values = TLongLongHashMap.this._values;
            long[] set = TLongLongHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == 0L || set[i] == 2L || entry != values[i]) continue;
                TLongLongHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Long) {
                    long ele = (Long)element;
                    if (TLongLongHashMap.this.containsValue(ele)) continue;
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
                if (TLongLongHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(long[] array) {
            for (long element : array) {
                if (TLongLongHashMap.this.containsValue(element)) continue;
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
            long[] values = TLongLongHashMap.this._values;
            byte[] states = TLongLongHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TLongLongHashMap.this.removeAt(i);
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
            TLongLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure procedure) {
            return TLongLongHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TLongLongHashMap.this.forEachValue(new TLongProcedure(){
                private boolean first = true;

                public boolean execute(long value) {
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
            return new TLongLongKeyHashIterator(TLongLongHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongLongHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongLongHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongLongHashMap.this._size;
        }

        @Override
        public boolean contains(long entry) {
            return TLongLongHashMap.this.contains(entry);
        }

        @Override
        public long[] toArray() {
            return TLongLongHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] dest) {
            return TLongLongHashMap.this.keys(dest);
        }

        @Override
        public boolean add(long entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long entry) {
            return TLongLongHashMap.this.no_entry_value != TLongLongHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Long) {
                    long ele = (Long)element;
                    if (TLongLongHashMap.this.containsKey(ele)) continue;
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
                if (TLongLongHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(long[] array) {
            for (long element : array) {
                if (TLongLongHashMap.this.contains(element)) continue;
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
            long[] set = TLongLongHashMap.this._set;
            byte[] states = TLongLongHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TLongLongHashMap.this.removeAt(i);
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
            TLongLongHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure procedure) {
            return TLongLongHashMap.this.forEachKey(procedure);
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
            int i = TLongLongHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongLongHashMap.this._states[i] != 1 || that.contains(TLongLongHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TLongLongHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongLongHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TLongLongHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TLongLongHashMap.this.forEachKey(new TLongProcedure(){
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


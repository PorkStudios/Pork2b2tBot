/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongFloatHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongFloatProcedure;
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
public class TLongFloatHashMap
extends TLongFloatHash
implements TLongFloatMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TLongFloatHashMap() {
    }

    public TLongFloatHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TLongFloatHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TLongFloatHashMap(int initialCapacity, float loadFactor, long noEntryKey, float noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TLongFloatHashMap(long[] keys, float[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TLongFloatHashMap(TLongFloatMap map) {
        super(map.size());
        if (map instanceof TLongFloatHashMap) {
            TLongFloatHashMap hashmap = (TLongFloatHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0L) {
                Arrays.fill(this._set, this.no_entry_key);
            }
            if (this.no_entry_value != 0.0f) {
                Arrays.fill(this._values, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.putAll(map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new float[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        long[] oldKeys = this._set;
        float[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._values = new float[newCapacity];
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
    public float put(long key, float value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public float putIfAbsent(long key, float value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private float doPut(long key, float value, int index) {
        float previous = this.no_entry_value;
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
    public void putAll(Map<? extends Long, ? extends Float> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Long, ? extends Float> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue().floatValue());
        }
    }

    @Override
    public void putAll(TLongFloatMap map) {
        this.ensureCapacity(map.size());
        TLongFloatIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public float get(long key) {
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
    public float remove(long key) {
        float prev = this.no_entry_value;
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
    public TFloatCollection valueCollection() {
        return new TValueView();
    }

    @Override
    public float[] values() {
        float[] vals = new float[this.size()];
        float[] v = this._values;
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
    public float[] values(float[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new float[size];
        }
        float[] v = this._values;
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
    public boolean containsValue(float val) {
        byte[] states = this._states;
        float[] vals = this._values;
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
    public TLongFloatIterator iterator() {
        return new TLongFloatHashIterator(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        byte[] states = this._states;
        float[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TLongFloatProcedure procedure) {
        byte[] states = this._states;
        long[] keys = this._set;
        float[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void transformValues(TFloatFunction function) {
        byte[] states = this._states;
        float[] values = this._values;
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
    public boolean retainEntries(TLongFloatProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        long[] keys = this._set;
        float[] values = this._values;
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
        return this.adjustValue(key, 1.0f);
    }

    @Override
    public boolean adjustValue(long key, float amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        float[] arrf = this._values;
        int n = index;
        arrf[n] = arrf[n] + amount;
        return true;
    }

    @Override
    public float adjustOrPutValue(long key, float adjust_amount, float put_amount) {
        boolean isNewMapping;
        float newValue;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            float[] arrf = this._values;
            int n = index;
            float f = arrf[n] + adjust_amount;
            arrf[n] = f;
            newValue = f;
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
        if (!(other instanceof TLongFloatMap)) {
            return false;
        }
        TLongFloatMap that = (TLongFloatMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        float[] values = this._values;
        byte[] states = this._states;
        float this_no_entry_value = this.getNoEntryValue();
        float that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            float this_value;
            float that_value;
            long key;
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
        this.forEachEntry(new TLongFloatProcedure(){
            private boolean first = true;

            public boolean execute(long key, float value) {
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
            out.writeFloat(this._values[i]);
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
            float val = in.readFloat();
            this.put(key, val);
        }
    }

    class TLongFloatHashIterator
    extends THashPrimitiveIterator
    implements TLongFloatIterator {
        TLongFloatHashIterator(TLongFloatHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public long key() {
            return TLongFloatHashMap.this._set[this._index];
        }

        public float value() {
            return TLongFloatHashMap.this._values[this._index];
        }

        public float setValue(float val) {
            float old = this.value();
            TLongFloatHashMap.this._values[this._index] = val;
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
                TLongFloatHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TLongFloatValueHashIterator
    extends THashPrimitiveIterator
    implements TFloatIterator {
        TLongFloatValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public float next() {
            this.moveToNextIndex();
            return TLongFloatHashMap.this._values[this._index];
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
                TLongFloatHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TLongFloatKeyHashIterator
    extends THashPrimitiveIterator
    implements TLongIterator {
        TLongFloatKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public long next() {
            this.moveToNextIndex();
            return TLongFloatHashMap.this._set[this._index];
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
                TLongFloatHashMap.this.removeAt(this._index);
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
    implements TFloatCollection {
        protected TValueView() {
        }

        @Override
        public TFloatIterator iterator() {
            return new TLongFloatValueHashIterator(TLongFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TLongFloatHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TLongFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float entry) {
            return TLongFloatHashMap.this.containsValue(entry);
        }

        @Override
        public float[] toArray() {
            return TLongFloatHashMap.this.values();
        }

        @Override
        public float[] toArray(float[] dest) {
            return TLongFloatHashMap.this.values(dest);
        }

        @Override
        public boolean add(float entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float entry) {
            float[] values = TLongFloatHashMap.this._values;
            long[] set = TLongFloatHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == 0L || set[i] == 2L || entry != values[i]) continue;
                TLongFloatHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Float) {
                    float ele = ((Float)element).floatValue();
                    if (TLongFloatHashMap.this.containsValue(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TFloatCollection collection) {
            TFloatIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TLongFloatHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(float[] array) {
            for (float element : array) {
                if (TLongFloatHashMap.this.containsValue(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Float> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TFloatCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(float[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TFloatIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(Float.valueOf(iter.next()))) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TFloatCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TFloatIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(float[] array) {
            boolean changed = false;
            Arrays.sort(array);
            float[] values = TLongFloatHashMap.this._values;
            byte[] states = TLongFloatHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TLongFloatHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                float c;
                if (!(element instanceof Float) || !this.remove(c = ((Float)element).floatValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TFloatCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TFloatIterator iter = collection.iterator();
            while (iter.hasNext()) {
                float element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(float[] array) {
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
            TLongFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure procedure) {
            return TLongFloatHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TLongFloatHashMap.this.forEachValue(new TFloatProcedure(){
                private boolean first = true;

                public boolean execute(float value) {
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
            return new TLongFloatKeyHashIterator(TLongFloatHashMap.this);
        }

        @Override
        public long getNoEntryValue() {
            return TLongFloatHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TLongFloatHashMap.this._size;
        }

        @Override
        public boolean contains(long entry) {
            return TLongFloatHashMap.this.contains(entry);
        }

        @Override
        public long[] toArray() {
            return TLongFloatHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] dest) {
            return TLongFloatHashMap.this.keys(dest);
        }

        @Override
        public boolean add(long entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long entry) {
            return TLongFloatHashMap.this.no_entry_value != TLongFloatHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Long) {
                    long ele = (Long)element;
                    if (TLongFloatHashMap.this.containsKey(ele)) continue;
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
                if (TLongFloatHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(long[] array) {
            for (long element : array) {
                if (TLongFloatHashMap.this.contains(element)) continue;
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
            long[] set = TLongFloatHashMap.this._set;
            byte[] states = TLongFloatHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TLongFloatHashMap.this.removeAt(i);
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
            TLongFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure procedure) {
            return TLongFloatHashMap.this.forEachKey(procedure);
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
            int i = TLongFloatHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongFloatHashMap.this._states[i] != 1 || that.contains(TLongFloatHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TLongFloatHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongFloatHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TLongFloatHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TLongFloatHashMap.this.forEachKey(new TLongProcedure(){
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


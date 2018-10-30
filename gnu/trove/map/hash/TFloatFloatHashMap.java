/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
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
public class TFloatFloatHashMap
extends TFloatFloatHash
implements TFloatFloatMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient float[] _values;

    public TFloatFloatHashMap() {
    }

    public TFloatFloatHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TFloatFloatHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TFloatFloatHashMap(int initialCapacity, float loadFactor, float noEntryKey, float noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TFloatFloatHashMap(float[] keys, float[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TFloatFloatHashMap(TFloatFloatMap map) {
        super(map.size());
        if (map instanceof TFloatFloatHashMap) {
            TFloatFloatHashMap hashmap = (TFloatFloatHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0.0f) {
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
        float[] oldKeys = this._set;
        float[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new float[newCapacity];
        this._values = new float[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            float o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public float put(float key, float value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public float putIfAbsent(float key, float value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private float doPut(float key, float value, int index) {
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
    public void putAll(Map<? extends Float, ? extends Float> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Float, ? extends Float> entry : map.entrySet()) {
            this.put(entry.getKey().floatValue(), entry.getValue().floatValue());
        }
    }

    @Override
    public void putAll(TFloatFloatMap map) {
        this.ensureCapacity(map.size());
        TFloatFloatIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public float get(float key) {
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
    public float remove(float key) {
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
    public TFloatSet keySet() {
        return new TKeyView();
    }

    @Override
    public float[] keys() {
        float[] keys = new float[this.size()];
        float[] k = this._set;
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
    public float[] keys(float[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new float[size];
        }
        float[] keys = this._set;
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
    public boolean containsKey(float key) {
        return this.contains(key);
    }

    @Override
    public TFloatFloatIterator iterator() {
        return new TFloatFloatHashIterator(this);
    }

    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
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
    public boolean forEachEntry(TFloatFloatProcedure procedure) {
        byte[] states = this._states;
        float[] keys = this._set;
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
    public boolean retainEntries(TFloatFloatProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        float[] keys = this._set;
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
    public boolean increment(float key) {
        return this.adjustValue(key, 1.0f);
    }

    @Override
    public boolean adjustValue(float key, float amount) {
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
    public float adjustOrPutValue(float key, float adjust_amount, float put_amount) {
        float newValue;
        boolean isNewMapping;
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
        if (!(other instanceof TFloatFloatMap)) {
            return false;
        }
        TFloatFloatMap that = (TFloatFloatMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        float[] values = this._values;
        byte[] states = this._states;
        float this_no_entry_value = this.getNoEntryValue();
        float that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            float that_value;
            float this_value;
            float key;
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
        this.forEachEntry(new TFloatFloatProcedure(){
            private boolean first = true;

            public boolean execute(float key, float value) {
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
            out.writeFloat(this._set[i]);
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
            float key = in.readFloat();
            float val = in.readFloat();
            this.put(key, val);
        }
    }

    class TFloatFloatHashIterator
    extends THashPrimitiveIterator
    implements TFloatFloatIterator {
        TFloatFloatHashIterator(TFloatFloatHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public float key() {
            return TFloatFloatHashMap.this._set[this._index];
        }

        public float value() {
            return TFloatFloatHashMap.this._values[this._index];
        }

        public float setValue(float val) {
            float old = this.value();
            TFloatFloatHashMap.this._values[this._index] = val;
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
                TFloatFloatHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TFloatFloatValueHashIterator
    extends THashPrimitiveIterator
    implements TFloatIterator {
        TFloatFloatValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public float next() {
            this.moveToNextIndex();
            return TFloatFloatHashMap.this._values[this._index];
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
                TFloatFloatHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TFloatFloatKeyHashIterator
    extends THashPrimitiveIterator
    implements TFloatIterator {
        TFloatFloatKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public float next() {
            this.moveToNextIndex();
            return TFloatFloatHashMap.this._set[this._index];
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
                TFloatFloatHashMap.this.removeAt(this._index);
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
            return new TFloatFloatValueHashIterator(TFloatFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TFloatFloatHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TFloatFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float entry) {
            return TFloatFloatHashMap.this.containsValue(entry);
        }

        @Override
        public float[] toArray() {
            return TFloatFloatHashMap.this.values();
        }

        @Override
        public float[] toArray(float[] dest) {
            return TFloatFloatHashMap.this.values(dest);
        }

        @Override
        public boolean add(float entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float entry) {
            float[] values = TFloatFloatHashMap.this._values;
            float[] set = TFloatFloatHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == 0.0f || set[i] == 2.0f || entry != values[i]) continue;
                TFloatFloatHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Float) {
                    float ele = ((Float)element).floatValue();
                    if (TFloatFloatHashMap.this.containsValue(ele)) continue;
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
                if (TFloatFloatHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(float[] array) {
            for (float element : array) {
                if (TFloatFloatHashMap.this.containsValue(element)) continue;
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
            float[] values = TFloatFloatHashMap.this._values;
            byte[] states = TFloatFloatHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TFloatFloatHashMap.this.removeAt(i);
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
            TFloatFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure procedure) {
            return TFloatFloatHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TFloatFloatHashMap.this.forEachValue(new TFloatProcedure(){
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
    implements TFloatSet {
        protected TKeyView() {
        }

        @Override
        public TFloatIterator iterator() {
            return new TFloatFloatKeyHashIterator(TFloatFloatHashMap.this);
        }

        @Override
        public float getNoEntryValue() {
            return TFloatFloatHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TFloatFloatHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TFloatFloatHashMap.this._size;
        }

        @Override
        public boolean contains(float entry) {
            return TFloatFloatHashMap.this.contains(entry);
        }

        @Override
        public float[] toArray() {
            return TFloatFloatHashMap.this.keys();
        }

        @Override
        public float[] toArray(float[] dest) {
            return TFloatFloatHashMap.this.keys(dest);
        }

        @Override
        public boolean add(float entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float entry) {
            return TFloatFloatHashMap.this.no_entry_value != TFloatFloatHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Float) {
                    float ele = ((Float)element).floatValue();
                    if (TFloatFloatHashMap.this.containsKey(ele)) continue;
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
                if (TFloatFloatHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(float[] array) {
            for (float element : array) {
                if (TFloatFloatHashMap.this.contains(element)) continue;
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
            float[] set = TFloatFloatHashMap.this._set;
            byte[] states = TFloatFloatHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TFloatFloatHashMap.this.removeAt(i);
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
            TFloatFloatHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure procedure) {
            return TFloatFloatHashMap.this.forEachKey(procedure);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TFloatSet)) {
                return false;
            }
            TFloatSet that = (TFloatSet)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = TFloatFloatHashMap.this._states.length;
            while (i-- > 0) {
                if (TFloatFloatHashMap.this._states[i] != 1 || that.contains(TFloatFloatHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TFloatFloatHashMap.this._states.length;
            while (i-- > 0) {
                if (TFloatFloatHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TFloatFloatHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TFloatFloatHashMap.this.forEachKey(new TFloatProcedure(){
                private boolean first = true;

                public boolean execute(float key) {
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


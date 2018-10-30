/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharIntHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TCharIntMap;
import gnu.trove.procedure.TCharIntProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TCharSet;
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
public class TCharIntHashMap
extends TCharIntHash
implements TCharIntMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient int[] _values;

    public TCharIntHashMap() {
    }

    public TCharIntHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TCharIntHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TCharIntHashMap(int initialCapacity, float loadFactor, char noEntryKey, int noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TCharIntHashMap(char[] keys, int[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TCharIntHashMap(TCharIntMap map) {
        super(map.size());
        if (map instanceof TCharIntHashMap) {
            TCharIntHashMap hashmap = (TCharIntHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != '\u0000') {
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
        char[] oldKeys = this._set;
        int[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new char[newCapacity];
        this._values = new int[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            char o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public int put(char key, int value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public int putIfAbsent(char key, int value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private int doPut(char key, int value, int index) {
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
    public void putAll(Map<? extends Character, ? extends Integer> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Character, ? extends Integer> entry : map.entrySet()) {
            this.put(entry.getKey().charValue(), entry.getValue());
        }
    }

    @Override
    public void putAll(TCharIntMap map) {
        this.ensureCapacity(map.size());
        TCharIntIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public int get(char key) {
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
    public int remove(char key) {
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
    public TCharSet keySet() {
        return new TKeyView();
    }

    @Override
    public char[] keys() {
        char[] keys = new char[this.size()];
        char[] k = this._set;
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
    public char[] keys(char[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new char[size];
        }
        char[] keys = this._set;
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
    public boolean containsKey(char key) {
        return this.contains(key);
    }

    @Override
    public TCharIntIterator iterator() {
        return new TCharIntHashIterator(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
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
    public boolean forEachEntry(TCharIntProcedure procedure) {
        byte[] states = this._states;
        char[] keys = this._set;
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
    public boolean retainEntries(TCharIntProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        char[] keys = this._set;
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
    public boolean increment(char key) {
        return this.adjustValue(key, 1);
    }

    @Override
    public boolean adjustValue(char key, int amount) {
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
    public int adjustOrPutValue(char key, int adjust_amount, int put_amount) {
        int newValue;
        boolean isNewMapping;
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
        if (!(other instanceof TCharIntMap)) {
            return false;
        }
        TCharIntMap that = (TCharIntMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        int[] values = this._values;
        byte[] states = this._states;
        int this_no_entry_value = this.getNoEntryValue();
        int that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            int that_value;
            char key;
            int this_value;
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
        this.forEachEntry(new TCharIntProcedure(){
            private boolean first = true;

            public boolean execute(char key, int value) {
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
            out.writeChar(this._set[i]);
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
            char key = in.readChar();
            int val = in.readInt();
            this.put(key, val);
        }
    }

    class TCharIntHashIterator
    extends THashPrimitiveIterator
    implements TCharIntIterator {
        TCharIntHashIterator(TCharIntHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public char key() {
            return TCharIntHashMap.this._set[this._index];
        }

        public int value() {
            return TCharIntHashMap.this._values[this._index];
        }

        public int setValue(int val) {
            int old = this.value();
            TCharIntHashMap.this._values[this._index] = val;
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
                TCharIntHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TCharIntValueHashIterator
    extends THashPrimitiveIterator
    implements TIntIterator {
        TCharIntValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public int next() {
            this.moveToNextIndex();
            return TCharIntHashMap.this._values[this._index];
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
                TCharIntHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TCharIntKeyHashIterator
    extends THashPrimitiveIterator
    implements TCharIterator {
        TCharIntKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public char next() {
            this.moveToNextIndex();
            return TCharIntHashMap.this._set[this._index];
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
                TCharIntHashMap.this.removeAt(this._index);
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
            return new TCharIntValueHashIterator(TCharIntHashMap.this);
        }

        @Override
        public int getNoEntryValue() {
            return TCharIntHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TCharIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharIntHashMap.this._size;
        }

        @Override
        public boolean contains(int entry) {
            return TCharIntHashMap.this.containsValue(entry);
        }

        @Override
        public int[] toArray() {
            return TCharIntHashMap.this.values();
        }

        @Override
        public int[] toArray(int[] dest) {
            return TCharIntHashMap.this.values(dest);
        }

        @Override
        public boolean add(int entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int entry) {
            int[] values = TCharIntHashMap.this._values;
            char[] set = TCharIntHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == '\u0000' || set[i] == '\u0002' || entry != values[i]) continue;
                TCharIntHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Integer) {
                    int ele = (Integer)element;
                    if (TCharIntHashMap.this.containsValue(ele)) continue;
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
                if (TCharIntHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(int[] array) {
            for (int element : array) {
                if (TCharIntHashMap.this.containsValue(element)) continue;
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
            int[] values = TCharIntHashMap.this._values;
            byte[] states = TCharIntHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TCharIntHashMap.this.removeAt(i);
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
            TCharIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TIntProcedure procedure) {
            return TCharIntHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TCharIntHashMap.this.forEachValue(new TIntProcedure(){
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
    implements TCharSet {
        protected TKeyView() {
        }

        @Override
        public TCharIterator iterator() {
            return new TCharIntKeyHashIterator(TCharIntHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharIntHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharIntHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharIntHashMap.this._size;
        }

        @Override
        public boolean contains(char entry) {
            return TCharIntHashMap.this.contains(entry);
        }

        @Override
        public char[] toArray() {
            return TCharIntHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] dest) {
            return TCharIntHashMap.this.keys(dest);
        }

        @Override
        public boolean add(char entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char entry) {
            return TCharIntHashMap.this.no_entry_value != TCharIntHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Character) {
                    char ele = ((Character)element).charValue();
                    if (TCharIntHashMap.this.containsKey(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TCharCollection collection) {
            TCharIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TCharIntHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(char[] array) {
            for (char element : array) {
                if (TCharIntHashMap.this.contains(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Character> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TCharCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(char[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TCharIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(Character.valueOf(iter.next()))) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TCharCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TCharIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(char[] array) {
            boolean changed = false;
            Arrays.sort(array);
            char[] set = TCharIntHashMap.this._set;
            byte[] states = TCharIntHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TCharIntHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                char c;
                if (!(element instanceof Character) || !this.remove(c = ((Character)element).charValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TCharCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TCharIterator iter = collection.iterator();
            while (iter.hasNext()) {
                char element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(char[] array) {
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
            TCharIntHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure procedure) {
            return TCharIntHashMap.this.forEachKey(procedure);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TCharSet)) {
                return false;
            }
            TCharSet that = (TCharSet)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = TCharIntHashMap.this._states.length;
            while (i-- > 0) {
                if (TCharIntHashMap.this._states[i] != 1 || that.contains(TCharIntHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TCharIntHashMap.this._states.length;
            while (i-- > 0) {
                if (TCharIntHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TCharIntHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TCharIntHashMap.this.forEachKey(new TCharProcedure(){
                private boolean first = true;

                public boolean execute(char key) {
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


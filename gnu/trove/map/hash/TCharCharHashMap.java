/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.map.TCharCharMap;
import gnu.trove.procedure.TCharCharProcedure;
import gnu.trove.procedure.TCharProcedure;
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
public class TCharCharHashMap
extends TCharCharHash
implements TCharCharMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TCharCharHashMap() {
    }

    public TCharCharHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TCharCharHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TCharCharHashMap(int initialCapacity, float loadFactor, char noEntryKey, char noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TCharCharHashMap(char[] keys, char[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TCharCharHashMap(TCharCharMap map) {
        super(map.size());
        if (map instanceof TCharCharHashMap) {
            TCharCharHashMap hashmap = (TCharCharHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != '\u0000') {
                Arrays.fill(this._set, this.no_entry_key);
            }
            if (this.no_entry_value != '\u0000') {
                Arrays.fill(this._values, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.putAll(map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new char[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        char[] oldKeys = this._set;
        char[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new char[newCapacity];
        this._values = new char[newCapacity];
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
    public char put(char key, char value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public char putIfAbsent(char key, char value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private char doPut(char key, char value, int index) {
        char previous = this.no_entry_value;
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
    public void putAll(Map<? extends Character, ? extends Character> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Character, ? extends Character> entry : map.entrySet()) {
            this.put(entry.getKey().charValue(), entry.getValue().charValue());
        }
    }

    @Override
    public void putAll(TCharCharMap map) {
        this.ensureCapacity(map.size());
        TCharCharIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public char get(char key) {
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
    public char remove(char key) {
        char prev = this.no_entry_value;
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
    public TCharCollection valueCollection() {
        return new TValueView();
    }

    @Override
    public char[] values() {
        char[] vals = new char[this.size()];
        char[] v = this._values;
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
    public char[] values(char[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new char[size];
        }
        char[] v = this._values;
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
    public boolean containsValue(char val) {
        byte[] states = this._states;
        char[] vals = this._values;
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
    public TCharCharIterator iterator() {
        return new TCharCharHashIterator(this);
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure procedure) {
        byte[] states = this._states;
        char[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TCharCharProcedure procedure) {
        byte[] states = this._states;
        char[] keys = this._set;
        char[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void transformValues(TCharFunction function) {
        byte[] states = this._states;
        char[] values = this._values;
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
    public boolean retainEntries(TCharCharProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        char[] keys = this._set;
        char[] values = this._values;
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
        return this.adjustValue(key, '\u0001');
    }

    @Override
    public boolean adjustValue(char key, char amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        char[] arrc = this._values;
        int n = index;
        arrc[n] = (char)(arrc[n] + amount);
        return true;
    }

    @Override
    public char adjustOrPutValue(char key, char adjust_amount, char put_amount) {
        char newValue;
        boolean isNewMapping;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            char[] arrc = this._values;
            int n = index;
            char c = (char)(arrc[n] + adjust_amount);
            arrc[n] = c;
            newValue = c;
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
        if (!(other instanceof TCharCharMap)) {
            return false;
        }
        TCharCharMap that = (TCharCharMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        char[] values = this._values;
        byte[] states = this._states;
        char this_no_entry_value = this.getNoEntryValue();
        char that_no_entry_value = that.getNoEntryValue();
        int i = values.length;
        while (i-- > 0) {
            char that_value;
            char key;
            char this_value;
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
        this.forEachEntry(new TCharCharProcedure(){
            private boolean first = true;

            public boolean execute(char key, char value) {
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
            out.writeChar(this._values[i]);
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
            char val = in.readChar();
            this.put(key, val);
        }
    }

    class TCharCharHashIterator
    extends THashPrimitiveIterator
    implements TCharCharIterator {
        TCharCharHashIterator(TCharCharHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public char key() {
            return TCharCharHashMap.this._set[this._index];
        }

        public char value() {
            return TCharCharHashMap.this._values[this._index];
        }

        public char setValue(char val) {
            char old = this.value();
            TCharCharHashMap.this._values[this._index] = val;
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
                TCharCharHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TCharCharValueHashIterator
    extends THashPrimitiveIterator
    implements TCharIterator {
        TCharCharValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public char next() {
            this.moveToNextIndex();
            return TCharCharHashMap.this._values[this._index];
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
                TCharCharHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TCharCharKeyHashIterator
    extends THashPrimitiveIterator
    implements TCharIterator {
        TCharCharKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public char next() {
            this.moveToNextIndex();
            return TCharCharHashMap.this._set[this._index];
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
                TCharCharHashMap.this.removeAt(this._index);
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
    implements TCharCollection {
        protected TValueView() {
        }

        @Override
        public TCharIterator iterator() {
            return new TCharCharValueHashIterator(TCharCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharCharHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TCharCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharCharHashMap.this._size;
        }

        @Override
        public boolean contains(char entry) {
            return TCharCharHashMap.this.containsValue(entry);
        }

        @Override
        public char[] toArray() {
            return TCharCharHashMap.this.values();
        }

        @Override
        public char[] toArray(char[] dest) {
            return TCharCharHashMap.this.values(dest);
        }

        @Override
        public boolean add(char entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char entry) {
            char[] values = TCharCharHashMap.this._values;
            char[] set = TCharCharHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == '\u0000' || set[i] == '\u0002' || entry != values[i]) continue;
                TCharCharHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Character) {
                    char ele = ((Character)element).charValue();
                    if (TCharCharHashMap.this.containsValue(ele)) continue;
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
                if (TCharCharHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(char[] array) {
            for (char element : array) {
                if (TCharCharHashMap.this.containsValue(element)) continue;
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
            char[] values = TCharCharHashMap.this._values;
            byte[] states = TCharCharHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TCharCharHashMap.this.removeAt(i);
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
            TCharCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure procedure) {
            return TCharCharHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TCharCharHashMap.this.forEachValue(new TCharProcedure(){
                private boolean first = true;

                public boolean execute(char value) {
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
            return new TCharCharKeyHashIterator(TCharCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TCharCharHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TCharCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TCharCharHashMap.this._size;
        }

        @Override
        public boolean contains(char entry) {
            return TCharCharHashMap.this.contains(entry);
        }

        @Override
        public char[] toArray() {
            return TCharCharHashMap.this.keys();
        }

        @Override
        public char[] toArray(char[] dest) {
            return TCharCharHashMap.this.keys(dest);
        }

        @Override
        public boolean add(char entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char entry) {
            return TCharCharHashMap.this.no_entry_value != TCharCharHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Character) {
                    char ele = ((Character)element).charValue();
                    if (TCharCharHashMap.this.containsKey(ele)) continue;
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
                if (TCharCharHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(char[] array) {
            for (char element : array) {
                if (TCharCharHashMap.this.contains(element)) continue;
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
            char[] set = TCharCharHashMap.this._set;
            byte[] states = TCharCharHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TCharCharHashMap.this.removeAt(i);
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
            TCharCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure procedure) {
            return TCharCharHashMap.this.forEachKey(procedure);
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
            int i = TCharCharHashMap.this._states.length;
            while (i-- > 0) {
                if (TCharCharHashMap.this._states[i] != 1 || that.contains(TCharCharHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TCharCharHashMap.this._states.length;
            while (i-- > 0) {
                if (TCharCharHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TCharCharHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TCharCharHashMap.this.forEachKey(new TCharProcedure(){
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


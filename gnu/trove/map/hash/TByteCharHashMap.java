/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.map.TByteCharMap;
import gnu.trove.procedure.TByteCharProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
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
public class TByteCharHashMap
extends TByteCharHash
implements TByteCharMap,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient char[] _values;

    public TByteCharHashMap() {
    }

    public TByteCharHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TByteCharHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TByteCharHashMap(int initialCapacity, float loadFactor, byte noEntryKey, char noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }

    public TByteCharHashMap(byte[] keys, char[] values) {
        super(Math.max(keys.length, values.length));
        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; ++i) {
            this.put(keys[i], values[i]);
        }
    }

    public TByteCharHashMap(TByteCharMap map) {
        super(map.size());
        if (map instanceof TByteCharHashMap) {
            TByteCharHashMap hashmap = (TByteCharHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != 0) {
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
        byte[] oldKeys = this._set;
        char[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new byte[newCapacity];
        this._values = new char[newCapacity];
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
    public char put(byte key, char value) {
        int index = this.insertKey(key);
        return this.doPut(key, value, index);
    }

    @Override
    public char putIfAbsent(byte key, char value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(key, value, index);
    }

    private char doPut(byte key, char value, int index) {
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
    public void putAll(Map<? extends Byte, ? extends Character> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<? extends Byte, ? extends Character> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue().charValue());
        }
    }

    @Override
    public void putAll(TByteCharMap map) {
        this.ensureCapacity(map.size());
        TByteCharIterator iter = map.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.put(iter.key(), iter.value());
        }
    }

    @Override
    public char get(byte key) {
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
    public char remove(byte key) {
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
    public boolean containsKey(byte key) {
        return this.contains(key);
    }

    @Override
    public TByteCharIterator iterator() {
        return new TByteCharHashIterator(this);
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
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
    public boolean forEachEntry(TByteCharProcedure procedure) {
        byte[] states = this._states;
        byte[] keys = this._set;
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
    public boolean retainEntries(TByteCharProcedure procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        byte[] keys = this._set;
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
    public boolean increment(byte key) {
        return this.adjustValue(key, '\u0001');
    }

    @Override
    public boolean adjustValue(byte key, char amount) {
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
    public char adjustOrPutValue(byte key, char adjust_amount, char put_amount) {
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
        if (!(other instanceof TByteCharMap)) {
            return false;
        }
        TByteCharMap that = (TByteCharMap)other;
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
            byte key;
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
        this.forEachEntry(new TByteCharProcedure(){
            private boolean first = true;

            public boolean execute(byte key, char value) {
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
            byte key = in.readByte();
            char val = in.readChar();
            this.put(key, val);
        }
    }

    class TByteCharHashIterator
    extends THashPrimitiveIterator
    implements TByteCharIterator {
        TByteCharHashIterator(TByteCharHashMap map) {
            super(map);
        }

        public void advance() {
            this.moveToNextIndex();
        }

        public byte key() {
            return TByteCharHashMap.this._set[this._index];
        }

        public char value() {
            return TByteCharHashMap.this._values[this._index];
        }

        public char setValue(char val) {
            char old = this.value();
            TByteCharHashMap.this._values[this._index] = val;
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
                TByteCharHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TByteCharValueHashIterator
    extends THashPrimitiveIterator
    implements TCharIterator {
        TByteCharValueHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public char next() {
            this.moveToNextIndex();
            return TByteCharHashMap.this._values[this._index];
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
                TByteCharHashMap.this.removeAt(this._index);
            }
            finally {
                this._hash.reenableAutoCompaction(false);
            }
            --this._expectedSize;
        }
    }

    class TByteCharKeyHashIterator
    extends THashPrimitiveIterator
    implements TByteIterator {
        TByteCharKeyHashIterator(TPrimitiveHash hash) {
            super(hash);
        }

        public byte next() {
            this.moveToNextIndex();
            return TByteCharHashMap.this._set[this._index];
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
                TByteCharHashMap.this.removeAt(this._index);
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
            return new TByteCharValueHashIterator(TByteCharHashMap.this);
        }

        @Override
        public char getNoEntryValue() {
            return TByteCharHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TByteCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteCharHashMap.this._size;
        }

        @Override
        public boolean contains(char entry) {
            return TByteCharHashMap.this.containsValue(entry);
        }

        @Override
        public char[] toArray() {
            return TByteCharHashMap.this.values();
        }

        @Override
        public char[] toArray(char[] dest) {
            return TByteCharHashMap.this.values(dest);
        }

        @Override
        public boolean add(char entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char entry) {
            char[] values = TByteCharHashMap.this._values;
            byte[] set = TByteCharHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == 0 || set[i] == 2 || entry != values[i]) continue;
                TByteCharHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Character) {
                    char ele = ((Character)element).charValue();
                    if (TByteCharHashMap.this.containsValue(ele)) continue;
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
                if (TByteCharHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(char[] array) {
            for (char element : array) {
                if (TByteCharHashMap.this.containsValue(element)) continue;
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
            char[] values = TByteCharHashMap.this._values;
            byte[] states = TByteCharHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TByteCharHashMap.this.removeAt(i);
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
            TByteCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TCharProcedure procedure) {
            return TByteCharHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TByteCharHashMap.this.forEachValue(new TCharProcedure(){
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
    implements TByteSet {
        protected TKeyView() {
        }

        @Override
        public TByteIterator iterator() {
            return new TByteCharKeyHashIterator(TByteCharHashMap.this);
        }

        @Override
        public byte getNoEntryValue() {
            return TByteCharHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TByteCharHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TByteCharHashMap.this._size;
        }

        @Override
        public boolean contains(byte entry) {
            return TByteCharHashMap.this.contains(entry);
        }

        @Override
        public byte[] toArray() {
            return TByteCharHashMap.this.keys();
        }

        @Override
        public byte[] toArray(byte[] dest) {
            return TByteCharHashMap.this.keys(dest);
        }

        @Override
        public boolean add(byte entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte entry) {
            return TByteCharHashMap.this.no_entry_value != TByteCharHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Byte) {
                    byte ele = (Byte)element;
                    if (TByteCharHashMap.this.containsKey(ele)) continue;
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
                if (TByteCharHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(byte[] array) {
            for (byte element : array) {
                if (TByteCharHashMap.this.contains(element)) continue;
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
            byte[] set = TByteCharHashMap.this._set;
            byte[] states = TByteCharHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TByteCharHashMap.this.removeAt(i);
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
            TByteCharHashMap.this.clear();
        }

        @Override
        public boolean forEach(TByteProcedure procedure) {
            return TByteCharHashMap.this.forEachKey(procedure);
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
            int i = TByteCharHashMap.this._states.length;
            while (i-- > 0) {
                if (TByteCharHashMap.this._states[i] != 1 || that.contains(TByteCharHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TByteCharHashMap.this._states.length;
            while (i-- > 0) {
                if (TByteCharHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TByteCharHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TByteCharHashMap.this.forEachKey(new TByteProcedure(){
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


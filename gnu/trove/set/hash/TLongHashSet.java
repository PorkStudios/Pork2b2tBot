/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.set.hash;

import gnu.trove.TLongCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TLongHashSet
extends TLongHash
implements TLongSet,
Externalizable {
    static final long serialVersionUID = 1L;

    public TLongHashSet() {
    }

    public TLongHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    public TLongHashSet(int initialCapacity, float load_factor) {
        super(initialCapacity, load_factor);
    }

    public TLongHashSet(int initial_capacity, float load_factor, long no_entry_value) {
        super(initial_capacity, load_factor, no_entry_value);
        if (no_entry_value != 0L) {
            Arrays.fill(this._set, no_entry_value);
        }
    }

    public TLongHashSet(Collection<? extends Long> collection) {
        this(Math.max(collection.size(), 10));
        this.addAll(collection);
    }

    public TLongHashSet(TLongCollection collection) {
        this(Math.max(collection.size(), 10));
        if (collection instanceof TLongHashSet) {
            TLongHashSet hashset = (TLongHashSet)collection;
            this._loadFactor = hashset._loadFactor;
            this.no_entry_value = hashset.no_entry_value;
            if (this.no_entry_value != 0L) {
                Arrays.fill(this._set, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.addAll(collection);
    }

    public TLongHashSet(long[] array) {
        this(Math.max(array.length, 10));
        this.addAll(array);
    }

    @Override
    public TLongIterator iterator() {
        return new TLongHashIterator(this);
    }

    @Override
    public long[] toArray() {
        long[] result = new long[this.size()];
        long[] set = this._set;
        byte[] states = this._states;
        int i = states.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            result[j++] = set[i];
        }
        return result;
    }

    @Override
    public long[] toArray(long[] dest) {
        long[] set = this._set;
        byte[] states = this._states;
        int i = states.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            dest[j++] = set[i];
        }
        if (dest.length > this._size) {
            dest[this._size] = this.no_entry_value;
        }
        return dest;
    }

    @Override
    public boolean add(long val) {
        int index = this.insertKey(val);
        if (index < 0) {
            return false;
        }
        this.postInsertHook(this.consumeFreeSlot);
        return true;
    }

    @Override
    public boolean remove(long val) {
        int index = this.index(val);
        if (index >= 0) {
            this.removeAt(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object element : collection) {
            if (element instanceof Long) {
                long c = (Long)element;
                if (this.contains(c)) continue;
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
            long element = iter.next();
            if (this.contains(element)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(long[] array) {
        int i = array.length;
        while (i-- > 0) {
            if (this.contains(array[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        boolean changed = false;
        for (Long element : collection) {
            long e = element;
            if (!this.add(e)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(TLongCollection collection) {
        boolean changed = false;
        TLongIterator iter = collection.iterator();
        while (iter.hasNext()) {
            long element = iter.next();
            if (!this.add(element)) continue;
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean addAll(long[] array) {
        boolean changed = false;
        int i = array.length;
        while (i-- > 0) {
            if (!this.add(array[i])) continue;
            changed = true;
        }
        return changed;
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
        long[] set = this._set;
        byte[] states = this._states;
        this._autoCompactTemporaryDisable = true;
        int i = set.length;
        while (i-- > 0) {
            if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
            this.removeAt(i);
            changed = true;
        }
        this._autoCompactTemporaryDisable = false;
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
        super.clear();
        long[] set = this._set;
        byte[] states = this._states;
        int i = set.length;
        while (i-- > 0) {
            set[i] = this.no_entry_value;
            states[i] = 0;
        }
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        long[] oldSet = this._set;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            long o = oldSet[i];
            int index = this.insertKey(o);
        }
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
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1 || that.contains(this._set[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            hashcode += HashFunctions.hash(this._set[i]);
        }
        return hashcode;
    }

    public String toString() {
        StringBuilder buffy = new StringBuilder(this._size * 2 + 2);
        buffy.append("{");
        int i = this._states.length;
        int j = 1;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            buffy.append(this._set[i]);
            if (j++ >= this._size) continue;
            buffy.append(",");
        }
        buffy.append("}");
        return buffy.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(1);
        super.writeExternal(out);
        out.writeInt(this._size);
        out.writeFloat(this._loadFactor);
        out.writeLong(this.no_entry_value);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeLong(this._set[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        super.readExternal(in);
        int size = in.readInt();
        if (version >= 1) {
            this._loadFactor = in.readFloat();
            this.no_entry_value = in.readLong();
            if (this.no_entry_value != 0L) {
                Arrays.fill(this._set, this.no_entry_value);
            }
        }
        this.setUp(size);
        while (size-- > 0) {
            long val = in.readLong();
            this.add(val);
        }
    }

    class TLongHashIterator
    extends THashPrimitiveIterator
    implements TLongIterator {
        private final TLongHash _hash;

        public TLongHashIterator(TLongHash hash) {
            super(hash);
            this._hash = hash;
        }

        public long next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
        }
    }

}


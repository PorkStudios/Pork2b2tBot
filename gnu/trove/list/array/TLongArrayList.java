/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list.array;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TLongArrayList
implements TLongList,
Externalizable {
    static final long serialVersionUID = 1L;
    protected long[] _data;
    protected int _pos;
    protected static final int DEFAULT_CAPACITY = 10;
    protected long no_entry_value;

    public TLongArrayList() {
        this(10, 0L);
    }

    public TLongArrayList(int capacity) {
        this(capacity, 0L);
    }

    public TLongArrayList(int capacity, long no_entry_value) {
        this._data = new long[capacity];
        this._pos = 0;
        this.no_entry_value = no_entry_value;
    }

    public TLongArrayList(TLongCollection collection) {
        this(collection.size());
        this.addAll(collection);
    }

    public TLongArrayList(long[] values) {
        this(values.length);
        this.add(values);
    }

    protected TLongArrayList(long[] values, long no_entry_value, boolean wrap) {
        if (!wrap) {
            throw new IllegalStateException("Wrong call");
        }
        if (values == null) {
            throw new IllegalArgumentException("values can not be null");
        }
        this._data = values;
        this._pos = values.length;
        this.no_entry_value = no_entry_value;
    }

    public static TLongArrayList wrap(long[] values) {
        return TLongArrayList.wrap(values, 0L);
    }

    public static TLongArrayList wrap(long[] values, long no_entry_value) {
        return new TLongArrayList(values, no_entry_value, true){

            public void ensureCapacity(int capacity) {
                if (capacity > this._data.length) {
                    throw new IllegalStateException("Can not grow ArrayList wrapped external array");
                }
            }
        };
    }

    @Override
    public long getNoEntryValue() {
        return this.no_entry_value;
    }

    public void ensureCapacity(int capacity) {
        if (capacity > this._data.length) {
            int newCap = Math.max(this._data.length << 1, capacity);
            long[] tmp = new long[newCap];
            System.arraycopy(this._data, 0, tmp, 0, this._data.length);
            this._data = tmp;
        }
    }

    @Override
    public int size() {
        return this._pos;
    }

    @Override
    public boolean isEmpty() {
        return this._pos == 0;
    }

    public void trimToSize() {
        if (this._data.length > this.size()) {
            long[] tmp = new long[this.size()];
            this.toArray(tmp, 0, tmp.length);
            this._data = tmp;
        }
    }

    @Override
    public boolean add(long val) {
        this.ensureCapacity(this._pos + 1);
        this._data[this._pos++] = val;
        return true;
    }

    @Override
    public void add(long[] vals) {
        this.add(vals, 0, vals.length);
    }

    @Override
    public void add(long[] vals, int offset, int length) {
        this.ensureCapacity(this._pos + length);
        System.arraycopy(vals, offset, this._data, this._pos, length);
        this._pos += length;
    }

    @Override
    public void insert(int offset, long value) {
        if (offset == this._pos) {
            this.add(value);
            return;
        }
        this.ensureCapacity(this._pos + 1);
        System.arraycopy(this._data, offset, this._data, offset + 1, this._pos - offset);
        this._data[offset] = value;
        ++this._pos;
    }

    @Override
    public void insert(int offset, long[] values) {
        this.insert(offset, values, 0, values.length);
    }

    @Override
    public void insert(int offset, long[] values, int valOffset, int len) {
        if (offset == this._pos) {
            this.add(values, valOffset, len);
            return;
        }
        this.ensureCapacity(this._pos + len);
        System.arraycopy(this._data, offset, this._data, offset + len, this._pos - offset);
        System.arraycopy(values, valOffset, this._data, offset, len);
        this._pos += len;
    }

    @Override
    public long get(int offset) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        return this._data[offset];
    }

    public long getQuick(int offset) {
        return this._data[offset];
    }

    @Override
    public long set(int offset, long val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        long prev_val = this._data[offset];
        this._data[offset] = val;
        return prev_val;
    }

    @Override
    public long replace(int offset, long val) {
        if (offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        long old = this._data[offset];
        this._data[offset] = val;
        return old;
    }

    @Override
    public void set(int offset, long[] values) {
        this.set(offset, values, 0, values.length);
    }

    @Override
    public void set(int offset, long[] values, int valOffset, int length) {
        if (offset < 0 || offset + length > this._pos) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        System.arraycopy(values, valOffset, this._data, offset, length);
    }

    public void setQuick(int offset, long val) {
        this._data[offset] = val;
    }

    @Override
    public void clear() {
        this.clear(10);
    }

    public void clear(int capacity) {
        this._data = new long[capacity];
        this._pos = 0;
    }

    public void reset() {
        this._pos = 0;
        Arrays.fill(this._data, this.no_entry_value);
    }

    public void resetQuick() {
        this._pos = 0;
    }

    @Override
    public boolean remove(long value) {
        for (int index = 0; index < this._pos; ++index) {
            if (value != this._data[index]) continue;
            this.remove(index, 1);
            return true;
        }
        return false;
    }

    @Override
    public long removeAt(int offset) {
        long old = this.get(offset);
        this.remove(offset, 1);
        return old;
    }

    @Override
    public void remove(int offset, int length) {
        if (length == 0) {
            return;
        }
        if (offset < 0 || offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        if (offset == 0) {
            System.arraycopy(this._data, length, this._data, 0, this._pos - length);
        } else if (this._pos - length != offset) {
            System.arraycopy(this._data, offset + length, this._data, offset, this._pos - (offset + length));
        }
        this._pos -= length;
    }

    @Override
    public TLongIterator iterator() {
        return new TLongArrayIterator(0);
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
        if (this == collection) {
            return true;
        }
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
        for (long element : array) {
            if (!this.add(element)) continue;
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
        long[] data = this._data;
        int i = this._pos;
        while (i-- > 0) {
            if (Arrays.binarySearch(array, data[i]) >= 0) continue;
            this.remove(i, 1);
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
        if (collection == this) {
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
    public void transformValues(TLongFunction function) {
        int i = this._pos;
        while (i-- > 0) {
            this._data[i] = function.execute(this._data[i]);
        }
    }

    @Override
    public void reverse() {
        this.reverse(0, this._pos);
    }

    @Override
    public void reverse(int from, int to) {
        if (from == to) {
            return;
        }
        if (from > to) {
            throw new IllegalArgumentException("from cannot be greater than to");
        }
        int i = from;
        for (int j = to - 1; i < j; ++i, --j) {
            this.swap(i, j);
        }
    }

    @Override
    public void shuffle(Random rand) {
        int i = this._pos;
        while (i-- > 1) {
            this.swap(i, rand.nextInt(i));
        }
    }

    private void swap(int i, int j) {
        long tmp = this._data[i];
        this._data[i] = this._data[j];
        this._data[j] = tmp;
    }

    @Override
    public TLongList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException("end index " + end + " greater than begin index " + begin);
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        }
        if (end > this._data.length) {
            throw new IndexOutOfBoundsException("end index < " + this._data.length);
        }
        TLongArrayList list = new TLongArrayList(end - begin);
        for (int i = begin; i < end; ++i) {
            list.add(this._data[i]);
        }
        return list;
    }

    @Override
    public long[] toArray() {
        return this.toArray(0, this._pos);
    }

    @Override
    public long[] toArray(int offset, int len) {
        long[] rv = new long[len];
        this.toArray(rv, offset, len);
        return rv;
    }

    @Override
    public long[] toArray(long[] dest) {
        int len = dest.length;
        if (dest.length > this._pos) {
            len = this._pos;
            dest[len] = this.no_entry_value;
        }
        this.toArray(dest, 0, len);
        return dest;
    }

    @Override
    public long[] toArray(long[] dest, int offset, int len) {
        if (len == 0) {
            return dest;
        }
        if (offset < 0 || offset >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
        System.arraycopy(this._data, offset, dest, 0, len);
        return dest;
    }

    @Override
    public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) {
        if (len == 0) {
            return dest;
        }
        if (source_pos < 0 || source_pos >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(source_pos);
        }
        System.arraycopy(this._data, source_pos, dest, dest_pos, len);
        return dest;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof TLongArrayList) {
            TLongArrayList that = (TLongArrayList)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = this._pos;
            while (i-- > 0) {
                if (this._data[i] == that._data[i]) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int i = this._pos;
        while (i-- > 0) {
            h += HashFunctions.hash(this._data[i]);
        }
        return h;
    }

    @Override
    public boolean forEach(TLongProcedure procedure) {
        for (int i = 0; i < this._pos; ++i) {
            if (procedure.execute(this._data[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TLongProcedure procedure) {
        int i = this._pos;
        while (i-- > 0) {
            if (procedure.execute(this._data[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public void sort() {
        Arrays.sort(this._data, 0, this._pos);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        Arrays.sort(this._data, fromIndex, toIndex);
    }

    @Override
    public void fill(long val) {
        Arrays.fill(this._data, 0, this._pos, val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        if (toIndex > this._pos) {
            this.ensureCapacity(toIndex);
            this._pos = toIndex;
        }
        Arrays.fill(this._data, fromIndex, toIndex, val);
    }

    @Override
    public int binarySearch(long value) {
        return this.binarySearch(value, 0, this._pos);
    }

    @Override
    public int binarySearch(long value, int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > this._pos) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
        int low = fromIndex;
        int high = toIndex - 1;
        while (low <= high) {
            int mid = low + high >>> 1;
            long midVal = this._data[mid];
            if (midVal < value) {
                low = mid + 1;
                continue;
            }
            if (midVal > value) {
                high = mid - 1;
                continue;
            }
            return mid;
        }
        return - low + 1;
    }

    @Override
    public int indexOf(long value) {
        return this.indexOf(0, value);
    }

    @Override
    public int indexOf(int offset, long value) {
        for (int i = offset; i < this._pos; ++i) {
            if (this._data[i] != value) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(long value) {
        return this.lastIndexOf(this._pos, value);
    }

    @Override
    public int lastIndexOf(int offset, long value) {
        int i = offset;
        while (i-- > 0) {
            if (this._data[i] != value) continue;
            return i;
        }
        return -1;
    }

    @Override
    public boolean contains(long value) {
        return this.lastIndexOf(value) >= 0;
    }

    @Override
    public TLongList grep(TLongProcedure condition) {
        TLongArrayList list = new TLongArrayList();
        for (int i = 0; i < this._pos; ++i) {
            if (!condition.execute(this._data[i])) continue;
            list.add(this._data[i]);
        }
        return list;
    }

    @Override
    public TLongList inverseGrep(TLongProcedure condition) {
        TLongArrayList list = new TLongArrayList();
        for (int i = 0; i < this._pos; ++i) {
            if (condition.execute(this._data[i])) continue;
            list.add(this._data[i]);
        }
        return list;
    }

    @Override
    public long max() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        }
        long max = Long.MIN_VALUE;
        for (int i = 0; i < this._pos; ++i) {
            if (this._data[i] <= max) continue;
            max = this._data[i];
        }
        return max;
    }

    @Override
    public long min() {
        if (this.size() == 0) {
            throw new IllegalStateException("cannot find minimum of an empty list");
        }
        long min = Long.MAX_VALUE;
        for (int i = 0; i < this._pos; ++i) {
            if (this._data[i] >= min) continue;
            min = this._data[i];
        }
        return min;
    }

    @Override
    public long sum() {
        long sum = 0L;
        for (int i = 0; i < this._pos; ++i) {
            sum += this._data[i];
        }
        return sum;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("{");
        int end = this._pos - 1;
        for (int i = 0; i < end; ++i) {
            buf.append(this._data[i]);
            buf.append(", ");
        }
        if (this.size() > 0) {
            buf.append(this._data[this._pos - 1]);
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeInt(this._pos);
        out.writeLong(this.no_entry_value);
        int len = this._data.length;
        out.writeInt(len);
        for (int i = 0; i < len; ++i) {
            out.writeLong(this._data[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._pos = in.readInt();
        this.no_entry_value = in.readLong();
        int len = in.readInt();
        this._data = new long[len];
        for (int i = 0; i < len; ++i) {
            this._data[i] = in.readLong();
        }
    }

    class TLongArrayIterator
    implements TLongIterator {
        private int cursor = 0;
        int lastRet = -1;

        TLongArrayIterator(int index) {
            this.cursor = index;
        }

        public boolean hasNext() {
            return this.cursor < TLongArrayList.this.size();
        }

        public long next() {
            try {
                long next = TLongArrayList.this.get(this.cursor);
                this.lastRet = this.cursor++;
                return next;
            }
            catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (this.lastRet == -1) {
                throw new IllegalStateException();
            }
            try {
                TLongArrayList.this.remove(this.lastRet, 1);
                if (this.lastRet < this.cursor) {
                    --this.cursor;
                }
                this.lastRet = -1;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list.linked;

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
import java.util.NoSuchElementException;
import java.util.Random;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TLongLinkedList
implements TLongList,
Externalizable {
    long no_entry_value;
    int size;
    TLongLink head;
    TLongLink tail;

    public TLongLinkedList() {
        this.tail = this.head = null;
    }

    public TLongLinkedList(long no_entry_value) {
        this.tail = this.head = null;
        this.no_entry_value = no_entry_value;
    }

    public TLongLinkedList(TLongList list) {
        this.tail = this.head = null;
        this.no_entry_value = list.getNoEntryValue();
        TLongIterator iterator = list.iterator();
        while (iterator.hasNext()) {
            long next = iterator.next();
            this.add(next);
        }
    }

    @Override
    public long getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean add(long val) {
        TLongLink l = new TLongLink(val);
        if (TLongLinkedList.no(this.head)) {
            this.head = l;
            this.tail = l;
        } else {
            l.setPrevious(this.tail);
            this.tail.setNext(l);
            this.tail = l;
        }
        ++this.size;
        return true;
    }

    @Override
    public void add(long[] vals) {
        for (long val : vals) {
            this.add(val);
        }
    }

    @Override
    public void add(long[] vals, int offset, int length) {
        for (int i = 0; i < length; ++i) {
            long val = vals[offset + i];
            this.add(val);
        }
    }

    @Override
    public void insert(int offset, long value) {
        TLongLinkedList tmp = new TLongLinkedList();
        tmp.add(value);
        this.insert(offset, tmp);
    }

    @Override
    public void insert(int offset, long[] values) {
        this.insert(offset, TLongLinkedList.link(values, 0, values.length));
    }

    @Override
    public void insert(int offset, long[] values, int valOffset, int len) {
        this.insert(offset, TLongLinkedList.link(values, valOffset, len));
    }

    void insert(int offset, TLongLinkedList tmp) {
        TLongLink l = this.getLinkAt(offset);
        this.size += tmp.size;
        if (l == this.head) {
            tmp.tail.setNext(this.head);
            this.head.setPrevious(tmp.tail);
            this.head = tmp.head;
            return;
        }
        if (TLongLinkedList.no(l)) {
            if (this.size == 0) {
                this.head = tmp.head;
                this.tail = tmp.tail;
            } else {
                this.tail.setNext(tmp.head);
                tmp.head.setPrevious(this.tail);
                this.tail = tmp.tail;
            }
        } else {
            TLongLink prev = l.getPrevious();
            l.getPrevious().setNext(tmp.head);
            tmp.tail.setNext(l);
            l.setPrevious(tmp.tail);
            tmp.head.setPrevious(prev);
        }
    }

    static TLongLinkedList link(long[] values, int valOffset, int len) {
        TLongLinkedList ret = new TLongLinkedList();
        for (int i = 0; i < len; ++i) {
            ret.add(values[valOffset + i]);
        }
        return ret;
    }

    @Override
    public long get(int offset) {
        if (offset > this.size) {
            throw new IndexOutOfBoundsException("index " + offset + " exceeds size " + this.size);
        }
        TLongLink l = this.getLinkAt(offset);
        if (TLongLinkedList.no(l)) {
            return this.no_entry_value;
        }
        return l.getValue();
    }

    public TLongLink getLinkAt(int offset) {
        if (offset >= this.size()) {
            return null;
        }
        if (offset <= this.size() >>> 1) {
            return TLongLinkedList.getLink(this.head, 0, offset, true);
        }
        return TLongLinkedList.getLink(this.tail, this.size() - 1, offset, false);
    }

    private static TLongLink getLink(TLongLink l, int idx, int offset) {
        return TLongLinkedList.getLink(l, idx, offset, true);
    }

    private static TLongLink getLink(TLongLink l, int idx, int offset, boolean next) {
        int i = idx;
        while (TLongLinkedList.got(l)) {
            if (i == offset) {
                return l;
            }
            i += next ? 1 : -1;
            l = next ? l.getNext() : l.getPrevious();
        }
        return null;
    }

    @Override
    public long set(int offset, long val) {
        if (offset > this.size) {
            throw new IndexOutOfBoundsException("index " + offset + " exceeds size " + this.size);
        }
        TLongLink l = this.getLinkAt(offset);
        if (TLongLinkedList.no(l)) {
            throw new IndexOutOfBoundsException("at offset " + offset);
        }
        long prev = l.getValue();
        l.setValue(val);
        return prev;
    }

    @Override
    public void set(int offset, long[] values) {
        this.set(offset, values, 0, values.length);
    }

    @Override
    public void set(int offset, long[] values, int valOffset, int length) {
        for (int i = 0; i < length; ++i) {
            long value = values[valOffset + i];
            this.set(offset + i, value);
        }
    }

    @Override
    public long replace(int offset, long val) {
        return this.set(offset, val);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(long value) {
        boolean changed = false;
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            if (l.getValue() == value) {
                changed = true;
                this.removeLink(l);
            }
            l = l.getNext();
        }
        return changed;
    }

    private void removeLink(TLongLink l) {
        if (TLongLinkedList.no(l)) {
            return;
        }
        --this.size;
        TLongLink prev = l.getPrevious();
        TLongLink next = l.getNext();
        if (TLongLinkedList.got(prev)) {
            prev.setNext(next);
        } else {
            this.head = next;
        }
        if (TLongLinkedList.got(next)) {
            next.setPrevious(prev);
        } else {
            this.tail = prev;
        }
        l.setNext(null);
        l.setPrevious(null);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (this.isEmpty()) {
            return false;
        }
        for (Object o : collection) {
            if (o instanceof Long) {
                Long i = (Long)o;
                if (this.contains(i)) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(TLongCollection collection) {
        if (this.isEmpty()) {
            return false;
        }
        TLongIterator it = collection.iterator();
        while (it.hasNext()) {
            long i = it.next();
            if (this.contains(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(long[] array) {
        if (this.isEmpty()) {
            return false;
        }
        for (long i : array) {
            if (this.contains(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Long> collection) {
        boolean ret = false;
        for (Long v : collection) {
            if (!this.add(v)) continue;
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean addAll(TLongCollection collection) {
        boolean ret = false;
        TLongIterator it = collection.iterator();
        while (it.hasNext()) {
            long i = it.next();
            if (!this.add(i)) continue;
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean addAll(long[] array) {
        boolean ret = false;
        for (long i : array) {
            if (!this.add(i)) continue;
            ret = true;
        }
        return ret;
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
        Arrays.sort(array);
        boolean modified = false;
        TLongIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (Arrays.binarySearch(array, iter.next()) >= 0) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean modified = false;
        TLongIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (!collection.contains(iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(TLongCollection collection) {
        boolean modified = false;
        TLongIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (!collection.contains(iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(long[] array) {
        Arrays.sort(array);
        boolean modified = false;
        TLongIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (Arrays.binarySearch(array, iter.next()) < 0) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public long removeAt(int offset) {
        TLongLink l = this.getLinkAt(offset);
        if (TLongLinkedList.no(l)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + offset);
        }
        long prev = l.getValue();
        this.removeLink(l);
        return prev;
    }

    @Override
    public void remove(int offset, int length) {
        for (int i = 0; i < length; ++i) {
            this.removeAt(offset);
        }
    }

    @Override
    public void transformValues(TLongFunction function) {
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            l.setValue(function.execute(l.getValue()));
            l = l.getNext();
        }
    }

    @Override
    public void reverse() {
        TLongLink h = this.head;
        TLongLink t = this.tail;
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            TLongLink next = l.getNext();
            TLongLink prev = l.getPrevious();
            TLongLink tmp = l;
            l = l.getNext();
            tmp.setNext(prev);
            tmp.setPrevious(next);
        }
        this.head = t;
        this.tail = h;
    }

    @Override
    public void reverse(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("from > to : " + from + ">" + to);
        }
        TLongLink start = this.getLinkAt(from);
        TLongLink stop = this.getLinkAt(to);
        TLongLink tmp = null;
        TLongLink tmpHead = start.getPrevious();
        for (TLongLink l = start; l != stop; l = l.getNext()) {
            TLongLink next = l.getNext();
            TLongLink prev = l.getPrevious();
            tmp = l;
            tmp.setNext(prev);
            tmp.setPrevious(next);
        }
        if (TLongLinkedList.got(tmp)) {
            tmpHead.setNext(tmp);
            stop.setPrevious(tmpHead);
        }
        start.setNext(stop);
        stop.setPrevious(start);
    }

    @Override
    public void shuffle(Random rand) {
        for (int i = 0; i < this.size; ++i) {
            TLongLink l = this.getLinkAt(rand.nextInt(this.size()));
            this.removeLink(l);
            this.add(l.getValue());
        }
    }

    @Override
    public TLongList subList(int begin, int end) {
        if (end < begin) {
            throw new IllegalArgumentException("begin index " + begin + " greater than end index " + end);
        }
        if (this.size < begin) {
            throw new IllegalArgumentException("begin index " + begin + " greater than last index " + this.size);
        }
        if (begin < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        }
        if (end > this.size) {
            throw new IndexOutOfBoundsException("end index < " + this.size);
        }
        TLongLinkedList ret = new TLongLinkedList();
        TLongLink tmp = this.getLinkAt(begin);
        for (int i = begin; i < end; ++i) {
            ret.add(tmp.getValue());
            tmp = tmp.getNext();
        }
        return ret;
    }

    @Override
    public long[] toArray() {
        return this.toArray(new long[this.size], 0, this.size);
    }

    @Override
    public long[] toArray(int offset, int len) {
        return this.toArray(new long[len], offset, 0, len);
    }

    @Override
    public long[] toArray(long[] dest) {
        return this.toArray(dest, 0, this.size);
    }

    @Override
    public long[] toArray(long[] dest, int offset, int len) {
        return this.toArray(dest, offset, 0, len);
    }

    @Override
    public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) {
        if (len == 0) {
            return dest;
        }
        if (source_pos < 0 || source_pos >= this.size()) {
            throw new ArrayIndexOutOfBoundsException(source_pos);
        }
        TLongLink tmp = this.getLinkAt(source_pos);
        for (int i = 0; i < len; ++i) {
            dest[dest_pos + i] = tmp.getValue();
            tmp = tmp.getNext();
        }
        return dest;
    }

    @Override
    public boolean forEach(TLongProcedure procedure) {
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            if (!procedure.execute(l.getValue())) {
                return false;
            }
            l = l.getNext();
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TLongProcedure procedure) {
        TLongLink l = this.tail;
        while (TLongLinkedList.got(l)) {
            if (!procedure.execute(l.getValue())) {
                return false;
            }
            l = l.getPrevious();
        }
        return true;
    }

    @Override
    public void sort() {
        this.sort(0, this.size);
    }

    @Override
    public void sort(int fromIndex, int toIndex) {
        TLongList tmp = this.subList(fromIndex, toIndex);
        long[] vals = tmp.toArray();
        Arrays.sort(vals);
        this.set(fromIndex, vals);
    }

    @Override
    public void fill(long val) {
        this.fill(0, this.size, val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, long val) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        }
        TLongLink l = this.getLinkAt(fromIndex);
        if (toIndex > this.size) {
            int i;
            for (i = fromIndex; i < this.size; ++i) {
                l.setValue(val);
                l = l.getNext();
            }
            for (i = this.size; i < toIndex; ++i) {
                this.add(val);
            }
        } else {
            for (int i = fromIndex; i < toIndex; ++i) {
                l.setValue(val);
                l = l.getNext();
            }
        }
    }

    @Override
    public int binarySearch(long value) {
        return this.binarySearch(value, 0, this.size());
    }

    @Override
    public int binarySearch(long value, int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        }
        if (toIndex > this.size) {
            throw new IndexOutOfBoundsException("end index > size: " + toIndex + " > " + this.size);
        }
        if (toIndex < fromIndex) {
            return - fromIndex + 1;
        }
        int from = fromIndex;
        TLongLink fromLink = this.getLinkAt(fromIndex);
        int to = toIndex;
        while (from < to) {
            int mid = from + to >>> 1;
            TLongLink middle = TLongLinkedList.getLink(fromLink, from, mid);
            if (middle.getValue() == value) {
                return mid;
            }
            if (middle.getValue() < value) {
                from = mid + 1;
                fromLink = middle.next;
                continue;
            }
            to = mid - 1;
        }
        return - from + 1;
    }

    @Override
    public int indexOf(long value) {
        return this.indexOf(0, value);
    }

    @Override
    public int indexOf(int offset, long value) {
        int count = offset;
        TLongLink l = this.getLinkAt(offset);
        while (TLongLinkedList.got(l.getNext())) {
            if (l.getValue() == value) {
                return count;
            }
            ++count;
            l = l.getNext();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(long value) {
        return this.lastIndexOf(0, value);
    }

    @Override
    public int lastIndexOf(int offset, long value) {
        if (this.isEmpty()) {
            return -1;
        }
        int last = -1;
        int count = offset;
        TLongLink l = this.getLinkAt(offset);
        while (TLongLinkedList.got(l.getNext())) {
            if (l.getValue() == value) {
                last = count;
            }
            ++count;
            l = l.getNext();
        }
        return last;
    }

    @Override
    public boolean contains(long value) {
        if (this.isEmpty()) {
            return false;
        }
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            if (l.getValue() == value) {
                return true;
            }
            l = l.getNext();
        }
        return false;
    }

    @Override
    public TLongIterator iterator() {
        return new TLongIterator(){
            TLongLink l;
            TLongLink current;
            {
                this.l = TLongLinkedList.this.head;
            }

            public long next() {
                if (TLongLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                }
                long ret = this.l.getValue();
                this.current = this.l;
                this.l = this.l.getNext();
                return ret;
            }

            public boolean hasNext() {
                return TLongLinkedList.got(this.l);
            }

            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                }
                TLongLinkedList.this.removeLink(this.current);
                this.current = null;
            }
        };
    }

    @Override
    public TLongList grep(TLongProcedure condition) {
        TLongLinkedList ret = new TLongLinkedList();
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            if (condition.execute(l.getValue())) {
                ret.add(l.getValue());
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public TLongList inverseGrep(TLongProcedure condition) {
        TLongLinkedList ret = new TLongLinkedList();
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            if (!condition.execute(l.getValue())) {
                ret.add(l.getValue());
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public long max() {
        long ret = Long.MIN_VALUE;
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            if (ret < l.getValue()) {
                ret = l.getValue();
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public long min() {
        long ret = Long.MAX_VALUE;
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            if (ret > l.getValue()) {
                ret = l.getValue();
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public long sum() {
        long sum = 0L;
        TLongLink l = this.head;
        while (TLongLinkedList.got(l)) {
            sum += l.getValue();
            l = l.getNext();
        }
        return sum;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeLong(this.no_entry_value);
        out.writeInt(this.size);
        TLongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            long next = iterator.next();
            out.writeLong(next);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.no_entry_value = in.readLong();
        int len = in.readInt();
        for (int i = 0; i < len; ++i) {
            this.add(in.readLong());
        }
    }

    static boolean got(Object ref) {
        return ref != null;
    }

    static boolean no(Object ref) {
        return ref == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TLongLinkedList that = (TLongLinkedList)o;
        if (this.no_entry_value != that.no_entry_value) {
            return false;
        }
        if (this.size != that.size) {
            return false;
        }
        TLongIterator iterator = this.iterator();
        TLongIterator thatIterator = that.iterator();
        while (iterator.hasNext()) {
            if (!thatIterator.hasNext()) {
                return false;
            }
            if (iterator.next() == thatIterator.next()) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = HashFunctions.hash(this.no_entry_value);
        result = 31 * result + this.size;
        TLongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            result = 31 * result + HashFunctions.hash(iterator.next());
        }
        return result;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("{");
        TLongIterator it = this.iterator();
        while (it.hasNext()) {
            long next = it.next();
            buf.append(next);
            if (!it.hasNext()) continue;
            buf.append(", ");
        }
        buf.append("}");
        return buf.toString();
    }

    class RemoveProcedure
    implements TLongProcedure {
        boolean changed = false;

        RemoveProcedure() {
        }

        public boolean execute(long value) {
            if (TLongLinkedList.this.remove(value)) {
                this.changed = true;
            }
            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TLongLink {
        long value;
        TLongLink previous;
        TLongLink next;

        TLongLink(long value) {
            this.value = value;
        }

        public long getValue() {
            return this.value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public TLongLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TLongLink previous) {
            this.previous = previous;
        }

        public TLongLink getNext() {
            return this.next;
        }

        public void setNext(TLongLink next) {
            this.next = next;
        }
    }

}


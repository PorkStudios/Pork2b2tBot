/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list.linked;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
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
public class TByteLinkedList
implements TByteList,
Externalizable {
    byte no_entry_value;
    int size;
    TByteLink head;
    TByteLink tail;

    public TByteLinkedList() {
        this.tail = this.head = null;
    }

    public TByteLinkedList(byte no_entry_value) {
        this.tail = this.head = null;
        this.no_entry_value = no_entry_value;
    }

    public TByteLinkedList(TByteList list) {
        this.tail = this.head = null;
        this.no_entry_value = list.getNoEntryValue();
        TByteIterator iterator = list.iterator();
        while (iterator.hasNext()) {
            byte next = iterator.next();
            this.add(next);
        }
    }

    @Override
    public byte getNoEntryValue() {
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
    public boolean add(byte val) {
        TByteLink l = new TByteLink(val);
        if (TByteLinkedList.no(this.head)) {
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
    public void add(byte[] vals) {
        for (byte val : vals) {
            this.add(val);
        }
    }

    @Override
    public void add(byte[] vals, int offset, int length) {
        for (int i = 0; i < length; ++i) {
            byte val = vals[offset + i];
            this.add(val);
        }
    }

    @Override
    public void insert(int offset, byte value) {
        TByteLinkedList tmp = new TByteLinkedList();
        tmp.add(value);
        this.insert(offset, tmp);
    }

    @Override
    public void insert(int offset, byte[] values) {
        this.insert(offset, TByteLinkedList.link(values, 0, values.length));
    }

    @Override
    public void insert(int offset, byte[] values, int valOffset, int len) {
        this.insert(offset, TByteLinkedList.link(values, valOffset, len));
    }

    void insert(int offset, TByteLinkedList tmp) {
        TByteLink l = this.getLinkAt(offset);
        this.size += tmp.size;
        if (l == this.head) {
            tmp.tail.setNext(this.head);
            this.head.setPrevious(tmp.tail);
            this.head = tmp.head;
            return;
        }
        if (TByteLinkedList.no(l)) {
            if (this.size == 0) {
                this.head = tmp.head;
                this.tail = tmp.tail;
            } else {
                this.tail.setNext(tmp.head);
                tmp.head.setPrevious(this.tail);
                this.tail = tmp.tail;
            }
        } else {
            TByteLink prev = l.getPrevious();
            l.getPrevious().setNext(tmp.head);
            tmp.tail.setNext(l);
            l.setPrevious(tmp.tail);
            tmp.head.setPrevious(prev);
        }
    }

    static TByteLinkedList link(byte[] values, int valOffset, int len) {
        TByteLinkedList ret = new TByteLinkedList();
        for (int i = 0; i < len; ++i) {
            ret.add(values[valOffset + i]);
        }
        return ret;
    }

    @Override
    public byte get(int offset) {
        if (offset > this.size) {
            throw new IndexOutOfBoundsException("index " + offset + " exceeds size " + this.size);
        }
        TByteLink l = this.getLinkAt(offset);
        if (TByteLinkedList.no(l)) {
            return this.no_entry_value;
        }
        return l.getValue();
    }

    public TByteLink getLinkAt(int offset) {
        if (offset >= this.size()) {
            return null;
        }
        if (offset <= this.size() >>> 1) {
            return TByteLinkedList.getLink(this.head, 0, offset, true);
        }
        return TByteLinkedList.getLink(this.tail, this.size() - 1, offset, false);
    }

    private static TByteLink getLink(TByteLink l, int idx, int offset) {
        return TByteLinkedList.getLink(l, idx, offset, true);
    }

    private static TByteLink getLink(TByteLink l, int idx, int offset, boolean next) {
        int i = idx;
        while (TByteLinkedList.got(l)) {
            if (i == offset) {
                return l;
            }
            i += next ? 1 : -1;
            l = next ? l.getNext() : l.getPrevious();
        }
        return null;
    }

    @Override
    public byte set(int offset, byte val) {
        if (offset > this.size) {
            throw new IndexOutOfBoundsException("index " + offset + " exceeds size " + this.size);
        }
        TByteLink l = this.getLinkAt(offset);
        if (TByteLinkedList.no(l)) {
            throw new IndexOutOfBoundsException("at offset " + offset);
        }
        byte prev = l.getValue();
        l.setValue(val);
        return prev;
    }

    @Override
    public void set(int offset, byte[] values) {
        this.set(offset, values, 0, values.length);
    }

    @Override
    public void set(int offset, byte[] values, int valOffset, int length) {
        for (int i = 0; i < length; ++i) {
            byte value = values[valOffset + i];
            this.set(offset + i, value);
        }
    }

    @Override
    public byte replace(int offset, byte val) {
        return this.set(offset, val);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(byte value) {
        boolean changed = false;
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            if (l.getValue() == value) {
                changed = true;
                this.removeLink(l);
            }
            l = l.getNext();
        }
        return changed;
    }

    private void removeLink(TByteLink l) {
        if (TByteLinkedList.no(l)) {
            return;
        }
        --this.size;
        TByteLink prev = l.getPrevious();
        TByteLink next = l.getNext();
        if (TByteLinkedList.got(prev)) {
            prev.setNext(next);
        } else {
            this.head = next;
        }
        if (TByteLinkedList.got(next)) {
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
            if (o instanceof Byte) {
                Byte i = (Byte)o;
                if (this.contains(i)) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(TByteCollection collection) {
        if (this.isEmpty()) {
            return false;
        }
        TByteIterator it = collection.iterator();
        while (it.hasNext()) {
            byte i = it.next();
            if (this.contains(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(byte[] array) {
        if (this.isEmpty()) {
            return false;
        }
        for (byte i : array) {
            if (this.contains(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> collection) {
        boolean ret = false;
        for (Byte v : collection) {
            if (!this.add(v)) continue;
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean addAll(TByteCollection collection) {
        boolean ret = false;
        TByteIterator it = collection.iterator();
        while (it.hasNext()) {
            byte i = it.next();
            if (!this.add(i)) continue;
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean addAll(byte[] array) {
        boolean ret = false;
        for (byte i : array) {
            if (!this.add(i)) continue;
            ret = true;
        }
        return ret;
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
        Arrays.sort(array);
        boolean modified = false;
        TByteIterator iter = this.iterator();
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
        TByteIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (!collection.contains(iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(TByteCollection collection) {
        boolean modified = false;
        TByteIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (!collection.contains(iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(byte[] array) {
        Arrays.sort(array);
        boolean modified = false;
        TByteIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (Arrays.binarySearch(array, iter.next()) < 0) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public byte removeAt(int offset) {
        TByteLink l = this.getLinkAt(offset);
        if (TByteLinkedList.no(l)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + offset);
        }
        byte prev = l.getValue();
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
    public void transformValues(TByteFunction function) {
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            l.setValue(function.execute(l.getValue()));
            l = l.getNext();
        }
    }

    @Override
    public void reverse() {
        TByteLink h = this.head;
        TByteLink t = this.tail;
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            TByteLink next = l.getNext();
            TByteLink prev = l.getPrevious();
            TByteLink tmp = l;
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
        TByteLink start = this.getLinkAt(from);
        TByteLink stop = this.getLinkAt(to);
        TByteLink tmp = null;
        TByteLink tmpHead = start.getPrevious();
        for (TByteLink l = start; l != stop; l = l.getNext()) {
            TByteLink next = l.getNext();
            TByteLink prev = l.getPrevious();
            tmp = l;
            tmp.setNext(prev);
            tmp.setPrevious(next);
        }
        if (TByteLinkedList.got(tmp)) {
            tmpHead.setNext(tmp);
            stop.setPrevious(tmpHead);
        }
        start.setNext(stop);
        stop.setPrevious(start);
    }

    @Override
    public void shuffle(Random rand) {
        for (int i = 0; i < this.size; ++i) {
            TByteLink l = this.getLinkAt(rand.nextInt(this.size()));
            this.removeLink(l);
            this.add(l.getValue());
        }
    }

    @Override
    public TByteList subList(int begin, int end) {
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
        TByteLinkedList ret = new TByteLinkedList();
        TByteLink tmp = this.getLinkAt(begin);
        for (int i = begin; i < end; ++i) {
            ret.add(tmp.getValue());
            tmp = tmp.getNext();
        }
        return ret;
    }

    @Override
    public byte[] toArray() {
        return this.toArray(new byte[this.size], 0, this.size);
    }

    @Override
    public byte[] toArray(int offset, int len) {
        return this.toArray(new byte[len], offset, 0, len);
    }

    @Override
    public byte[] toArray(byte[] dest) {
        return this.toArray(dest, 0, this.size);
    }

    @Override
    public byte[] toArray(byte[] dest, int offset, int len) {
        return this.toArray(dest, offset, 0, len);
    }

    @Override
    public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
        if (len == 0) {
            return dest;
        }
        if (source_pos < 0 || source_pos >= this.size()) {
            throw new ArrayIndexOutOfBoundsException(source_pos);
        }
        TByteLink tmp = this.getLinkAt(source_pos);
        for (int i = 0; i < len; ++i) {
            dest[dest_pos + i] = tmp.getValue();
            tmp = tmp.getNext();
        }
        return dest;
    }

    @Override
    public boolean forEach(TByteProcedure procedure) {
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            if (!procedure.execute(l.getValue())) {
                return false;
            }
            l = l.getNext();
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TByteProcedure procedure) {
        TByteLink l = this.tail;
        while (TByteLinkedList.got(l)) {
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
        TByteList tmp = this.subList(fromIndex, toIndex);
        byte[] vals = tmp.toArray();
        Arrays.sort(vals);
        this.set(fromIndex, vals);
    }

    @Override
    public void fill(byte val) {
        this.fill(0, this.size, val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, byte val) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        }
        TByteLink l = this.getLinkAt(fromIndex);
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
    public int binarySearch(byte value) {
        return this.binarySearch(value, 0, this.size());
    }

    @Override
    public int binarySearch(byte value, int fromIndex, int toIndex) {
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
        TByteLink fromLink = this.getLinkAt(fromIndex);
        int to = toIndex;
        while (from < to) {
            int mid = from + to >>> 1;
            TByteLink middle = TByteLinkedList.getLink(fromLink, from, mid);
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
    public int indexOf(byte value) {
        return this.indexOf(0, value);
    }

    @Override
    public int indexOf(int offset, byte value) {
        int count = offset;
        TByteLink l = this.getLinkAt(offset);
        while (TByteLinkedList.got(l.getNext())) {
            if (l.getValue() == value) {
                return count;
            }
            ++count;
            l = l.getNext();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(byte value) {
        return this.lastIndexOf(0, value);
    }

    @Override
    public int lastIndexOf(int offset, byte value) {
        if (this.isEmpty()) {
            return -1;
        }
        int last = -1;
        int count = offset;
        TByteLink l = this.getLinkAt(offset);
        while (TByteLinkedList.got(l.getNext())) {
            if (l.getValue() == value) {
                last = count;
            }
            ++count;
            l = l.getNext();
        }
        return last;
    }

    @Override
    public boolean contains(byte value) {
        if (this.isEmpty()) {
            return false;
        }
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            if (l.getValue() == value) {
                return true;
            }
            l = l.getNext();
        }
        return false;
    }

    @Override
    public TByteIterator iterator() {
        return new TByteIterator(){
            TByteLink l;
            TByteLink current;
            {
                this.l = TByteLinkedList.this.head;
            }

            public byte next() {
                if (TByteLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                }
                byte ret = this.l.getValue();
                this.current = this.l;
                this.l = this.l.getNext();
                return ret;
            }

            public boolean hasNext() {
                return TByteLinkedList.got(this.l);
            }

            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                }
                TByteLinkedList.this.removeLink(this.current);
                this.current = null;
            }
        };
    }

    @Override
    public TByteList grep(TByteProcedure condition) {
        TByteLinkedList ret = new TByteLinkedList();
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            if (condition.execute(l.getValue())) {
                ret.add(l.getValue());
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public TByteList inverseGrep(TByteProcedure condition) {
        TByteLinkedList ret = new TByteLinkedList();
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            if (!condition.execute(l.getValue())) {
                ret.add(l.getValue());
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public byte max() {
        byte ret = -128;
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            if (ret < l.getValue()) {
                ret = l.getValue();
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public byte min() {
        byte ret = 127;
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            if (ret > l.getValue()) {
                ret = l.getValue();
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public byte sum() {
        byte sum = 0;
        TByteLink l = this.head;
        while (TByteLinkedList.got(l)) {
            sum = (byte)(sum + l.getValue());
            l = l.getNext();
        }
        return sum;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeByte(this.no_entry_value);
        out.writeInt(this.size);
        TByteIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            byte next = iterator.next();
            out.writeByte(next);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.no_entry_value = in.readByte();
        int len = in.readInt();
        for (int i = 0; i < len; ++i) {
            this.add(in.readByte());
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
        TByteLinkedList that = (TByteLinkedList)o;
        if (this.no_entry_value != that.no_entry_value) {
            return false;
        }
        if (this.size != that.size) {
            return false;
        }
        TByteIterator iterator = this.iterator();
        TByteIterator thatIterator = that.iterator();
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
        TByteIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            result = 31 * result + HashFunctions.hash(iterator.next());
        }
        return result;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("{");
        TByteIterator it = this.iterator();
        while (it.hasNext()) {
            byte next = it.next();
            buf.append(next);
            if (!it.hasNext()) continue;
            buf.append(", ");
        }
        buf.append("}");
        return buf.toString();
    }

    class RemoveProcedure
    implements TByteProcedure {
        boolean changed = false;

        RemoveProcedure() {
        }

        public boolean execute(byte value) {
            if (TByteLinkedList.this.remove(value)) {
                this.changed = true;
            }
            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TByteLink {
        byte value;
        TByteLink previous;
        TByteLink next;

        TByteLink(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return this.value;
        }

        public void setValue(byte value) {
            this.value = value;
        }

        public TByteLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TByteLink previous) {
            this.previous = previous;
        }

        public TByteLink getNext() {
            return this.next;
        }

        public void setNext(TByteLink next) {
            this.next = next;
        }
    }

}


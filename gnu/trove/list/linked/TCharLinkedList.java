/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list.linked;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.list.TCharList;
import gnu.trove.procedure.TCharProcedure;
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
public class TCharLinkedList
implements TCharList,
Externalizable {
    char no_entry_value;
    int size;
    TCharLink head;
    TCharLink tail;

    public TCharLinkedList() {
        this.tail = this.head = null;
    }

    public TCharLinkedList(char no_entry_value) {
        this.tail = this.head = null;
        this.no_entry_value = no_entry_value;
    }

    public TCharLinkedList(TCharList list) {
        this.tail = this.head = null;
        this.no_entry_value = list.getNoEntryValue();
        TCharIterator iterator = list.iterator();
        while (iterator.hasNext()) {
            char next = iterator.next();
            this.add(next);
        }
    }

    @Override
    public char getNoEntryValue() {
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
    public boolean add(char val) {
        TCharLink l = new TCharLink(val);
        if (TCharLinkedList.no(this.head)) {
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
    public void add(char[] vals) {
        for (char val : vals) {
            this.add(val);
        }
    }

    @Override
    public void add(char[] vals, int offset, int length) {
        for (int i = 0; i < length; ++i) {
            char val = vals[offset + i];
            this.add(val);
        }
    }

    @Override
    public void insert(int offset, char value) {
        TCharLinkedList tmp = new TCharLinkedList();
        tmp.add(value);
        this.insert(offset, tmp);
    }

    @Override
    public void insert(int offset, char[] values) {
        this.insert(offset, TCharLinkedList.link(values, 0, values.length));
    }

    @Override
    public void insert(int offset, char[] values, int valOffset, int len) {
        this.insert(offset, TCharLinkedList.link(values, valOffset, len));
    }

    void insert(int offset, TCharLinkedList tmp) {
        TCharLink l = this.getLinkAt(offset);
        this.size += tmp.size;
        if (l == this.head) {
            tmp.tail.setNext(this.head);
            this.head.setPrevious(tmp.tail);
            this.head = tmp.head;
            return;
        }
        if (TCharLinkedList.no(l)) {
            if (this.size == 0) {
                this.head = tmp.head;
                this.tail = tmp.tail;
            } else {
                this.tail.setNext(tmp.head);
                tmp.head.setPrevious(this.tail);
                this.tail = tmp.tail;
            }
        } else {
            TCharLink prev = l.getPrevious();
            l.getPrevious().setNext(tmp.head);
            tmp.tail.setNext(l);
            l.setPrevious(tmp.tail);
            tmp.head.setPrevious(prev);
        }
    }

    static TCharLinkedList link(char[] values, int valOffset, int len) {
        TCharLinkedList ret = new TCharLinkedList();
        for (int i = 0; i < len; ++i) {
            ret.add(values[valOffset + i]);
        }
        return ret;
    }

    @Override
    public char get(int offset) {
        if (offset > this.size) {
            throw new IndexOutOfBoundsException("index " + offset + " exceeds size " + this.size);
        }
        TCharLink l = this.getLinkAt(offset);
        if (TCharLinkedList.no(l)) {
            return this.no_entry_value;
        }
        return l.getValue();
    }

    public TCharLink getLinkAt(int offset) {
        if (offset >= this.size()) {
            return null;
        }
        if (offset <= this.size() >>> 1) {
            return TCharLinkedList.getLink(this.head, 0, offset, true);
        }
        return TCharLinkedList.getLink(this.tail, this.size() - 1, offset, false);
    }

    private static TCharLink getLink(TCharLink l, int idx, int offset) {
        return TCharLinkedList.getLink(l, idx, offset, true);
    }

    private static TCharLink getLink(TCharLink l, int idx, int offset, boolean next) {
        int i = idx;
        while (TCharLinkedList.got(l)) {
            if (i == offset) {
                return l;
            }
            i += next ? 1 : -1;
            l = next ? l.getNext() : l.getPrevious();
        }
        return null;
    }

    @Override
    public char set(int offset, char val) {
        if (offset > this.size) {
            throw new IndexOutOfBoundsException("index " + offset + " exceeds size " + this.size);
        }
        TCharLink l = this.getLinkAt(offset);
        if (TCharLinkedList.no(l)) {
            throw new IndexOutOfBoundsException("at offset " + offset);
        }
        char prev = l.getValue();
        l.setValue(val);
        return prev;
    }

    @Override
    public void set(int offset, char[] values) {
        this.set(offset, values, 0, values.length);
    }

    @Override
    public void set(int offset, char[] values, int valOffset, int length) {
        for (int i = 0; i < length; ++i) {
            char value = values[valOffset + i];
            this.set(offset + i, value);
        }
    }

    @Override
    public char replace(int offset, char val) {
        return this.set(offset, val);
    }

    @Override
    public void clear() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @Override
    public boolean remove(char value) {
        boolean changed = false;
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            if (l.getValue() == value) {
                changed = true;
                this.removeLink(l);
            }
            l = l.getNext();
        }
        return changed;
    }

    private void removeLink(TCharLink l) {
        if (TCharLinkedList.no(l)) {
            return;
        }
        --this.size;
        TCharLink prev = l.getPrevious();
        TCharLink next = l.getNext();
        if (TCharLinkedList.got(prev)) {
            prev.setNext(next);
        } else {
            this.head = next;
        }
        if (TCharLinkedList.got(next)) {
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
            if (o instanceof Character) {
                Character i = (Character)o;
                if (this.contains(i.charValue())) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(TCharCollection collection) {
        if (this.isEmpty()) {
            return false;
        }
        TCharIterator it = collection.iterator();
        while (it.hasNext()) {
            char i = it.next();
            if (this.contains(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsAll(char[] array) {
        if (this.isEmpty()) {
            return false;
        }
        for (char i : array) {
            if (this.contains(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Character> collection) {
        boolean ret = false;
        for (Character v : collection) {
            if (!this.add(v.charValue())) continue;
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean addAll(TCharCollection collection) {
        boolean ret = false;
        TCharIterator it = collection.iterator();
        while (it.hasNext()) {
            char i = it.next();
            if (!this.add(i)) continue;
            ret = true;
        }
        return ret;
    }

    @Override
    public boolean addAll(char[] array) {
        boolean ret = false;
        for (char i : array) {
            if (!this.add(i)) continue;
            ret = true;
        }
        return ret;
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
        Arrays.sort(array);
        boolean modified = false;
        TCharIterator iter = this.iterator();
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
        TCharIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (!collection.contains(Character.valueOf(iter.next()))) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(TCharCollection collection) {
        boolean modified = false;
        TCharIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (!collection.contains(iter.next())) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(char[] array) {
        Arrays.sort(array);
        boolean modified = false;
        TCharIterator iter = this.iterator();
        while (iter.hasNext()) {
            if (Arrays.binarySearch(array, iter.next()) < 0) continue;
            iter.remove();
            modified = true;
        }
        return modified;
    }

    @Override
    public char removeAt(int offset) {
        TCharLink l = this.getLinkAt(offset);
        if (TCharLinkedList.no(l)) {
            throw new ArrayIndexOutOfBoundsException("no elemenet at " + offset);
        }
        char prev = l.getValue();
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
    public void transformValues(TCharFunction function) {
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            l.setValue(function.execute(l.getValue()));
            l = l.getNext();
        }
    }

    @Override
    public void reverse() {
        TCharLink h = this.head;
        TCharLink t = this.tail;
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            TCharLink next = l.getNext();
            TCharLink prev = l.getPrevious();
            TCharLink tmp = l;
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
        TCharLink start = this.getLinkAt(from);
        TCharLink stop = this.getLinkAt(to);
        TCharLink tmp = null;
        TCharLink tmpHead = start.getPrevious();
        for (TCharLink l = start; l != stop; l = l.getNext()) {
            TCharLink next = l.getNext();
            TCharLink prev = l.getPrevious();
            tmp = l;
            tmp.setNext(prev);
            tmp.setPrevious(next);
        }
        if (TCharLinkedList.got(tmp)) {
            tmpHead.setNext(tmp);
            stop.setPrevious(tmpHead);
        }
        start.setNext(stop);
        stop.setPrevious(start);
    }

    @Override
    public void shuffle(Random rand) {
        for (int i = 0; i < this.size; ++i) {
            TCharLink l = this.getLinkAt(rand.nextInt(this.size()));
            this.removeLink(l);
            this.add(l.getValue());
        }
    }

    @Override
    public TCharList subList(int begin, int end) {
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
        TCharLinkedList ret = new TCharLinkedList();
        TCharLink tmp = this.getLinkAt(begin);
        for (int i = begin; i < end; ++i) {
            ret.add(tmp.getValue());
            tmp = tmp.getNext();
        }
        return ret;
    }

    @Override
    public char[] toArray() {
        return this.toArray(new char[this.size], 0, this.size);
    }

    @Override
    public char[] toArray(int offset, int len) {
        return this.toArray(new char[len], offset, 0, len);
    }

    @Override
    public char[] toArray(char[] dest) {
        return this.toArray(dest, 0, this.size);
    }

    @Override
    public char[] toArray(char[] dest, int offset, int len) {
        return this.toArray(dest, offset, 0, len);
    }

    @Override
    public char[] toArray(char[] dest, int source_pos, int dest_pos, int len) {
        if (len == 0) {
            return dest;
        }
        if (source_pos < 0 || source_pos >= this.size()) {
            throw new ArrayIndexOutOfBoundsException(source_pos);
        }
        TCharLink tmp = this.getLinkAt(source_pos);
        for (int i = 0; i < len; ++i) {
            dest[dest_pos + i] = tmp.getValue();
            tmp = tmp.getNext();
        }
        return dest;
    }

    @Override
    public boolean forEach(TCharProcedure procedure) {
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            if (!procedure.execute(l.getValue())) {
                return false;
            }
            l = l.getNext();
        }
        return true;
    }

    @Override
    public boolean forEachDescending(TCharProcedure procedure) {
        TCharLink l = this.tail;
        while (TCharLinkedList.got(l)) {
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
        TCharList tmp = this.subList(fromIndex, toIndex);
        char[] vals = tmp.toArray();
        Arrays.sort(vals);
        this.set(fromIndex, vals);
    }

    @Override
    public void fill(char val) {
        this.fill(0, this.size, val);
    }

    @Override
    public void fill(int fromIndex, int toIndex, char val) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("begin index can not be < 0");
        }
        TCharLink l = this.getLinkAt(fromIndex);
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
    public int binarySearch(char value) {
        return this.binarySearch(value, 0, this.size());
    }

    @Override
    public int binarySearch(char value, int fromIndex, int toIndex) {
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
        TCharLink fromLink = this.getLinkAt(fromIndex);
        int to = toIndex;
        while (from < to) {
            int mid = from + to >>> 1;
            TCharLink middle = TCharLinkedList.getLink(fromLink, from, mid);
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
    public int indexOf(char value) {
        return this.indexOf(0, value);
    }

    @Override
    public int indexOf(int offset, char value) {
        int count = offset;
        TCharLink l = this.getLinkAt(offset);
        while (TCharLinkedList.got(l.getNext())) {
            if (l.getValue() == value) {
                return count;
            }
            ++count;
            l = l.getNext();
        }
        return -1;
    }

    @Override
    public int lastIndexOf(char value) {
        return this.lastIndexOf(0, value);
    }

    @Override
    public int lastIndexOf(int offset, char value) {
        if (this.isEmpty()) {
            return -1;
        }
        int last = -1;
        int count = offset;
        TCharLink l = this.getLinkAt(offset);
        while (TCharLinkedList.got(l.getNext())) {
            if (l.getValue() == value) {
                last = count;
            }
            ++count;
            l = l.getNext();
        }
        return last;
    }

    @Override
    public boolean contains(char value) {
        if (this.isEmpty()) {
            return false;
        }
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            if (l.getValue() == value) {
                return true;
            }
            l = l.getNext();
        }
        return false;
    }

    @Override
    public TCharIterator iterator() {
        return new TCharIterator(){
            TCharLink l;
            TCharLink current;
            {
                this.l = TCharLinkedList.this.head;
            }

            public char next() {
                if (TCharLinkedList.no(this.l)) {
                    throw new NoSuchElementException();
                }
                char ret = this.l.getValue();
                this.current = this.l;
                this.l = this.l.getNext();
                return ret;
            }

            public boolean hasNext() {
                return TCharLinkedList.got(this.l);
            }

            public void remove() {
                if (this.current == null) {
                    throw new IllegalStateException();
                }
                TCharLinkedList.this.removeLink(this.current);
                this.current = null;
            }
        };
    }

    @Override
    public TCharList grep(TCharProcedure condition) {
        TCharLinkedList ret = new TCharLinkedList();
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            if (condition.execute(l.getValue())) {
                ret.add(l.getValue());
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public TCharList inverseGrep(TCharProcedure condition) {
        TCharLinkedList ret = new TCharLinkedList();
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            if (!condition.execute(l.getValue())) {
                ret.add(l.getValue());
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public char max() {
        char ret = '\u0000';
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            if (ret < l.getValue()) {
                ret = l.getValue();
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public char min() {
        char ret = '\uffff';
        if (this.isEmpty()) {
            throw new IllegalStateException();
        }
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            if (ret > l.getValue()) {
                ret = l.getValue();
            }
            l = l.getNext();
        }
        return ret;
    }

    @Override
    public char sum() {
        char sum = '\u0000';
        TCharLink l = this.head;
        while (TCharLinkedList.got(l)) {
            sum = (char)(sum + l.getValue());
            l = l.getNext();
        }
        return sum;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeChar(this.no_entry_value);
        out.writeInt(this.size);
        TCharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            char next = iterator.next();
            out.writeChar(next);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this.no_entry_value = in.readChar();
        int len = in.readInt();
        for (int i = 0; i < len; ++i) {
            this.add(in.readChar());
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
        TCharLinkedList that = (TCharLinkedList)o;
        if (this.no_entry_value != that.no_entry_value) {
            return false;
        }
        if (this.size != that.size) {
            return false;
        }
        TCharIterator iterator = this.iterator();
        TCharIterator thatIterator = that.iterator();
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
        TCharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            result = 31 * result + HashFunctions.hash(iterator.next());
        }
        return result;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("{");
        TCharIterator it = this.iterator();
        while (it.hasNext()) {
            char next = it.next();
            buf.append(next);
            if (!it.hasNext()) continue;
            buf.append(", ");
        }
        buf.append("}");
        return buf.toString();
    }

    class RemoveProcedure
    implements TCharProcedure {
        boolean changed = false;

        RemoveProcedure() {
        }

        public boolean execute(char value) {
            if (TCharLinkedList.this.remove(value)) {
                this.changed = true;
            }
            return true;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }

    static class TCharLink {
        char value;
        TCharLink previous;
        TCharLink next;

        TCharLink(char value) {
            this.value = value;
        }

        public char getValue() {
            return this.value;
        }

        public void setValue(char value) {
            this.value = value;
        }

        public TCharLink getPrevious() {
            return this.previous;
        }

        public void setPrevious(TCharLink previous) {
            this.previous = previous;
        }

        public TCharLink getNext() {
            return this.next;
        }

        public void setNext(TCharLink next) {
            this.next = next;
        }
    }

}


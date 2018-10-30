/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongSortedSet;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class LongAVLTreeSet
extends AbstractLongSortedSet
implements Serializable,
Cloneable,
LongSortedSet {
    protected transient Entry tree;
    protected int count;
    protected transient Entry firstEntry;
    protected transient Entry lastEntry;
    protected Comparator<? super Long> storedComparator;
    protected transient LongComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353130L;
    private transient boolean[] dirPath;

    public LongAVLTreeSet() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = LongComparators.asLongComparator(this.storedComparator);
    }

    public LongAVLTreeSet(Comparator<? super Long> c) {
        this();
        this.storedComparator = c;
        this.setActualComparator();
    }

    public LongAVLTreeSet(Collection<? extends Long> c) {
        this();
        this.addAll(c);
    }

    public LongAVLTreeSet(SortedSet<Long> s) {
        this(s.comparator());
        this.addAll(s);
    }

    public LongAVLTreeSet(LongCollection c) {
        this();
        this.addAll(c);
    }

    public LongAVLTreeSet(LongSortedSet s) {
        this(s.comparator());
        this.addAll(s);
    }

    public LongAVLTreeSet(LongIterator i) {
        this.allocatePaths();
        while (i.hasNext()) {
            this.add(i.nextLong());
        }
    }

    public LongAVLTreeSet(Iterator<?> i) {
        this(LongIterators.asLongIterator(i));
    }

    public LongAVLTreeSet(long[] a, int offset, int length, Comparator<? super Long> c) {
        this(c);
        LongArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; ++i) {
            this.add(a[offset + i]);
        }
    }

    public LongAVLTreeSet(long[] a, int offset, int length) {
        this(a, offset, length, null);
    }

    public LongAVLTreeSet(long[] a) {
        this();
        int i = a.length;
        while (i-- != 0) {
            this.add(a[i]);
        }
    }

    public LongAVLTreeSet(long[] a, Comparator<? super Long> c) {
        this(c);
        int i = a.length;
        while (i-- != 0) {
            this.add(a[i]);
        }
    }

    final int compare(long k1, long k2) {
        return this.actualComparator == null ? Long.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    private Entry findKey(long k) {
        int cmp;
        Entry e = this.tree;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            e = cmp < 0 ? e.left() : e.right();
        }
        return e;
    }

    final Entry locateKey(long k) {
        Entry e = this.tree;
        Entry last = this.tree;
        int cmp = 0;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            last = e;
            e = cmp < 0 ? e.left() : e.right();
        }
        return cmp == 0 ? e : last;
    }

    private void allocatePaths() {
        this.dirPath = new boolean[48];
    }

    @Override
    public boolean add(long k) {
        if (this.tree == null) {
            ++this.count;
            this.lastEntry = this.firstEntry = new Entry(k);
            this.tree = this.firstEntry;
        } else {
            Entry p = this.tree;
            Entry q = null;
            Entry y = this.tree;
            Entry z = null;
            Entry e = null;
            Entry w = null;
            int i = 0;
            do {
                int cmp;
                if ((cmp = this.compare(k, p.key)) == 0) {
                    return false;
                }
                if (p.balance() != 0) {
                    i = 0;
                    z = q;
                    y = p;
                }
                if (this.dirPath[i++] = cmp > 0) {
                    if (p.succ()) {
                        ++this.count;
                        e = new Entry(k);
                        if (p.right == null) {
                            this.lastEntry = e;
                        }
                        e.left = p;
                        e.right = p.right;
                        p.right(e);
                        break;
                    }
                    q = p;
                    p = p.right;
                    continue;
                }
                if (p.pred()) {
                    ++this.count;
                    e = new Entry(k);
                    if (p.left == null) {
                        this.firstEntry = e;
                    }
                    e.right = p;
                    e.left = p.left;
                    p.left(e);
                    break;
                }
                q = p;
                p = p.left;
            } while (true);
            p = y;
            i = 0;
            while (p != e) {
                if (this.dirPath[i]) {
                    p.incBalance();
                } else {
                    p.decBalance();
                }
                p = this.dirPath[i++] ? p.right : p.left;
            }
            if (y.balance() == -2) {
                Entry x = y.left;
                if (x.balance() == -1) {
                    w = x;
                    if (x.succ()) {
                        x.succ(false);
                        y.pred(x);
                    } else {
                        y.left = x.right;
                    }
                    x.right = y;
                    x.balance(0);
                    y.balance(0);
                } else {
                    assert (x.balance() == 1);
                    w = x.right;
                    x.right = w.left;
                    w.left = x;
                    y.left = w.right;
                    w.right = y;
                    if (w.balance() == -1) {
                        x.balance(0);
                        y.balance(1);
                    } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                    } else {
                        x.balance(-1);
                        y.balance(0);
                    }
                    w.balance(0);
                    if (w.pred()) {
                        x.succ(w);
                        w.pred(false);
                    }
                    if (w.succ()) {
                        y.pred(w);
                        w.succ(false);
                    }
                }
            } else if (y.balance() == 2) {
                Entry x = y.right;
                if (x.balance() == 1) {
                    w = x;
                    if (x.pred()) {
                        x.pred(false);
                        y.succ(x);
                    } else {
                        y.right = x.left;
                    }
                    x.left = y;
                    x.balance(0);
                    y.balance(0);
                } else {
                    assert (x.balance() == -1);
                    w = x.left;
                    x.left = w.right;
                    w.right = x;
                    y.right = w.left;
                    w.left = y;
                    if (w.balance() == 1) {
                        x.balance(0);
                        y.balance(-1);
                    } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                    } else {
                        x.balance(1);
                        y.balance(0);
                    }
                    w.balance(0);
                    if (w.pred()) {
                        y.succ(w);
                        w.pred(false);
                    }
                    if (w.succ()) {
                        x.pred(w);
                        w.succ(false);
                    }
                }
            } else {
                return true;
            }
            if (z == null) {
                this.tree = w;
            } else if (z.left == y) {
                z.left = w;
            } else {
                z.right = w;
            }
        }
        return true;
    }

    private Entry parent(Entry e) {
        Entry y;
        if (e == this.tree) {
            return null;
        }
        Entry x = y = e;
        do {
            if (y.succ()) {
                Entry p = y.right;
                if (p == null || p.left != e) {
                    while (!x.pred()) {
                        x = x.left;
                    }
                    p = x.left;
                }
                return p;
            }
            if (x.pred()) {
                Entry p = x.left;
                if (p == null || p.right != e) {
                    while (!y.succ()) {
                        y = y.right;
                    }
                    p = y.right;
                }
                return p;
            }
            x = x.left;
            y = y.right;
        } while (true);
    }

    @Override
    public boolean remove(long k) {
        int cmp;
        if (this.tree == null) {
            return false;
        }
        Entry p = this.tree;
        Entry q = null;
        boolean dir = false;
        long kk = k;
        while ((cmp = this.compare(kk, p.key)) != 0) {
            dir = cmp > 0;
            if (dir) {
                q = p;
                if ((p = p.right()) != null) continue;
                return false;
            }
            q = p;
            if ((p = p.left()) != null) continue;
            return false;
        }
        if (p.left == null) {
            this.firstEntry = p.next();
        }
        if (p.right == null) {
            this.lastEntry = p.prev();
        }
        if (p.succ()) {
            if (p.pred()) {
                if (q != null) {
                    if (dir) {
                        q.succ(p.right);
                    } else {
                        q.pred(p.left);
                    }
                } else {
                    this.tree = dir ? p.right : p.left;
                }
            } else {
                p.prev().right = p.right;
                if (q != null) {
                    if (dir) {
                        q.right = p.left;
                    } else {
                        q.left = p.left;
                    }
                } else {
                    this.tree = p.left;
                }
            }
        } else {
            Entry r = p.right;
            if (r.pred()) {
                r.left = p.left;
                r.pred(p.pred());
                if (!r.pred()) {
                    r.prev().right = r;
                }
                if (q != null) {
                    if (dir) {
                        q.right = r;
                    } else {
                        q.left = r;
                    }
                } else {
                    this.tree = r;
                }
                r.balance(p.balance());
                q = r;
                dir = true;
            } else {
                Entry s;
                while (!(s = r.left).pred()) {
                    r = s;
                }
                if (s.succ()) {
                    r.pred(s);
                } else {
                    r.left = s.right;
                }
                s.left = p.left;
                if (!p.pred()) {
                    p.prev().right = s;
                    s.pred(false);
                }
                s.right = p.right;
                s.succ(false);
                if (q != null) {
                    if (dir) {
                        q.right = s;
                    } else {
                        q.left = s;
                    }
                } else {
                    this.tree = s;
                }
                s.balance(p.balance());
                q = r;
                dir = false;
            }
        }
        while (q != null) {
            Entry w;
            Entry x;
            Entry y = q;
            q = this.parent(y);
            if (!dir) {
                dir = q != null && q.left != y;
                y.incBalance();
                if (y.balance() == 1) break;
                if (y.balance() != 2) continue;
                x = y.right;
                assert (x != null);
                if (x.balance() == -1) {
                    assert (x.balance() == -1);
                    w = x.left;
                    x.left = w.right;
                    w.right = x;
                    y.right = w.left;
                    w.left = y;
                    if (w.balance() == 1) {
                        x.balance(0);
                        y.balance(-1);
                    } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                    } else {
                        assert (w.balance() == -1);
                        x.balance(1);
                        y.balance(0);
                    }
                    w.balance(0);
                    if (w.pred()) {
                        y.succ(w);
                        w.pred(false);
                    }
                    if (w.succ()) {
                        x.pred(w);
                        w.succ(false);
                    }
                    if (q != null) {
                        if (dir) {
                            q.right = w;
                            continue;
                        }
                        q.left = w;
                        continue;
                    }
                    this.tree = w;
                    continue;
                }
                if (q != null) {
                    if (dir) {
                        q.right = x;
                    } else {
                        q.left = x;
                    }
                } else {
                    this.tree = x;
                }
                if (x.balance() == 0) {
                    y.right = x.left;
                    x.left = y;
                    x.balance(-1);
                    y.balance(1);
                    break;
                }
                assert (x.balance() == 1);
                if (x.pred()) {
                    y.succ(true);
                    x.pred(false);
                } else {
                    y.right = x.left;
                }
                x.left = y;
                y.balance(0);
                x.balance(0);
                continue;
            }
            dir = q != null && q.left != y;
            y.decBalance();
            if (y.balance() == -1) break;
            if (y.balance() != -2) continue;
            x = y.left;
            assert (x != null);
            if (x.balance() == 1) {
                assert (x.balance() == 1);
                w = x.right;
                x.right = w.left;
                w.left = x;
                y.left = w.right;
                w.right = y;
                if (w.balance() == -1) {
                    x.balance(0);
                    y.balance(1);
                } else if (w.balance() == 0) {
                    x.balance(0);
                    y.balance(0);
                } else {
                    assert (w.balance() == 1);
                    x.balance(-1);
                    y.balance(0);
                }
                w.balance(0);
                if (w.pred()) {
                    x.succ(w);
                    w.pred(false);
                }
                if (w.succ()) {
                    y.pred(w);
                    w.succ(false);
                }
                if (q != null) {
                    if (dir) {
                        q.right = w;
                        continue;
                    }
                    q.left = w;
                    continue;
                }
                this.tree = w;
                continue;
            }
            if (q != null) {
                if (dir) {
                    q.right = x;
                } else {
                    q.left = x;
                }
            } else {
                this.tree = x;
            }
            if (x.balance() == 0) {
                y.left = x.right;
                x.right = y;
                x.balance(1);
                y.balance(-1);
                break;
            }
            assert (x.balance() == -1);
            if (x.succ()) {
                y.pred(true);
                x.succ(false);
            } else {
                y.left = x.right;
            }
            x.right = y;
            y.balance(0);
            x.balance(0);
        }
        --this.count;
        return true;
    }

    @Override
    public boolean contains(long k) {
        return this.findKey(k) != null;
    }

    @Override
    public void clear() {
        this.count = 0;
        this.tree = null;
        this.lastEntry = null;
        this.firstEntry = null;
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override
    public long firstLong() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override
    public long lastLong() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    @Override
    public LongBidirectionalIterator iterator() {
        return new SetIterator();
    }

    @Override
    public LongBidirectionalIterator iterator(long from) {
        return new SetIterator(from);
    }

    @Override
    public LongComparator comparator() {
        return this.actualComparator;
    }

    @Override
    public LongSortedSet headSet(long to) {
        return new Subset(0L, true, to, false);
    }

    @Override
    public LongSortedSet tailSet(long from) {
        return new Subset(from, false, 0L, true);
    }

    @Override
    public LongSortedSet subSet(long from, long to) {
        return new Subset(from, false, to, false);
    }

    public Object clone() {
        LongAVLTreeSet c;
        try {
            c = (LongAVLTreeSet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.allocatePaths();
        if (this.count != 0) {
            Entry rp = new Entry();
            Entry rq = new Entry();
            Entry p = rp;
            rp.left(this.tree);
            Entry q = rq;
            rq.pred(null);
            do {
                Entry e;
                if (!p.pred()) {
                    e = p.left.clone();
                    e.pred(q.left);
                    e.succ(q);
                    q.left(e);
                    p = p.left;
                    q = q.left;
                } else {
                    while (p.succ()) {
                        p = p.right;
                        if (p == null) {
                            q.right = null;
                            c.firstEntry = c.tree = rq.left;
                            while (c.firstEntry.left != null) {
                                c.firstEntry = c.firstEntry.left;
                            }
                            c.lastEntry = c.tree;
                            while (c.lastEntry.right != null) {
                                c.lastEntry = c.lastEntry.right;
                            }
                            return c;
                        }
                        q = q.right;
                    }
                    p = p.right;
                    q = q.right;
                }
                if (p.succ()) continue;
                e = p.right.clone();
                e.succ(q.right);
                e.pred(q);
                q.right(e);
            } while (true);
        }
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int n = this.count;
        SetIterator i = new SetIterator();
        s.defaultWriteObject();
        while (n-- != 0) {
            s.writeLong(i.nextLong());
        }
    }

    private Entry readTree(ObjectInputStream s, int n, Entry pred, Entry succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            Entry top = new Entry(s.readLong());
            top.pred(pred);
            top.succ(succ);
            return top;
        }
        if (n == 2) {
            Entry top = new Entry(s.readLong());
            top.right(new Entry(s.readLong()));
            top.right.pred(top);
            top.balance(1);
            top.pred(pred);
            top.right.succ(succ);
            return top;
        }
        int rightN = n / 2;
        int leftN = n - rightN - 1;
        Entry top = new Entry();
        top.left(this.readTree(s, leftN, pred, top));
        top.key = s.readLong();
        top.right(this.readTree(s, rightN, top, succ));
        if (n == (n & - n)) {
            top.balance(1);
        }
        return top;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.setActualComparator();
        this.allocatePaths();
        if (this.count != 0) {
            Entry e = this.tree = this.readTree(s, this.count, null, null);
            while (e.left() != null) {
                e = e.left();
            }
            this.firstEntry = e;
            e = this.tree;
            while (e.right() != null) {
                e = e.right();
            }
            this.lastEntry = e;
        }
    }

    private final class Subset
    extends AbstractLongSortedSet
    implements Serializable,
    LongSortedSet {
        private static final long serialVersionUID = -7046029254386353129L;
        long from;
        long to;
        boolean bottom;
        boolean top;

        public Subset(long from, boolean bottom, long to, boolean top) {
            if (!bottom && !top && LongAVLTreeSet.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start element (" + from + ") is larger than end element (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
        }

        @Override
        public void clear() {
            SubsetIterator i = new SubsetIterator();
            while (i.hasNext()) {
                i.nextLong();
                i.remove();
            }
        }

        final boolean in(long k) {
            return !(!this.bottom && LongAVLTreeSet.this.compare(k, this.from) < 0 || !this.top && LongAVLTreeSet.this.compare(k, this.to) >= 0);
        }

        @Override
        public boolean contains(long k) {
            return this.in(k) && LongAVLTreeSet.this.contains(k);
        }

        @Override
        public boolean add(long k) {
            if (!this.in(k)) {
                throw new IllegalArgumentException("Element (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            return LongAVLTreeSet.this.add(k);
        }

        @Override
        public boolean remove(long k) {
            if (!this.in(k)) {
                return false;
            }
            return LongAVLTreeSet.this.remove(k);
        }

        @Override
        public int size() {
            SubsetIterator i = new SubsetIterator();
            int n = 0;
            while (i.hasNext()) {
                ++n;
                i.nextLong();
            }
            return n;
        }

        @Override
        public boolean isEmpty() {
            return !new SubsetIterator().hasNext();
        }

        @Override
        public LongComparator comparator() {
            return LongAVLTreeSet.this.actualComparator;
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return new SubsetIterator();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return new SubsetIterator(from);
        }

        @Override
        public LongSortedSet headSet(long to) {
            if (this.top) {
                return new Subset(this.from, this.bottom, to, false);
            }
            return LongAVLTreeSet.this.compare(to, this.to) < 0 ? new Subset(this.from, this.bottom, to, false) : this;
        }

        @Override
        public LongSortedSet tailSet(long from) {
            if (this.bottom) {
                return new Subset(from, false, this.to, this.top);
            }
            return LongAVLTreeSet.this.compare(from, this.from) > 0 ? new Subset(from, false, this.to, this.top) : this;
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            if (this.top && this.bottom) {
                return new Subset(from, false, to, false);
            }
            if (!this.top) {
                long l = to = LongAVLTreeSet.this.compare(to, this.to) < 0 ? to : this.to;
            }
            if (!this.bottom) {
                long l = from = LongAVLTreeSet.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Subset(from, false, to, false);
        }

        public Entry firstEntry() {
            Entry e;
            if (LongAVLTreeSet.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e = LongAVLTreeSet.this.firstEntry;
            } else {
                e = LongAVLTreeSet.this.locateKey(this.from);
                if (LongAVLTreeSet.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || !this.top && LongAVLTreeSet.this.compare(e.key, this.to) >= 0) {
                return null;
            }
            return e;
        }

        public Entry lastEntry() {
            Entry e;
            if (LongAVLTreeSet.this.tree == null) {
                return null;
            }
            if (this.top) {
                e = LongAVLTreeSet.this.lastEntry;
            } else {
                e = LongAVLTreeSet.this.locateKey(this.to);
                if (LongAVLTreeSet.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || !this.bottom && LongAVLTreeSet.this.compare(e.key, this.from) < 0) {
                return null;
            }
            return e;
        }

        @Override
        public long firstLong() {
            Entry e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        @Override
        public long lastLong() {
            Entry e = this.lastEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        private final class SubsetIterator
        extends SetIterator {
            SubsetIterator() {
                super();
                this.next = Subset.this.firstEntry();
            }

            /*
             * Enabled aggressive block sorting
             */
            SubsetIterator(long k) {
                this();
                if (this.next == null) return;
                if (!subset.bottom && subset.LongAVLTreeSet.this.compare(k, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!subset.top) {
                    this.prev = subset.lastEntry();
                    if (subset.LongAVLTreeSet.this.compare(k, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = subset.LongAVLTreeSet.this.locateKey(k);
                if (subset.LongAVLTreeSet.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Subset.this.bottom && this.prev != null && LongAVLTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Subset.this.top && this.next != null && LongAVLTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

    }

    private class SetIterator
    implements LongListIterator {
        Entry prev;
        Entry next;
        Entry curr;
        int index = 0;

        SetIterator() {
            this.next = LongAVLTreeSet.this.firstEntry;
        }

        SetIterator(long k) {
            this.next = LongAVLTreeSet.this.locateKey(k);
            if (this.next != null) {
                if (LongAVLTreeSet.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                } else {
                    this.prev = this.next.prev();
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public boolean hasPrevious() {
            return this.prev != null;
        }

        void updateNext() {
            this.next = this.next.next();
        }

        Entry nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
        }

        @Override
        public long nextLong() {
            return this.nextEntry().key;
        }

        @Override
        public long previousLong() {
            return this.previousEntry().key;
        }

        void updatePrevious() {
            this.prev = this.prev.prev();
        }

        Entry previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = this.next = this.prev;
            --this.index;
            this.updatePrevious();
            return this.curr;
        }

        @Override
        public int nextIndex() {
            return this.index;
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
        public void remove() {
            if (this.curr == null) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
            }
            this.next = this.prev = this.curr;
            this.updatePrevious();
            this.updateNext();
            LongAVLTreeSet.this.remove(this.curr.key);
            this.curr = null;
        }
    }

    private static final class Entry
    implements Cloneable {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 1073741824;
        private static final int BALANCE_MASK = 255;
        long key;
        Entry left;
        Entry right;
        int info;

        Entry() {
        }

        Entry(long k) {
            this.key = k;
            this.info = -1073741824;
        }

        Entry left() {
            return (this.info & 1073741824) != 0 ? null : this.left;
        }

        Entry right() {
            return (this.info & Integer.MIN_VALUE) != 0 ? null : this.right;
        }

        boolean pred() {
            return (this.info & 1073741824) != 0;
        }

        boolean succ() {
            return (this.info & Integer.MIN_VALUE) != 0;
        }

        void pred(boolean pred) {
            this.info = pred ? (this.info |= 1073741824) : (this.info &= -1073741825);
        }

        void succ(boolean succ) {
            this.info = succ ? (this.info |= Integer.MIN_VALUE) : (this.info &= Integer.MAX_VALUE);
        }

        void pred(Entry pred) {
            this.info |= 1073741824;
            this.left = pred;
        }

        void succ(Entry succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry left) {
            this.info &= -1073741825;
            this.left = left;
        }

        void right(Entry right) {
            this.info &= Integer.MAX_VALUE;
            this.right = right;
        }

        int balance() {
            return (byte)this.info;
        }

        void balance(int level) {
            this.info &= -256;
            this.info |= level & 255;
        }

        void incBalance() {
            this.info = this.info & -256 | (byte)this.info + 1 & 255;
        }

        protected void decBalance() {
            this.info = this.info & -256 | (byte)this.info - 1 & 255;
        }

        Entry next() {
            Entry next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 1073741824) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry prev() {
            Entry prev = this.left;
            if ((this.info & 1073741824) == 0) {
                while ((prev.info & Integer.MIN_VALUE) == 0) {
                    prev = prev.right;
                }
            }
            return prev;
        }

        public Entry clone() {
            Entry c;
            try {
                c = (Entry)super.clone();
            }
            catch (CloneNotSupportedException cantHappen) {
                throw new InternalError();
            }
            c.key = this.key;
            c.info = this.info;
            return c;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry e = (Entry)o;
            return this.key == e.key;
        }

        public int hashCode() {
            return HashCommon.long2int(this.key);
        }

        public String toString() {
            return String.valueOf(this.key);
        }
    }

}


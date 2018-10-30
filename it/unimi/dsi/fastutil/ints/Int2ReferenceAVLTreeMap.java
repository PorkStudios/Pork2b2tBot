/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2ReferenceMap;
import it.unimi.dsi.fastutil.ints.AbstractInt2ReferenceSortedMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceSortedMap;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class Int2ReferenceAVLTreeMap<V>
extends AbstractInt2ReferenceSortedMap<V>
implements Serializable,
Cloneable {
    protected transient Entry<V> tree;
    protected int count;
    protected transient Entry<V> firstEntry;
    protected transient Entry<V> lastEntry;
    protected transient ObjectSortedSet<Int2ReferenceMap.Entry<V>> entries;
    protected transient IntSortedSet keys;
    protected transient ReferenceCollection<V> values;
    protected transient boolean modified;
    protected Comparator<? super Integer> storedComparator;
    protected transient IntComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private transient boolean[] dirPath;

    public Int2ReferenceAVLTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = IntComparators.asIntComparator(this.storedComparator);
    }

    public Int2ReferenceAVLTreeMap(Comparator<? super Integer> c) {
        this();
        this.storedComparator = c;
    }

    public Int2ReferenceAVLTreeMap(Map<? extends Integer, ? extends V> m) {
        this();
        this.putAll(m);
    }

    public Int2ReferenceAVLTreeMap(SortedMap<Integer, V> m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Int2ReferenceAVLTreeMap(Int2ReferenceMap<? extends V> m) {
        this();
        this.putAll(m);
    }

    public Int2ReferenceAVLTreeMap(Int2ReferenceSortedMap<V> m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Int2ReferenceAVLTreeMap(int[] k, V[] v, Comparator<? super Integer> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Int2ReferenceAVLTreeMap(int[] k, V[] v) {
        this(k, v, null);
    }

    final int compare(int k1, int k2) {
        return this.actualComparator == null ? Integer.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry<V> findKey(int k) {
        int cmp;
        Entry<V> e = this.tree;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            e = cmp < 0 ? e.left() : e.right();
        }
        return e;
    }

    final Entry<V> locateKey(int k) {
        Entry<V> e = this.tree;
        Entry<V> last = this.tree;
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
    public V put(int k, V v) {
        Entry<V> e = this.add(k);
        Object oldValue = e.value;
        e.value = v;
        return (V)oldValue;
    }

    private Entry<V> add(int k) {
        this.modified = false;
        Entry<Object> e = null;
        if (this.tree == null) {
            ++this.count;
            this.firstEntry = new Entry<Object>(k, this.defRetValue);
            this.lastEntry = this.firstEntry;
            this.tree = this.firstEntry;
            e = this.firstEntry;
            this.modified = true;
        } else {
            Entry<Object> p = this.tree;
            Entry<Object> q = null;
            Entry y = this.tree;
            Entry<Object> z = null;
            Entry w = null;
            int i = 0;
            do {
                int cmp;
                if ((cmp = this.compare(k, p.key)) == 0) {
                    return p;
                }
                if (p.balance() != 0) {
                    i = 0;
                    z = q;
                    y = p;
                }
                if (this.dirPath[i++] = cmp > 0) {
                    if (p.succ()) {
                        ++this.count;
                        e = new Entry<Object>(k, this.defRetValue);
                        this.modified = true;
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
                    e = new Entry<Object>(k, this.defRetValue);
                    this.modified = true;
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
                return e;
            }
            if (z == null) {
                this.tree = w;
            } else if (z.left == y) {
                z.left = w;
            } else {
                z.right = w;
            }
        }
        return e;
    }

    private Entry<V> parent(Entry<V> e) {
        Entry<V> y;
        if (e == this.tree) {
            return null;
        }
        Entry<V> x = y = e;
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
    public V remove(int k) {
        int cmp;
        this.modified = false;
        if (this.tree == null) {
            return (V)this.defRetValue;
        }
        Entry p = this.tree;
        Entry q = null;
        boolean dir = false;
        int kk = k;
        while ((cmp = this.compare(kk, p.key)) != 0) {
            dir = cmp > 0;
            if (dir) {
                q = p;
                if ((p = p.right()) != null) continue;
                return (V)this.defRetValue;
            }
            q = p;
            if ((p = p.left()) != null) continue;
            return (V)this.defRetValue;
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
            Entry x;
            Entry w;
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
        this.modified = true;
        --this.count;
        return (V)p.value;
    }

    @Override
    public boolean containsValue(Object v) {
        ValueIterator i = new ValueIterator();
        int j = this.count;
        while (j-- != 0) {
            Object ev = i.next();
            if (ev != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.count = 0;
        this.tree = null;
        this.entries = null;
        this.values = null;
        this.keys = null;
        this.lastEntry = null;
        this.firstEntry = null;
    }

    @Override
    public boolean containsKey(int k) {
        return this.findKey(k) != null;
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
    public V get(int k) {
        Entry<V> e = this.findKey(k);
        return (V)(e == null ? this.defRetValue : e.value);
    }

    @Override
    public int firstIntKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override
    public int lastIntKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    @Override
    public ObjectSortedSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Int2ReferenceMap.Entry<V>>(){
                final Comparator<? super Int2ReferenceMap.Entry<V>> comparator = (x, y) -> Int2ReferenceAVLTreeMap.this.actualComparator.compare(x.getIntKey(), y.getIntKey());

                @Override
                public Comparator<? super Int2ReferenceMap.Entry<V>> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> iterator(Int2ReferenceMap.Entry<V> from) {
                    return new EntryIterator(from.getIntKey());
                }

                @Override
                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                        return false;
                    }
                    Entry f = Int2ReferenceAVLTreeMap.this.findKey((Integer)e.getKey());
                    return e.equals(f);
                }

                @Override
                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                        return false;
                    }
                    Entry f = Int2ReferenceAVLTreeMap.this.findKey((Integer)e.getKey());
                    if (f == null || f.getValue() != e.getValue()) {
                        return false;
                    }
                    Int2ReferenceAVLTreeMap.this.remove(f.key);
                    return true;
                }

                @Override
                public int size() {
                    return Int2ReferenceAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Int2ReferenceAVLTreeMap.this.clear();
                }

                @Override
                public Int2ReferenceMap.Entry<V> first() {
                    return Int2ReferenceAVLTreeMap.this.firstEntry;
                }

                @Override
                public Int2ReferenceMap.Entry<V> last() {
                    return Int2ReferenceAVLTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Int2ReferenceMap.Entry<V>> subSet(Int2ReferenceMap.Entry<V> from, Int2ReferenceMap.Entry<V> to) {
                    return Int2ReferenceAVLTreeMap.this.subMap(from.getIntKey(), to.getIntKey()).int2ReferenceEntrySet();
                }

                @Override
                public ObjectSortedSet<Int2ReferenceMap.Entry<V>> headSet(Int2ReferenceMap.Entry<V> to) {
                    return Int2ReferenceAVLTreeMap.this.headMap(to.getIntKey()).int2ReferenceEntrySet();
                }

                @Override
                public ObjectSortedSet<Int2ReferenceMap.Entry<V>> tailSet(Int2ReferenceMap.Entry<V> from) {
                    return Int2ReferenceAVLTreeMap.this.tailMap(from.getIntKey()).int2ReferenceEntrySet();
                }
            };
        }
        return this.entries;
    }

    @Override
    public IntSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ReferenceCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(Object k) {
                    return Int2ReferenceAVLTreeMap.this.containsValue(k);
                }

                @Override
                public int size() {
                    return Int2ReferenceAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Int2ReferenceAVLTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override
    public IntComparator comparator() {
        return this.actualComparator;
    }

    @Override
    public Int2ReferenceSortedMap<V> headMap(int to) {
        return new Submap(0, true, to, false);
    }

    @Override
    public Int2ReferenceSortedMap<V> tailMap(int from) {
        return new Submap(from, false, 0, true);
    }

    @Override
    public Int2ReferenceSortedMap<V> subMap(int from, int to) {
        return new Submap(from, false, to, false);
    }

    public Int2ReferenceAVLTreeMap<V> clone() {
        Int2ReferenceAVLTreeMap c;
        try {
            c = (Int2ReferenceAVLTreeMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.allocatePaths();
        if (this.count != 0) {
            Entry<V> rp = new Entry<V>();
            Entry rq = new Entry();
            Entry<V> p = rp;
            rp.left(this.tree);
            Entry q = rq;
            rq.pred(null);
            do {
                Object e;
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
                            c.tree = rq.left;
                            c.firstEntry = c.tree;
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
        EntryIterator i = new EntryIterator();
        s.defaultWriteObject();
        while (n-- != 0) {
            Entry e = i.nextEntry();
            s.writeInt(e.key);
            s.writeObject(e.value);
        }
    }

    private Entry<V> readTree(ObjectInputStream s, int n, Entry<V> pred, Entry<V> succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            Entry<Object> top = new Entry<Object>(s.readInt(), s.readObject());
            top.pred(pred);
            top.succ(succ);
            return top;
        }
        if (n == 2) {
            Entry<Object> top = new Entry<Object>(s.readInt(), s.readObject());
            top.right(new Entry<Object>(s.readInt(), s.readObject()));
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
        top.key = s.readInt();
        top.value = s.readObject();
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
            this.tree = this.readTree(s, this.count, null, null);
            Entry<V> e = this.tree;
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

    private final class Submap
    extends AbstractInt2ReferenceSortedMap<V>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        int from;
        int to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Int2ReferenceMap.Entry<V>> entries;
        protected transient IntSortedSet keys;
        protected transient ReferenceCollection<V> values;

        public Submap(int from, boolean bottom, int to, boolean top) {
            if (!bottom && !top && Int2ReferenceAVLTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Int2ReferenceAVLTreeMap.this.defRetValue;
        }

        @Override
        public void clear() {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                i.nextEntry();
                i.remove();
            }
        }

        final boolean in(int k) {
            return !(!this.bottom && Int2ReferenceAVLTreeMap.this.compare(k, this.from) < 0 || !this.top && Int2ReferenceAVLTreeMap.this.compare(k, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Int2ReferenceMap.Entry<V>>(){

                    @Override
                    public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> iterator(Int2ReferenceMap.Entry<V> from) {
                        return new SubmapEntryIterator(from.getIntKey());
                    }

                    @Override
                    public Comparator<? super Int2ReferenceMap.Entry<V>> comparator() {
                        return Int2ReferenceAVLTreeMap.this.int2ReferenceEntrySet().comparator();
                    }

                    @Override
                    public boolean contains(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                            return false;
                        }
                        Entry f = Int2ReferenceAVLTreeMap.this.findKey((Integer)e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                    }

                    @Override
                    public boolean remove(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                            return false;
                        }
                        Entry f = Int2ReferenceAVLTreeMap.this.findKey((Integer)e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                            Submap.this.remove(f.key);
                        }
                        return f != null;
                    }

                    @Override
                    public int size() {
                        int c = 0;
                        ObjectIterator i = this.iterator();
                        while (i.hasNext()) {
                            ++c;
                            i.next();
                        }
                        return c;
                    }

                    @Override
                    public boolean isEmpty() {
                        return !new SubmapIterator().hasNext();
                    }

                    @Override
                    public void clear() {
                        Submap.this.clear();
                    }

                    @Override
                    public Int2ReferenceMap.Entry<V> first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Int2ReferenceMap.Entry<V> last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Int2ReferenceMap.Entry<V>> subSet(Int2ReferenceMap.Entry<V> from, Int2ReferenceMap.Entry<V> to) {
                        return Submap.this.subMap(from.getIntKey(), to.getIntKey()).int2ReferenceEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Int2ReferenceMap.Entry<V>> headSet(Int2ReferenceMap.Entry<V> to) {
                        return Submap.this.headMap(to.getIntKey()).int2ReferenceEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Int2ReferenceMap.Entry<V>> tailSet(Int2ReferenceMap.Entry<V> from) {
                        return Submap.this.tailMap(from.getIntKey()).int2ReferenceEntrySet();
                    }
                };
            }
            return this.entries;
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = new KeySet();
            }
            return this.keys;
        }

        @Override
        public ReferenceCollection<V> values() {
            if (this.values == null) {
                this.values = new AbstractReferenceCollection<V>(){

                    @Override
                    public ObjectIterator<V> iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(Object k) {
                        return Submap.this.containsValue(k);
                    }

                    @Override
                    public int size() {
                        return Submap.this.size();
                    }

                    @Override
                    public void clear() {
                        Submap.this.clear();
                    }
                };
            }
            return this.values;
        }

        @Override
        public boolean containsKey(int k) {
            return this.in(k) && Int2ReferenceAVLTreeMap.this.containsKey(k);
        }

        @Override
        public boolean containsValue(Object v) {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                Object ev = i.nextEntry().value;
                if (ev != v) continue;
                return true;
            }
            return false;
        }

        @Override
        public V get(int k) {
            Entry e;
            int kk = k;
            return (V)(this.in(kk) && (e = Int2ReferenceAVLTreeMap.this.findKey(kk)) != null ? e.value : this.defRetValue);
        }

        @Override
        public V put(int k, V v) {
            Int2ReferenceAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            V oldValue = Int2ReferenceAVLTreeMap.this.put(k, v);
            return (V)(Int2ReferenceAVLTreeMap.this.modified ? this.defRetValue : oldValue);
        }

        @Override
        public V remove(int k) {
            Int2ReferenceAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                return (V)this.defRetValue;
            }
            Object oldValue = Int2ReferenceAVLTreeMap.this.remove(k);
            return (V)(Int2ReferenceAVLTreeMap.this.modified ? oldValue : this.defRetValue);
        }

        @Override
        public int size() {
            SubmapIterator i = new SubmapIterator();
            int n = 0;
            while (i.hasNext()) {
                ++n;
                i.nextEntry();
            }
            return n;
        }

        @Override
        public boolean isEmpty() {
            return !new SubmapIterator().hasNext();
        }

        @Override
        public IntComparator comparator() {
            return Int2ReferenceAVLTreeMap.this.actualComparator;
        }

        @Override
        public Int2ReferenceSortedMap<V> headMap(int to) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to, false);
            }
            return Int2ReferenceAVLTreeMap.this.compare(to, this.to) < 0 ? new Submap(this.from, this.bottom, to, false) : this;
        }

        @Override
        public Int2ReferenceSortedMap<V> tailMap(int from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Int2ReferenceAVLTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Int2ReferenceSortedMap<V> subMap(int from, int to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                int n = to = Int2ReferenceAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }
            if (!this.bottom) {
                int n = from = Int2ReferenceAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Submap(from, false, to, false);
        }

        public Entry<V> firstEntry() {
            Entry e;
            if (Int2ReferenceAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e = Int2ReferenceAVLTreeMap.this.firstEntry;
            } else {
                e = Int2ReferenceAVLTreeMap.this.locateKey(this.from);
                if (Int2ReferenceAVLTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || !this.top && Int2ReferenceAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                return null;
            }
            return e;
        }

        public Entry<V> lastEntry() {
            Entry e;
            if (Int2ReferenceAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e = Int2ReferenceAVLTreeMap.this.lastEntry;
            } else {
                e = Int2ReferenceAVLTreeMap.this.locateKey(this.to);
                if (Int2ReferenceAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || !this.bottom && Int2ReferenceAVLTreeMap.this.compare(e.key, this.from) < 0) {
                return null;
            }
            return e;
        }

        @Override
        public int firstIntKey() {
            Entry<V> e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        @Override
        public int lastIntKey() {
            Entry<V> e = this.lastEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        private final class SubmapValueIterator
        extends Int2ReferenceAVLTreeMap<V>
        implements ObjectListIterator<V> {
            private SubmapValueIterator() {
                super();
            }

            @Override
            public V next() {
                return (V)this.nextEntry().value;
            }

            @Override
            public V previous() {
                return (V)this.previousEntry().value;
            }
        }

        private final class SubmapKeyIterator
        extends Int2ReferenceAVLTreeMap<V>
        implements IntListIterator {
            public SubmapKeyIterator() {
                super();
            }

            public SubmapKeyIterator(int from) {
                super(from);
            }

            @Override
            public int nextInt() {
                return this.nextEntry().key;
            }

            @Override
            public int previousInt() {
                return this.previousEntry().key;
            }
        }

        private class SubmapEntryIterator
        extends Int2ReferenceAVLTreeMap<V>
        implements ObjectListIterator<Int2ReferenceMap.Entry<V>> {
            SubmapEntryIterator() {
                super();
            }

            SubmapEntryIterator(int k) {
                super(k);
            }

            @Override
            public Int2ReferenceMap.Entry<V> next() {
                return this.nextEntry();
            }

            @Override
            public Int2ReferenceMap.Entry<V> previous() {
                return this.previousEntry();
            }
        }

        private class SubmapIterator
        extends Int2ReferenceAVLTreeMap<V> {
            SubmapIterator() {
                super();
                this.next = Submap.this.firstEntry();
            }

            /*
             * Enabled aggressive block sorting
             */
            SubmapIterator(int k) {
                this();
                if (this.next == null) return;
                if (!submap.bottom && submap.Int2ReferenceAVLTreeMap.this.compare(k, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Int2ReferenceAVLTreeMap.this.compare(k, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Int2ReferenceAVLTreeMap.this.locateKey(k);
                if (submap.Int2ReferenceAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Int2ReferenceAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Int2ReferenceAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractInt2ReferenceSortedMap<V> {
            private KeySet() {
            }

            public IntBidirectionalIterator iterator() {
                return new SubmapKeyIterator();
            }

            public IntBidirectionalIterator iterator(int from) {
                return new SubmapKeyIterator(from);
            }
        }

    }

    private final class ValueIterator
    extends Int2ReferenceAVLTreeMap<V>
    implements ObjectListIterator<V> {
        private ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return (V)this.nextEntry().value;
        }

        @Override
        public V previous() {
            return (V)this.previousEntry().value;
        }
    }

    private class KeySet
    extends AbstractInt2ReferenceSortedMap<V> {
        private KeySet() {
        }

        public IntBidirectionalIterator iterator() {
            return new KeyIterator();
        }

        public IntBidirectionalIterator iterator(int from) {
            return new KeyIterator(from);
        }
    }

    private final class KeyIterator
    extends Int2ReferenceAVLTreeMap<V>
    implements IntListIterator {
        public KeyIterator() {
            super();
        }

        public KeyIterator(int k) {
            super(k);
        }

        @Override
        public int nextInt() {
            return this.nextEntry().key;
        }

        @Override
        public int previousInt() {
            return this.previousEntry().key;
        }
    }

    private class EntryIterator
    extends Int2ReferenceAVLTreeMap<V>
    implements ObjectListIterator<Int2ReferenceMap.Entry<V>> {
        EntryIterator() {
            super();
        }

        EntryIterator(int k) {
            super(k);
        }

        @Override
        public Int2ReferenceMap.Entry<V> next() {
            return this.nextEntry();
        }

        @Override
        public Int2ReferenceMap.Entry<V> previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Int2ReferenceMap.Entry<V> ok) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Int2ReferenceMap.Entry<V> ok) {
            throw new UnsupportedOperationException();
        }
    }

    private class TreeIterator {
        Entry<V> prev;
        Entry<V> next;
        Entry<V> curr;
        int index = 0;

        TreeIterator() {
            this.next = Int2ReferenceAVLTreeMap.this.firstEntry;
        }

        TreeIterator(int k) {
            this.next = Int2ReferenceAVLTreeMap.this.locateKey(k);
            if (this.next != null) {
                if (Int2ReferenceAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                } else {
                    this.prev = this.next.prev();
                }
            }
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public boolean hasPrevious() {
            return this.prev != null;
        }

        void updateNext() {
            this.next = this.next.next();
        }

        Entry<V> nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.prev = this.next;
            this.curr = this.prev;
            ++this.index;
            this.updateNext();
            return this.curr;
        }

        void updatePrevious() {
            this.prev = this.prev.prev();
        }

        Entry<V> previousEntry() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.next = this.prev;
            this.curr = this.next;
            --this.index;
            this.updatePrevious();
            return this.curr;
        }

        public int nextIndex() {
            return this.index;
        }

        public int previousIndex() {
            return this.index - 1;
        }

        public void remove() {
            if (this.curr == null) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                --this.index;
            }
            this.prev = this.curr;
            this.next = this.prev;
            this.updatePrevious();
            this.updateNext();
            Int2ReferenceAVLTreeMap.this.remove(this.curr.key);
            this.curr = null;
        }

        public int skip(int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }

        public int back(int n) {
            int i = n;
            while (i-- != 0 && this.hasPrevious()) {
                this.previousEntry();
            }
            return n - i - 1;
        }
    }

    private static final class Entry<V>
    extends AbstractInt2ReferenceMap.BasicEntry<V>
    implements Cloneable {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 1073741824;
        private static final int BALANCE_MASK = 255;
        Entry<V> left;
        Entry<V> right;
        int info;

        Entry() {
            super(0, null);
        }

        Entry(int k, V v) {
            super(k, v);
            this.info = -1073741824;
        }

        Entry<V> left() {
            return (this.info & 1073741824) != 0 ? null : this.left;
        }

        Entry<V> right() {
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

        void pred(Entry<V> pred) {
            this.info |= 1073741824;
            this.left = pred;
        }

        void succ(Entry<V> succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry<V> left) {
            this.info &= -1073741825;
            this.left = left;
        }

        void right(Entry<V> right) {
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

        Entry<V> next() {
            Entry<V> next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 1073741824) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry<V> prev() {
            Entry<V> prev = this.left;
            if ((this.info & 1073741824) == 0) {
                while ((prev.info & Integer.MIN_VALUE) == 0) {
                    prev = prev.right;
                }
            }
            return prev;
        }

        @Override
        public V setValue(V value) {
            Object oldValue = this.value;
            this.value = value;
            return (V)oldValue;
        }

        public Entry<V> clone() {
            Entry c;
            try {
                c = (Entry)Object.super.clone();
            }
            catch (CloneNotSupportedException cantHappen) {
                throw new InternalError();
            }
            c.key = this.key;
            c.value = this.value;
            c.info = this.info;
            return c;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return this.key == (Integer)e.getKey() && this.value == e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
        }

        @Override
        public String toString() {
            return "" + this.key + "=>" + this.value;
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatSortedMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class Object2FloatAVLTreeMap<K>
extends AbstractObject2FloatSortedMap<K>
implements Serializable,
Cloneable {
    protected transient Entry<K> tree;
    protected int count;
    protected transient Entry<K> firstEntry;
    protected transient Entry<K> lastEntry;
    protected transient ObjectSortedSet<Object2FloatMap.Entry<K>> entries;
    protected transient ObjectSortedSet<K> keys;
    protected transient FloatCollection values;
    protected transient boolean modified;
    protected Comparator<? super K> storedComparator;
    protected transient Comparator<? super K> actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private transient boolean[] dirPath;

    public Object2FloatAVLTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator;
    }

    public Object2FloatAVLTreeMap(Comparator<? super K> c) {
        this();
        this.storedComparator = c;
    }

    public Object2FloatAVLTreeMap(Map<? extends K, ? extends Float> m) {
        this();
        this.putAll(m);
    }

    public Object2FloatAVLTreeMap(SortedMap<K, Float> m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Object2FloatAVLTreeMap(Object2FloatMap<? extends K> m) {
        this();
        this.putAll(m);
    }

    public Object2FloatAVLTreeMap(Object2FloatSortedMap<K> m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Object2FloatAVLTreeMap(K[] k, float[] v, Comparator<? super K> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2FloatAVLTreeMap(K[] k, float[] v) {
        this(k, v, null);
    }

    final int compare(K k1, K k2) {
        return this.actualComparator == null ? ((Comparable)k1).compareTo(k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry<K> findKey(K k) {
        int cmp;
        Entry<K> e = this.tree;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            e = cmp < 0 ? e.left() : e.right();
        }
        return e;
    }

    final Entry<K> locateKey(K k) {
        Entry<K> e = this.tree;
        Entry<K> last = this.tree;
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

    public float addTo(K k, float incr) {
        Entry<K> e = this.add(k);
        float oldValue = e.value;
        e.value += incr;
        return oldValue;
    }

    @Override
    public float put(K k, float v) {
        Entry<K> e = this.add(k);
        float oldValue = e.value;
        e.value = v;
        return oldValue;
    }

    private Entry<K> add(K k) {
        this.modified = false;
        Entry<K> e = null;
        if (this.tree == null) {
            ++this.count;
            this.firstEntry = new Entry<K>(k, this.defRetValue);
            this.lastEntry = this.firstEntry;
            this.tree = this.firstEntry;
            e = this.firstEntry;
            this.modified = true;
        } else {
            Entry<K> p = this.tree;
            Entry<K> q = null;
            Entry y = this.tree;
            Entry<K> z = null;
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
                        e = new Entry<K>(k, this.defRetValue);
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
                    e = new Entry<K>(k, this.defRetValue);
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

    private Entry<K> parent(Entry<K> e) {
        Entry<K> y;
        if (e == this.tree) {
            return null;
        }
        Entry<K> x = y = e;
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
    public float removeFloat(Object k) {
        int cmp;
        this.modified = false;
        if (this.tree == null) {
            return this.defRetValue;
        }
        Entry p = this.tree;
        Entry q = null;
        boolean dir = false;
        Object kk = k;
        while ((cmp = this.compare(kk, p.key)) != 0) {
            dir = cmp > 0;
            if (dir) {
                q = p;
                if ((p = p.right()) != null) continue;
                return this.defRetValue;
            }
            q = p;
            if ((p = p.left()) != null) continue;
            return this.defRetValue;
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
        this.modified = true;
        --this.count;
        return p.value;
    }

    @Override
    public boolean containsValue(float v) {
        ValueIterator i = new ValueIterator();
        int j = this.count;
        while (j-- != 0) {
            float ev = i.nextFloat();
            if (Float.floatToIntBits(ev) != Float.floatToIntBits(v)) continue;
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
    public boolean containsKey(Object k) {
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
    public float getFloat(Object k) {
        Entry<Object> e = this.findKey(k);
        return e == null ? this.defRetValue : e.value;
    }

    @Override
    public K firstKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return (K)this.firstEntry.key;
    }

    @Override
    public K lastKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return (K)this.lastEntry.key;
    }

    @Override
    public ObjectSortedSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Object2FloatMap.Entry<K>>(){
                final Comparator<? super Object2FloatMap.Entry<K>> comparator = (x, y) -> Object2FloatAVLTreeMap.this.actualComparator.compare(x.getKey(), y.getKey());

                @Override
                public Comparator<? super Object2FloatMap.Entry<K>> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator(Object2FloatMap.Entry<K> from) {
                    return new EntryIterator(from.getKey());
                }

                @Override
                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                        return false;
                    }
                    Entry f = Object2FloatAVLTreeMap.this.findKey(e.getKey());
                    return e.equals(f);
                }

                @Override
                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                        return false;
                    }
                    Entry f = Object2FloatAVLTreeMap.this.findKey(e.getKey());
                    if (f == null || Float.floatToIntBits(f.getFloatValue()) != Float.floatToIntBits(((Float)e.getValue()).floatValue())) {
                        return false;
                    }
                    Object2FloatAVLTreeMap.this.removeFloat(f.key);
                    return true;
                }

                @Override
                public int size() {
                    return Object2FloatAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Object2FloatAVLTreeMap.this.clear();
                }

                @Override
                public Object2FloatMap.Entry<K> first() {
                    return Object2FloatAVLTreeMap.this.firstEntry;
                }

                @Override
                public Object2FloatMap.Entry<K> last() {
                    return Object2FloatAVLTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Object2FloatMap.Entry<K>> subSet(Object2FloatMap.Entry<K> from, Object2FloatMap.Entry<K> to) {
                    return Object2FloatAVLTreeMap.this.subMap(from.getKey(), to.getKey()).object2FloatEntrySet();
                }

                @Override
                public ObjectSortedSet<Object2FloatMap.Entry<K>> headSet(Object2FloatMap.Entry<K> to) {
                    return Object2FloatAVLTreeMap.this.headMap(to.getKey()).object2FloatEntrySet();
                }

                @Override
                public ObjectSortedSet<Object2FloatMap.Entry<K>> tailSet(Object2FloatMap.Entry<K> from) {
                    return Object2FloatAVLTreeMap.this.tailMap(from.getKey()).object2FloatEntrySet();
                }
            };
        }
        return this.entries;
    }

    @Override
    public ObjectSortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public FloatCollection values() {
        if (this.values == null) {
            this.values = new AbstractFloatCollection(){

                @Override
                public FloatIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(float k) {
                    return Object2FloatAVLTreeMap.this.containsValue(k);
                }

                @Override
                public int size() {
                    return Object2FloatAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Object2FloatAVLTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override
    public Comparator<? super K> comparator() {
        return this.actualComparator;
    }

    @Override
    public Object2FloatSortedMap<K> headMap(K to) {
        return new Submap(null, true, to, false);
    }

    @Override
    public Object2FloatSortedMap<K> tailMap(K from) {
        return new Submap(from, false, null, true);
    }

    @Override
    public Object2FloatSortedMap<K> subMap(K from, K to) {
        return new Submap(from, false, to, false);
    }

    public Object2FloatAVLTreeMap<K> clone() {
        Object2FloatAVLTreeMap c;
        try {
            c = (Object2FloatAVLTreeMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.allocatePaths();
        if (this.count != 0) {
            Entry<K> rp = new Entry<K>();
            Entry rq = new Entry();
            Entry<K> p = rp;
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
            s.writeObject(e.key);
            s.writeFloat(e.value);
        }
    }

    private Entry<K> readTree(ObjectInputStream s, int n, Entry<K> pred, Entry<K> succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            Entry<Object> top = new Entry<Object>(s.readObject(), s.readFloat());
            top.pred(pred);
            top.succ(succ);
            return top;
        }
        if (n == 2) {
            Entry<Object> top = new Entry<Object>(s.readObject(), s.readFloat());
            top.right(new Entry<Object>(s.readObject(), s.readFloat()));
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
        top.key = s.readObject();
        top.value = s.readFloat();
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
            Entry<K> e = this.tree;
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
    extends AbstractObject2FloatSortedMap<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        K from;
        K to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Object2FloatMap.Entry<K>> entries;
        protected transient ObjectSortedSet<K> keys;
        protected transient FloatCollection values;

        public Submap(K from, boolean bottom, K to, boolean top) {
            if (!bottom && !top && Object2FloatAVLTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Object2FloatAVLTreeMap.this.defRetValue;
        }

        @Override
        public void clear() {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                i.nextEntry();
                i.remove();
            }
        }

        final boolean in(K k) {
            return !(!this.bottom && Object2FloatAVLTreeMap.this.compare(k, this.from) < 0 || !this.top && Object2FloatAVLTreeMap.this.compare(k, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Object2FloatMap.Entry<K>>(){

                    @Override
                    public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator(Object2FloatMap.Entry<K> from) {
                        return new SubmapEntryIterator(from.getKey());
                    }

                    @Override
                    public Comparator<? super Object2FloatMap.Entry<K>> comparator() {
                        return Object2FloatAVLTreeMap.this.object2FloatEntrySet().comparator();
                    }

                    @Override
                    public boolean contains(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                            return false;
                        }
                        Entry f = Object2FloatAVLTreeMap.this.findKey(e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                    }

                    @Override
                    public boolean remove(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                            return false;
                        }
                        Entry f = Object2FloatAVLTreeMap.this.findKey(e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                            Submap.this.removeFloat(f.key);
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
                    public Object2FloatMap.Entry<K> first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Object2FloatMap.Entry<K> last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Object2FloatMap.Entry<K>> subSet(Object2FloatMap.Entry<K> from, Object2FloatMap.Entry<K> to) {
                        return Submap.this.subMap(from.getKey(), to.getKey()).object2FloatEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Object2FloatMap.Entry<K>> headSet(Object2FloatMap.Entry<K> to) {
                        return Submap.this.headMap(to.getKey()).object2FloatEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Object2FloatMap.Entry<K>> tailSet(Object2FloatMap.Entry<K> from) {
                        return Submap.this.tailMap(from.getKey()).object2FloatEntrySet();
                    }
                };
            }
            return this.entries;
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = new KeySet();
            }
            return this.keys;
        }

        @Override
        public FloatCollection values() {
            if (this.values == null) {
                this.values = new AbstractFloatCollection(){

                    @Override
                    public FloatIterator iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(float k) {
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
        public boolean containsKey(Object k) {
            return this.in(k) && Object2FloatAVLTreeMap.this.containsKey(k);
        }

        @Override
        public boolean containsValue(float v) {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                float ev = i.nextEntry().value;
                if (Float.floatToIntBits(ev) != Float.floatToIntBits(v)) continue;
                return true;
            }
            return false;
        }

        @Override
        public float getFloat(Object k) {
            Entry<Object> e;
            Object kk = k;
            return this.in(kk) && (e = Object2FloatAVLTreeMap.this.findKey(kk)) != null ? e.value : this.defRetValue;
        }

        @Override
        public float put(K k, float v) {
            Object2FloatAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            float oldValue = Object2FloatAVLTreeMap.this.put(k, v);
            return Object2FloatAVLTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        public float removeFloat(Object k) {
            Object2FloatAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                return this.defRetValue;
            }
            float oldValue = Object2FloatAVLTreeMap.this.removeFloat(k);
            return Object2FloatAVLTreeMap.this.modified ? oldValue : this.defRetValue;
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
        public Comparator<? super K> comparator() {
            return Object2FloatAVLTreeMap.this.actualComparator;
        }

        @Override
        public Object2FloatSortedMap<K> headMap(K to) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to, false);
            }
            return Object2FloatAVLTreeMap.this.compare(to, this.to) < 0 ? new Submap(this.from, this.bottom, to, false) : this;
        }

        @Override
        public Object2FloatSortedMap<K> tailMap(K from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Object2FloatAVLTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Object2FloatSortedMap<K> subMap(K from, K to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                K k = to = Object2FloatAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }
            if (!this.bottom) {
                K k = from = Object2FloatAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Submap(from, false, to, false);
        }

        public Entry<K> firstEntry() {
            Entry e;
            if (Object2FloatAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e = Object2FloatAVLTreeMap.this.firstEntry;
            } else {
                e = Object2FloatAVLTreeMap.this.locateKey(this.from);
                if (Object2FloatAVLTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || !this.top && Object2FloatAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                return null;
            }
            return e;
        }

        public Entry<K> lastEntry() {
            Entry e;
            if (Object2FloatAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e = Object2FloatAVLTreeMap.this.lastEntry;
            } else {
                e = Object2FloatAVLTreeMap.this.locateKey(this.to);
                if (Object2FloatAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || !this.bottom && Object2FloatAVLTreeMap.this.compare(e.key, this.from) < 0) {
                return null;
            }
            return e;
        }

        @Override
        public K firstKey() {
            Entry<K> e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return (K)e.key;
        }

        @Override
        public K lastKey() {
            Entry<K> e = this.lastEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return (K)e.key;
        }

        private final class SubmapValueIterator
        extends Object2FloatAVLTreeMap<K>
        implements FloatListIterator {
            private SubmapValueIterator() {
                super();
            }

            @Override
            public float nextFloat() {
                return this.nextEntry().value;
            }

            @Override
            public float previousFloat() {
                return this.previousEntry().value;
            }
        }

        private final class SubmapKeyIterator
        extends Object2FloatAVLTreeMap<K>
        implements ObjectListIterator<K> {
            public SubmapKeyIterator() {
                super();
            }

            public SubmapKeyIterator(K from) {
                super(from);
            }

            @Override
            public K next() {
                return (K)this.nextEntry().key;
            }

            @Override
            public K previous() {
                return (K)this.previousEntry().key;
            }
        }

        private class SubmapEntryIterator
        extends Object2FloatAVLTreeMap<K>
        implements ObjectListIterator<Object2FloatMap.Entry<K>> {
            SubmapEntryIterator() {
                super();
            }

            SubmapEntryIterator(K k) {
                super(k);
            }

            @Override
            public Object2FloatMap.Entry<K> next() {
                return this.nextEntry();
            }

            @Override
            public Object2FloatMap.Entry<K> previous() {
                return this.previousEntry();
            }
        }

        private class SubmapIterator
        extends Object2FloatAVLTreeMap<K> {
            SubmapIterator() {
                super();
                this.next = Submap.this.firstEntry();
            }

            /*
             * Enabled aggressive block sorting
             */
            SubmapIterator(K k) {
                this();
                if (this.next == null) return;
                if (!submap.bottom && submap.Object2FloatAVLTreeMap.this.compare(k, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Object2FloatAVLTreeMap.this.compare(k, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Object2FloatAVLTreeMap.this.locateKey(k);
                if (submap.Object2FloatAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Object2FloatAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Object2FloatAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractObject2FloatSortedMap<K> {
            private KeySet() {
            }

            public ObjectBidirectionalIterator<K> iterator() {
                return new SubmapKeyIterator();
            }

            public ObjectBidirectionalIterator<K> iterator(K from) {
                return new SubmapKeyIterator(from);
            }
        }

    }

    private final class ValueIterator
    extends Object2FloatAVLTreeMap<K>
    implements FloatListIterator {
        private ValueIterator() {
            super();
        }

        @Override
        public float nextFloat() {
            return this.nextEntry().value;
        }

        @Override
        public float previousFloat() {
            return this.previousEntry().value;
        }
    }

    private class KeySet
    extends AbstractObject2FloatSortedMap<K> {
        private KeySet() {
        }

        public ObjectBidirectionalIterator<K> iterator() {
            return new KeyIterator();
        }

        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeyIterator(from);
        }
    }

    private final class KeyIterator
    extends Object2FloatAVLTreeMap<K>
    implements ObjectListIterator<K> {
        public KeyIterator() {
            super();
        }

        public KeyIterator(K k) {
            super(k);
        }

        @Override
        public K next() {
            return (K)this.nextEntry().key;
        }

        @Override
        public K previous() {
            return (K)this.previousEntry().key;
        }
    }

    private class EntryIterator
    extends Object2FloatAVLTreeMap<K>
    implements ObjectListIterator<Object2FloatMap.Entry<K>> {
        EntryIterator() {
            super();
        }

        EntryIterator(K k) {
            super(k);
        }

        @Override
        public Object2FloatMap.Entry<K> next() {
            return this.nextEntry();
        }

        @Override
        public Object2FloatMap.Entry<K> previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Object2FloatMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Object2FloatMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }

    private class TreeIterator {
        Entry<K> prev;
        Entry<K> next;
        Entry<K> curr;
        int index = 0;

        TreeIterator() {
            this.next = Object2FloatAVLTreeMap.this.firstEntry;
        }

        TreeIterator(K k) {
            this.next = Object2FloatAVLTreeMap.this.locateKey(k);
            if (this.next != null) {
                if (Object2FloatAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

        Entry<K> nextEntry() {
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

        Entry<K> previousEntry() {
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
            Object2FloatAVLTreeMap.this.removeFloat(this.curr.key);
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

    private static final class Entry<K>
    extends AbstractObject2FloatMap.BasicEntry<K>
    implements Cloneable {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 1073741824;
        private static final int BALANCE_MASK = 255;
        Entry<K> left;
        Entry<K> right;
        int info;

        Entry() {
            super(null, 0.0f);
        }

        Entry(K k, float v) {
            super(k, v);
            this.info = -1073741824;
        }

        Entry<K> left() {
            return (this.info & 1073741824) != 0 ? null : this.left;
        }

        Entry<K> right() {
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

        void pred(Entry<K> pred) {
            this.info |= 1073741824;
            this.left = pred;
        }

        void succ(Entry<K> succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry<K> left) {
            this.info &= -1073741825;
            this.left = left;
        }

        void right(Entry<K> right) {
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

        Entry<K> next() {
            Entry<K> next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 1073741824) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry<K> prev() {
            Entry<K> prev = this.left;
            if ((this.info & 1073741824) == 0) {
                while ((prev.info & Integer.MIN_VALUE) == 0) {
                    prev = prev.right;
                }
            }
            return prev;
        }

        @Override
        public float setValue(float value) {
            float oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public Entry<K> clone() {
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
            return Objects.equals(this.key, e.getKey()) && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() ^ HashCommon.float2int(this.value);
        }

        @Override
        public String toString() {
            return this.key + "=>" + this.value;
        }
    }

}


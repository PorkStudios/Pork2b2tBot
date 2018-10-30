/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ShortMap;
import it.unimi.dsi.fastutil.bytes.AbstractByte2ShortSortedMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortSortedMap;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteComparators;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
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

public class Byte2ShortAVLTreeMap
extends AbstractByte2ShortSortedMap
implements Serializable,
Cloneable {
    protected transient Entry tree;
    protected int count;
    protected transient Entry firstEntry;
    protected transient Entry lastEntry;
    protected transient ObjectSortedSet<Byte2ShortMap.Entry> entries;
    protected transient ByteSortedSet keys;
    protected transient ShortCollection values;
    protected transient boolean modified;
    protected Comparator<? super Byte> storedComparator;
    protected transient ByteComparator actualComparator;
    private static final long serialVersionUID = -7046029254386353129L;
    private transient boolean[] dirPath;

    public Byte2ShortAVLTreeMap() {
        this.allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = ByteComparators.asByteComparator(this.storedComparator);
    }

    public Byte2ShortAVLTreeMap(Comparator<? super Byte> c) {
        this();
        this.storedComparator = c;
        this.setActualComparator();
    }

    public Byte2ShortAVLTreeMap(Map<? extends Byte, ? extends Short> m) {
        this();
        this.putAll(m);
    }

    public Byte2ShortAVLTreeMap(SortedMap<Byte, Short> m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Byte2ShortAVLTreeMap(Byte2ShortMap m) {
        this();
        this.putAll(m);
    }

    public Byte2ShortAVLTreeMap(Byte2ShortSortedMap m) {
        this(m.comparator());
        this.putAll(m);
    }

    public Byte2ShortAVLTreeMap(byte[] k, short[] v, Comparator<? super Byte> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Byte2ShortAVLTreeMap(byte[] k, short[] v) {
        this(k, v, null);
    }

    final int compare(byte k1, byte k2) {
        return this.actualComparator == null ? Byte.compare(k1, k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry findKey(byte k) {
        int cmp;
        Entry e = this.tree;
        while (e != null && (cmp = this.compare(k, e.key)) != 0) {
            e = cmp < 0 ? e.left() : e.right();
        }
        return e;
    }

    final Entry locateKey(byte k) {
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

    public short addTo(byte k, short incr) {
        Entry e = this.add(k);
        short oldValue = e.value;
        e.value = (short)(e.value + incr);
        return oldValue;
    }

    @Override
    public short put(byte k, short v) {
        Entry e = this.add(k);
        short oldValue = e.value;
        e.value = v;
        return oldValue;
    }

    private Entry add(byte k) {
        this.modified = false;
        Entry e = null;
        if (this.tree == null) {
            ++this.count;
            this.lastEntry = this.firstEntry = new Entry(k, this.defRetValue);
            this.tree = this.firstEntry;
            e = this.firstEntry;
            this.modified = true;
        } else {
            Entry p = this.tree;
            Entry q = null;
            Entry y = this.tree;
            Entry z = null;
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
                        e = new Entry(k, this.defRetValue);
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
                    e = new Entry(k, this.defRetValue);
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
    public short remove(byte k) {
        int cmp;
        this.modified = false;
        if (this.tree == null) {
            return this.defRetValue;
        }
        Entry p = this.tree;
        Entry q = null;
        boolean dir = false;
        byte kk = k;
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
        return p.value;
    }

    @Override
    public boolean containsValue(short v) {
        ValueIterator i = new ValueIterator();
        int j = this.count;
        while (j-- != 0) {
            short ev = i.nextShort();
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
    public boolean containsKey(byte k) {
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
    public short get(byte k) {
        Entry e = this.findKey(k);
        return e == null ? this.defRetValue : e.value;
    }

    @Override
    public byte firstByteKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override
    public byte lastByteKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    @Override
    public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
        if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Byte2ShortMap.Entry>(){
                final Comparator<? super Byte2ShortMap.Entry> comparator = (x, y) -> Byte2ShortAVLTreeMap.this.actualComparator.compare(x.getByteKey(), y.getByteKey());

                @Override
                public Comparator<? super Byte2ShortMap.Entry> comparator() {
                    return this.comparator;
                }

                @Override
                public ObjectBidirectionalIterator<Byte2ShortMap.Entry> iterator() {
                    return new EntryIterator();
                }

                @Override
                public ObjectBidirectionalIterator<Byte2ShortMap.Entry> iterator(Byte2ShortMap.Entry from) {
                    return new EntryIterator(from.getByteKey());
                }

                @Override
                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                        return false;
                    }
                    if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                        return false;
                    }
                    Entry f = Byte2ShortAVLTreeMap.this.findKey((Byte)e.getKey());
                    return e.equals(f);
                }

                @Override
                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry e = (Map.Entry)o;
                    if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                        return false;
                    }
                    if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                        return false;
                    }
                    Entry f = Byte2ShortAVLTreeMap.this.findKey((Byte)e.getKey());
                    if (f == null || f.getShortValue() != ((Short)e.getValue()).shortValue()) {
                        return false;
                    }
                    Byte2ShortAVLTreeMap.this.remove(f.key);
                    return true;
                }

                @Override
                public int size() {
                    return Byte2ShortAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Byte2ShortAVLTreeMap.this.clear();
                }

                @Override
                public Byte2ShortMap.Entry first() {
                    return Byte2ShortAVLTreeMap.this.firstEntry;
                }

                @Override
                public Byte2ShortMap.Entry last() {
                    return Byte2ShortAVLTreeMap.this.lastEntry;
                }

                @Override
                public ObjectSortedSet<Byte2ShortMap.Entry> subSet(Byte2ShortMap.Entry from, Byte2ShortMap.Entry to) {
                    return Byte2ShortAVLTreeMap.this.subMap(from.getByteKey(), to.getByteKey()).byte2ShortEntrySet();
                }

                @Override
                public ObjectSortedSet<Byte2ShortMap.Entry> headSet(Byte2ShortMap.Entry to) {
                    return Byte2ShortAVLTreeMap.this.headMap(to.getByteKey()).byte2ShortEntrySet();
                }

                @Override
                public ObjectSortedSet<Byte2ShortMap.Entry> tailSet(Byte2ShortMap.Entry from) {
                    return Byte2ShortAVLTreeMap.this.tailMap(from.getByteKey()).byte2ShortEntrySet();
                }
            };
        }
        return this.entries;
    }

    @Override
    public ByteSortedSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ShortCollection values() {
        if (this.values == null) {
            this.values = new AbstractShortCollection(){

                @Override
                public ShortIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public boolean contains(short k) {
                    return Byte2ShortAVLTreeMap.this.containsValue(k);
                }

                @Override
                public int size() {
                    return Byte2ShortAVLTreeMap.this.count;
                }

                @Override
                public void clear() {
                    Byte2ShortAVLTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override
    public ByteComparator comparator() {
        return this.actualComparator;
    }

    @Override
    public Byte2ShortSortedMap headMap(byte to) {
        return new Submap(0, true, to, false);
    }

    @Override
    public Byte2ShortSortedMap tailMap(byte from) {
        return new Submap(from, false, 0, true);
    }

    @Override
    public Byte2ShortSortedMap subMap(byte from, byte to) {
        return new Submap(from, false, to, false);
    }

    public Byte2ShortAVLTreeMap clone() {
        Byte2ShortAVLTreeMap c;
        try {
            c = (Byte2ShortAVLTreeMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
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
        EntryIterator i = new EntryIterator();
        s.defaultWriteObject();
        while (n-- != 0) {
            Entry e = i.nextEntry();
            s.writeByte(e.key);
            s.writeShort(e.value);
        }
    }

    private Entry readTree(ObjectInputStream s, int n, Entry pred, Entry succ) throws IOException, ClassNotFoundException {
        if (n == 1) {
            Entry top = new Entry(s.readByte(), s.readShort());
            top.pred(pred);
            top.succ(succ);
            return top;
        }
        if (n == 2) {
            Entry top = new Entry(s.readByte(), s.readShort());
            top.right(new Entry(s.readByte(), s.readShort()));
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
        top.key = s.readByte();
        top.value = s.readShort();
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

    private final class Submap
    extends AbstractByte2ShortSortedMap
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        byte from;
        byte to;
        boolean bottom;
        boolean top;
        protected transient ObjectSortedSet<Byte2ShortMap.Entry> entries;
        protected transient ByteSortedSet keys;
        protected transient ShortCollection values;

        public Submap(byte from, boolean bottom, byte to, boolean top) {
            if (!bottom && !top && Byte2ShortAVLTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Byte2ShortAVLTreeMap.this.defRetValue;
        }

        @Override
        public void clear() {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                i.nextEntry();
                i.remove();
            }
        }

        final boolean in(byte k) {
            return !(!this.bottom && Byte2ShortAVLTreeMap.this.compare(k, this.from) < 0 || !this.top && Byte2ShortAVLTreeMap.this.compare(k, this.to) >= 0);
        }

        @Override
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Byte2ShortMap.Entry>(){

                    @Override
                    public ObjectBidirectionalIterator<Byte2ShortMap.Entry> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override
                    public ObjectBidirectionalIterator<Byte2ShortMap.Entry> iterator(Byte2ShortMap.Entry from) {
                        return new SubmapEntryIterator(from.getByteKey());
                    }

                    @Override
                    public Comparator<? super Byte2ShortMap.Entry> comparator() {
                        return Byte2ShortAVLTreeMap.this.byte2ShortEntrySet().comparator();
                    }

                    @Override
                    public boolean contains(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                            return false;
                        }
                        if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                            return false;
                        }
                        Entry f = Byte2ShortAVLTreeMap.this.findKey((Byte)e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                    }

                    @Override
                    public boolean remove(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry e = (Map.Entry)o;
                        if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                            return false;
                        }
                        if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                            return false;
                        }
                        Entry f = Byte2ShortAVLTreeMap.this.findKey((Byte)e.getKey());
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
                    public Byte2ShortMap.Entry first() {
                        return Submap.this.firstEntry();
                    }

                    @Override
                    public Byte2ShortMap.Entry last() {
                        return Submap.this.lastEntry();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ShortMap.Entry> subSet(Byte2ShortMap.Entry from, Byte2ShortMap.Entry to) {
                        return Submap.this.subMap(from.getByteKey(), to.getByteKey()).byte2ShortEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ShortMap.Entry> headSet(Byte2ShortMap.Entry to) {
                        return Submap.this.headMap(to.getByteKey()).byte2ShortEntrySet();
                    }

                    @Override
                    public ObjectSortedSet<Byte2ShortMap.Entry> tailSet(Byte2ShortMap.Entry from) {
                        return Submap.this.tailMap(from.getByteKey()).byte2ShortEntrySet();
                    }
                };
            }
            return this.entries;
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = new KeySet();
            }
            return this.keys;
        }

        @Override
        public ShortCollection values() {
            if (this.values == null) {
                this.values = new AbstractShortCollection(){

                    @Override
                    public ShortIterator iterator() {
                        return new SubmapValueIterator();
                    }

                    @Override
                    public boolean contains(short k) {
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
        public boolean containsKey(byte k) {
            return this.in(k) && Byte2ShortAVLTreeMap.this.containsKey(k);
        }

        @Override
        public boolean containsValue(short v) {
            SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                short ev = i.nextEntry().value;
                if (ev != v) continue;
                return true;
            }
            return false;
        }

        @Override
        public short get(byte k) {
            Entry e;
            byte kk = k;
            return this.in(kk) && (e = Byte2ShortAVLTreeMap.this.findKey(kk)) != null ? e.value : this.defRetValue;
        }

        @Override
        public short put(byte k, short v) {
            Byte2ShortAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            short oldValue = Byte2ShortAVLTreeMap.this.put(k, v);
            return Byte2ShortAVLTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override
        public short remove(byte k) {
            Byte2ShortAVLTreeMap.this.modified = false;
            if (!this.in(k)) {
                return this.defRetValue;
            }
            short oldValue = Byte2ShortAVLTreeMap.this.remove(k);
            return Byte2ShortAVLTreeMap.this.modified ? oldValue : this.defRetValue;
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
        public ByteComparator comparator() {
            return Byte2ShortAVLTreeMap.this.actualComparator;
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to) {
            if (this.top) {
                return new Submap(this.from, this.bottom, to, false);
            }
            return Byte2ShortAVLTreeMap.this.compare(to, this.to) < 0 ? new Submap(this.from, this.bottom, to, false) : this;
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
            if (this.bottom) {
                return new Submap(from, false, this.to, this.top);
            }
            return Byte2ShortAVLTreeMap.this.compare(from, this.from) > 0 ? new Submap(from, false, this.to, this.top) : this;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                byte by = to = Byte2ShortAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }
            if (!this.bottom) {
                byte by = from = Byte2ShortAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            if (!this.top && !this.bottom && from == this.from && to == this.to) {
                return this;
            }
            return new Submap(from, false, to, false);
        }

        public Entry firstEntry() {
            Entry e;
            if (Byte2ShortAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e = Byte2ShortAVLTreeMap.this.firstEntry;
            } else {
                e = Byte2ShortAVLTreeMap.this.locateKey(this.from);
                if (Byte2ShortAVLTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || !this.top && Byte2ShortAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                return null;
            }
            return e;
        }

        public Entry lastEntry() {
            Entry e;
            if (Byte2ShortAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e = Byte2ShortAVLTreeMap.this.lastEntry;
            } else {
                e = Byte2ShortAVLTreeMap.this.locateKey(this.to);
                if (Byte2ShortAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || !this.bottom && Byte2ShortAVLTreeMap.this.compare(e.key, this.from) < 0) {
                return null;
            }
            return e;
        }

        @Override
        public byte firstByteKey() {
            Entry e = this.firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        @Override
        public byte lastByteKey() {
            Entry e = this.lastEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        private final class SubmapValueIterator
        extends SubmapIterator
        implements ShortListIterator {
            private SubmapValueIterator() {
                super();
            }

            @Override
            public short nextShort() {
                return this.nextEntry().value;
            }

            @Override
            public short previousShort() {
                return this.previousEntry().value;
            }
        }

        private final class SubmapKeyIterator
        extends SubmapIterator
        implements ByteListIterator {
            public SubmapKeyIterator() {
                super();
            }

            public SubmapKeyIterator(byte from) {
                super(from);
            }

            @Override
            public byte nextByte() {
                return this.nextEntry().key;
            }

            @Override
            public byte previousByte() {
                return this.previousEntry().key;
            }
        }

        private class SubmapEntryIterator
        extends SubmapIterator
        implements ObjectListIterator<Byte2ShortMap.Entry> {
            SubmapEntryIterator() {
                super();
            }

            SubmapEntryIterator(byte k) {
                super(k);
            }

            @Override
            public Byte2ShortMap.Entry next() {
                return this.nextEntry();
            }

            @Override
            public Byte2ShortMap.Entry previous() {
                return this.previousEntry();
            }
        }

        private class SubmapIterator
        extends TreeIterator {
            SubmapIterator() {
                super();
                this.next = Submap.this.firstEntry();
            }

            /*
             * Enabled aggressive block sorting
             */
            SubmapIterator(byte k) {
                this();
                if (this.next == null) return;
                if (!submap.bottom && submap.Byte2ShortAVLTreeMap.this.compare(k, this.next.key) < 0) {
                    this.prev = null;
                    return;
                }
                if (!submap.top) {
                    this.prev = submap.lastEntry();
                    if (submap.Byte2ShortAVLTreeMap.this.compare(k, this.prev.key) >= 0) {
                        this.next = null;
                        return;
                    }
                }
                this.next = submap.Byte2ShortAVLTreeMap.this.locateKey(k);
                if (submap.Byte2ShortAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                    return;
                }
                this.prev = this.next.prev();
            }

            @Override
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (!Submap.this.bottom && this.prev != null && Byte2ShortAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
                    this.prev = null;
                }
            }

            @Override
            void updateNext() {
                this.next = this.next.next();
                if (!Submap.this.top && this.next != null && Byte2ShortAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
                    this.next = null;
                }
            }
        }

        private class KeySet
        extends AbstractByte2ShortSortedMap.KeySet {
            private KeySet() {
            }

            @Override
            public ByteBidirectionalIterator iterator() {
                return new SubmapKeyIterator();
            }

            @Override
            public ByteBidirectionalIterator iterator(byte from) {
                return new SubmapKeyIterator(from);
            }
        }

    }

    private final class ValueIterator
    extends TreeIterator
    implements ShortListIterator {
        private ValueIterator() {
            super();
        }

        @Override
        public short nextShort() {
            return this.nextEntry().value;
        }

        @Override
        public short previousShort() {
            return this.previousEntry().value;
        }
    }

    private class KeySet
    extends AbstractByte2ShortSortedMap.KeySet {
        private KeySet() {
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return new KeyIterator(from);
        }
    }

    private final class KeyIterator
    extends TreeIterator
    implements ByteListIterator {
        public KeyIterator() {
            super();
        }

        public KeyIterator(byte k) {
            super(k);
        }

        @Override
        public byte nextByte() {
            return this.nextEntry().key;
        }

        @Override
        public byte previousByte() {
            return this.previousEntry().key;
        }
    }

    private class EntryIterator
    extends TreeIterator
    implements ObjectListIterator<Byte2ShortMap.Entry> {
        EntryIterator() {
            super();
        }

        EntryIterator(byte k) {
            super(k);
        }

        @Override
        public Byte2ShortMap.Entry next() {
            return this.nextEntry();
        }

        @Override
        public Byte2ShortMap.Entry previous() {
            return this.previousEntry();
        }

        @Override
        public void set(Byte2ShortMap.Entry ok) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Byte2ShortMap.Entry ok) {
            throw new UnsupportedOperationException();
        }
    }

    private class TreeIterator {
        Entry prev;
        Entry next;
        Entry curr;
        int index = 0;

        TreeIterator() {
            this.next = Byte2ShortAVLTreeMap.this.firstEntry;
        }

        TreeIterator(byte k) {
            this.next = Byte2ShortAVLTreeMap.this.locateKey(k);
            if (this.next != null) {
                if (Byte2ShortAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

        Entry nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
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
            this.next = this.prev = this.curr;
            this.updatePrevious();
            this.updateNext();
            Byte2ShortAVLTreeMap.this.remove(this.curr.key);
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

    private static final class Entry
    extends AbstractByte2ShortMap.BasicEntry
    implements Cloneable {
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        private static final int PRED_MASK = 1073741824;
        private static final int BALANCE_MASK = 255;
        Entry left;
        Entry right;
        int info;

        Entry() {
            super((byte)0, (short)0);
        }

        Entry(byte k, short v) {
            super(k, v);
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

        @Override
        public short setValue(short value) {
            short oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public Entry clone() {
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
            return this.key == (Byte)e.getKey() && this.value == (Short)e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ this.value;
        }

        @Override
        public String toString() {
            return "" + this.key + "=>" + this.value;
        }
    }

}


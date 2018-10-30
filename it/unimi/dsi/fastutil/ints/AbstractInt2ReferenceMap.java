/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractInt2ReferenceMap<V>
extends AbstractInt2ReferenceFunction<V>
implements Int2ReferenceMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2ReferenceMap() {
    }

    @Override
    public boolean containsValue(Object v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2ReferenceEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2ReferenceMap.Entry)i.next()).getIntKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public IntSet keySet() {
        return new AbstractIntSet(){

            @Override
            public boolean contains(int k) {
                return AbstractInt2ReferenceMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2ReferenceMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Int2ReferenceMaps.fastIterator(AbstractInt2ReferenceMap.this);
                    }

                    @Override
                    public int nextInt() {
                        return this.i.next().getIntKey();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.i.remove();
                    }
                };
            }

        };
    }

    @Override
    public ReferenceCollection<V> values() {
        return new AbstractReferenceCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractInt2ReferenceMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2ReferenceMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Int2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Int2ReferenceMaps.fastIterator(AbstractInt2ReferenceMap.this);
                    }

                    @Override
                    public V next() {
                        return this.i.next().getValue();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.i.hasNext();
                    }
                };
            }

        };
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> m) {
        if (m instanceof Int2ReferenceMap) {
            ObjectIterator i = Int2ReferenceMaps.fastIterator((Int2ReferenceMap)m);
            while (i.hasNext()) {
                Int2ReferenceMap.Entry e = i.next();
                this.put(e.getIntKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Integer, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Integer, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Int2ReferenceMaps.fastIterator(this);
        while (n-- != 0) {
            h += i.next().hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map m = (Map)o;
        if (m.size() != this.size()) {
            return false;
        }
        return this.int2ReferenceEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Int2ReferenceMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2ReferenceMap.Entry e = i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            if (this == e.getValue()) {
                s.append("(this map)");
                continue;
            }
            s.append(String.valueOf(e.getValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry<V>
    implements Int2ReferenceMap.Entry<V> {
        protected int key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, V value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(int key, V value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Integer getKey() {
            return this.key;
        }

        @Override
        public int getIntKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                return false;
            }
            return this.key == (Integer)e.getKey() && this.value == e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


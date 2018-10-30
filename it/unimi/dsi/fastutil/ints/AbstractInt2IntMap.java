/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2IntFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractInt2IntMap
extends AbstractInt2IntFunction
implements Int2IntMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2IntMap() {
    }

    @Override
    public boolean containsValue(int v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2IntEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2IntMap.Entry)i.next()).getIntKey() != k) continue;
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
                return AbstractInt2IntMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2IntMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2IntMap.Entry> i;
                    {
                        this.i = Int2IntMaps.fastIterator(AbstractInt2IntMap.this);
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
    public IntCollection values() {
        return new AbstractIntCollection(){

            @Override
            public boolean contains(int k) {
                return AbstractInt2IntMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2IntMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2IntMap.Entry> i;
                    {
                        this.i = Int2IntMaps.fastIterator(AbstractInt2IntMap.this);
                    }

                    @Override
                    public int nextInt() {
                        return this.i.next().getIntValue();
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
    public void putAll(Map<? extends Integer, ? extends Integer> m) {
        if (m instanceof Int2IntMap) {
            ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator((Int2IntMap)m);
            while (i.hasNext()) {
                Int2IntMap.Entry e = i.next();
                this.put(e.getIntKey(), e.getIntValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Integer, ? extends Integer>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Integer, ? extends Integer> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator(this);
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
        return this.int2IntEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2IntMap.Entry e = i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            s.append(String.valueOf(e.getIntValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Int2IntMap.Entry {
        protected int key;
        protected int value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(int key, int value) {
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

        @Deprecated
        @Override
        public Integer getValue() {
            return this.value;
        }

        @Override
        public int getIntValue() {
            return this.value;
        }

        @Override
        public int setValue(int value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer setValue(Integer value) {
            return this.setValue((int)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            return this.key == (Integer)e.getKey() && this.value == (Integer)e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ this.value;
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.AbstractInt2LongFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractInt2LongMap
extends AbstractInt2LongFunction
implements Int2LongMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2LongMap() {
    }

    @Override
    public boolean containsValue(long v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2LongEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2LongMap.Entry)i.next()).getIntKey() != k) continue;
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
                return AbstractInt2LongMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2LongMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2LongMap.Entry> i;
                    {
                        this.i = Int2LongMaps.fastIterator(AbstractInt2LongMap.this);
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
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long k) {
                return AbstractInt2LongMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2LongMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Int2LongMap.Entry> i;
                    {
                        this.i = Int2LongMaps.fastIterator(AbstractInt2LongMap.this);
                    }

                    @Override
                    public long nextLong() {
                        return this.i.next().getLongValue();
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
    public void putAll(Map<? extends Integer, ? extends Long> m) {
        if (m instanceof Int2LongMap) {
            ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator((Int2LongMap)m);
            while (i.hasNext()) {
                Int2LongMap.Entry e = i.next();
                this.put(e.getIntKey(), e.getLongValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Integer, ? extends Long>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Integer, ? extends Long> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator(this);
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
        return this.int2LongEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2LongMap.Entry e = i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            s.append(String.valueOf(e.getLongValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Int2LongMap.Entry {
        protected int key;
        protected long value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, Long value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(int key, long value) {
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
        public Long getValue() {
            return this.value;
        }

        @Override
        public long getLongValue() {
            return this.value;
        }

        @Override
        public long setValue(long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long setValue(Long value) {
            return this.setValue((long)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            return this.key == (Integer)e.getKey() && this.value == (Long)e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.long2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


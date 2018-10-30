/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2IntFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLong2IntMap
extends AbstractLong2IntFunction
implements Long2IntMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2IntMap() {
    }

    @Override
    public boolean containsValue(int v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2IntEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2IntMap.Entry)i.next()).getLongKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public LongSet keySet() {
        return new AbstractLongSet(){

            @Override
            public boolean contains(long k) {
                return AbstractLong2IntMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2IntMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2IntMap.Entry> i;
                    {
                        this.i = Long2IntMaps.fastIterator(AbstractLong2IntMap.this);
                    }

                    @Override
                    public long nextLong() {
                        return this.i.next().getLongKey();
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
                return AbstractLong2IntMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2IntMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Long2IntMap.Entry> i;
                    {
                        this.i = Long2IntMaps.fastIterator(AbstractLong2IntMap.this);
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
    public void putAll(Map<? extends Long, ? extends Integer> m) {
        if (m instanceof Long2IntMap) {
            ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator((Long2IntMap)m);
            while (i.hasNext()) {
                Long2IntMap.Entry e = i.next();
                this.put(e.getLongKey(), e.getIntValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Long, ? extends Integer>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Long, ? extends Integer> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(this);
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
        return this.long2IntEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2IntMap.Entry e = i.next();
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getIntValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Long2IntMap.Entry {
        protected long key;
        protected int value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(long key, int value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Long getKey() {
            return this.key;
        }

        @Override
        public long getLongKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            return this.key == (Long)e.getKey() && this.value == (Integer)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ this.value;
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


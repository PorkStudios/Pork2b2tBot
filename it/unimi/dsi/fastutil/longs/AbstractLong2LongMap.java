/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLong2LongFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLong2LongMap
extends AbstractLong2LongFunction
implements Long2LongMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2LongMap() {
    }

    @Override
    public boolean containsValue(long v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2LongEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2LongMap.Entry)i.next()).getLongKey() != k) continue;
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
                return AbstractLong2LongMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2LongMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2LongMap.Entry> i;
                    {
                        this.i = Long2LongMaps.fastIterator(AbstractLong2LongMap.this);
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
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long k) {
                return AbstractLong2LongMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2LongMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2LongMap.Entry> i;
                    {
                        this.i = Long2LongMaps.fastIterator(AbstractLong2LongMap.this);
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
    public void putAll(Map<? extends Long, ? extends Long> m) {
        if (m instanceof Long2LongMap) {
            ObjectIterator<Long2LongMap.Entry> i = Long2LongMaps.fastIterator((Long2LongMap)m);
            while (i.hasNext()) {
                Long2LongMap.Entry e = i.next();
                this.put(e.getLongKey(), e.getLongValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Long, ? extends Long>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Long, ? extends Long> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Long2LongMap.Entry> i = Long2LongMaps.fastIterator(this);
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
        return this.long2LongEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Long2LongMap.Entry> i = Long2LongMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2LongMap.Entry e = i.next();
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getLongValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Long2LongMap.Entry {
        protected long key;
        protected long value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, Long value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(long key, long value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            return this.key == (Long)e.getKey() && this.value == (Long)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ HashCommon.long2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


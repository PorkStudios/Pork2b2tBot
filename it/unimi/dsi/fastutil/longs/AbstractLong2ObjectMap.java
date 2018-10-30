/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLong2ObjectFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractLong2ObjectMap<V>
extends AbstractLong2ObjectFunction<V>
implements Long2ObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2ObjectMap() {
    }

    @Override
    public boolean containsValue(Object v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2ObjectEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2ObjectMap.Entry)i.next()).getLongKey() != k) continue;
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
                return AbstractLong2ObjectMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2ObjectMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2ObjectMap.Entry<V>> i;
                    {
                        this.i = Long2ObjectMaps.fastIterator(AbstractLong2ObjectMap.this);
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
    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractLong2ObjectMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2ObjectMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Long2ObjectMap.Entry<V>> i;
                    {
                        this.i = Long2ObjectMaps.fastIterator(AbstractLong2ObjectMap.this);
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
    public void putAll(Map<? extends Long, ? extends V> m) {
        if (m instanceof Long2ObjectMap) {
            ObjectIterator i = Long2ObjectMaps.fastIterator((Long2ObjectMap)m);
            while (i.hasNext()) {
                Long2ObjectMap.Entry e = i.next();
                this.put(e.getLongKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Long, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Long, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Long2ObjectMaps.fastIterator(this);
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
        return this.long2ObjectEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Long2ObjectMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2ObjectMap.Entry e = i.next();
            s.append(String.valueOf(e.getLongKey()));
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
    implements Long2ObjectMap.Entry<V> {
        protected long key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, V value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(long key, V value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            return this.key == (Long)e.getKey() && Objects.equals(this.value, e.getValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


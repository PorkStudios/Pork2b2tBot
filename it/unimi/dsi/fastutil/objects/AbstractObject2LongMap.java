/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2LongFunction;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractObject2LongMap<K>
extends AbstractObject2LongFunction<K>
implements Object2LongMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractObject2LongMap() {
    }

    @Override
    public boolean containsValue(long v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.object2LongEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2LongMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ObjectSet<K> keySet() {
        return new AbstractObjectSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractObject2LongMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractObject2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2LongMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Object2LongMap.Entry<K>> i;
                    {
                        this.i = Object2LongMaps.fastIterator(AbstractObject2LongMap.this);
                    }

                    @Override
                    public K next() {
                        return this.i.next().getKey();
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
                return AbstractObject2LongMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractObject2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2LongMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Object2LongMap.Entry<K>> i;
                    {
                        this.i = Object2LongMaps.fastIterator(AbstractObject2LongMap.this);
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
    public void putAll(Map<? extends K, ? extends Long> m) {
        if (m instanceof Object2LongMap) {
            ObjectIterator i = Object2LongMaps.fastIterator((Object2LongMap)m);
            while (i.hasNext()) {
                Object2LongMap.Entry e = i.next();
                this.put(e.getKey(), e.getLongValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Long>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Long> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Object2LongMaps.fastIterator(this);
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
        return this.object2LongEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Object2LongMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Object2LongMap.Entry e = i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getLongValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry<K>
    implements Object2LongMap.Entry<K> {
        protected K key;
        protected long value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Long value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(K key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            return Objects.equals(this.key, e.getKey()) && this.value == (Long)e.getValue();
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.long2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }

}


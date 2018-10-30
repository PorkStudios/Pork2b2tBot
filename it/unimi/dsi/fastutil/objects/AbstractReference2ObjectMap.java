/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractReference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractReference2ObjectMap<K, V>
extends AbstractReference2ObjectFunction<K, V>
implements Reference2ObjectMap<K, V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractReference2ObjectMap() {
    }

    @Override
    public boolean containsValue(Object v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.reference2ObjectEntrySet().iterator();
        while (i.hasNext()) {
            if (((Reference2ObjectMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ReferenceSet<K> keySet() {
        return new AbstractReferenceSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractReference2ObjectMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractReference2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2ObjectMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Reference2ObjectMap.Entry<K, V>> i;
                    {
                        this.i = Reference2ObjectMaps.fastIterator(AbstractReference2ObjectMap.this);
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
    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractReference2ObjectMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractReference2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2ObjectMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Reference2ObjectMap.Entry<K, V>> i;
                    {
                        this.i = Reference2ObjectMaps.fastIterator(AbstractReference2ObjectMap.this);
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
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m instanceof Reference2ObjectMap) {
            ObjectIterator i = Reference2ObjectMaps.fastIterator((Reference2ObjectMap)m);
            while (i.hasNext()) {
                Reference2ObjectMap.Entry e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Reference2ObjectMaps.fastIterator(this);
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
        return this.reference2ObjectEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Reference2ObjectMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Reference2ObjectMap.Entry e = i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
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

    public static class BasicEntry<K, V>
    implements Reference2ObjectMap.Entry<K, V> {
        protected K key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
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
            return this.key == e.getKey() && Objects.equals(this.value, e.getValue());
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }

}


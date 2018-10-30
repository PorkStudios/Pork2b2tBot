/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ReferenceMap;
import it.unimi.dsi.fastutil.floats.Float2ReferenceMaps;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFloat2ReferenceMap<V>
extends AbstractFloat2ReferenceFunction<V>
implements Float2ReferenceMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2ReferenceMap() {
    }

    @Override
    public boolean containsValue(Object v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2ReferenceEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2ReferenceMap.Entry)i.next()).getFloatKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public FloatSet keySet() {
        return new AbstractFloatSet(){

            @Override
            public boolean contains(float k) {
                return AbstractFloat2ReferenceMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ReferenceMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Float2ReferenceMaps.fastIterator(AbstractFloat2ReferenceMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return this.i.next().getFloatKey();
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
                return AbstractFloat2ReferenceMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ReferenceMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Float2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Float2ReferenceMaps.fastIterator(AbstractFloat2ReferenceMap.this);
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
    public void putAll(Map<? extends Float, ? extends V> m) {
        if (m instanceof Float2ReferenceMap) {
            ObjectIterator i = Float2ReferenceMaps.fastIterator((Float2ReferenceMap)m);
            while (i.hasNext()) {
                Float2ReferenceMap.Entry e = i.next();
                this.put(e.getFloatKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Float, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Float, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Float2ReferenceMaps.fastIterator(this);
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
        return this.float2ReferenceEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Float2ReferenceMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2ReferenceMap.Entry e = i.next();
            s.append(String.valueOf(e.getFloatKey()));
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
    implements Float2ReferenceMap.Entry<V> {
        protected float key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, V value) {
            this.key = key.floatValue();
            this.value = value;
        }

        public BasicEntry(float key, V value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Float getKey() {
            return Float.valueOf(this.key);
        }

        @Override
        public float getFloatKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)e.getKey()).floatValue()) && this.value == e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


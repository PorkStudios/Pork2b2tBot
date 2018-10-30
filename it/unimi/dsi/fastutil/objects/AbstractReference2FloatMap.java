/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2FloatFunction;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractReference2FloatMap<K>
extends AbstractReference2FloatFunction<K>
implements Reference2FloatMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractReference2FloatMap() {
    }

    @Override
    public boolean containsValue(float v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.reference2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Reference2FloatMap.Entry)i.next()).getKey() != k) continue;
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
                return AbstractReference2FloatMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractReference2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2FloatMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Reference2FloatMap.Entry<K>> i;
                    {
                        this.i = Reference2FloatMaps.fastIterator(AbstractReference2FloatMap.this);
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
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float k) {
                return AbstractReference2FloatMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractReference2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Reference2FloatMap.Entry<K>> i;
                    {
                        this.i = Reference2FloatMaps.fastIterator(AbstractReference2FloatMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return this.i.next().getFloatValue();
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
    public void putAll(Map<? extends K, ? extends Float> m) {
        if (m instanceof Reference2FloatMap) {
            ObjectIterator i = Reference2FloatMaps.fastIterator((Reference2FloatMap)m);
            while (i.hasNext()) {
                Reference2FloatMap.Entry e = i.next();
                this.put(e.getKey(), e.getFloatValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Float>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Float> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Reference2FloatMaps.fastIterator(this);
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
        return this.reference2FloatEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Reference2FloatMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Reference2FloatMap.Entry e = i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getFloatValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry<K>
    implements Reference2FloatMap.Entry<K> {
        protected K key;
        protected float value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Float value) {
            this.key = key;
            this.value = value.floatValue();
        }

        public BasicEntry(K key, float value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Float getValue() {
            return Float.valueOf(this.value);
        }

        @Override
        public float getFloatValue() {
            return this.value;
        }

        @Override
        public float setValue(float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float setValue(Float value) {
            return Float.valueOf(this.setValue(value.floatValue()));
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            return this.key == e.getKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.key) ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }

}


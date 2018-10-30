/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMaps;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
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

public abstract class AbstractDouble2ObjectMap<V>
extends AbstractDouble2ObjectFunction<V>
implements Double2ObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2ObjectMap() {
    }

    @Override
    public boolean containsValue(Object v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2ObjectEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2ObjectMap.Entry)i.next()).getDoubleKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public DoubleSet keySet() {
        return new AbstractDoubleSet(){

            @Override
            public boolean contains(double k) {
                return AbstractDouble2ObjectMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2ObjectMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2ObjectMap.Entry<V>> i;
                    {
                        this.i = Double2ObjectMaps.fastIterator(AbstractDouble2ObjectMap.this);
                    }

                    @Override
                    public double nextDouble() {
                        return this.i.next().getDoubleKey();
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
                return AbstractDouble2ObjectMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2ObjectMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Double2ObjectMap.Entry<V>> i;
                    {
                        this.i = Double2ObjectMaps.fastIterator(AbstractDouble2ObjectMap.this);
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
    public void putAll(Map<? extends Double, ? extends V> m) {
        if (m instanceof Double2ObjectMap) {
            ObjectIterator i = Double2ObjectMaps.fastIterator((Double2ObjectMap)m);
            while (i.hasNext()) {
                Double2ObjectMap.Entry e = i.next();
                this.put(e.getDoubleKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Double, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Double, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Double2ObjectMaps.fastIterator(this);
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
        return this.double2ObjectEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Double2ObjectMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2ObjectMap.Entry e = i.next();
            s.append(String.valueOf(e.getDoubleKey()));
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
    implements Double2ObjectMap.Entry<V> {
        protected double key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Double key, V value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(double key, V value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Double getKey() {
            return this.key;
        }

        @Override
        public double getDoubleKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)e.getKey()) && Objects.equals(this.value, e.getValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


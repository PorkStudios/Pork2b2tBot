/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceMaps;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDouble2ReferenceMap<V>
extends AbstractDouble2ReferenceFunction<V>
implements Double2ReferenceMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2ReferenceMap() {
    }

    @Override
    public boolean containsValue(Object v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2ReferenceEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2ReferenceMap.Entry)i.next()).getDoubleKey() != k) continue;
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
                return AbstractDouble2ReferenceMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2ReferenceMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Double2ReferenceMaps.fastIterator(AbstractDouble2ReferenceMap.this);
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
    public ReferenceCollection<V> values() {
        return new AbstractReferenceCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractDouble2ReferenceMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2ReferenceMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Double2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Double2ReferenceMaps.fastIterator(AbstractDouble2ReferenceMap.this);
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
        if (m instanceof Double2ReferenceMap) {
            ObjectIterator i = Double2ReferenceMaps.fastIterator((Double2ReferenceMap)m);
            while (i.hasNext()) {
                Double2ReferenceMap.Entry e = i.next();
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
        ObjectIterator i = Double2ReferenceMaps.fastIterator(this);
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
        return this.double2ReferenceEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Double2ReferenceMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2ReferenceMap.Entry e = i.next();
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
    implements Double2ReferenceMap.Entry<V> {
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
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)e.getKey()) && this.value == e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


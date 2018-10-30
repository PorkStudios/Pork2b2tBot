/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractReference2DoubleMap<K>
extends AbstractReference2DoubleFunction<K>
implements Reference2DoubleMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractReference2DoubleMap() {
    }

    @Override
    public boolean containsValue(double v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.reference2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Reference2DoubleMap.Entry)i.next()).getKey() != k) continue;
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
                return AbstractReference2DoubleMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractReference2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2DoubleMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Reference2DoubleMap.Entry<K>> i;
                    {
                        this.i = Reference2DoubleMaps.fastIterator(AbstractReference2DoubleMap.this);
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
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double k) {
                return AbstractReference2DoubleMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractReference2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2DoubleMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Reference2DoubleMap.Entry<K>> i;
                    {
                        this.i = Reference2DoubleMaps.fastIterator(AbstractReference2DoubleMap.this);
                    }

                    @Override
                    public double nextDouble() {
                        return this.i.next().getDoubleValue();
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
    public void putAll(Map<? extends K, ? extends Double> m) {
        if (m instanceof Reference2DoubleMap) {
            ObjectIterator i = Reference2DoubleMaps.fastIterator((Reference2DoubleMap)m);
            while (i.hasNext()) {
                Reference2DoubleMap.Entry e = i.next();
                this.put(e.getKey(), e.getDoubleValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Double>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Double> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Reference2DoubleMaps.fastIterator(this);
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
        return this.reference2DoubleEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Reference2DoubleMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Reference2DoubleMap.Entry e = i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getDoubleValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry<K>
    implements Reference2DoubleMap.Entry<K> {
        protected K key;
        protected double value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Double value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(K key, double value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Double getValue() {
            return this.value;
        }

        @Override
        public double getDoubleValue() {
            return this.value;
        }

        @Override
        public double setValue(double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double setValue(Double value) {
            return this.setValue((double)value);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            return this.key == e.getKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.key) ^ HashCommon.double2int(this.value);
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }

}


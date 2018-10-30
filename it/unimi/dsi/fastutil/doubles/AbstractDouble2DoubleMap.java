/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2DoubleFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2DoubleMap;
import it.unimi.dsi.fastutil.doubles.Double2DoubleMaps;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDouble2DoubleMap
extends AbstractDouble2DoubleFunction
implements Double2DoubleMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2DoubleMap() {
    }

    @Override
    public boolean containsValue(double v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2DoubleMap.Entry)i.next()).getDoubleKey() != k) continue;
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
                return AbstractDouble2DoubleMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2DoubleMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2DoubleMap.Entry> i;
                    {
                        this.i = Double2DoubleMaps.fastIterator(AbstractDouble2DoubleMap.this);
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
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double k) {
                return AbstractDouble2DoubleMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2DoubleMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2DoubleMap.Entry> i;
                    {
                        this.i = Double2DoubleMaps.fastIterator(AbstractDouble2DoubleMap.this);
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
    public void putAll(Map<? extends Double, ? extends Double> m) {
        if (m instanceof Double2DoubleMap) {
            ObjectIterator<Double2DoubleMap.Entry> i = Double2DoubleMaps.fastIterator((Double2DoubleMap)m);
            while (i.hasNext()) {
                Double2DoubleMap.Entry e = i.next();
                this.put(e.getDoubleKey(), e.getDoubleValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Double, ? extends Double>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Double, ? extends Double> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Double2DoubleMap.Entry> i = Double2DoubleMaps.fastIterator(this);
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
        return this.double2DoubleEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Double2DoubleMap.Entry> i = Double2DoubleMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2DoubleMap.Entry e = i.next();
            s.append(String.valueOf(e.getDoubleKey()));
            s.append("=>");
            s.append(String.valueOf(e.getDoubleValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Double2DoubleMap.Entry {
        protected double key;
        protected double value;

        public BasicEntry() {
        }

        public BasicEntry(Double key, Double value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(double key, double value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)e.getKey()) && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ HashCommon.double2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


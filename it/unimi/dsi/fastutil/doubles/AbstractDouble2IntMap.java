/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2IntFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2IntMap;
import it.unimi.dsi.fastutil.doubles.Double2IntMaps;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDouble2IntMap
extends AbstractDouble2IntFunction
implements Double2IntMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2IntMap() {
    }

    @Override
    public boolean containsValue(int v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2IntEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2IntMap.Entry)i.next()).getDoubleKey() != k) continue;
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
                return AbstractDouble2IntMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2IntMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2IntMap.Entry> i;
                    {
                        this.i = Double2IntMaps.fastIterator(AbstractDouble2IntMap.this);
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
    public IntCollection values() {
        return new AbstractIntCollection(){

            @Override
            public boolean contains(int k) {
                return AbstractDouble2IntMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2IntMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Double2IntMap.Entry> i;
                    {
                        this.i = Double2IntMaps.fastIterator(AbstractDouble2IntMap.this);
                    }

                    @Override
                    public int nextInt() {
                        return this.i.next().getIntValue();
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
    public void putAll(Map<? extends Double, ? extends Integer> m) {
        if (m instanceof Double2IntMap) {
            ObjectIterator<Double2IntMap.Entry> i = Double2IntMaps.fastIterator((Double2IntMap)m);
            while (i.hasNext()) {
                Double2IntMap.Entry e = i.next();
                this.put(e.getDoubleKey(), e.getIntValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Double, ? extends Integer>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Double, ? extends Integer> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Double2IntMap.Entry> i = Double2IntMaps.fastIterator(this);
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
        return this.double2IntEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Double2IntMap.Entry> i = Double2IntMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2IntMap.Entry e = i.next();
            s.append(String.valueOf(e.getDoubleKey()));
            s.append("=>");
            s.append(String.valueOf(e.getIntValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Double2IntMap.Entry {
        protected double key;
        protected int value;

        public BasicEntry() {
        }

        public BasicEntry(Double key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(double key, int value) {
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
        public Integer getValue() {
            return this.value;
        }

        @Override
        public int getIntValue() {
            return this.value;
        }

        @Override
        public int setValue(int value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer setValue(Integer value) {
            return this.setValue((int)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)e.getKey()) && this.value == (Integer)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ this.value;
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ShortFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2ShortMap;
import it.unimi.dsi.fastutil.doubles.Double2ShortMaps;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDouble2ShortMap
extends AbstractDouble2ShortFunction
implements Double2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2ShortMap() {
    }

    @Override
    public boolean containsValue(short v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2ShortMap.Entry)i.next()).getDoubleKey() != k) continue;
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
                return AbstractDouble2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2ShortMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2ShortMap.Entry> i;
                    {
                        this.i = Double2ShortMaps.fastIterator(AbstractDouble2ShortMap.this);
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
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short k) {
                return AbstractDouble2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Double2ShortMap.Entry> i;
                    {
                        this.i = Double2ShortMaps.fastIterator(AbstractDouble2ShortMap.this);
                    }

                    @Override
                    public short nextShort() {
                        return this.i.next().getShortValue();
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
    public void putAll(Map<? extends Double, ? extends Short> m) {
        if (m instanceof Double2ShortMap) {
            ObjectIterator<Double2ShortMap.Entry> i = Double2ShortMaps.fastIterator((Double2ShortMap)m);
            while (i.hasNext()) {
                Double2ShortMap.Entry e = i.next();
                this.put(e.getDoubleKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Double, ? extends Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Double, ? extends Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Double2ShortMap.Entry> i = Double2ShortMaps.fastIterator(this);
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
        return this.double2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Double2ShortMap.Entry> i = Double2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2ShortMap.Entry e = i.next();
            s.append(String.valueOf(e.getDoubleKey()));
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Double2ShortMap.Entry {
        protected double key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(Double key, Short value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(double key, short value) {
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
        public Short getValue() {
            return this.value;
        }

        @Override
        public short getShortValue() {
            return this.value;
        }

        @Override
        public short setValue(short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short setValue(Short value) {
            return this.setValue((short)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)e.getKey()) && this.value == (Short)e.getValue();
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


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2FloatFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2FloatMap;
import it.unimi.dsi.fastutil.doubles.Double2FloatMaps;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDouble2FloatMap
extends AbstractDouble2FloatFunction
implements Double2FloatMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2FloatMap() {
    }

    @Override
    public boolean containsValue(float v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2FloatMap.Entry)i.next()).getDoubleKey() != k) continue;
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
                return AbstractDouble2FloatMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2FloatMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2FloatMap.Entry> i;
                    {
                        this.i = Double2FloatMaps.fastIterator(AbstractDouble2FloatMap.this);
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
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float k) {
                return AbstractDouble2FloatMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Double2FloatMap.Entry> i;
                    {
                        this.i = Double2FloatMaps.fastIterator(AbstractDouble2FloatMap.this);
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
    public void putAll(Map<? extends Double, ? extends Float> m) {
        if (m instanceof Double2FloatMap) {
            ObjectIterator<Double2FloatMap.Entry> i = Double2FloatMaps.fastIterator((Double2FloatMap)m);
            while (i.hasNext()) {
                Double2FloatMap.Entry e = i.next();
                this.put(e.getDoubleKey(), e.getFloatValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Double, ? extends Float>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Double, ? extends Float> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Double2FloatMap.Entry> i = Double2FloatMaps.fastIterator(this);
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
        return this.double2FloatEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Double2FloatMap.Entry> i = Double2FloatMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2FloatMap.Entry e = i.next();
            s.append(String.valueOf(e.getDoubleKey()));
            s.append("=>");
            s.append(String.valueOf(e.getFloatValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Double2FloatMap.Entry {
        protected double key;
        protected float value;

        public BasicEntry() {
        }

        public BasicEntry(Double key, Float value) {
            this.key = key;
            this.value = value.floatValue();
        }

        public BasicEntry(double key, float value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)e.getKey()) && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


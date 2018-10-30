/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2BooleanMap;
import it.unimi.dsi.fastutil.doubles.Double2BooleanMaps;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDouble2BooleanMap
extends AbstractDouble2BooleanFunction
implements Double2BooleanMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractDouble2BooleanMap() {
    }

    @Override
    public boolean containsValue(boolean v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(double k) {
        Iterator i = this.double2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Double2BooleanMap.Entry)i.next()).getDoubleKey() != k) continue;
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
                return AbstractDouble2BooleanMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractDouble2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2BooleanMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Double2BooleanMap.Entry> i;
                    {
                        this.i = Double2BooleanMaps.fastIterator(AbstractDouble2BooleanMap.this);
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
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean k) {
                return AbstractDouble2BooleanMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractDouble2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractDouble2BooleanMap.this.clear();
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    private final ObjectIterator<Double2BooleanMap.Entry> i;
                    {
                        this.i = Double2BooleanMaps.fastIterator(AbstractDouble2BooleanMap.this);
                    }

                    @Override
                    public boolean nextBoolean() {
                        return this.i.next().getBooleanValue();
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
    public void putAll(Map<? extends Double, ? extends Boolean> m) {
        if (m instanceof Double2BooleanMap) {
            ObjectIterator<Double2BooleanMap.Entry> i = Double2BooleanMaps.fastIterator((Double2BooleanMap)m);
            while (i.hasNext()) {
                Double2BooleanMap.Entry e = i.next();
                this.put(e.getDoubleKey(), e.getBooleanValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Double, ? extends Boolean>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Double, ? extends Boolean> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Double2BooleanMap.Entry> i = Double2BooleanMaps.fastIterator(this);
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
        return this.double2BooleanEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Double2BooleanMap.Entry> i = Double2BooleanMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Double2BooleanMap.Entry e = i.next();
            s.append(String.valueOf(e.getDoubleKey()));
            s.append("=>");
            s.append(String.valueOf(e.getBooleanValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Double2BooleanMap.Entry {
        protected double key;
        protected boolean value;

        public BasicEntry() {
        }

        public BasicEntry(Double key, Boolean value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(double key, boolean value) {
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
        public Boolean getValue() {
            return this.value;
        }

        @Override
        public boolean getBooleanValue() {
            return this.value;
        }

        @Override
        public boolean setValue(boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean setValue(Boolean value) {
            return this.setValue((boolean)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)e.getKey()) && this.value == (Boolean)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ (this.value ? 1231 : 1237);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


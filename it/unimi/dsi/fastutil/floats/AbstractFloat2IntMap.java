/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloat2IntFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2IntMap;
import it.unimi.dsi.fastutil.floats.Float2IntMaps;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
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

public abstract class AbstractFloat2IntMap
extends AbstractFloat2IntFunction
implements Float2IntMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2IntMap() {
    }

    @Override
    public boolean containsValue(int v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2IntEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2IntMap.Entry)i.next()).getFloatKey() != k) continue;
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
                return AbstractFloat2IntMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2IntMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2IntMap.Entry> i;
                    {
                        this.i = Float2IntMaps.fastIterator(AbstractFloat2IntMap.this);
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
    public IntCollection values() {
        return new AbstractIntCollection(){

            @Override
            public boolean contains(int k) {
                return AbstractFloat2IntMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2IntMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Float2IntMap.Entry> i;
                    {
                        this.i = Float2IntMaps.fastIterator(AbstractFloat2IntMap.this);
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
    public void putAll(Map<? extends Float, ? extends Integer> m) {
        if (m instanceof Float2IntMap) {
            ObjectIterator<Float2IntMap.Entry> i = Float2IntMaps.fastIterator((Float2IntMap)m);
            while (i.hasNext()) {
                Float2IntMap.Entry e = i.next();
                this.put(e.getFloatKey(), e.getIntValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Float, ? extends Integer>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Float, ? extends Integer> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Float2IntMap.Entry> i = Float2IntMaps.fastIterator(this);
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
        return this.float2IntEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Float2IntMap.Entry> i = Float2IntMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2IntMap.Entry e = i.next();
            s.append(String.valueOf(e.getFloatKey()));
            s.append("=>");
            s.append(String.valueOf(e.getIntValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Float2IntMap.Entry {
        protected float key;
        protected int value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, Integer value) {
            this.key = key.floatValue();
            this.value = value;
        }

        public BasicEntry(float key, int value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)e.getKey()).floatValue()) && this.value == (Integer)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ this.value;
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


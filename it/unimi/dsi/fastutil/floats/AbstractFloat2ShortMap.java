/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ShortFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ShortMap;
import it.unimi.dsi.fastutil.floats.Float2ShortMaps;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
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

public abstract class AbstractFloat2ShortMap
extends AbstractFloat2ShortFunction
implements Float2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2ShortMap() {
    }

    @Override
    public boolean containsValue(short v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2ShortMap.Entry)i.next()).getFloatKey() != k) continue;
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
                return AbstractFloat2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ShortMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2ShortMap.Entry> i;
                    {
                        this.i = Float2ShortMaps.fastIterator(AbstractFloat2ShortMap.this);
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
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short k) {
                return AbstractFloat2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Float2ShortMap.Entry> i;
                    {
                        this.i = Float2ShortMaps.fastIterator(AbstractFloat2ShortMap.this);
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
    public void putAll(Map<? extends Float, ? extends Short> m) {
        if (m instanceof Float2ShortMap) {
            ObjectIterator<Float2ShortMap.Entry> i = Float2ShortMaps.fastIterator((Float2ShortMap)m);
            while (i.hasNext()) {
                Float2ShortMap.Entry e = i.next();
                this.put(e.getFloatKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Float, ? extends Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Float, ? extends Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Float2ShortMap.Entry> i = Float2ShortMaps.fastIterator(this);
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
        return this.float2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Float2ShortMap.Entry> i = Float2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2ShortMap.Entry e = i.next();
            s.append(String.valueOf(e.getFloatKey()));
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Float2ShortMap.Entry {
        protected float key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, Short value) {
            this.key = key.floatValue();
            this.value = value;
        }

        public BasicEntry(float key, short value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)e.getKey()).floatValue()) && this.value == (Short)e.getValue();
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


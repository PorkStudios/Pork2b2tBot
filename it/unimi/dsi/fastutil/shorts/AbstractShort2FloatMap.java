/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2FloatFunction;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2FloatMap;
import it.unimi.dsi.fastutil.shorts.Short2FloatMaps;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractShort2FloatMap
extends AbstractShort2FloatFunction
implements Short2FloatMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractShort2FloatMap() {
    }

    @Override
    public boolean containsValue(float v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(short k) {
        Iterator i = this.short2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2FloatMap.Entry)i.next()).getShortKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ShortSet keySet() {
        return new AbstractShortSet(){

            @Override
            public boolean contains(short k) {
                return AbstractShort2FloatMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractShort2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2FloatMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Short2FloatMap.Entry> i;
                    {
                        this.i = Short2FloatMaps.fastIterator(AbstractShort2FloatMap.this);
                    }

                    @Override
                    public short nextShort() {
                        return this.i.next().getShortKey();
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
                return AbstractShort2FloatMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractShort2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Short2FloatMap.Entry> i;
                    {
                        this.i = Short2FloatMaps.fastIterator(AbstractShort2FloatMap.this);
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
    public void putAll(Map<? extends Short, ? extends Float> m) {
        if (m instanceof Short2FloatMap) {
            ObjectIterator<Short2FloatMap.Entry> i = Short2FloatMaps.fastIterator((Short2FloatMap)m);
            while (i.hasNext()) {
                Short2FloatMap.Entry e = i.next();
                this.put(e.getShortKey(), e.getFloatValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Short, ? extends Float>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Short, ? extends Float> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Short2FloatMap.Entry> i = Short2FloatMaps.fastIterator(this);
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
        return this.short2FloatEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Short2FloatMap.Entry> i = Short2FloatMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Short2FloatMap.Entry e = i.next();
            s.append(String.valueOf(e.getShortKey()));
            s.append("=>");
            s.append(String.valueOf(e.getFloatValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Short2FloatMap.Entry {
        protected short key;
        protected float value;

        public BasicEntry() {
        }

        public BasicEntry(Short key, Float value) {
            this.key = key;
            this.value = value.floatValue();
        }

        public BasicEntry(short key, float value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Short getKey() {
            return this.key;
        }

        @Override
        public short getShortKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            return this.key == (Short)e.getKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


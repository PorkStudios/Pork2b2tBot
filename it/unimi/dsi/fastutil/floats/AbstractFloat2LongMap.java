/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloat2LongFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2LongMap;
import it.unimi.dsi.fastutil.floats.Float2LongMaps;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFloat2LongMap
extends AbstractFloat2LongFunction
implements Float2LongMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2LongMap() {
    }

    @Override
    public boolean containsValue(long v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2LongEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2LongMap.Entry)i.next()).getFloatKey() != k) continue;
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
                return AbstractFloat2LongMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2LongMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2LongMap.Entry> i;
                    {
                        this.i = Float2LongMaps.fastIterator(AbstractFloat2LongMap.this);
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
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long k) {
                return AbstractFloat2LongMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2LongMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Float2LongMap.Entry> i;
                    {
                        this.i = Float2LongMaps.fastIterator(AbstractFloat2LongMap.this);
                    }

                    @Override
                    public long nextLong() {
                        return this.i.next().getLongValue();
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
    public void putAll(Map<? extends Float, ? extends Long> m) {
        if (m instanceof Float2LongMap) {
            ObjectIterator<Float2LongMap.Entry> i = Float2LongMaps.fastIterator((Float2LongMap)m);
            while (i.hasNext()) {
                Float2LongMap.Entry e = i.next();
                this.put(e.getFloatKey(), e.getLongValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Float, ? extends Long>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Float, ? extends Long> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Float2LongMap.Entry> i = Float2LongMaps.fastIterator(this);
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
        return this.float2LongEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Float2LongMap.Entry> i = Float2LongMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2LongMap.Entry e = i.next();
            s.append(String.valueOf(e.getFloatKey()));
            s.append("=>");
            s.append(String.valueOf(e.getLongValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Float2LongMap.Entry {
        protected float key;
        protected long value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, Long value) {
            this.key = key.floatValue();
            this.value = value;
        }

        public BasicEntry(float key, long value) {
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
        public Long getValue() {
            return this.value;
        }

        @Override
        public long getLongValue() {
            return this.value;
        }

        @Override
        public long setValue(long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long setValue(Long value) {
            return this.setValue((long)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)e.getKey()).floatValue()) && this.value == (Long)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ HashCommon.long2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


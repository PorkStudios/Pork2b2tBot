/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2FloatFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2FloatMap;
import it.unimi.dsi.fastutil.longs.Long2FloatMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLong2FloatMap
extends AbstractLong2FloatFunction
implements Long2FloatMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2FloatMap() {
    }

    @Override
    public boolean containsValue(float v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2FloatMap.Entry)i.next()).getLongKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public LongSet keySet() {
        return new AbstractLongSet(){

            @Override
            public boolean contains(long k) {
                return AbstractLong2FloatMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2FloatMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2FloatMap.Entry> i;
                    {
                        this.i = Long2FloatMaps.fastIterator(AbstractLong2FloatMap.this);
                    }

                    @Override
                    public long nextLong() {
                        return this.i.next().getLongKey();
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
                return AbstractLong2FloatMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Long2FloatMap.Entry> i;
                    {
                        this.i = Long2FloatMaps.fastIterator(AbstractLong2FloatMap.this);
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
    public void putAll(Map<? extends Long, ? extends Float> m) {
        if (m instanceof Long2FloatMap) {
            ObjectIterator<Long2FloatMap.Entry> i = Long2FloatMaps.fastIterator((Long2FloatMap)m);
            while (i.hasNext()) {
                Long2FloatMap.Entry e = i.next();
                this.put(e.getLongKey(), e.getFloatValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Long, ? extends Float>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Long, ? extends Float> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Long2FloatMap.Entry> i = Long2FloatMaps.fastIterator(this);
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
        return this.long2FloatEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Long2FloatMap.Entry> i = Long2FloatMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2FloatMap.Entry e = i.next();
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getFloatValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Long2FloatMap.Entry {
        protected long key;
        protected float value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, Float value) {
            this.key = key;
            this.value = value.floatValue();
        }

        public BasicEntry(long key, float value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Long getKey() {
            return this.key;
        }

        @Override
        public long getLongKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            return this.key == (Long)e.getKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


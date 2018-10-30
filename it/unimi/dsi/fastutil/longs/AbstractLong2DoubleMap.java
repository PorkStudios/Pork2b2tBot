/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2DoubleFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLong2DoubleMap
extends AbstractLong2DoubleFunction
implements Long2DoubleMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2DoubleMap() {
    }

    @Override
    public boolean containsValue(double v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2DoubleMap.Entry)i.next()).getLongKey() != k) continue;
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
                return AbstractLong2DoubleMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2DoubleMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2DoubleMap.Entry> i;
                    {
                        this.i = Long2DoubleMaps.fastIterator(AbstractLong2DoubleMap.this);
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
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double k) {
                return AbstractLong2DoubleMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2DoubleMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Long2DoubleMap.Entry> i;
                    {
                        this.i = Long2DoubleMaps.fastIterator(AbstractLong2DoubleMap.this);
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
    public void putAll(Map<? extends Long, ? extends Double> m) {
        if (m instanceof Long2DoubleMap) {
            ObjectIterator<Long2DoubleMap.Entry> i = Long2DoubleMaps.fastIterator((Long2DoubleMap)m);
            while (i.hasNext()) {
                Long2DoubleMap.Entry e = i.next();
                this.put(e.getLongKey(), e.getDoubleValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Long, ? extends Double>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Long, ? extends Double> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Long2DoubleMap.Entry> i = Long2DoubleMaps.fastIterator(this);
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
        return this.long2DoubleEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Long2DoubleMap.Entry> i = Long2DoubleMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2DoubleMap.Entry e = i.next();
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getDoubleValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Long2DoubleMap.Entry {
        protected long key;
        protected double value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, Double value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(long key, double value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            return this.key == (Long)e.getKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ HashCommon.double2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


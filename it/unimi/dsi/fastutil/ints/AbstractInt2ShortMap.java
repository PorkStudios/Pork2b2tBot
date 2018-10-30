/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2ShortFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
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

public abstract class AbstractInt2ShortMap
extends AbstractInt2ShortFunction
implements Int2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2ShortMap() {
    }

    @Override
    public boolean containsValue(short v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2ShortMap.Entry)i.next()).getIntKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public IntSet keySet() {
        return new AbstractIntSet(){

            @Override
            public boolean contains(int k) {
                return AbstractInt2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2ShortMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2ShortMap.Entry> i;
                    {
                        this.i = Int2ShortMaps.fastIterator(AbstractInt2ShortMap.this);
                    }

                    @Override
                    public int nextInt() {
                        return this.i.next().getIntKey();
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
                return AbstractInt2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Int2ShortMap.Entry> i;
                    {
                        this.i = Int2ShortMaps.fastIterator(AbstractInt2ShortMap.this);
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
    public void putAll(Map<? extends Integer, ? extends Short> m) {
        if (m instanceof Int2ShortMap) {
            ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator((Int2ShortMap)m);
            while (i.hasNext()) {
                Int2ShortMap.Entry e = i.next();
                this.put(e.getIntKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Integer, ? extends Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Integer, ? extends Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator(this);
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
        return this.int2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Int2ShortMap.Entry> i = Int2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2ShortMap.Entry e = i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Int2ShortMap.Entry {
        protected int key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, Short value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(int key, short value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Integer getKey() {
            return this.key;
        }

        @Override
        public int getIntKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            return this.key == (Integer)e.getKey() && this.value == (Short)e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ this.value;
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


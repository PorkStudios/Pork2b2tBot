/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ShortFunction;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2ShortMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortMaps;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractShort2ShortMap
extends AbstractShort2ShortFunction
implements Short2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractShort2ShortMap() {
    }

    @Override
    public boolean containsValue(short v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(short k) {
        Iterator i = this.short2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2ShortMap.Entry)i.next()).getShortKey() != k) continue;
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
                return AbstractShort2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractShort2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Short2ShortMap.Entry> i;
                    {
                        this.i = Short2ShortMaps.fastIterator(AbstractShort2ShortMap.this);
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
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short k) {
                return AbstractShort2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractShort2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Short2ShortMap.Entry> i;
                    {
                        this.i = Short2ShortMaps.fastIterator(AbstractShort2ShortMap.this);
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
    public void putAll(Map<? extends Short, ? extends Short> m) {
        if (m instanceof Short2ShortMap) {
            ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator((Short2ShortMap)m);
            while (i.hasNext()) {
                Short2ShortMap.Entry e = i.next();
                this.put(e.getShortKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Short, ? extends Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Short, ? extends Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator(this);
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
        return this.short2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Short2ShortMap.Entry e = i.next();
            s.append(String.valueOf(e.getShortKey()));
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Short2ShortMap.Entry {
        protected short key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(Short key, Short value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(short key, short value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            return this.key == (Short)e.getKey() && this.value == (Short)e.getValue();
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


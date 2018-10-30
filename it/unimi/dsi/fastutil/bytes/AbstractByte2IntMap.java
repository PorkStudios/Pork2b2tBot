/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2IntFunction;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntMaps;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
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

public abstract class AbstractByte2IntMap
extends AbstractByte2IntFunction
implements Byte2IntMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractByte2IntMap() {
    }

    @Override
    public boolean containsValue(int v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(byte k) {
        Iterator i = this.byte2IntEntrySet().iterator();
        while (i.hasNext()) {
            if (((Byte2IntMap.Entry)i.next()).getByteKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ByteSet keySet() {
        return new AbstractByteSet(){

            @Override
            public boolean contains(byte k) {
                return AbstractByte2IntMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractByte2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2IntMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Byte2IntMap.Entry> i;
                    {
                        this.i = Byte2IntMaps.fastIterator(AbstractByte2IntMap.this);
                    }

                    @Override
                    public byte nextByte() {
                        return this.i.next().getByteKey();
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
                return AbstractByte2IntMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractByte2IntMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2IntMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Byte2IntMap.Entry> i;
                    {
                        this.i = Byte2IntMaps.fastIterator(AbstractByte2IntMap.this);
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
    public void putAll(Map<? extends Byte, ? extends Integer> m) {
        if (m instanceof Byte2IntMap) {
            ObjectIterator<Byte2IntMap.Entry> i = Byte2IntMaps.fastIterator((Byte2IntMap)m);
            while (i.hasNext()) {
                Byte2IntMap.Entry e = i.next();
                this.put(e.getByteKey(), e.getIntValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Byte, ? extends Integer>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Byte, ? extends Integer> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Byte2IntMap.Entry> i = Byte2IntMaps.fastIterator(this);
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
        return this.byte2IntEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Byte2IntMap.Entry> i = Byte2IntMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Byte2IntMap.Entry e = i.next();
            s.append(String.valueOf(e.getByteKey()));
            s.append("=>");
            s.append(String.valueOf(e.getIntValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Byte2IntMap.Entry {
        protected byte key;
        protected int value;

        public BasicEntry() {
        }

        public BasicEntry(Byte key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(byte key, int value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Byte getKey() {
            return this.key;
        }

        @Override
        public byte getByteKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            return this.key == (Byte)e.getKey() && this.value == (Integer)e.getValue();
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


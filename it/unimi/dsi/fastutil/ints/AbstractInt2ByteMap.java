/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.ints.AbstractInt2ByteFunction;
import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMaps;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractInt2ByteMap
extends AbstractInt2ByteFunction
implements Int2ByteMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractInt2ByteMap() {
    }

    @Override
    public boolean containsValue(byte v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(int k) {
        Iterator i = this.int2ByteEntrySet().iterator();
        while (i.hasNext()) {
            if (((Int2ByteMap.Entry)i.next()).getIntKey() != k) continue;
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
                return AbstractInt2ByteMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractInt2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2ByteMap.this.clear();
            }

            @Override
            public IntIterator iterator() {
                return new IntIterator(){
                    private final ObjectIterator<Int2ByteMap.Entry> i;
                    {
                        this.i = Int2ByteMaps.fastIterator(AbstractInt2ByteMap.this);
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
    public ByteCollection values() {
        return new AbstractByteCollection(){

            @Override
            public boolean contains(byte k) {
                return AbstractInt2ByteMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractInt2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractInt2ByteMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Int2ByteMap.Entry> i;
                    {
                        this.i = Int2ByteMaps.fastIterator(AbstractInt2ByteMap.this);
                    }

                    @Override
                    public byte nextByte() {
                        return this.i.next().getByteValue();
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
    public void putAll(Map<? extends Integer, ? extends Byte> m) {
        if (m instanceof Int2ByteMap) {
            ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator((Int2ByteMap)m);
            while (i.hasNext()) {
                Int2ByteMap.Entry e = i.next();
                this.put(e.getIntKey(), e.getByteValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Integer, ? extends Byte>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Integer, ? extends Byte> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator(this);
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
        return this.int2ByteEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Int2ByteMap.Entry e = i.next();
            s.append(String.valueOf(e.getIntKey()));
            s.append("=>");
            s.append(String.valueOf(e.getByteValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Int2ByteMap.Entry {
        protected int key;
        protected byte value;

        public BasicEntry() {
        }

        public BasicEntry(Integer key, Byte value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(int key, byte value) {
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
        public Byte getValue() {
            return this.value;
        }

        @Override
        public byte getByteValue() {
            return this.value;
        }

        @Override
        public byte setValue(byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte setValue(Byte value) {
            return this.setValue((byte)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            return this.key == (Integer)e.getKey() && this.value == (Byte)e.getValue();
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


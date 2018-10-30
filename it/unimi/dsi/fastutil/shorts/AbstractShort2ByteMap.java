/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ByteFunction;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteMaps;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractShort2ByteMap
extends AbstractShort2ByteFunction
implements Short2ByteMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractShort2ByteMap() {
    }

    @Override
    public boolean containsValue(byte v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(short k) {
        Iterator i = this.short2ByteEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2ByteMap.Entry)i.next()).getShortKey() != k) continue;
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
                return AbstractShort2ByteMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractShort2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2ByteMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Short2ByteMap.Entry> i;
                    {
                        this.i = Short2ByteMaps.fastIterator(AbstractShort2ByteMap.this);
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
    public ByteCollection values() {
        return new AbstractByteCollection(){

            @Override
            public boolean contains(byte k) {
                return AbstractShort2ByteMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractShort2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2ByteMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Short2ByteMap.Entry> i;
                    {
                        this.i = Short2ByteMaps.fastIterator(AbstractShort2ByteMap.this);
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
    public void putAll(Map<? extends Short, ? extends Byte> m) {
        if (m instanceof Short2ByteMap) {
            ObjectIterator<Short2ByteMap.Entry> i = Short2ByteMaps.fastIterator((Short2ByteMap)m);
            while (i.hasNext()) {
                Short2ByteMap.Entry e = i.next();
                this.put(e.getShortKey(), e.getByteValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Short, ? extends Byte>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Short, ? extends Byte> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Short2ByteMap.Entry> i = Short2ByteMaps.fastIterator(this);
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
        return this.short2ByteEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Short2ByteMap.Entry> i = Short2ByteMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Short2ByteMap.Entry e = i.next();
            s.append(String.valueOf(e.getShortKey()));
            s.append("=>");
            s.append(String.valueOf(e.getByteValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Short2ByteMap.Entry {
        protected short key;
        protected byte value;

        public BasicEntry() {
        }

        public BasicEntry(Short key, Byte value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(short key, byte value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            return this.key == (Short)e.getKey() && this.value == (Byte)e.getValue();
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


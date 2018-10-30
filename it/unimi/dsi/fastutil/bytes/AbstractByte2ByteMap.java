/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ByteFunction;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ByteMap;
import it.unimi.dsi.fastutil.bytes.Byte2ByteMaps;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractByte2ByteMap
extends AbstractByte2ByteFunction
implements Byte2ByteMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractByte2ByteMap() {
    }

    @Override
    public boolean containsValue(byte v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(byte k) {
        Iterator i = this.byte2ByteEntrySet().iterator();
        while (i.hasNext()) {
            if (((Byte2ByteMap.Entry)i.next()).getByteKey() != k) continue;
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
                return AbstractByte2ByteMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractByte2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2ByteMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Byte2ByteMap.Entry> i;
                    {
                        this.i = Byte2ByteMaps.fastIterator(AbstractByte2ByteMap.this);
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
    public ByteCollection values() {
        return new AbstractByteCollection(){

            @Override
            public boolean contains(byte k) {
                return AbstractByte2ByteMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractByte2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2ByteMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Byte2ByteMap.Entry> i;
                    {
                        this.i = Byte2ByteMaps.fastIterator(AbstractByte2ByteMap.this);
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
    public void putAll(Map<? extends Byte, ? extends Byte> m) {
        if (m instanceof Byte2ByteMap) {
            ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator((Byte2ByteMap)m);
            while (i.hasNext()) {
                Byte2ByteMap.Entry e = i.next();
                this.put(e.getByteKey(), e.getByteValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Byte, ? extends Byte>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Byte, ? extends Byte> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator(this);
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
        return this.byte2ByteEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Byte2ByteMap.Entry e = i.next();
            s.append(String.valueOf(e.getByteKey()));
            s.append("=>");
            s.append(String.valueOf(e.getByteValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Byte2ByteMap.Entry {
        protected byte key;
        protected byte value;

        public BasicEntry() {
        }

        public BasicEntry(Byte key, Byte value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(byte key, byte value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            return this.key == (Byte)e.getKey() && this.value == (Byte)e.getValue();
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


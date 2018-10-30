/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ShortFunction;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMaps;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
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

public abstract class AbstractByte2ShortMap
extends AbstractByte2ShortFunction
implements Byte2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractByte2ShortMap() {
    }

    @Override
    public boolean containsValue(short v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(byte k) {
        Iterator i = this.byte2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Byte2ShortMap.Entry)i.next()).getByteKey() != k) continue;
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
                return AbstractByte2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractByte2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2ShortMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Byte2ShortMap.Entry> i;
                    {
                        this.i = Byte2ShortMaps.fastIterator(AbstractByte2ShortMap.this);
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
    public ShortCollection values() {
        return new AbstractShortCollection(){

            @Override
            public boolean contains(short k) {
                return AbstractByte2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractByte2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Byte2ShortMap.Entry> i;
                    {
                        this.i = Byte2ShortMaps.fastIterator(AbstractByte2ShortMap.this);
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
    public void putAll(Map<? extends Byte, ? extends Short> m) {
        if (m instanceof Byte2ShortMap) {
            ObjectIterator<Byte2ShortMap.Entry> i = Byte2ShortMaps.fastIterator((Byte2ShortMap)m);
            while (i.hasNext()) {
                Byte2ShortMap.Entry e = i.next();
                this.put(e.getByteKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Byte, ? extends Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Byte, ? extends Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Byte2ShortMap.Entry> i = Byte2ShortMaps.fastIterator(this);
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
        return this.byte2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Byte2ShortMap.Entry> i = Byte2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Byte2ShortMap.Entry e = i.next();
            s.append(String.valueOf(e.getByteKey()));
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Byte2ShortMap.Entry {
        protected byte key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(Byte key, Short value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(byte key, short value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            return this.key == (Byte)e.getKey() && this.value == (Short)e.getValue();
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


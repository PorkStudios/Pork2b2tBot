/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2ByteFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLong2ByteMap
extends AbstractLong2ByteFunction
implements Long2ByteMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2ByteMap() {
    }

    @Override
    public boolean containsValue(byte v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2ByteEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2ByteMap.Entry)i.next()).getLongKey() != k) continue;
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
                return AbstractLong2ByteMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2ByteMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2ByteMap.Entry> i;
                    {
                        this.i = Long2ByteMaps.fastIterator(AbstractLong2ByteMap.this);
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
    public ByteCollection values() {
        return new AbstractByteCollection(){

            @Override
            public boolean contains(byte k) {
                return AbstractLong2ByteMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2ByteMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Long2ByteMap.Entry> i;
                    {
                        this.i = Long2ByteMaps.fastIterator(AbstractLong2ByteMap.this);
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
    public void putAll(Map<? extends Long, ? extends Byte> m) {
        if (m instanceof Long2ByteMap) {
            ObjectIterator<Long2ByteMap.Entry> i = Long2ByteMaps.fastIterator((Long2ByteMap)m);
            while (i.hasNext()) {
                Long2ByteMap.Entry e = i.next();
                this.put(e.getLongKey(), e.getByteValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Long, ? extends Byte>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Long, ? extends Byte> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Long2ByteMap.Entry> i = Long2ByteMaps.fastIterator(this);
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
        return this.long2ByteEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Long2ByteMap.Entry> i = Long2ByteMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2ByteMap.Entry e = i.next();
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getByteValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Long2ByteMap.Entry {
        protected long key;
        protected byte value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, Byte value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(long key, byte value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            return this.key == (Long)e.getKey() && this.value == (Byte)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ this.value;
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ByteFunction;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ByteMap;
import it.unimi.dsi.fastutil.floats.Float2ByteMaps;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractFloat2ByteMap
extends AbstractFloat2ByteFunction
implements Float2ByteMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractFloat2ByteMap() {
    }

    @Override
    public boolean containsValue(byte v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(float k) {
        Iterator i = this.float2ByteEntrySet().iterator();
        while (i.hasNext()) {
            if (((Float2ByteMap.Entry)i.next()).getFloatKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public FloatSet keySet() {
        return new AbstractFloatSet(){

            @Override
            public boolean contains(float k) {
                return AbstractFloat2ByteMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ByteMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Float2ByteMap.Entry> i;
                    {
                        this.i = Float2ByteMaps.fastIterator(AbstractFloat2ByteMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return this.i.next().getFloatKey();
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
                return AbstractFloat2ByteMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractFloat2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractFloat2ByteMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Float2ByteMap.Entry> i;
                    {
                        this.i = Float2ByteMaps.fastIterator(AbstractFloat2ByteMap.this);
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
    public void putAll(Map<? extends Float, ? extends Byte> m) {
        if (m instanceof Float2ByteMap) {
            ObjectIterator<Float2ByteMap.Entry> i = Float2ByteMaps.fastIterator((Float2ByteMap)m);
            while (i.hasNext()) {
                Float2ByteMap.Entry e = i.next();
                this.put(e.getFloatKey(), e.getByteValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Float, ? extends Byte>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Float, ? extends Byte> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Float2ByteMap.Entry> i = Float2ByteMaps.fastIterator(this);
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
        return this.float2ByteEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Float2ByteMap.Entry> i = Float2ByteMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Float2ByteMap.Entry e = i.next();
            s.append(String.valueOf(e.getFloatKey()));
            s.append("=>");
            s.append(String.valueOf(e.getByteValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Float2ByteMap.Entry {
        protected float key;
        protected byte value;

        public BasicEntry() {
        }

        public BasicEntry(Float key, Byte value) {
            this.key = key.floatValue();
            this.value = value;
        }

        public BasicEntry(float key, byte value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Float getKey() {
            return Float.valueOf(this.key);
        }

        @Override
        public float getFloatKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(((Float)e.getKey()).floatValue()) && this.value == (Byte)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ this.value;
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


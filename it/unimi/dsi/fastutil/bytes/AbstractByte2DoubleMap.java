/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByte2DoubleFunction;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleMap;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleMaps;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractByte2DoubleMap
extends AbstractByte2DoubleFunction
implements Byte2DoubleMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractByte2DoubleMap() {
    }

    @Override
    public boolean containsValue(double v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(byte k) {
        Iterator i = this.byte2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Byte2DoubleMap.Entry)i.next()).getByteKey() != k) continue;
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
                return AbstractByte2DoubleMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractByte2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2DoubleMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Byte2DoubleMap.Entry> i;
                    {
                        this.i = Byte2DoubleMaps.fastIterator(AbstractByte2DoubleMap.this);
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
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double k) {
                return AbstractByte2DoubleMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractByte2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2DoubleMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Byte2DoubleMap.Entry> i;
                    {
                        this.i = Byte2DoubleMaps.fastIterator(AbstractByte2DoubleMap.this);
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
    public void putAll(Map<? extends Byte, ? extends Double> m) {
        if (m instanceof Byte2DoubleMap) {
            ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator((Byte2DoubleMap)m);
            while (i.hasNext()) {
                Byte2DoubleMap.Entry e = i.next();
                this.put(e.getByteKey(), e.getDoubleValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Byte, ? extends Double>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Byte, ? extends Double> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator(this);
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
        return this.byte2DoubleEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Byte2DoubleMap.Entry e = i.next();
            s.append(String.valueOf(e.getByteKey()));
            s.append("=>");
            s.append(String.valueOf(e.getDoubleValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Byte2DoubleMap.Entry {
        protected byte key;
        protected double value;

        public BasicEntry() {
        }

        public BasicEntry(Byte key, Double value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(byte key, double value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            return this.key == (Byte)e.getKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.double2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


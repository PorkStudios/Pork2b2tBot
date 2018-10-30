/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanMap;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanMaps;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractByte2BooleanMap
extends AbstractByte2BooleanFunction
implements Byte2BooleanMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractByte2BooleanMap() {
    }

    @Override
    public boolean containsValue(boolean v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(byte k) {
        Iterator i = this.byte2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Byte2BooleanMap.Entry)i.next()).getByteKey() != k) continue;
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
                return AbstractByte2BooleanMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractByte2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2BooleanMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Byte2BooleanMap.Entry> i;
                    {
                        this.i = Byte2BooleanMaps.fastIterator(AbstractByte2BooleanMap.this);
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
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean k) {
                return AbstractByte2BooleanMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractByte2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2BooleanMap.this.clear();
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    private final ObjectIterator<Byte2BooleanMap.Entry> i;
                    {
                        this.i = Byte2BooleanMaps.fastIterator(AbstractByte2BooleanMap.this);
                    }

                    @Override
                    public boolean nextBoolean() {
                        return this.i.next().getBooleanValue();
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
    public void putAll(Map<? extends Byte, ? extends Boolean> m) {
        if (m instanceof Byte2BooleanMap) {
            ObjectIterator<Byte2BooleanMap.Entry> i = Byte2BooleanMaps.fastIterator((Byte2BooleanMap)m);
            while (i.hasNext()) {
                Byte2BooleanMap.Entry e = i.next();
                this.put(e.getByteKey(), e.getBooleanValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Byte, ? extends Boolean>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Byte, ? extends Boolean> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Byte2BooleanMap.Entry> i = Byte2BooleanMaps.fastIterator(this);
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
        return this.byte2BooleanEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Byte2BooleanMap.Entry> i = Byte2BooleanMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Byte2BooleanMap.Entry e = i.next();
            s.append(String.valueOf(e.getByteKey()));
            s.append("=>");
            s.append(String.valueOf(e.getBooleanValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Byte2BooleanMap.Entry {
        protected byte key;
        protected boolean value;

        public BasicEntry() {
        }

        public BasicEntry(Byte key, Boolean value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(byte key, boolean value) {
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
        public Boolean getValue() {
            return this.value;
        }

        @Override
        public boolean getBooleanValue() {
            return this.value;
        }

        @Override
        public boolean setValue(boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean setValue(Boolean value) {
            return this.setValue((boolean)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            return this.key == (Byte)e.getKey() && this.value == (Boolean)e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value ? 1231 : 1237);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


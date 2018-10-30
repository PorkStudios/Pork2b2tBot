/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2CharFunction;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2CharMap;
import it.unimi.dsi.fastutil.bytes.Byte2CharMaps;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractByte2CharMap
extends AbstractByte2CharFunction
implements Byte2CharMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractByte2CharMap() {
    }

    @Override
    public boolean containsValue(char v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(byte k) {
        Iterator i = this.byte2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Byte2CharMap.Entry)i.next()).getByteKey() != k) continue;
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
                return AbstractByte2CharMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractByte2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2CharMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Byte2CharMap.Entry> i;
                    {
                        this.i = Byte2CharMaps.fastIterator(AbstractByte2CharMap.this);
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
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char k) {
                return AbstractByte2CharMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractByte2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Byte2CharMap.Entry> i;
                    {
                        this.i = Byte2CharMaps.fastIterator(AbstractByte2CharMap.this);
                    }

                    @Override
                    public char nextChar() {
                        return this.i.next().getCharValue();
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
    public void putAll(Map<? extends Byte, ? extends Character> m) {
        if (m instanceof Byte2CharMap) {
            ObjectIterator<Byte2CharMap.Entry> i = Byte2CharMaps.fastIterator((Byte2CharMap)m);
            while (i.hasNext()) {
                Byte2CharMap.Entry e = i.next();
                this.put(e.getByteKey(), e.getCharValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Byte, ? extends Character>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Byte, ? extends Character> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Byte2CharMap.Entry> i = Byte2CharMaps.fastIterator(this);
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
        return this.byte2CharEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Byte2CharMap.Entry> i = Byte2CharMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Byte2CharMap.Entry e = i.next();
            s.append(String.valueOf(e.getByteKey()));
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Byte2CharMap.Entry {
        protected byte key;
        protected char value;

        public BasicEntry() {
        }

        public BasicEntry(Byte key, Character value) {
            this.key = key;
            this.value = value.charValue();
        }

        public BasicEntry(byte key, char value) {
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
        public Character getValue() {
            return Character.valueOf(this.value);
        }

        @Override
        public char getCharValue() {
            return this.value;
        }

        @Override
        public char setValue(char value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character setValue(Character value) {
            return Character.valueOf(this.setValue(value.charValue()));
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
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            return this.key == (Byte)e.getKey() && this.value == ((Character)e.getValue()).charValue();
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


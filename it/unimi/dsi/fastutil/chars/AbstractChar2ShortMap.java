/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2ShortFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2ShortMap;
import it.unimi.dsi.fastutil.chars.Char2ShortMaps;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
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

public abstract class AbstractChar2ShortMap
extends AbstractChar2ShortFunction
implements Char2ShortMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2ShortMap() {
    }

    @Override
    public boolean containsValue(short v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(char k) {
        Iterator i = this.char2ShortEntrySet().iterator();
        while (i.hasNext()) {
            if (((Char2ShortMap.Entry)i.next()).getCharKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public CharSet keySet() {
        return new AbstractCharSet(){

            @Override
            public boolean contains(char k) {
                return AbstractChar2ShortMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractChar2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2ShortMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Char2ShortMap.Entry> i;
                    {
                        this.i = Char2ShortMaps.fastIterator(AbstractChar2ShortMap.this);
                    }

                    @Override
                    public char nextChar() {
                        return this.i.next().getCharKey();
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
                return AbstractChar2ShortMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractChar2ShortMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2ShortMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Char2ShortMap.Entry> i;
                    {
                        this.i = Char2ShortMaps.fastIterator(AbstractChar2ShortMap.this);
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
    public void putAll(Map<? extends Character, ? extends Short> m) {
        if (m instanceof Char2ShortMap) {
            ObjectIterator<Char2ShortMap.Entry> i = Char2ShortMaps.fastIterator((Char2ShortMap)m);
            while (i.hasNext()) {
                Char2ShortMap.Entry e = i.next();
                this.put(e.getCharKey(), e.getShortValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Character, ? extends Short>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Character, ? extends Short> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Char2ShortMap.Entry> i = Char2ShortMaps.fastIterator(this);
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
        return this.char2ShortEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Char2ShortMap.Entry> i = Char2ShortMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Char2ShortMap.Entry e = i.next();
            s.append(String.valueOf(e.getCharKey()));
            s.append("=>");
            s.append(String.valueOf(e.getShortValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Char2ShortMap.Entry {
        protected char key;
        protected short value;

        public BasicEntry() {
        }

        public BasicEntry(Character key, Short value) {
            this.key = key.charValue();
            this.value = value;
        }

        public BasicEntry(char key, short value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Character getKey() {
            return Character.valueOf(this.key);
        }

        @Override
        public char getCharKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            return this.key == ((Character)e.getKey()).charValue() && this.value == (Short)e.getValue();
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


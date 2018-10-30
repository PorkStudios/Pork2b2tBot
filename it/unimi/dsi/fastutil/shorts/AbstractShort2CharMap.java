/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2CharFunction;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2CharMap;
import it.unimi.dsi.fastutil.shorts.Short2CharMaps;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractShort2CharMap
extends AbstractShort2CharFunction
implements Short2CharMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractShort2CharMap() {
    }

    @Override
    public boolean containsValue(char v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(short k) {
        Iterator i = this.short2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2CharMap.Entry)i.next()).getShortKey() != k) continue;
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
                return AbstractShort2CharMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractShort2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2CharMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Short2CharMap.Entry> i;
                    {
                        this.i = Short2CharMaps.fastIterator(AbstractShort2CharMap.this);
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
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char k) {
                return AbstractShort2CharMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractShort2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Short2CharMap.Entry> i;
                    {
                        this.i = Short2CharMaps.fastIterator(AbstractShort2CharMap.this);
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
    public void putAll(Map<? extends Short, ? extends Character> m) {
        if (m instanceof Short2CharMap) {
            ObjectIterator<Short2CharMap.Entry> i = Short2CharMaps.fastIterator((Short2CharMap)m);
            while (i.hasNext()) {
                Short2CharMap.Entry e = i.next();
                this.put(e.getShortKey(), e.getCharValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Short, ? extends Character>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Short, ? extends Character> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Short2CharMap.Entry> i = Short2CharMaps.fastIterator(this);
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
        return this.short2CharEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Short2CharMap.Entry> i = Short2CharMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Short2CharMap.Entry e = i.next();
            s.append(String.valueOf(e.getShortKey()));
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Short2CharMap.Entry {
        protected short key;
        protected char value;

        public BasicEntry() {
        }

        public BasicEntry(Short key, Character value) {
            this.key = key;
            this.value = value.charValue();
        }

        public BasicEntry(short key, char value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            return this.key == (Short)e.getKey() && this.value == ((Character)e.getValue()).charValue();
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


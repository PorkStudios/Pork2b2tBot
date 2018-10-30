/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2CharFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharMaps;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractChar2CharMap
extends AbstractChar2CharFunction
implements Char2CharMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2CharMap() {
    }

    @Override
    public boolean containsValue(char v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(char k) {
        Iterator i = this.char2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Char2CharMap.Entry)i.next()).getCharKey() != k) continue;
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
                return AbstractChar2CharMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractChar2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Char2CharMap.Entry> i;
                    {
                        this.i = Char2CharMaps.fastIterator(AbstractChar2CharMap.this);
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
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char k) {
                return AbstractChar2CharMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractChar2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Char2CharMap.Entry> i;
                    {
                        this.i = Char2CharMaps.fastIterator(AbstractChar2CharMap.this);
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
    public void putAll(Map<? extends Character, ? extends Character> m) {
        if (m instanceof Char2CharMap) {
            ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator((Char2CharMap)m);
            while (i.hasNext()) {
                Char2CharMap.Entry e = i.next();
                this.put(e.getCharKey(), e.getCharValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Character, ? extends Character>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Character, ? extends Character> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator(this);
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
        return this.char2CharEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Char2CharMap.Entry e = i.next();
            s.append(String.valueOf(e.getCharKey()));
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Char2CharMap.Entry {
        protected char key;
        protected char value;

        public BasicEntry() {
        }

        public BasicEntry(Character key, Character value) {
            this.key = key.charValue();
            this.value = value.charValue();
        }

        public BasicEntry(char key, char value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            return this.key == ((Character)e.getKey()).charValue() && this.value == ((Character)e.getValue()).charValue();
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


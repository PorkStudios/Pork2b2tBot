/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2LongFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2LongMap;
import it.unimi.dsi.fastutil.chars.Char2LongMaps;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractChar2LongMap
extends AbstractChar2LongFunction
implements Char2LongMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2LongMap() {
    }

    @Override
    public boolean containsValue(long v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(char k) {
        Iterator i = this.char2LongEntrySet().iterator();
        while (i.hasNext()) {
            if (((Char2LongMap.Entry)i.next()).getCharKey() != k) continue;
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
                return AbstractChar2LongMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractChar2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2LongMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Char2LongMap.Entry> i;
                    {
                        this.i = Char2LongMaps.fastIterator(AbstractChar2LongMap.this);
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
    public LongCollection values() {
        return new AbstractLongCollection(){

            @Override
            public boolean contains(long k) {
                return AbstractChar2LongMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractChar2LongMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2LongMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Char2LongMap.Entry> i;
                    {
                        this.i = Char2LongMaps.fastIterator(AbstractChar2LongMap.this);
                    }

                    @Override
                    public long nextLong() {
                        return this.i.next().getLongValue();
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
    public void putAll(Map<? extends Character, ? extends Long> m) {
        if (m instanceof Char2LongMap) {
            ObjectIterator<Char2LongMap.Entry> i = Char2LongMaps.fastIterator((Char2LongMap)m);
            while (i.hasNext()) {
                Char2LongMap.Entry e = i.next();
                this.put(e.getCharKey(), e.getLongValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Character, ? extends Long>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Character, ? extends Long> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Char2LongMap.Entry> i = Char2LongMaps.fastIterator(this);
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
        return this.char2LongEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Char2LongMap.Entry> i = Char2LongMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Char2LongMap.Entry e = i.next();
            s.append(String.valueOf(e.getCharKey()));
            s.append("=>");
            s.append(String.valueOf(e.getLongValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Char2LongMap.Entry {
        protected char key;
        protected long value;

        public BasicEntry() {
        }

        public BasicEntry(Character key, Long value) {
            this.key = key.charValue();
            this.value = value;
        }

        public BasicEntry(char key, long value) {
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
        public Long getValue() {
            return this.value;
        }

        @Override
        public long getLongValue() {
            return this.value;
        }

        @Override
        public long setValue(long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long setValue(Long value) {
            return this.setValue((long)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            return this.key == ((Character)e.getKey()).charValue() && this.value == (Long)e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.long2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


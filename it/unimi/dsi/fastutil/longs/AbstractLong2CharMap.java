/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2CharFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2CharMap;
import it.unimi.dsi.fastutil.longs.Long2CharMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLong2CharMap
extends AbstractLong2CharFunction
implements Long2CharMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2CharMap() {
    }

    @Override
    public boolean containsValue(char v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2CharMap.Entry)i.next()).getLongKey() != k) continue;
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
                return AbstractLong2CharMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2CharMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2CharMap.Entry> i;
                    {
                        this.i = Long2CharMaps.fastIterator(AbstractLong2CharMap.this);
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
    public CharCollection values() {
        return new AbstractCharCollection(){

            @Override
            public boolean contains(char k) {
                return AbstractLong2CharMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Long2CharMap.Entry> i;
                    {
                        this.i = Long2CharMaps.fastIterator(AbstractLong2CharMap.this);
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
    public void putAll(Map<? extends Long, ? extends Character> m) {
        if (m instanceof Long2CharMap) {
            ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator((Long2CharMap)m);
            while (i.hasNext()) {
                Long2CharMap.Entry e = i.next();
                this.put(e.getLongKey(), e.getCharValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Long, ? extends Character>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Long, ? extends Character> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator(this);
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
        return this.long2CharEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Long2CharMap.Entry> i = Long2CharMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2CharMap.Entry e = i.next();
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Long2CharMap.Entry {
        protected long key;
        protected char value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, Character value) {
            this.key = key;
            this.value = value.charValue();
        }

        public BasicEntry(long key, char value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            return this.key == (Long)e.getKey() && this.value == ((Character)e.getValue()).charValue();
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


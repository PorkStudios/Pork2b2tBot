/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2ObjectFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMaps;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractChar2ObjectMap<V>
extends AbstractChar2ObjectFunction<V>
implements Char2ObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2ObjectMap() {
    }

    @Override
    public boolean containsValue(Object v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(char k) {
        Iterator i = this.char2ObjectEntrySet().iterator();
        while (i.hasNext()) {
            if (((Char2ObjectMap.Entry)i.next()).getCharKey() != k) continue;
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
                return AbstractChar2ObjectMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractChar2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2ObjectMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Char2ObjectMap.Entry<V>> i;
                    {
                        this.i = Char2ObjectMaps.fastIterator(AbstractChar2ObjectMap.this);
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
    public ObjectCollection<V> values() {
        return new AbstractObjectCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractChar2ObjectMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractChar2ObjectMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2ObjectMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Char2ObjectMap.Entry<V>> i;
                    {
                        this.i = Char2ObjectMaps.fastIterator(AbstractChar2ObjectMap.this);
                    }

                    @Override
                    public V next() {
                        return this.i.next().getValue();
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
    public void putAll(Map<? extends Character, ? extends V> m) {
        if (m instanceof Char2ObjectMap) {
            ObjectIterator i = Char2ObjectMaps.fastIterator((Char2ObjectMap)m);
            while (i.hasNext()) {
                Char2ObjectMap.Entry e = i.next();
                this.put(e.getCharKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Character, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Character, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Char2ObjectMaps.fastIterator(this);
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
        return this.char2ObjectEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Char2ObjectMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Char2ObjectMap.Entry e = i.next();
            s.append(String.valueOf(e.getCharKey()));
            s.append("=>");
            if (this == e.getValue()) {
                s.append("(this map)");
                continue;
            }
            s.append(String.valueOf(e.getValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry<V>
    implements Char2ObjectMap.Entry<V> {
        protected char key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Character key, V value) {
            this.key = key.charValue();
            this.value = value;
        }

        public BasicEntry(char key, V value) {
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

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
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
            return this.key == ((Character)e.getKey()).charValue() && Objects.equals(this.value, e.getValue());
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : this.value.hashCode());
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


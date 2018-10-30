/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.AbstractReference2CharFunction;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2CharMap;
import it.unimi.dsi.fastutil.objects.Reference2CharMaps;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractReference2CharMap<K>
extends AbstractReference2CharFunction<K>
implements Reference2CharMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractReference2CharMap() {
    }

    @Override
    public boolean containsValue(char v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.reference2CharEntrySet().iterator();
        while (i.hasNext()) {
            if (((Reference2CharMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ReferenceSet<K> keySet() {
        return new AbstractReferenceSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractReference2CharMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractReference2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2CharMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Reference2CharMap.Entry<K>> i;
                    {
                        this.i = Reference2CharMaps.fastIterator(AbstractReference2CharMap.this);
                    }

                    @Override
                    public K next() {
                        return this.i.next().getKey();
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
                return AbstractReference2CharMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractReference2CharMap.this.size();
            }

            @Override
            public void clear() {
                AbstractReference2CharMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Reference2CharMap.Entry<K>> i;
                    {
                        this.i = Reference2CharMaps.fastIterator(AbstractReference2CharMap.this);
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
    public void putAll(Map<? extends K, ? extends Character> m) {
        if (m instanceof Reference2CharMap) {
            ObjectIterator i = Reference2CharMaps.fastIterator((Reference2CharMap)m);
            while (i.hasNext()) {
                Reference2CharMap.Entry e = i.next();
                this.put(e.getKey(), e.getCharValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Character>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Character> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Reference2CharMaps.fastIterator(this);
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
        return this.reference2CharEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Reference2CharMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Reference2CharMap.Entry e = i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getCharValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry<K>
    implements Reference2CharMap.Entry<K> {
        protected K key;
        protected char value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Character value) {
            this.key = key;
            this.value = value.charValue();
        }

        public BasicEntry(K key, char value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
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
            if (e.getValue() == null || !(e.getValue() instanceof Character)) {
                return false;
            }
            return this.key == e.getKey() && this.value == ((Character)e.getValue()).charValue();
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.key) ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2FloatFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2FloatMap;
import it.unimi.dsi.fastutil.chars.Char2FloatMaps;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractChar2FloatMap
extends AbstractChar2FloatFunction
implements Char2FloatMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2FloatMap() {
    }

    @Override
    public boolean containsValue(float v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(char k) {
        Iterator i = this.char2FloatEntrySet().iterator();
        while (i.hasNext()) {
            if (((Char2FloatMap.Entry)i.next()).getCharKey() != k) continue;
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
                return AbstractChar2FloatMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractChar2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2FloatMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Char2FloatMap.Entry> i;
                    {
                        this.i = Char2FloatMaps.fastIterator(AbstractChar2FloatMap.this);
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
    public FloatCollection values() {
        return new AbstractFloatCollection(){

            @Override
            public boolean contains(float k) {
                return AbstractChar2FloatMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractChar2FloatMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2FloatMap.this.clear();
            }

            @Override
            public FloatIterator iterator() {
                return new FloatIterator(){
                    private final ObjectIterator<Char2FloatMap.Entry> i;
                    {
                        this.i = Char2FloatMaps.fastIterator(AbstractChar2FloatMap.this);
                    }

                    @Override
                    public float nextFloat() {
                        return this.i.next().getFloatValue();
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
    public void putAll(Map<? extends Character, ? extends Float> m) {
        if (m instanceof Char2FloatMap) {
            ObjectIterator<Char2FloatMap.Entry> i = Char2FloatMaps.fastIterator((Char2FloatMap)m);
            while (i.hasNext()) {
                Char2FloatMap.Entry e = i.next();
                this.put(e.getCharKey(), e.getFloatValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Character, ? extends Float>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Character, ? extends Float> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Char2FloatMap.Entry> i = Char2FloatMaps.fastIterator(this);
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
        return this.char2FloatEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Char2FloatMap.Entry> i = Char2FloatMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Char2FloatMap.Entry e = i.next();
            s.append(String.valueOf(e.getCharKey()));
            s.append("=>");
            s.append(String.valueOf(e.getFloatValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Char2FloatMap.Entry {
        protected char key;
        protected float value;

        public BasicEntry() {
        }

        public BasicEntry(Character key, Float value) {
            this.key = key.charValue();
            this.value = value.floatValue();
        }

        public BasicEntry(char key, float value) {
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
        public Float getValue() {
            return Float.valueOf(this.value);
        }

        @Override
        public float getFloatValue() {
            return this.value;
        }

        @Override
        public float setValue(float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float setValue(Float value) {
            return Float.valueOf(this.setValue(value.floatValue()));
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
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            return this.key == ((Character)e.getKey()).charValue() && Float.floatToIntBits(this.value) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.float2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2DoubleFunction;
import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.Char2DoubleMap;
import it.unimi.dsi.fastutil.chars.Char2DoubleMaps;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractChar2DoubleMap
extends AbstractChar2DoubleFunction
implements Char2DoubleMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractChar2DoubleMap() {
    }

    @Override
    public boolean containsValue(double v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(char k) {
        Iterator i = this.char2DoubleEntrySet().iterator();
        while (i.hasNext()) {
            if (((Char2DoubleMap.Entry)i.next()).getCharKey() != k) continue;
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
                return AbstractChar2DoubleMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractChar2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2DoubleMap.this.clear();
            }

            @Override
            public CharIterator iterator() {
                return new CharIterator(){
                    private final ObjectIterator<Char2DoubleMap.Entry> i;
                    {
                        this.i = Char2DoubleMaps.fastIterator(AbstractChar2DoubleMap.this);
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
    public DoubleCollection values() {
        return new AbstractDoubleCollection(){

            @Override
            public boolean contains(double k) {
                return AbstractChar2DoubleMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractChar2DoubleMap.this.size();
            }

            @Override
            public void clear() {
                AbstractChar2DoubleMap.this.clear();
            }

            @Override
            public DoubleIterator iterator() {
                return new DoubleIterator(){
                    private final ObjectIterator<Char2DoubleMap.Entry> i;
                    {
                        this.i = Char2DoubleMaps.fastIterator(AbstractChar2DoubleMap.this);
                    }

                    @Override
                    public double nextDouble() {
                        return this.i.next().getDoubleValue();
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
    public void putAll(Map<? extends Character, ? extends Double> m) {
        if (m instanceof Char2DoubleMap) {
            ObjectIterator<Char2DoubleMap.Entry> i = Char2DoubleMaps.fastIterator((Char2DoubleMap)m);
            while (i.hasNext()) {
                Char2DoubleMap.Entry e = i.next();
                this.put(e.getCharKey(), e.getDoubleValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Character, ? extends Double>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Character, ? extends Double> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Char2DoubleMap.Entry> i = Char2DoubleMaps.fastIterator(this);
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
        return this.char2DoubleEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Char2DoubleMap.Entry> i = Char2DoubleMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Char2DoubleMap.Entry e = i.next();
            s.append(String.valueOf(e.getCharKey()));
            s.append("=>");
            s.append(String.valueOf(e.getDoubleValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Char2DoubleMap.Entry {
        protected char key;
        protected double value;

        public BasicEntry() {
        }

        public BasicEntry(Character key, Double value) {
            this.key = key.charValue();
            this.value = value;
        }

        public BasicEntry(char key, double value) {
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
        public Double getValue() {
            return this.value;
        }

        @Override
        public double getDoubleValue() {
            return this.value;
        }

        @Override
        public double setValue(double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double setValue(Double value) {
            return this.setValue((double)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            return this.key == ((Character)e.getKey()).charValue() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.double2int(this.value);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


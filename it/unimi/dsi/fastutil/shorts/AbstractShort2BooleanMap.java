/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMaps;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractShort2BooleanMap
extends AbstractShort2BooleanFunction
implements Short2BooleanMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractShort2BooleanMap() {
    }

    @Override
    public boolean containsValue(boolean v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(short k) {
        Iterator i = this.short2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Short2BooleanMap.Entry)i.next()).getShortKey() != k) continue;
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
                return AbstractShort2BooleanMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractShort2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2BooleanMap.this.clear();
            }

            @Override
            public ShortIterator iterator() {
                return new ShortIterator(){
                    private final ObjectIterator<Short2BooleanMap.Entry> i;
                    {
                        this.i = Short2BooleanMaps.fastIterator(AbstractShort2BooleanMap.this);
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
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean k) {
                return AbstractShort2BooleanMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractShort2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractShort2BooleanMap.this.clear();
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    private final ObjectIterator<Short2BooleanMap.Entry> i;
                    {
                        this.i = Short2BooleanMaps.fastIterator(AbstractShort2BooleanMap.this);
                    }

                    @Override
                    public boolean nextBoolean() {
                        return this.i.next().getBooleanValue();
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
    public void putAll(Map<? extends Short, ? extends Boolean> m) {
        if (m instanceof Short2BooleanMap) {
            ObjectIterator<Short2BooleanMap.Entry> i = Short2BooleanMaps.fastIterator((Short2BooleanMap)m);
            while (i.hasNext()) {
                Short2BooleanMap.Entry e = i.next();
                this.put(e.getShortKey(), e.getBooleanValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Short, ? extends Boolean>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Short, ? extends Boolean> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Short2BooleanMap.Entry> i = Short2BooleanMaps.fastIterator(this);
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
        return this.short2BooleanEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Short2BooleanMap.Entry> i = Short2BooleanMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Short2BooleanMap.Entry e = i.next();
            s.append(String.valueOf(e.getShortKey()));
            s.append("=>");
            s.append(String.valueOf(e.getBooleanValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Short2BooleanMap.Entry {
        protected short key;
        protected boolean value;

        public BasicEntry() {
        }

        public BasicEntry(Short key, Boolean value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(short key, boolean value) {
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
        public Boolean getValue() {
            return this.value;
        }

        @Override
        public boolean getBooleanValue() {
            return this.value;
        }

        @Override
        public boolean setValue(boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean setValue(Boolean value) {
            return this.setValue((boolean)value);
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
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            return this.key == (Short)e.getKey() && this.value == (Boolean)e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value ? 1231 : 1237);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


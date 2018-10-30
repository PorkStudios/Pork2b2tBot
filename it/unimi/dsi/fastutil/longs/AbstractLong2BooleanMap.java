/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2BooleanFunction;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanMaps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractLong2BooleanMap
extends AbstractLong2BooleanFunction
implements Long2BooleanMap,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractLong2BooleanMap() {
    }

    @Override
    public boolean containsValue(boolean v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(long k) {
        Iterator i = this.long2BooleanEntrySet().iterator();
        while (i.hasNext()) {
            if (((Long2BooleanMap.Entry)i.next()).getLongKey() != k) continue;
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
                return AbstractLong2BooleanMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractLong2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2BooleanMap.this.clear();
            }

            @Override
            public LongIterator iterator() {
                return new LongIterator(){
                    private final ObjectIterator<Long2BooleanMap.Entry> i;
                    {
                        this.i = Long2BooleanMaps.fastIterator(AbstractLong2BooleanMap.this);
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
    public BooleanCollection values() {
        return new AbstractBooleanCollection(){

            @Override
            public boolean contains(boolean k) {
                return AbstractLong2BooleanMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractLong2BooleanMap.this.size();
            }

            @Override
            public void clear() {
                AbstractLong2BooleanMap.this.clear();
            }

            @Override
            public BooleanIterator iterator() {
                return new BooleanIterator(){
                    private final ObjectIterator<Long2BooleanMap.Entry> i;
                    {
                        this.i = Long2BooleanMaps.fastIterator(AbstractLong2BooleanMap.this);
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
    public void putAll(Map<? extends Long, ? extends Boolean> m) {
        if (m instanceof Long2BooleanMap) {
            ObjectIterator<Long2BooleanMap.Entry> i = Long2BooleanMaps.fastIterator((Long2BooleanMap)m);
            while (i.hasNext()) {
                Long2BooleanMap.Entry e = i.next();
                this.put(e.getLongKey(), e.getBooleanValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<? extends Long, ? extends Boolean>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<? extends Long, ? extends Boolean> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator<Long2BooleanMap.Entry> i = Long2BooleanMaps.fastIterator(this);
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
        return this.long2BooleanEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator<Long2BooleanMap.Entry> i = Long2BooleanMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Long2BooleanMap.Entry e = i.next();
            s.append(String.valueOf(e.getLongKey()));
            s.append("=>");
            s.append(String.valueOf(e.getBooleanValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry
    implements Long2BooleanMap.Entry {
        protected long key;
        protected boolean value;

        public BasicEntry() {
        }

        public BasicEntry(Long key, Boolean value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(long key, boolean value) {
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
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            return this.key == (Long)e.getKey() && this.value == (Boolean)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ (this.value ? 1231 : 1237);
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


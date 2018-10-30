/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMaps;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractByte2ReferenceMap<V>
extends AbstractByte2ReferenceFunction<V>
implements Byte2ReferenceMap<V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractByte2ReferenceMap() {
    }

    @Override
    public boolean containsValue(Object v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(byte k) {
        Iterator i = this.byte2ReferenceEntrySet().iterator();
        while (i.hasNext()) {
            if (((Byte2ReferenceMap.Entry)i.next()).getByteKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ByteSet keySet() {
        return new AbstractByteSet(){

            @Override
            public boolean contains(byte k) {
                return AbstractByte2ReferenceMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractByte2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2ReferenceMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Byte2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Byte2ReferenceMaps.fastIterator(AbstractByte2ReferenceMap.this);
                    }

                    @Override
                    public byte nextByte() {
                        return this.i.next().getByteKey();
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
    public ReferenceCollection<V> values() {
        return new AbstractReferenceCollection<V>(){

            @Override
            public boolean contains(Object k) {
                return AbstractByte2ReferenceMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractByte2ReferenceMap.this.size();
            }

            @Override
            public void clear() {
                AbstractByte2ReferenceMap.this.clear();
            }

            @Override
            public ObjectIterator<V> iterator() {
                return new ObjectIterator<V>(){
                    private final ObjectIterator<Byte2ReferenceMap.Entry<V>> i;
                    {
                        this.i = Byte2ReferenceMaps.fastIterator(AbstractByte2ReferenceMap.this);
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
    public void putAll(Map<? extends Byte, ? extends V> m) {
        if (m instanceof Byte2ReferenceMap) {
            ObjectIterator i = Byte2ReferenceMaps.fastIterator((Byte2ReferenceMap)m);
            while (i.hasNext()) {
                Byte2ReferenceMap.Entry e = i.next();
                this.put(e.getByteKey(), e.getValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<Byte, V>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<Byte, V> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Byte2ReferenceMaps.fastIterator(this);
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
        return this.byte2ReferenceEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Byte2ReferenceMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Byte2ReferenceMap.Entry e = i.next();
            s.append(String.valueOf(e.getByteKey()));
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
    implements Byte2ReferenceMap.Entry<V> {
        protected byte key;
        protected V value;

        public BasicEntry() {
        }

        public BasicEntry(Byte key, V value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(byte key, V value) {
            this.key = key;
            this.value = value;
        }

        @Deprecated
        @Override
        public Byte getKey() {
            return this.key;
        }

        @Override
        public byte getByteKey() {
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
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            return this.key == (Byte)e.getKey() && this.value == e.getValue();
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
        }

        public String toString() {
            return "" + this.key + "->" + this.value;
        }
    }

}


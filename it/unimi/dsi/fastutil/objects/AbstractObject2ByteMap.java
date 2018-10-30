/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2ByteFunction;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractObject2ByteMap<K>
extends AbstractObject2ByteFunction<K>
implements Object2ByteMap<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;

    protected AbstractObject2ByteMap() {
    }

    @Override
    public boolean containsValue(byte v) {
        return this.values().contains(v);
    }

    @Override
    public boolean containsKey(Object k) {
        Iterator i = this.object2ByteEntrySet().iterator();
        while (i.hasNext()) {
            if (((Object2ByteMap.Entry)i.next()).getKey() != k) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public ObjectSet<K> keySet() {
        return new AbstractObjectSet<K>(){

            @Override
            public boolean contains(Object k) {
                return AbstractObject2ByteMap.this.containsKey(k);
            }

            @Override
            public int size() {
                return AbstractObject2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2ByteMap.this.clear();
            }

            @Override
            public ObjectIterator<K> iterator() {
                return new ObjectIterator<K>(){
                    private final ObjectIterator<Object2ByteMap.Entry<K>> i;
                    {
                        this.i = Object2ByteMaps.fastIterator(AbstractObject2ByteMap.this);
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
    public ByteCollection values() {
        return new AbstractByteCollection(){

            @Override
            public boolean contains(byte k) {
                return AbstractObject2ByteMap.this.containsValue(k);
            }

            @Override
            public int size() {
                return AbstractObject2ByteMap.this.size();
            }

            @Override
            public void clear() {
                AbstractObject2ByteMap.this.clear();
            }

            @Override
            public ByteIterator iterator() {
                return new ByteIterator(){
                    private final ObjectIterator<Object2ByteMap.Entry<K>> i;
                    {
                        this.i = Object2ByteMaps.fastIterator(AbstractObject2ByteMap.this);
                    }

                    @Override
                    public byte nextByte() {
                        return this.i.next().getByteValue();
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
    public void putAll(Map<? extends K, ? extends Byte> m) {
        if (m instanceof Object2ByteMap) {
            ObjectIterator i = Object2ByteMaps.fastIterator((Object2ByteMap)m);
            while (i.hasNext()) {
                Object2ByteMap.Entry e = i.next();
                this.put(e.getKey(), e.getByteValue());
            }
        } else {
            int n = m.size();
            Iterator<Map.Entry<K, Byte>> i = m.entrySet().iterator();
            while (n-- != 0) {
                Map.Entry<K, Byte> e = i.next();
                this.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        ObjectIterator i = Object2ByteMaps.fastIterator(this);
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
        return this.object2ByteEntrySet().containsAll(m.entrySet());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        ObjectIterator i = Object2ByteMaps.fastIterator(this);
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            Object2ByteMap.Entry e = i.next();
            if (this == e.getKey()) {
                s.append("(this map)");
            } else {
                s.append(String.valueOf(e.getKey()));
            }
            s.append("=>");
            s.append(String.valueOf(e.getByteValue()));
        }
        s.append("}");
        return s.toString();
    }

    public static class BasicEntry<K>
    implements Object2ByteMap.Entry<K> {
        protected K key;
        protected byte value;

        public BasicEntry() {
        }

        public BasicEntry(K key, Byte value) {
            this.key = key;
            this.value = value;
        }

        public BasicEntry(K key, byte value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Byte getValue() {
            return this.value;
        }

        @Override
        public byte getByteValue() {
            return this.value;
        }

        @Override
        public byte setValue(byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte setValue(Byte value) {
            return this.setValue((byte)value);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Byte)) {
                return false;
            }
            return Objects.equals(this.key, e.getKey()) && this.value == (Byte)e.getValue();
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
        }

        public String toString() {
            return this.key + "->" + this.value;
        }
    }

}


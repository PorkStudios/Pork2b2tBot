/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TObjectByteMapDecorator<K>
extends AbstractMap<K, Byte>
implements Map<K, Byte>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TObjectByteMap<K> _map;

    public TObjectByteMapDecorator() {
    }

    public TObjectByteMapDecorator(TObjectByteMap<K> map) {
        this._map = map;
    }

    public TObjectByteMap<K> getMap() {
        return this._map;
    }

    @Override
    public Byte put(K key, Byte value) {
        if (value == null) {
            return this.wrapValue(this._map.put(key, this._map.getNoEntryValue()));
        }
        return this.wrapValue(this._map.put(key, this.unwrapValue(value)));
    }

    @Override
    public Byte get(Object key) {
        byte v = this._map.get(key);
        if (v == this._map.getNoEntryValue()) {
            return null;
        }
        return this.wrapValue(v);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    @Override
    public Byte remove(Object key) {
        byte v = this._map.remove(key);
        if (v == this._map.getNoEntryValue()) {
            return null;
        }
        return this.wrapValue(v);
    }

    @Override
    public Set<Map.Entry<K, Byte>> entrySet() {
        return new AbstractSet<Map.Entry<K, Byte>>(){

            @Override
            public int size() {
                return TObjectByteMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TObjectByteMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TObjectByteMapDecorator.this.containsKey(k) && TObjectByteMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<K, Byte>> iterator() {
                return new Iterator<Map.Entry<K, Byte>>(){
                    private final TObjectByteIterator<K> it;
                    {
                        this.it = TObjectByteMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<K, Byte> next() {
                        this.it.advance();
                        final K key = this.it.key();
                        final Byte v = TObjectByteMapDecorator.this.wrapValue(this.it.value());
                        return new Map.Entry<K, Byte>(){
                            private Byte val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public K getKey() {
                                return (K)key;
                            }

                            @Override
                            public Byte getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Byte setValue(Byte value) {
                                this.val = value;
                                return TObjectByteMapDecorator.this.put(key, value);
                            }
                        };
                    }

                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    @Override
                    public void remove() {
                        this.it.remove();
                    }

                };
            }

            @Override
            public boolean add(Map.Entry<K, Byte> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Object key = ((Map.Entry)o).getKey();
                    TObjectByteMapDecorator.this._map.remove(key);
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<K, Byte>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TObjectByteMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Byte && this._map.containsValue(this.unwrapValue(val));
    }

    @Override
    public boolean containsKey(Object key) {
        return this._map.containsKey(key);
    }

    @Override
    public int size() {
        return this._map.size();
    }

    @Override
    public boolean isEmpty() {
        return this._map.size() == 0;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Byte> map) {
        Iterator<Map.Entry<K, Byte>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<K, Byte> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Byte wrapValue(byte k) {
        return k;
    }

    protected byte unwrapValue(Object value) {
        return (Byte)value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TObjectByteMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


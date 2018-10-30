/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
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
public class TObjectFloatMapDecorator<K>
extends AbstractMap<K, Float>
implements Map<K, Float>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TObjectFloatMap<K> _map;

    public TObjectFloatMapDecorator() {
    }

    public TObjectFloatMapDecorator(TObjectFloatMap<K> map) {
        this._map = map;
    }

    public TObjectFloatMap<K> getMap() {
        return this._map;
    }

    @Override
    public Float put(K key, Float value) {
        if (value == null) {
            return this.wrapValue(this._map.put(key, this._map.getNoEntryValue()));
        }
        return this.wrapValue(this._map.put(key, this.unwrapValue(value)));
    }

    @Override
    public Float get(Object key) {
        float v = this._map.get(key);
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
    public Float remove(Object key) {
        float v = this._map.remove(key);
        if (v == this._map.getNoEntryValue()) {
            return null;
        }
        return this.wrapValue(v);
    }

    @Override
    public Set<Map.Entry<K, Float>> entrySet() {
        return new AbstractSet<Map.Entry<K, Float>>(){

            @Override
            public int size() {
                return TObjectFloatMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TObjectFloatMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TObjectFloatMapDecorator.this.containsKey(k) && TObjectFloatMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<K, Float>> iterator() {
                return new Iterator<Map.Entry<K, Float>>(){
                    private final TObjectFloatIterator<K> it;
                    {
                        this.it = TObjectFloatMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<K, Float> next() {
                        this.it.advance();
                        final K key = this.it.key();
                        final Float v = TObjectFloatMapDecorator.this.wrapValue(this.it.value());
                        return new Map.Entry<K, Float>(){
                            private Float val;
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
                            public Float getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Float setValue(Float value) {
                                this.val = value;
                                return TObjectFloatMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<K, Float> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Object key = ((Map.Entry)o).getKey();
                    TObjectFloatMapDecorator.this._map.remove(key);
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<K, Float>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TObjectFloatMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Float && this._map.containsValue(this.unwrapValue(val));
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
    public void putAll(Map<? extends K, ? extends Float> map) {
        Iterator<Map.Entry<K, Float>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<K, Float> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Float wrapValue(float k) {
        return Float.valueOf(k);
    }

    protected float unwrapValue(Object value) {
        return ((Float)value).floatValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TObjectFloatMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


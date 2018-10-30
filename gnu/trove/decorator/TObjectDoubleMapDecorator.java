/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
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
public class TObjectDoubleMapDecorator<K>
extends AbstractMap<K, Double>
implements Map<K, Double>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TObjectDoubleMap<K> _map;

    public TObjectDoubleMapDecorator() {
    }

    public TObjectDoubleMapDecorator(TObjectDoubleMap<K> map) {
        this._map = map;
    }

    public TObjectDoubleMap<K> getMap() {
        return this._map;
    }

    @Override
    public Double put(K key, Double value) {
        if (value == null) {
            return this.wrapValue(this._map.put(key, this._map.getNoEntryValue()));
        }
        return this.wrapValue(this._map.put(key, this.unwrapValue(value)));
    }

    @Override
    public Double get(Object key) {
        double v = this._map.get(key);
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
    public Double remove(Object key) {
        double v = this._map.remove(key);
        if (v == this._map.getNoEntryValue()) {
            return null;
        }
        return this.wrapValue(v);
    }

    @Override
    public Set<Map.Entry<K, Double>> entrySet() {
        return new AbstractSet<Map.Entry<K, Double>>(){

            @Override
            public int size() {
                return TObjectDoubleMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TObjectDoubleMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TObjectDoubleMapDecorator.this.containsKey(k) && TObjectDoubleMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<K, Double>> iterator() {
                return new Iterator<Map.Entry<K, Double>>(){
                    private final TObjectDoubleIterator<K> it;
                    {
                        this.it = TObjectDoubleMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<K, Double> next() {
                        this.it.advance();
                        final K key = this.it.key();
                        final Double v = TObjectDoubleMapDecorator.this.wrapValue(this.it.value());
                        return new Map.Entry<K, Double>(){
                            private Double val;
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
                            public Double getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Double setValue(Double value) {
                                this.val = value;
                                return TObjectDoubleMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<K, Double> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Object key = ((Map.Entry)o).getKey();
                    TObjectDoubleMapDecorator.this._map.remove(key);
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<K, Double>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TObjectDoubleMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Double && this._map.containsValue(this.unwrapValue(val));
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
    public void putAll(Map<? extends K, ? extends Double> map) {
        Iterator<Map.Entry<K, Double>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<K, Double> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Double wrapValue(double k) {
        return k;
    }

    protected double unwrapValue(Object value) {
        return (Double)value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TObjectDoubleMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


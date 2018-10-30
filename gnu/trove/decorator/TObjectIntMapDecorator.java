/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
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
public class TObjectIntMapDecorator<K>
extends AbstractMap<K, Integer>
implements Map<K, Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TObjectIntMap<K> _map;

    public TObjectIntMapDecorator() {
    }

    public TObjectIntMapDecorator(TObjectIntMap<K> map) {
        this._map = map;
    }

    public TObjectIntMap<K> getMap() {
        return this._map;
    }

    @Override
    public Integer put(K key, Integer value) {
        if (value == null) {
            return this.wrapValue(this._map.put(key, this._map.getNoEntryValue()));
        }
        return this.wrapValue(this._map.put(key, this.unwrapValue(value)));
    }

    @Override
    public Integer get(Object key) {
        int v = this._map.get(key);
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
    public Integer remove(Object key) {
        int v = this._map.remove(key);
        if (v == this._map.getNoEntryValue()) {
            return null;
        }
        return this.wrapValue(v);
    }

    @Override
    public Set<Map.Entry<K, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<K, Integer>>(){

            @Override
            public int size() {
                return TObjectIntMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TObjectIntMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TObjectIntMapDecorator.this.containsKey(k) && TObjectIntMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<K, Integer>> iterator() {
                return new Iterator<Map.Entry<K, Integer>>(){
                    private final TObjectIntIterator<K> it;
                    {
                        this.it = TObjectIntMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<K, Integer> next() {
                        this.it.advance();
                        final K key = this.it.key();
                        final Integer v = TObjectIntMapDecorator.this.wrapValue(this.it.value());
                        return new Map.Entry<K, Integer>(){
                            private Integer val;
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
                            public Integer getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Integer setValue(Integer value) {
                                this.val = value;
                                return TObjectIntMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<K, Integer> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Object key = ((Map.Entry)o).getKey();
                    TObjectIntMapDecorator.this._map.remove(key);
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<K, Integer>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TObjectIntMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Integer && this._map.containsValue(this.unwrapValue(val));
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
    public void putAll(Map<? extends K, ? extends Integer> map) {
        Iterator<Map.Entry<K, Integer>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<K, Integer> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Integer wrapValue(int k) {
        return k;
    }

    protected int unwrapValue(Object value) {
        return (Integer)value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TObjectIntMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


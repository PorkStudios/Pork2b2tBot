/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
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
public class TObjectCharMapDecorator<K>
extends AbstractMap<K, Character>
implements Map<K, Character>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TObjectCharMap<K> _map;

    public TObjectCharMapDecorator() {
    }

    public TObjectCharMapDecorator(TObjectCharMap<K> map) {
        this._map = map;
    }

    public TObjectCharMap<K> getMap() {
        return this._map;
    }

    @Override
    public Character put(K key, Character value) {
        if (value == null) {
            return this.wrapValue(this._map.put(key, this._map.getNoEntryValue()));
        }
        return this.wrapValue(this._map.put(key, this.unwrapValue(value)));
    }

    @Override
    public Character get(Object key) {
        char v = this._map.get(key);
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
    public Character remove(Object key) {
        char v = this._map.remove(key);
        if (v == this._map.getNoEntryValue()) {
            return null;
        }
        return this.wrapValue(v);
    }

    @Override
    public Set<Map.Entry<K, Character>> entrySet() {
        return new AbstractSet<Map.Entry<K, Character>>(){

            @Override
            public int size() {
                return TObjectCharMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TObjectCharMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TObjectCharMapDecorator.this.containsKey(k) && TObjectCharMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<K, Character>> iterator() {
                return new Iterator<Map.Entry<K, Character>>(){
                    private final TObjectCharIterator<K> it;
                    {
                        this.it = TObjectCharMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<K, Character> next() {
                        this.it.advance();
                        final K key = this.it.key();
                        final Character v = TObjectCharMapDecorator.this.wrapValue(this.it.value());
                        return new Map.Entry<K, Character>(){
                            private Character val;
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
                            public Character getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Character setValue(Character value) {
                                this.val = value;
                                return TObjectCharMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<K, Character> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Object key = ((Map.Entry)o).getKey();
                    TObjectCharMapDecorator.this._map.remove(key);
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<K, Character>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TObjectCharMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Character && this._map.containsValue(this.unwrapValue(val));
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
    public void putAll(Map<? extends K, ? extends Character> map) {
        Iterator<Map.Entry<K, Character>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<K, Character> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Character wrapValue(char k) {
        return Character.valueOf(k);
    }

    protected char unwrapValue(Object value) {
        return ((Character)value).charValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TObjectCharMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


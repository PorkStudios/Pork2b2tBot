/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
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
public class TCharObjectMapDecorator<V>
extends AbstractMap<Character, V>
implements Map<Character, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharObjectMap<V> _map;

    public TCharObjectMapDecorator() {
    }

    public TCharObjectMapDecorator(TCharObjectMap<V> map) {
        this._map = map;
    }

    public TCharObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Character key, V value) {
        char k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        return this._map.put(k, value);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public V get(Object key) {
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey((Character)key);
            return this._map.get(k);
        } else {
            k = this._map.getNoEntryKey();
        }
        return this._map.get(k);
    }

    @Override
    public void clear() {
        this._map.clear();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public V remove(Object key) {
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey((Character)key);
            return this._map.remove(k);
        } else {
            k = this._map.getNoEntryKey();
        }
        return this._map.remove(k);
    }

    @Override
    public Set<Map.Entry<Character, V>> entrySet() {
        return new AbstractSet<Map.Entry<Character, V>>(){

            @Override
            public int size() {
                return TCharObjectMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TCharObjectMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TCharObjectMapDecorator.this.containsKey(k) && TCharObjectMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Character, V>> iterator() {
                return new Iterator<Map.Entry<Character, V>>(){
                    private final TCharObjectIterator<V> it;
                    {
                        this.it = TCharObjectMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Character, V> next() {
                        this.it.advance();
                        char k = this.it.key();
                        final Character key = k == TCharObjectMapDecorator.this._map.getNoEntryKey() ? null : TCharObjectMapDecorator.this.wrapKey(k);
                        final V v = this.it.value();
                        return new Map.Entry<Character, V>(){
                            private V val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Character getKey() {
                                return key;
                            }

                            @Override
                            public V getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public V setValue(V value) {
                                this.val = value;
                                return TCharObjectMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Character, V> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Character key = (Character)((Map.Entry)o).getKey();
                    TCharObjectMapDecorator.this._map.remove(TCharObjectMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Character, V>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TCharObjectMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return this._map.containsValue(val);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return this._map.containsKey(this._map.getNoEntryKey());
        }
        return key instanceof Character && this._map.containsKey(((Character)key).charValue());
    }

    @Override
    public int size() {
        return this._map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> map) {
        Iterator<Map.Entry<Character, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Character, V> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Character wrapKey(char k) {
        return Character.valueOf(k);
    }

    protected char unwrapKey(Character key) {
        return key.charValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TCharObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


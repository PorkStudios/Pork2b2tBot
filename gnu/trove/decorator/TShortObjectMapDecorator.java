/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
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
public class TShortObjectMapDecorator<V>
extends AbstractMap<Short, V>
implements Map<Short, V>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TShortObjectMap<V> _map;

    public TShortObjectMapDecorator() {
    }

    public TShortObjectMapDecorator(TShortObjectMap<V> map) {
        this._map = map;
    }

    public TShortObjectMap<V> getMap() {
        return this._map;
    }

    @Override
    public V put(Short key, V value) {
        short k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        return this._map.put(k, value);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public V get(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey((Short)key);
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
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey((Short)key);
            return this._map.remove(k);
        } else {
            k = this._map.getNoEntryKey();
        }
        return this._map.remove(k);
    }

    @Override
    public Set<Map.Entry<Short, V>> entrySet() {
        return new AbstractSet<Map.Entry<Short, V>>(){

            @Override
            public int size() {
                return TShortObjectMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TShortObjectMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TShortObjectMapDecorator.this.containsKey(k) && TShortObjectMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Short, V>> iterator() {
                return new Iterator<Map.Entry<Short, V>>(){
                    private final TShortObjectIterator<V> it;
                    {
                        this.it = TShortObjectMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Short, V> next() {
                        this.it.advance();
                        short k = this.it.key();
                        final Short key = k == TShortObjectMapDecorator.this._map.getNoEntryKey() ? null : TShortObjectMapDecorator.this.wrapKey(k);
                        final V v = this.it.value();
                        return new Map.Entry<Short, V>(){
                            private V val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Short getKey() {
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
                                return TShortObjectMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Short, V> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Short key = (Short)((Map.Entry)o).getKey();
                    TShortObjectMapDecorator.this._map.remove(TShortObjectMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Short, V>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TShortObjectMapDecorator.this.clear();
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
        return key instanceof Short && this._map.containsKey((Short)key);
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
    public void putAll(Map<? extends Short, ? extends V> map) {
        Iterator<Map.Entry<Short, V>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<Short, V> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Short wrapKey(short k) {
        return k;
    }

    protected short unwrapKey(Short key) {
        return key;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TShortObjectMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


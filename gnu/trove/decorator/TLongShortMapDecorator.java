/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.map.TLongShortMap;
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
public class TLongShortMapDecorator
extends AbstractMap<Long, Short>
implements Map<Long, Short>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TLongShortMap _map;

    public TLongShortMapDecorator() {
    }

    public TLongShortMapDecorator(TLongShortMap map) {
        this._map = map;
    }

    public TLongShortMap getMap() {
        return this._map;
    }

    @Override
    public Short put(Long key, Short value) {
        long k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        short v = value == null ? this._map.getNoEntryValue() : this.unwrapValue(value);
        short retval = this._map.put(k, v);
        if (retval == this._map.getNoEntryValue()) {
            return null;
        }
        return this.wrapValue(retval);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Short get(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        short v = this._map.get(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
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
    public Short remove(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        short v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Long, Short>> entrySet() {
        return new AbstractSet<Map.Entry<Long, Short>>(){

            @Override
            public int size() {
                return TLongShortMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TLongShortMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TLongShortMapDecorator.this.containsKey(k) && TLongShortMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Long, Short>> iterator() {
                return new Iterator<Map.Entry<Long, Short>>(){
                    private final TLongShortIterator it;
                    {
                        this.it = TLongShortMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Long, Short> next() {
                        this.it.advance();
                        long ik = this.it.key();
                        final Long key = ik == TLongShortMapDecorator.this._map.getNoEntryKey() ? null : TLongShortMapDecorator.this.wrapKey(ik);
                        short iv = this.it.value();
                        final Short v = iv == TLongShortMapDecorator.this._map.getNoEntryValue() ? null : TLongShortMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Long, Short>(){
                            private Short val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Long getKey() {
                                return key;
                            }

                            @Override
                            public Short getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Short setValue(Short value) {
                                this.val = value;
                                return TLongShortMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Long, Short> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Long key = (Long)((Map.Entry)o).getKey();
                    TLongShortMapDecorator.this._map.remove(TLongShortMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Long, Short>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TLongShortMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Short && this._map.containsValue(this.unwrapValue(val));
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return this._map.containsKey(this._map.getNoEntryKey());
        }
        return key instanceof Long && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Long, ? extends Short> map) {
        Iterator<Map.Entry<? extends Long, ? extends Short>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Long, ? extends Short> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Long wrapKey(long k) {
        return k;
    }

    protected long unwrapKey(Object key) {
        return (Long)key;
    }

    protected Short wrapValue(short k) {
        return k;
    }

    protected short unwrapValue(Object value) {
        return (Short)value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TLongShortMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


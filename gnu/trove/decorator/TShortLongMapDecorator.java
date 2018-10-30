/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
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
public class TShortLongMapDecorator
extends AbstractMap<Short, Long>
implements Map<Short, Long>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TShortLongMap _map;

    public TShortLongMapDecorator() {
    }

    public TShortLongMapDecorator(TShortLongMap map) {
        this._map = map;
    }

    public TShortLongMap getMap() {
        return this._map;
    }

    @Override
    public Long put(Short key, Long value) {
        short k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        long v = value == null ? this._map.getNoEntryValue() : this.unwrapValue(value);
        long retval = this._map.put(k, v);
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
    public Long get(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        long v = this._map.get(k);
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
    public Long remove(Object key) {
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        long v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Short, Long>> entrySet() {
        return new AbstractSet<Map.Entry<Short, Long>>(){

            @Override
            public int size() {
                return TShortLongMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TShortLongMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TShortLongMapDecorator.this.containsKey(k) && TShortLongMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Short, Long>> iterator() {
                return new Iterator<Map.Entry<Short, Long>>(){
                    private final TShortLongIterator it;
                    {
                        this.it = TShortLongMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Short, Long> next() {
                        this.it.advance();
                        short ik = this.it.key();
                        final Short key = ik == TShortLongMapDecorator.this._map.getNoEntryKey() ? null : TShortLongMapDecorator.this.wrapKey(ik);
                        long iv = this.it.value();
                        final Long v = iv == TShortLongMapDecorator.this._map.getNoEntryValue() ? null : TShortLongMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Short, Long>(){
                            private Long val;
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
                            public Long getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Long setValue(Long value) {
                                this.val = value;
                                return TShortLongMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Short, Long> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Short key = (Short)((Map.Entry)o).getKey();
                    TShortLongMapDecorator.this._map.remove(TShortLongMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Short, Long>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TShortLongMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Long && this._map.containsValue(this.unwrapValue(val));
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return this._map.containsKey(this._map.getNoEntryKey());
        }
        return key instanceof Short && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Short, ? extends Long> map) {
        Iterator<Map.Entry<? extends Short, ? extends Long>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Short, ? extends Long> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Short wrapKey(short k) {
        return k;
    }

    protected short unwrapKey(Object key) {
        return (Short)key;
    }

    protected Long wrapValue(long k) {
        return k;
    }

    protected long unwrapValue(Object value) {
        return (Long)value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TShortLongMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


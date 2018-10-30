/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
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
public class TByteLongMapDecorator
extends AbstractMap<Byte, Long>
implements Map<Byte, Long>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TByteLongMap _map;

    public TByteLongMapDecorator() {
    }

    public TByteLongMapDecorator(TByteLongMap map) {
        this._map = map;
    }

    public TByteLongMap getMap() {
        return this._map;
    }

    @Override
    public Long put(Byte key, Long value) {
        byte k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        byte k;
        if (key != null) {
            if (!(key instanceof Byte)) return null;
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
        byte k;
        if (key != null) {
            if (!(key instanceof Byte)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        long v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Byte, Long>> entrySet() {
        return new AbstractSet<Map.Entry<Byte, Long>>(){

            @Override
            public int size() {
                return TByteLongMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TByteLongMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TByteLongMapDecorator.this.containsKey(k) && TByteLongMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Byte, Long>> iterator() {
                return new Iterator<Map.Entry<Byte, Long>>(){
                    private final TByteLongIterator it;
                    {
                        this.it = TByteLongMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Byte, Long> next() {
                        this.it.advance();
                        byte ik = this.it.key();
                        final Byte key = ik == TByteLongMapDecorator.this._map.getNoEntryKey() ? null : TByteLongMapDecorator.this.wrapKey(ik);
                        long iv = this.it.value();
                        final Long v = iv == TByteLongMapDecorator.this._map.getNoEntryValue() ? null : TByteLongMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Byte, Long>(){
                            private Long val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Byte getKey() {
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
                                return TByteLongMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Byte, Long> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Byte key = (Byte)((Map.Entry)o).getKey();
                    TByteLongMapDecorator.this._map.remove(TByteLongMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Byte, Long>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TByteLongMapDecorator.this.clear();
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
        return key instanceof Byte && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Byte, ? extends Long> map) {
        Iterator<Map.Entry<? extends Byte, ? extends Long>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Byte, ? extends Long> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Byte wrapKey(byte k) {
        return k;
    }

    protected byte unwrapKey(Object key) {
        return (Byte)key;
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
        this._map = (TByteLongMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


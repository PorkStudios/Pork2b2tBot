/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.map.TFloatLongMap;
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
public class TFloatLongMapDecorator
extends AbstractMap<Float, Long>
implements Map<Float, Long>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatLongMap _map;

    public TFloatLongMapDecorator() {
    }

    public TFloatLongMapDecorator(TFloatLongMap map) {
        this._map = map;
    }

    public TFloatLongMap getMap() {
        return this._map;
    }

    @Override
    public Long put(Float key, Long value) {
        float k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
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
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        long v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Float, Long>> entrySet() {
        return new AbstractSet<Map.Entry<Float, Long>>(){

            @Override
            public int size() {
                return TFloatLongMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TFloatLongMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TFloatLongMapDecorator.this.containsKey(k) && TFloatLongMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Float, Long>> iterator() {
                return new Iterator<Map.Entry<Float, Long>>(){
                    private final TFloatLongIterator it;
                    {
                        this.it = TFloatLongMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Float, Long> next() {
                        this.it.advance();
                        float ik = this.it.key();
                        final Float key = ik == TFloatLongMapDecorator.this._map.getNoEntryKey() ? null : TFloatLongMapDecorator.this.wrapKey(ik);
                        long iv = this.it.value();
                        final Long v = iv == TFloatLongMapDecorator.this._map.getNoEntryValue() ? null : TFloatLongMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Float, Long>(){
                            private Long val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Float getKey() {
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
                                return TFloatLongMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Float, Long> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Float key = (Float)((Map.Entry)o).getKey();
                    TFloatLongMapDecorator.this._map.remove(TFloatLongMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Float, Long>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TFloatLongMapDecorator.this.clear();
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
        return key instanceof Float && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Float, ? extends Long> map) {
        Iterator<Map.Entry<? extends Float, ? extends Long>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Float, ? extends Long> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf(k);
    }

    protected float unwrapKey(Object key) {
        return ((Float)key).floatValue();
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
        this._map = (TFloatLongMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


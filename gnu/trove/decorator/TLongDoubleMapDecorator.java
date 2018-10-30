/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
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
public class TLongDoubleMapDecorator
extends AbstractMap<Long, Double>
implements Map<Long, Double>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TLongDoubleMap _map;

    public TLongDoubleMapDecorator() {
    }

    public TLongDoubleMapDecorator(TLongDoubleMap map) {
        this._map = map;
    }

    public TLongDoubleMap getMap() {
        return this._map;
    }

    @Override
    public Double put(Long key, Double value) {
        long k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        double v = value == null ? this._map.getNoEntryValue() : this.unwrapValue(value);
        double retval = this._map.put(k, v);
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
    public Double get(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        double v = this._map.get(k);
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
    public Double remove(Object key) {
        long k;
        if (key != null) {
            if (!(key instanceof Long)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        double v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Long, Double>> entrySet() {
        return new AbstractSet<Map.Entry<Long, Double>>(){

            @Override
            public int size() {
                return TLongDoubleMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TLongDoubleMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TLongDoubleMapDecorator.this.containsKey(k) && TLongDoubleMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Long, Double>> iterator() {
                return new Iterator<Map.Entry<Long, Double>>(){
                    private final TLongDoubleIterator it;
                    {
                        this.it = TLongDoubleMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Long, Double> next() {
                        this.it.advance();
                        long ik = this.it.key();
                        final Long key = ik == TLongDoubleMapDecorator.this._map.getNoEntryKey() ? null : TLongDoubleMapDecorator.this.wrapKey(ik);
                        double iv = this.it.value();
                        final Double v = iv == TLongDoubleMapDecorator.this._map.getNoEntryValue() ? null : TLongDoubleMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Long, Double>(){
                            private Double val;
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
                                return TLongDoubleMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Long, Double> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Long key = (Long)((Map.Entry)o).getKey();
                    TLongDoubleMapDecorator.this._map.remove(TLongDoubleMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Long, Double>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TLongDoubleMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Double && this._map.containsValue(this.unwrapValue(val));
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
    public void putAll(Map<? extends Long, ? extends Double> map) {
        Iterator<Map.Entry<? extends Long, ? extends Double>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Long, ? extends Double> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Long wrapKey(long k) {
        return k;
    }

    protected long unwrapKey(Object key) {
        return (Long)key;
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
        this._map = (TLongDoubleMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


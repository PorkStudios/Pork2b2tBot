/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.TIntDoubleMap;
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
public class TIntDoubleMapDecorator
extends AbstractMap<Integer, Double>
implements Map<Integer, Double>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TIntDoubleMap _map;

    public TIntDoubleMapDecorator() {
    }

    public TIntDoubleMapDecorator(TIntDoubleMap map) {
        this._map = map;
    }

    public TIntDoubleMap getMap() {
        return this._map;
    }

    @Override
    public Double put(Integer key, Double value) {
        int k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
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
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        double v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Integer, Double>> entrySet() {
        return new AbstractSet<Map.Entry<Integer, Double>>(){

            @Override
            public int size() {
                return TIntDoubleMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TIntDoubleMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TIntDoubleMapDecorator.this.containsKey(k) && TIntDoubleMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Integer, Double>> iterator() {
                return new Iterator<Map.Entry<Integer, Double>>(){
                    private final TIntDoubleIterator it;
                    {
                        this.it = TIntDoubleMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Integer, Double> next() {
                        this.it.advance();
                        int ik = this.it.key();
                        final Integer key = ik == TIntDoubleMapDecorator.this._map.getNoEntryKey() ? null : TIntDoubleMapDecorator.this.wrapKey(ik);
                        double iv = this.it.value();
                        final Double v = iv == TIntDoubleMapDecorator.this._map.getNoEntryValue() ? null : TIntDoubleMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Integer, Double>(){
                            private Double val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Integer getKey() {
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
                                return TIntDoubleMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Integer, Double> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Integer key = (Integer)((Map.Entry)o).getKey();
                    TIntDoubleMapDecorator.this._map.remove(TIntDoubleMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Integer, Double>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TIntDoubleMapDecorator.this.clear();
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
        return key instanceof Integer && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Integer, ? extends Double> map) {
        Iterator<Map.Entry<? extends Integer, ? extends Double>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Integer, ? extends Double> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Integer wrapKey(int k) {
        return k;
    }

    protected int unwrapKey(Object key) {
        return (Integer)key;
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
        this._map = (TIntDoubleMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


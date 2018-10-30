/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.map.TDoubleIntMap;
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
public class TDoubleIntMapDecorator
extends AbstractMap<Double, Integer>
implements Map<Double, Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TDoubleIntMap _map;

    public TDoubleIntMapDecorator() {
    }

    public TDoubleIntMapDecorator(TDoubleIntMap map) {
        this._map = map;
    }

    public TDoubleIntMap getMap() {
        return this._map;
    }

    @Override
    public Integer put(Double key, Integer value) {
        double k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        int v = value == null ? this._map.getNoEntryValue() : this.unwrapValue(value);
        int retval = this._map.put(k, v);
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
    public Integer get(Object key) {
        double k;
        if (key != null) {
            if (!(key instanceof Double)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.get(k);
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
    public Integer remove(Object key) {
        double k;
        if (key != null) {
            if (!(key instanceof Double)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Double, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<Double, Integer>>(){

            @Override
            public int size() {
                return TDoubleIntMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TDoubleIntMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TDoubleIntMapDecorator.this.containsKey(k) && TDoubleIntMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Double, Integer>> iterator() {
                return new Iterator<Map.Entry<Double, Integer>>(){
                    private final TDoubleIntIterator it;
                    {
                        this.it = TDoubleIntMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Double, Integer> next() {
                        this.it.advance();
                        double ik = this.it.key();
                        final Double key = ik == TDoubleIntMapDecorator.this._map.getNoEntryKey() ? null : TDoubleIntMapDecorator.this.wrapKey(ik);
                        int iv = this.it.value();
                        final Integer v = iv == TDoubleIntMapDecorator.this._map.getNoEntryValue() ? null : TDoubleIntMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Double, Integer>(){
                            private Integer val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Double getKey() {
                                return key;
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
                                return TDoubleIntMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Double, Integer> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Double key = (Double)((Map.Entry)o).getKey();
                    TDoubleIntMapDecorator.this._map.remove(TDoubleIntMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Double, Integer>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TDoubleIntMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Integer && this._map.containsValue(this.unwrapValue(val));
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return this._map.containsKey(this._map.getNoEntryKey());
        }
        return key instanceof Double && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Double, ? extends Integer> map) {
        Iterator<Map.Entry<? extends Double, ? extends Integer>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Double, ? extends Integer> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Double wrapKey(double k) {
        return k;
    }

    protected double unwrapKey(Object key) {
        return (Double)key;
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
        this._map = (TDoubleIntMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


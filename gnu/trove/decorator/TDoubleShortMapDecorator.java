/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
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
public class TDoubleShortMapDecorator
extends AbstractMap<Double, Short>
implements Map<Double, Short>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TDoubleShortMap _map;

    public TDoubleShortMapDecorator() {
    }

    public TDoubleShortMapDecorator(TDoubleShortMap map) {
        this._map = map;
    }

    public TDoubleShortMap getMap() {
        return this._map;
    }

    @Override
    public Short put(Double key, Short value) {
        double k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        double k;
        if (key != null) {
            if (!(key instanceof Double)) return null;
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
        double k;
        if (key != null) {
            if (!(key instanceof Double)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        short v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Double, Short>> entrySet() {
        return new AbstractSet<Map.Entry<Double, Short>>(){

            @Override
            public int size() {
                return TDoubleShortMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TDoubleShortMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TDoubleShortMapDecorator.this.containsKey(k) && TDoubleShortMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Double, Short>> iterator() {
                return new Iterator<Map.Entry<Double, Short>>(){
                    private final TDoubleShortIterator it;
                    {
                        this.it = TDoubleShortMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Double, Short> next() {
                        this.it.advance();
                        double ik = this.it.key();
                        final Double key = ik == TDoubleShortMapDecorator.this._map.getNoEntryKey() ? null : TDoubleShortMapDecorator.this.wrapKey(ik);
                        short iv = this.it.value();
                        final Short v = iv == TDoubleShortMapDecorator.this._map.getNoEntryValue() ? null : TDoubleShortMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Double, Short>(){
                            private Short val;
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
                                return TDoubleShortMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Double, Short> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Double key = (Double)((Map.Entry)o).getKey();
                    TDoubleShortMapDecorator.this._map.remove(TDoubleShortMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Double, Short>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TDoubleShortMapDecorator.this.clear();
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
    public void putAll(Map<? extends Double, ? extends Short> map) {
        Iterator<Map.Entry<? extends Double, ? extends Short>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Double, ? extends Short> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Double wrapKey(double k) {
        return k;
    }

    protected double unwrapKey(Object key) {
        return (Double)key;
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
        this._map = (TDoubleShortMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


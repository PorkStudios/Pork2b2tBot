/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
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
public class TCharDoubleMapDecorator
extends AbstractMap<Character, Double>
implements Map<Character, Double>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharDoubleMap _map;

    public TCharDoubleMapDecorator() {
    }

    public TCharDoubleMapDecorator(TCharDoubleMap map) {
        this._map = map;
    }

    public TCharDoubleMap getMap() {
        return this._map;
    }

    @Override
    public Double put(Character key, Double value) {
        char k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
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
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        double v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Character, Double>> entrySet() {
        return new AbstractSet<Map.Entry<Character, Double>>(){

            @Override
            public int size() {
                return TCharDoubleMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TCharDoubleMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TCharDoubleMapDecorator.this.containsKey(k) && TCharDoubleMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Character, Double>> iterator() {
                return new Iterator<Map.Entry<Character, Double>>(){
                    private final TCharDoubleIterator it;
                    {
                        this.it = TCharDoubleMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Character, Double> next() {
                        this.it.advance();
                        char ik = this.it.key();
                        final Character key = ik == TCharDoubleMapDecorator.this._map.getNoEntryKey() ? null : TCharDoubleMapDecorator.this.wrapKey(ik);
                        double iv = this.it.value();
                        final Double v = iv == TCharDoubleMapDecorator.this._map.getNoEntryValue() ? null : TCharDoubleMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Character, Double>(){
                            private Double val;
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
                                return TCharDoubleMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Character, Double> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Character key = (Character)((Map.Entry)o).getKey();
                    TCharDoubleMapDecorator.this._map.remove(TCharDoubleMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Character, Double>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TCharDoubleMapDecorator.this.clear();
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
        return key instanceof Character && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Character, ? extends Double> map) {
        Iterator<Map.Entry<? extends Character, ? extends Double>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Character, ? extends Double> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Character wrapKey(char k) {
        return Character.valueOf(k);
    }

    protected char unwrapKey(Object key) {
        return ((Character)key).charValue();
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
        this._map = (TCharDoubleMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


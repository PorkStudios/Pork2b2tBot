/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.map.TDoubleCharMap;
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
public class TDoubleCharMapDecorator
extends AbstractMap<Double, Character>
implements Map<Double, Character>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TDoubleCharMap _map;

    public TDoubleCharMapDecorator() {
    }

    public TDoubleCharMapDecorator(TDoubleCharMap map) {
        this._map = map;
    }

    public TDoubleCharMap getMap() {
        return this._map;
    }

    @Override
    public Character put(Double key, Character value) {
        double k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        char v = value == null ? this._map.getNoEntryValue() : this.unwrapValue(value);
        char retval = this._map.put(k, v);
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
    public Character get(Object key) {
        double k;
        if (key != null) {
            if (!(key instanceof Double)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.get(k);
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
    public Character remove(Object key) {
        double k;
        if (key != null) {
            if (!(key instanceof Double)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Double, Character>> entrySet() {
        return new AbstractSet<Map.Entry<Double, Character>>(){

            @Override
            public int size() {
                return TDoubleCharMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TDoubleCharMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TDoubleCharMapDecorator.this.containsKey(k) && TDoubleCharMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Double, Character>> iterator() {
                return new Iterator<Map.Entry<Double, Character>>(){
                    private final TDoubleCharIterator it;
                    {
                        this.it = TDoubleCharMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Double, Character> next() {
                        this.it.advance();
                        double ik = this.it.key();
                        final Double key = ik == TDoubleCharMapDecorator.this._map.getNoEntryKey() ? null : TDoubleCharMapDecorator.this.wrapKey(ik);
                        char iv = this.it.value();
                        final Character v = iv == TDoubleCharMapDecorator.this._map.getNoEntryValue() ? null : TDoubleCharMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Double, Character>(){
                            private Character val;
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
                            public Character getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Character setValue(Character value) {
                                this.val = value;
                                return TDoubleCharMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Double, Character> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Double key = (Double)((Map.Entry)o).getKey();
                    TDoubleCharMapDecorator.this._map.remove(TDoubleCharMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Double, Character>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TDoubleCharMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Character && this._map.containsValue(this.unwrapValue(val));
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
    public void putAll(Map<? extends Double, ? extends Character> map) {
        Iterator<Map.Entry<? extends Double, ? extends Character>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Double, ? extends Character> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Double wrapKey(double k) {
        return k;
    }

    protected double unwrapKey(Object key) {
        return (Double)key;
    }

    protected Character wrapValue(char k) {
        return Character.valueOf(k);
    }

    protected char unwrapValue(Object value) {
        return ((Character)value).charValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TDoubleCharMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


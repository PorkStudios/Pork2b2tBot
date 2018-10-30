/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TFloatIntIterator;
import gnu.trove.map.TFloatIntMap;
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
public class TFloatIntMapDecorator
extends AbstractMap<Float, Integer>
implements Map<Float, Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatIntMap _map;

    public TFloatIntMapDecorator() {
    }

    public TFloatIntMapDecorator(TFloatIntMap map) {
        this._map = map;
    }

    public TFloatIntMap getMap() {
        return this._map;
    }

    @Override
    public Integer put(Float key, Integer value) {
        float k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
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
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Float, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<Float, Integer>>(){

            @Override
            public int size() {
                return TFloatIntMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TFloatIntMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TFloatIntMapDecorator.this.containsKey(k) && TFloatIntMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Float, Integer>> iterator() {
                return new Iterator<Map.Entry<Float, Integer>>(){
                    private final TFloatIntIterator it;
                    {
                        this.it = TFloatIntMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Float, Integer> next() {
                        this.it.advance();
                        float ik = this.it.key();
                        final Float key = ik == TFloatIntMapDecorator.this._map.getNoEntryKey() ? null : TFloatIntMapDecorator.this.wrapKey(ik);
                        int iv = this.it.value();
                        final Integer v = iv == TFloatIntMapDecorator.this._map.getNoEntryValue() ? null : TFloatIntMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Float, Integer>(){
                            private Integer val;
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
                                return TFloatIntMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Float, Integer> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Float key = (Float)((Map.Entry)o).getKey();
                    TFloatIntMapDecorator.this._map.remove(TFloatIntMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Float, Integer>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TFloatIntMapDecorator.this.clear();
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
    public void putAll(Map<? extends Float, ? extends Integer> map) {
        Iterator<Map.Entry<? extends Float, ? extends Integer>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Float, ? extends Integer> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf(k);
    }

    protected float unwrapKey(Object key) {
        return ((Float)key).floatValue();
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
        this._map = (TFloatIntMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


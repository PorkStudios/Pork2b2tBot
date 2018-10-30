/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.map.TFloatFloatMap;
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
public class TFloatFloatMapDecorator
extends AbstractMap<Float, Float>
implements Map<Float, Float>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatFloatMap _map;

    public TFloatFloatMapDecorator() {
    }

    public TFloatFloatMapDecorator(TFloatFloatMap map) {
        this._map = map;
    }

    public TFloatFloatMap getMap() {
        return this._map;
    }

    @Override
    public Float put(Float key, Float value) {
        float k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        float v = value == null ? this._map.getNoEntryValue() : this.unwrapValue(value);
        float retval = this._map.put(k, v);
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
    public Float get(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        float v = this._map.get(k);
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
    public Float remove(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        float v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Float, Float>> entrySet() {
        return new AbstractSet<Map.Entry<Float, Float>>(){

            @Override
            public int size() {
                return TFloatFloatMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TFloatFloatMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TFloatFloatMapDecorator.this.containsKey(k) && TFloatFloatMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Float, Float>> iterator() {
                return new Iterator<Map.Entry<Float, Float>>(){
                    private final TFloatFloatIterator it;
                    {
                        this.it = TFloatFloatMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Float, Float> next() {
                        this.it.advance();
                        float ik = this.it.key();
                        final Float key = ik == TFloatFloatMapDecorator.this._map.getNoEntryKey() ? null : TFloatFloatMapDecorator.this.wrapKey(ik);
                        float iv = this.it.value();
                        final Float v = iv == TFloatFloatMapDecorator.this._map.getNoEntryValue() ? null : TFloatFloatMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Float, Float>(){
                            private Float val;
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
                            public Float getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Float setValue(Float value) {
                                this.val = value;
                                return TFloatFloatMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Float, Float> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Float key = (Float)((Map.Entry)o).getKey();
                    TFloatFloatMapDecorator.this._map.remove(TFloatFloatMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Float, Float>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TFloatFloatMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Float && this._map.containsValue(this.unwrapValue(val));
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
    public void putAll(Map<? extends Float, ? extends Float> map) {
        Iterator<Map.Entry<? extends Float, ? extends Float>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Float, ? extends Float> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf(k);
    }

    protected float unwrapKey(Object key) {
        return ((Float)key).floatValue();
    }

    protected Float wrapValue(float k) {
        return Float.valueOf(k);
    }

    protected float unwrapValue(Object value) {
        return ((Float)value).floatValue();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TFloatFloatMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
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
public class TFloatByteMapDecorator
extends AbstractMap<Float, Byte>
implements Map<Float, Byte>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TFloatByteMap _map;

    public TFloatByteMapDecorator() {
    }

    public TFloatByteMapDecorator(TFloatByteMap map) {
        this._map = map;
    }

    public TFloatByteMap getMap() {
        return this._map;
    }

    @Override
    public Byte put(Float key, Byte value) {
        float k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
        byte v = value == null ? this._map.getNoEntryValue() : this.unwrapValue(value);
        byte retval = this._map.put(k, v);
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
    public Byte get(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        byte v = this._map.get(k);
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
    public Byte remove(Object key) {
        float k;
        if (key != null) {
            if (!(key instanceof Float)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        byte v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Float, Byte>> entrySet() {
        return new AbstractSet<Map.Entry<Float, Byte>>(){

            @Override
            public int size() {
                return TFloatByteMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TFloatByteMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TFloatByteMapDecorator.this.containsKey(k) && TFloatByteMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Float, Byte>> iterator() {
                return new Iterator<Map.Entry<Float, Byte>>(){
                    private final TFloatByteIterator it;
                    {
                        this.it = TFloatByteMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Float, Byte> next() {
                        this.it.advance();
                        float ik = this.it.key();
                        final Float key = ik == TFloatByteMapDecorator.this._map.getNoEntryKey() ? null : TFloatByteMapDecorator.this.wrapKey(ik);
                        byte iv = this.it.value();
                        final Byte v = iv == TFloatByteMapDecorator.this._map.getNoEntryValue() ? null : TFloatByteMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Float, Byte>(){
                            private Byte val;
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
                            public Byte getValue() {
                                return this.val;
                            }

                            @Override
                            public int hashCode() {
                                return key.hashCode() + this.val.hashCode();
                            }

                            @Override
                            public Byte setValue(Byte value) {
                                this.val = value;
                                return TFloatByteMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Float, Byte> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Float key = (Float)((Map.Entry)o).getKey();
                    TFloatByteMapDecorator.this._map.remove(TFloatByteMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Float, Byte>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TFloatByteMapDecorator.this.clear();
            }

        };
    }

    @Override
    public boolean containsValue(Object val) {
        return val instanceof Byte && this._map.containsValue(this.unwrapValue(val));
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
    public void putAll(Map<? extends Float, ? extends Byte> map) {
        Iterator<Map.Entry<? extends Float, ? extends Byte>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Float, ? extends Byte> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Float wrapKey(float k) {
        return Float.valueOf(k);
    }

    protected float unwrapKey(Object key) {
        return ((Float)key).floatValue();
    }

    protected Byte wrapValue(byte k) {
        return k;
    }

    protected byte unwrapValue(Object value) {
        return (Byte)value;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        this._map = (TFloatByteMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


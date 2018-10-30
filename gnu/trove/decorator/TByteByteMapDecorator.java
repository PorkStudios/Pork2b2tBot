/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.map.TByteByteMap;
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
public class TByteByteMapDecorator
extends AbstractMap<Byte, Byte>
implements Map<Byte, Byte>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TByteByteMap _map;

    public TByteByteMapDecorator() {
    }

    public TByteByteMapDecorator(TByteByteMap map) {
        this._map = map;
    }

    public TByteByteMap getMap() {
        return this._map;
    }

    @Override
    public Byte put(Byte key, Byte value) {
        byte k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        byte k;
        if (key != null) {
            if (!(key instanceof Byte)) return null;
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
        byte k;
        if (key != null) {
            if (!(key instanceof Byte)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        byte v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Byte, Byte>> entrySet() {
        return new AbstractSet<Map.Entry<Byte, Byte>>(){

            @Override
            public int size() {
                return TByteByteMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TByteByteMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TByteByteMapDecorator.this.containsKey(k) && TByteByteMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Byte, Byte>> iterator() {
                return new Iterator<Map.Entry<Byte, Byte>>(){
                    private final TByteByteIterator it;
                    {
                        this.it = TByteByteMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Byte, Byte> next() {
                        this.it.advance();
                        byte ik = this.it.key();
                        final Byte key = ik == TByteByteMapDecorator.this._map.getNoEntryKey() ? null : TByteByteMapDecorator.this.wrapKey(ik);
                        byte iv = this.it.value();
                        final Byte v = iv == TByteByteMapDecorator.this._map.getNoEntryValue() ? null : TByteByteMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Byte, Byte>(){
                            private Byte val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Byte getKey() {
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
                                return TByteByteMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Byte, Byte> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Byte key = (Byte)((Map.Entry)o).getKey();
                    TByteByteMapDecorator.this._map.remove(TByteByteMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Byte, Byte>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TByteByteMapDecorator.this.clear();
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
        return key instanceof Byte && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Byte, ? extends Byte> map) {
        Iterator<Map.Entry<? extends Byte, ? extends Byte>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Byte, ? extends Byte> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Byte wrapKey(byte k) {
        return k;
    }

    protected byte unwrapKey(Object key) {
        return (Byte)key;
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
        this._map = (TByteByteMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


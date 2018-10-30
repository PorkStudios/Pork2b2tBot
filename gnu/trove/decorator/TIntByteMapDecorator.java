/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.map.TIntByteMap;
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
public class TIntByteMapDecorator
extends AbstractMap<Integer, Byte>
implements Map<Integer, Byte>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TIntByteMap _map;

    public TIntByteMapDecorator() {
    }

    public TIntByteMapDecorator(TIntByteMap map) {
        this._map = map;
    }

    public TIntByteMap getMap() {
        return this._map;
    }

    @Override
    public Byte put(Integer key, Byte value) {
        int k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
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
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        byte v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Integer, Byte>> entrySet() {
        return new AbstractSet<Map.Entry<Integer, Byte>>(){

            @Override
            public int size() {
                return TIntByteMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TIntByteMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TIntByteMapDecorator.this.containsKey(k) && TIntByteMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Integer, Byte>> iterator() {
                return new Iterator<Map.Entry<Integer, Byte>>(){
                    private final TIntByteIterator it;
                    {
                        this.it = TIntByteMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Integer, Byte> next() {
                        this.it.advance();
                        int ik = this.it.key();
                        final Integer key = ik == TIntByteMapDecorator.this._map.getNoEntryKey() ? null : TIntByteMapDecorator.this.wrapKey(ik);
                        byte iv = this.it.value();
                        final Byte v = iv == TIntByteMapDecorator.this._map.getNoEntryValue() ? null : TIntByteMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Integer, Byte>(){
                            private Byte val;
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
                                return TIntByteMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Integer, Byte> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Integer key = (Integer)((Map.Entry)o).getKey();
                    TIntByteMapDecorator.this._map.remove(TIntByteMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Integer, Byte>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TIntByteMapDecorator.this.clear();
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
    public void putAll(Map<? extends Integer, ? extends Byte> map) {
        Iterator<Map.Entry<? extends Integer, ? extends Byte>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Integer, ? extends Byte> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Integer wrapKey(int k) {
        return k;
    }

    protected int unwrapKey(Object key) {
        return (Integer)key;
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
        this._map = (TIntByteMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


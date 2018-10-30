/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
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
public class TShortShortMapDecorator
extends AbstractMap<Short, Short>
implements Map<Short, Short>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TShortShortMap _map;

    public TShortShortMapDecorator() {
    }

    public TShortShortMapDecorator(TShortShortMap map) {
        this._map = map;
    }

    public TShortShortMap getMap() {
        return this._map;
    }

    @Override
    public Short put(Short key, Short value) {
        short k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
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
        short k;
        if (key != null) {
            if (!(key instanceof Short)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        short v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Short, Short>> entrySet() {
        return new AbstractSet<Map.Entry<Short, Short>>(){

            @Override
            public int size() {
                return TShortShortMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TShortShortMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TShortShortMapDecorator.this.containsKey(k) && TShortShortMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Short, Short>> iterator() {
                return new Iterator<Map.Entry<Short, Short>>(){
                    private final TShortShortIterator it;
                    {
                        this.it = TShortShortMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Short, Short> next() {
                        this.it.advance();
                        short ik = this.it.key();
                        final Short key = ik == TShortShortMapDecorator.this._map.getNoEntryKey() ? null : TShortShortMapDecorator.this.wrapKey(ik);
                        short iv = this.it.value();
                        final Short v = iv == TShortShortMapDecorator.this._map.getNoEntryValue() ? null : TShortShortMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Short, Short>(){
                            private Short val;
                            {
                                this.val = v;
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                            }

                            @Override
                            public Short getKey() {
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
                                return TShortShortMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Short, Short> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Short key = (Short)((Map.Entry)o).getKey();
                    TShortShortMapDecorator.this._map.remove(TShortShortMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Short, Short>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TShortShortMapDecorator.this.clear();
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
        return key instanceof Short && this._map.containsKey(this.unwrapKey(key));
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
    public void putAll(Map<? extends Short, ? extends Short> map) {
        Iterator<Map.Entry<? extends Short, ? extends Short>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Short, ? extends Short> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Short wrapKey(short k) {
        return k;
    }

    protected short unwrapKey(Object key) {
        return (Short)key;
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
        this._map = (TShortShortMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


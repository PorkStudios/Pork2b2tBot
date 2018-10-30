/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.map.TCharShortMap;
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
public class TCharShortMapDecorator
extends AbstractMap<Character, Short>
implements Map<Character, Short>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharShortMap _map;

    public TCharShortMapDecorator() {
    }

    public TCharShortMapDecorator(TCharShortMap map) {
        this._map = map;
    }

    public TCharShortMap getMap() {
        return this._map;
    }

    @Override
    public Short put(Character key, Short value) {
        char k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
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
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        short v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Character, Short>> entrySet() {
        return new AbstractSet<Map.Entry<Character, Short>>(){

            @Override
            public int size() {
                return TCharShortMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TCharShortMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TCharShortMapDecorator.this.containsKey(k) && TCharShortMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Character, Short>> iterator() {
                return new Iterator<Map.Entry<Character, Short>>(){
                    private final TCharShortIterator it;
                    {
                        this.it = TCharShortMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Character, Short> next() {
                        this.it.advance();
                        char ik = this.it.key();
                        final Character key = ik == TCharShortMapDecorator.this._map.getNoEntryKey() ? null : TCharShortMapDecorator.this.wrapKey(ik);
                        short iv = this.it.value();
                        final Short v = iv == TCharShortMapDecorator.this._map.getNoEntryValue() ? null : TCharShortMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Character, Short>(){
                            private Short val;
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
                                return TCharShortMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Character, Short> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Character key = (Character)((Map.Entry)o).getKey();
                    TCharShortMapDecorator.this._map.remove(TCharShortMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Character, Short>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TCharShortMapDecorator.this.clear();
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
    public void putAll(Map<? extends Character, ? extends Short> map) {
        Iterator<Map.Entry<? extends Character, ? extends Short>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Character, ? extends Short> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Character wrapKey(char k) {
        return Character.valueOf(k);
    }

    protected char unwrapKey(Object key) {
        return ((Character)key).charValue();
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
        this._map = (TCharShortMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


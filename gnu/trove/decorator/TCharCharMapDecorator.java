/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.map.TCharCharMap;
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
public class TCharCharMapDecorator
extends AbstractMap<Character, Character>
implements Map<Character, Character>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharCharMap _map;

    public TCharCharMapDecorator() {
    }

    public TCharCharMapDecorator(TCharCharMap map) {
        this._map = map;
    }

    public TCharCharMap getMap() {
        return this._map;
    }

    @Override
    public Character put(Character key, Character value) {
        char k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
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
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        char v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Character, Character>> entrySet() {
        return new AbstractSet<Map.Entry<Character, Character>>(){

            @Override
            public int size() {
                return TCharCharMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TCharCharMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TCharCharMapDecorator.this.containsKey(k) && TCharCharMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Character, Character>> iterator() {
                return new Iterator<Map.Entry<Character, Character>>(){
                    private final TCharCharIterator it;
                    {
                        this.it = TCharCharMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Character, Character> next() {
                        this.it.advance();
                        char ik = this.it.key();
                        final Character key = ik == TCharCharMapDecorator.this._map.getNoEntryKey() ? null : TCharCharMapDecorator.this.wrapKey(ik);
                        char iv = this.it.value();
                        final Character v = iv == TCharCharMapDecorator.this._map.getNoEntryValue() ? null : TCharCharMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Character, Character>(){
                            private Character val;
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
                                return TCharCharMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Character, Character> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Character key = (Character)((Map.Entry)o).getKey();
                    TCharCharMapDecorator.this._map.remove(TCharCharMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Character, Character>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TCharCharMapDecorator.this.clear();
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
    public void putAll(Map<? extends Character, ? extends Character> map) {
        Iterator<Map.Entry<? extends Character, ? extends Character>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Character, ? extends Character> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Character wrapKey(char k) {
        return Character.valueOf(k);
    }

    protected char unwrapKey(Object key) {
        return ((Character)key).charValue();
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
        this._map = (TCharCharMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


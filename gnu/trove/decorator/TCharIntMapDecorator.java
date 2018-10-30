/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.map.TCharIntMap;
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
public class TCharIntMapDecorator
extends AbstractMap<Character, Integer>
implements Map<Character, Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TCharIntMap _map;

    public TCharIntMapDecorator() {
    }

    public TCharIntMapDecorator(TCharIntMap map) {
        this._map = map;
    }

    public TCharIntMap getMap() {
        return this._map;
    }

    @Override
    public Integer put(Character key, Integer value) {
        char k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
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
        char k;
        if (key != null) {
            if (!(key instanceof Character)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Character, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<Character, Integer>>(){

            @Override
            public int size() {
                return TCharIntMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TCharIntMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TCharIntMapDecorator.this.containsKey(k) && TCharIntMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Character, Integer>> iterator() {
                return new Iterator<Map.Entry<Character, Integer>>(){
                    private final TCharIntIterator it;
                    {
                        this.it = TCharIntMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Character, Integer> next() {
                        this.it.advance();
                        char ik = this.it.key();
                        final Character key = ik == TCharIntMapDecorator.this._map.getNoEntryKey() ? null : TCharIntMapDecorator.this.wrapKey(ik);
                        int iv = this.it.value();
                        final Integer v = iv == TCharIntMapDecorator.this._map.getNoEntryValue() ? null : TCharIntMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Character, Integer>(){
                            private Integer val;
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
                                return TCharIntMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Character, Integer> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Character key = (Character)((Map.Entry)o).getKey();
                    TCharIntMapDecorator.this._map.remove(TCharIntMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Character, Integer>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TCharIntMapDecorator.this.clear();
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
    public void putAll(Map<? extends Character, ? extends Integer> map) {
        Iterator<Map.Entry<? extends Character, ? extends Integer>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Character, ? extends Integer> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Character wrapKey(char k) {
        return Character.valueOf(k);
    }

    protected char unwrapKey(Object key) {
        return ((Character)key).charValue();
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
        this._map = (TCharIntMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


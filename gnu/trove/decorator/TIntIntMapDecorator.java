/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.decorator;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
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
public class TIntIntMapDecorator
extends AbstractMap<Integer, Integer>
implements Map<Integer, Integer>,
Externalizable,
Cloneable {
    static final long serialVersionUID = 1L;
    protected TIntIntMap _map;

    public TIntIntMapDecorator() {
    }

    public TIntIntMapDecorator(TIntIntMap map) {
        this._map = map;
    }

    public TIntIntMap getMap() {
        return this._map;
    }

    @Override
    public Integer put(Integer key, Integer value) {
        int k = key == null ? this._map.getNoEntryKey() : this.unwrapKey(key);
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
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
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
        int k;
        if (key != null) {
            if (!(key instanceof Integer)) return null;
            k = this.unwrapKey(key);
        } else {
            k = this._map.getNoEntryKey();
        }
        int v = this._map.remove(k);
        if (v != this._map.getNoEntryValue()) return this.wrapValue(v);
        return null;
    }

    @Override
    public Set<Map.Entry<Integer, Integer>> entrySet() {
        return new AbstractSet<Map.Entry<Integer, Integer>>(){

            @Override
            public int size() {
                return TIntIntMapDecorator.this._map.size();
            }

            @Override
            public boolean isEmpty() {
                return TIntIntMapDecorator.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Object k = ((Map.Entry)o).getKey();
                    Object v = ((Map.Entry)o).getValue();
                    return TIntIntMapDecorator.this.containsKey(k) && TIntIntMapDecorator.this.get(k).equals(v);
                }
                return false;
            }

            @Override
            public Iterator<Map.Entry<Integer, Integer>> iterator() {
                return new Iterator<Map.Entry<Integer, Integer>>(){
                    private final TIntIntIterator it;
                    {
                        this.it = TIntIntMapDecorator.this._map.iterator();
                    }

                    @Override
                    public Map.Entry<Integer, Integer> next() {
                        this.it.advance();
                        int ik = this.it.key();
                        final Integer key = ik == TIntIntMapDecorator.this._map.getNoEntryKey() ? null : TIntIntMapDecorator.this.wrapKey(ik);
                        int iv = this.it.value();
                        final Integer v = iv == TIntIntMapDecorator.this._map.getNoEntryValue() ? null : TIntIntMapDecorator.this.wrapValue(iv);
                        return new Map.Entry<Integer, Integer>(){
                            private Integer val;
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
                                return TIntIntMapDecorator.this.put(key, value);
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
            public boolean add(Map.Entry<Integer, Integer> o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                boolean modified = false;
                if (this.contains(o)) {
                    Integer key = (Integer)((Map.Entry)o).getKey();
                    TIntIntMapDecorator.this._map.remove(TIntIntMapDecorator.this.unwrapKey(key));
                    modified = true;
                }
                return modified;
            }

            @Override
            public boolean addAll(Collection<? extends Map.Entry<Integer, Integer>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                TIntIntMapDecorator.this.clear();
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
    public void putAll(Map<? extends Integer, ? extends Integer> map) {
        Iterator<Map.Entry<? extends Integer, ? extends Integer>> it = map.entrySet().iterator();
        int i = map.size();
        while (i-- > 0) {
            Map.Entry<? extends Integer, ? extends Integer> e = it.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    protected Integer wrapKey(int k) {
        return k;
    }

    protected int unwrapKey(Object key) {
        return (Integer)key;
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
        this._map = (TIntIntMap)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        out.writeObject(this._map);
    }

}


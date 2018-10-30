/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.AbstractByte2FloatMap;
import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.Byte2FloatMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public class Byte2FloatOpenHashMap
extends AbstractByte2FloatMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient byte[] key;
    protected transient float[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Byte2FloatMap.FastEntrySet entries;
    protected transient ByteSet keys;
    protected transient FloatCollection values;

    public Byte2FloatOpenHashMap(int expected, float f) {
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = new byte[this.n + 1];
        this.value = new float[this.n + 1];
    }

    public Byte2FloatOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Byte2FloatOpenHashMap() {
        this(16, 0.75f);
    }

    public Byte2FloatOpenHashMap(Map<? extends Byte, ? extends Float> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Byte2FloatOpenHashMap(Map<? extends Byte, ? extends Float> m) {
        this(m, 0.75f);
    }

    public Byte2FloatOpenHashMap(Byte2FloatMap m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Byte2FloatOpenHashMap(Byte2FloatMap m) {
        this(m, 0.75f);
    }

    public Byte2FloatOpenHashMap(byte[] k, float[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Byte2FloatOpenHashMap(byte[] k, float[] v) {
        this(k, v, 0.75f);
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
    }

    private void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(0x40000000L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private float removeEntry(int pos) {
        float oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private float removeNullEntry() {
        this.containsNullKey = false;
        float oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Float> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(byte k) {
        if (k == 0) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return - pos + 1;
        }
        if (k == curr) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return - pos + 1;
        } while (k != curr);
        return pos;
    }

    private void insert(int pos, byte k, float v) {
        if (pos == this.n) {
            this.containsNullKey = true;
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
    }

    @Override
    public float put(byte k, float v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        float oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    private float addToValue(int pos, float incr) {
        float oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }

    public float addTo(byte k, float incr) {
        int pos;
        if (k == 0) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            byte[] key = this.key;
            pos = HashCommon.mix(k) & this.mask;
            byte curr = key[pos];
            if (curr != 0) {
                if (curr == k) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (curr != k) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = this.defRetValue + incr;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
    }

    protected final void shiftKeys(int pos) {
        byte[] key = this.key;
        do {
            byte curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    return;
                }
                int slot = HashCommon.mix(curr) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public float remove(byte k) {
        if (k == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    @Override
    public float get(byte k) {
        if (k == 0) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(byte k) {
        if (k == 0) {
            return this.containsNullKey;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr);
        return true;
    }

    @Override
    public boolean containsValue(float v) {
        float[] value = this.value;
        byte[] key = this.key;
        if (this.containsNullKey && Float.floatToIntBits(value[this.n]) == Float.floatToIntBits(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == 0 || Float.floatToIntBits(value[i]) != Float.floatToIntBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public float getOrDefault(byte k, float defaultValue) {
        if (k == 0) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return defaultValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return defaultValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public float putIfAbsent(byte k, float v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(byte k, float v) {
        if (k == 0) {
            if (this.containsNullKey && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[this.n])) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        byte[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        byte curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr || Float.floatToIntBits(v) != Float.floatToIntBits(this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(byte k, float oldValue, float v) {
        int pos = this.find(k);
        if (pos < 0 || Float.floatToIntBits(oldValue) != Float.floatToIntBits(this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public float replace(byte k, float v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        float oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public float computeIfAbsent(byte k, IntToDoubleFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(k));
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public float computeIfAbsentNullable(byte k, IntFunction<? extends Float> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        Float newValue = mappingFunction.apply(k);
        if (newValue == null) {
            return this.defRetValue;
        }
        float v = newValue.floatValue();
        this.insert(- pos - 1, k, v);
        return v;
    }

    @Override
    public float computeIfPresent(byte k, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Float newValue = remappingFunction.apply((Byte)k, Float.valueOf(this.value[pos]));
        if (newValue == null) {
            if (k == 0) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue.floatValue();
        return this.value[pos];
    }

    @Override
    public float compute(byte k, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Float newValue = remappingFunction.apply((Byte)k, pos >= 0 ? Float.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k == 0) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
        float newVal = newValue.floatValue();
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public float merge(byte k, float v, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Float newValue = remappingFunction.apply(Float.valueOf(this.value[pos]), Float.valueOf(v));
        if (newValue == null) {
            if (k == 0) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue.floatValue();
        return this.value[pos];
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, (byte)0);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Byte2FloatMap.FastEntrySet byte2FloatEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ByteSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public FloatCollection values() {
        if (this.values == null) {
            this.values = new AbstractFloatCollection(){

                @Override
                public FloatIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Byte2FloatOpenHashMap.this.size;
                }

                @Override
                public boolean contains(float v) {
                    return Byte2FloatOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Byte2FloatOpenHashMap.this.clear();
                }

                @Override
                public void forEach(DoubleConsumer consumer) {
                    if (Byte2FloatOpenHashMap.this.containsNullKey) {
                        consumer.accept(Byte2FloatOpenHashMap.this.value[Byte2FloatOpenHashMap.this.n]);
                    }
                    int pos = Byte2FloatOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Byte2FloatOpenHashMap.this.key[pos] == 0) continue;
                        consumer.accept(Byte2FloatOpenHashMap.this.value[pos]);
                    }
                }
            };
        }
        return this.values;
    }

    public boolean trim() {
        int l = HashCommon.arraySize(this.size, this.f);
        if (l >= this.n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    public boolean trim(int n) {
        int l = HashCommon.nextPowerOfTwo((int)Math.ceil((float)n / this.f));
        if (l >= n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    protected void rehash(int newN) {
        byte[] key = this.key;
        float[] value = this.value;
        int mask = newN - 1;
        byte[] newKey = new byte[newN + 1];
        float[] newValue = new float[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0) {
            }
            int pos = HashCommon.mix(key[i]) & mask;
            if (newKey[pos] != 0) {
                while (newKey[pos = pos + 1 & mask] != 0) {
                }
            }
            newKey[pos] = key[i];
            newValue[pos] = value[i];
        }
        newValue[newN] = value[this.n];
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Byte2FloatOpenHashMap clone() {
        Byte2FloatOpenHashMap c;
        try {
            c = (Byte2FloatOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (byte[])this.key.clone();
        c.value = (float[])this.value.clone();
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == 0) {
                ++i;
            }
            t = this.key[i];
            h += (t ^= HashCommon.float2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.float2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        byte[] key = this.key;
        float[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeByte(key[e]);
            s.writeFloat(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new byte[this.n + 1];
        byte[] key = this.key;
        this.value = new float[this.n + 1];
        float[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            byte k = s.readByte();
            float v = s.readFloat();
            if (k == 0) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k) & this.mask;
                while (key[pos] != 0) {
                    pos = pos + 1 & this.mask;
                }
            }
            key[pos] = k;
            value[pos] = v;
        }
    }

    private void checkTable() {
    }

    private final class ValueIterator
    extends MapIterator
    implements FloatIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public float nextFloat() {
            return Byte2FloatOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractByteSet {
        private KeySet() {
        }

        @Override
        public ByteIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(IntConsumer consumer) {
            if (Byte2FloatOpenHashMap.this.containsNullKey) {
                consumer.accept(Byte2FloatOpenHashMap.this.key[Byte2FloatOpenHashMap.this.n]);
            }
            int pos = Byte2FloatOpenHashMap.this.n;
            while (pos-- != 0) {
                byte k = Byte2FloatOpenHashMap.this.key[pos];
                if (k == 0) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Byte2FloatOpenHashMap.this.size;
        }

        @Override
        public boolean contains(byte k) {
            return Byte2FloatOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(byte k) {
            int oldSize = Byte2FloatOpenHashMap.this.size;
            Byte2FloatOpenHashMap.this.remove(k);
            return Byte2FloatOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Byte2FloatOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements ByteIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public byte nextByte() {
            return Byte2FloatOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Byte2FloatMap.Entry>
    implements Byte2FloatMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Byte2FloatMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Byte2FloatMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            float v = ((Float)e.getValue()).floatValue();
            if (k == 0) {
                return Byte2FloatOpenHashMap.this.containsNullKey && Float.floatToIntBits(Byte2FloatOpenHashMap.this.value[Byte2FloatOpenHashMap.this.n]) == Float.floatToIntBits(v);
            }
            byte[] key = Byte2FloatOpenHashMap.this.key;
            int pos = HashCommon.mix(k) & Byte2FloatOpenHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (k == curr) {
                return Float.floatToIntBits(Byte2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v);
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2FloatOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (k != curr);
            return Float.floatToIntBits(Byte2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            byte k = (Byte)e.getKey();
            float v = ((Float)e.getValue()).floatValue();
            if (k == 0) {
                if (Byte2FloatOpenHashMap.this.containsNullKey && Float.floatToIntBits(Byte2FloatOpenHashMap.this.value[Byte2FloatOpenHashMap.this.n]) == Float.floatToIntBits(v)) {
                    Byte2FloatOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            byte[] key = Byte2FloatOpenHashMap.this.key;
            int pos = HashCommon.mix(k) & Byte2FloatOpenHashMap.this.mask;
            byte curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (curr == k) {
                if (Float.floatToIntBits(Byte2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v)) {
                    Byte2FloatOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Byte2FloatOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (curr != k || Float.floatToIntBits(Byte2FloatOpenHashMap.this.value[pos]) != Float.floatToIntBits(v));
            Byte2FloatOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Byte2FloatOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Byte2FloatOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Byte2FloatMap.Entry> consumer) {
            if (Byte2FloatOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractByte2FloatMap.BasicEntry(Byte2FloatOpenHashMap.this.key[Byte2FloatOpenHashMap.this.n], Byte2FloatOpenHashMap.this.value[Byte2FloatOpenHashMap.this.n]));
            }
            int pos = Byte2FloatOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Byte2FloatOpenHashMap.this.key[pos] == 0) continue;
                consumer.accept(new AbstractByte2FloatMap.BasicEntry(Byte2FloatOpenHashMap.this.key[pos], Byte2FloatOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Byte2FloatMap.Entry> consumer) {
            AbstractByte2FloatMap.BasicEntry entry = new AbstractByte2FloatMap.BasicEntry();
            if (Byte2FloatOpenHashMap.this.containsNullKey) {
                entry.key = Byte2FloatOpenHashMap.this.key[Byte2FloatOpenHashMap.this.n];
                entry.value = Byte2FloatOpenHashMap.this.value[Byte2FloatOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Byte2FloatOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Byte2FloatOpenHashMap.this.key[pos] == 0) continue;
                entry.key = Byte2FloatOpenHashMap.this.key[pos];
                entry.value = Byte2FloatOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Byte2FloatMap.Entry> {
        private final MapEntry entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends MapIterator
    implements ObjectIterator<Byte2FloatMap.Entry> {
        private MapEntry entry;

        private EntryIterator() {
            super();
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
            this.entry.index = -1;
        }
    }

    private class MapIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        ByteArrayList wrapped;

        private MapIterator() {
            this.pos = Byte2FloatOpenHashMap.this.n;
            this.last = -1;
            this.c = Byte2FloatOpenHashMap.this.size;
            this.mustReturnNullKey = Byte2FloatOpenHashMap.this.containsNullKey;
        }

        public boolean hasNext() {
            return this.c != 0;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                this.last = Byte2FloatOpenHashMap.this.n;
                return this.last;
            }
            byte[] key = Byte2FloatOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                byte k = this.wrapped.getByte(- this.pos - 1);
                int p = HashCommon.mix(k) & Byte2FloatOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Byte2FloatOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == 0);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            byte[] key = Byte2FloatOpenHashMap.this.key;
            do {
                byte curr;
                int last = pos;
                pos = last + 1 & Byte2FloatOpenHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == 0) {
                        key[last] = 0;
                        return;
                    }
                    int slot = HashCommon.mix(curr) & Byte2FloatOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Byte2FloatOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ByteArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Byte2FloatOpenHashMap.this.value[last] = Byte2FloatOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Byte2FloatOpenHashMap.this.n) {
                Byte2FloatOpenHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Byte2FloatOpenHashMap.this.remove(this.wrapped.getByte(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Byte2FloatOpenHashMap.this.size;
            this.last = -1;
        }

        public int skip(int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }
    }

    final class MapEntry
    implements Byte2FloatMap.Entry,
    Map.Entry<Byte, Float> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public byte getByteKey() {
            return Byte2FloatOpenHashMap.this.key[this.index];
        }

        @Override
        public float getFloatValue() {
            return Byte2FloatOpenHashMap.this.value[this.index];
        }

        @Override
        public float setValue(float v) {
            float oldValue = Byte2FloatOpenHashMap.this.value[this.index];
            Byte2FloatOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Byte getKey() {
            return Byte2FloatOpenHashMap.this.key[this.index];
        }

        @Deprecated
        @Override
        public Float getValue() {
            return Float.valueOf(Byte2FloatOpenHashMap.this.value[this.index]);
        }

        @Deprecated
        @Override
        public Float setValue(Float v) {
            return Float.valueOf(this.setValue(v.floatValue()));
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Byte2FloatOpenHashMap.this.key[this.index] == (Byte)e.getKey() && Float.floatToIntBits(Byte2FloatOpenHashMap.this.value[this.index]) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public int hashCode() {
            return Byte2FloatOpenHashMap.this.key[this.index] ^ HashCommon.float2int(Byte2FloatOpenHashMap.this.value[this.index]);
        }

        public String toString() {
            return "" + Byte2FloatOpenHashMap.this.key[this.index] + "=>" + Byte2FloatOpenHashMap.this.value[this.index];
        }
    }

}


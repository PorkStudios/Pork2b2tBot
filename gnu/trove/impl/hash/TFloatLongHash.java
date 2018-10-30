/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.procedure.TFloatProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TFloatLongHash
extends TPrimitiveHash {
    static final long serialVersionUID = 1L;
    public transient float[] _set;
    protected float no_entry_key;
    protected long no_entry_value;
    protected boolean consumeFreeSlot;

    public TFloatLongHash() {
        this.no_entry_key = 0.0f;
        this.no_entry_value = 0L;
    }

    public TFloatLongHash(int initialCapacity) {
        super(initialCapacity);
        this.no_entry_key = 0.0f;
        this.no_entry_value = 0L;
    }

    public TFloatLongHash(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = 0.0f;
        this.no_entry_value = 0L;
    }

    public TFloatLongHash(int initialCapacity, float loadFactor, float no_entry_key, long no_entry_value) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = no_entry_key;
        this.no_entry_value = no_entry_value;
    }

    public float getNoEntryKey() {
        return this.no_entry_key;
    }

    public long getNoEntryValue() {
        return this.no_entry_value;
    }

    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._set = new float[capacity];
        return capacity;
    }

    public boolean contains(float val) {
        return this.index(val) >= 0;
    }

    public boolean forEach(TFloatProcedure procedure) {
        byte[] states = this._states;
        float[] set = this._set;
        int i = set.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(set[i])) continue;
            return false;
        }
        return true;
    }

    protected void removeAt(int index) {
        this._set[index] = this.no_entry_key;
        super.removeAt(index);
    }

    protected int index(float key) {
        byte[] states = this._states;
        float[] set = this._set;
        int length = states.length;
        int hash = HashFunctions.hash(key) & Integer.MAX_VALUE;
        int index = hash % length;
        byte state = states[index];
        if (state == 0) {
            return -1;
        }
        if (state == 1 && set[index] == key) {
            return index;
        }
        return this.indexRehashed(key, index, hash, state);
    }

    int indexRehashed(float key, int index, int hash, byte state) {
        int length = this._set.length;
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;
        do {
            if ((index -= probe) < 0) {
                index += length;
            }
            if ((state = this._states[index]) == 0) {
                return -1;
            }
            if (key != this._set[index] || state == 2) continue;
            return index;
        } while (index != loopIndex);
        return -1;
    }

    protected int insertKey(float val) {
        int hash = HashFunctions.hash(val) & Integer.MAX_VALUE;
        int index = hash % this._states.length;
        byte state = this._states[index];
        this.consumeFreeSlot = false;
        if (state == 0) {
            this.consumeFreeSlot = true;
            this.insertKeyAt(index, val);
            return index;
        }
        if (state == 1 && this._set[index] == val) {
            return - index - 1;
        }
        return this.insertKeyRehash(val, index, hash, state);
    }

    int insertKeyRehash(float val, int index, int hash, byte state) {
        int length = this._set.length;
        int probe = 1 + hash % (length - 2);
        int loopIndex = index;
        int firstRemoved = -1;
        do {
            if (state == 2 && firstRemoved == -1) {
                firstRemoved = index;
            }
            if ((index -= probe) < 0) {
                index += length;
            }
            if ((state = this._states[index]) == 0) {
                if (firstRemoved != -1) {
                    this.insertKeyAt(firstRemoved, val);
                    return firstRemoved;
                }
                this.consumeFreeSlot = true;
                this.insertKeyAt(index, val);
                return index;
            }
            if (state != 1 || this._set[index] != val) continue;
            return - index - 1;
        } while (index != loopIndex);
        if (firstRemoved != -1) {
            this.insertKeyAt(firstRemoved, val);
            return firstRemoved;
        }
        throw new IllegalStateException("No free or removed slots available. Key set full?!!");
    }

    void insertKeyAt(int index, float val) {
        this._set[index] = val;
        this._states[index] = 1;
    }

    protected int XinsertKey(float key) {
        byte[] states = this._states;
        float[] set = this._set;
        int length = states.length;
        int hash = HashFunctions.hash(key) & Integer.MAX_VALUE;
        int index = hash % length;
        byte state = states[index];
        this.consumeFreeSlot = false;
        if (state == 0) {
            this.consumeFreeSlot = true;
            set[index] = key;
            states[index] = 1;
            return index;
        }
        if (state == 1 && set[index] == key) {
            return - index - 1;
        }
        int probe = 1 + hash % (length - 2);
        if (state != 2) {
            do {
                if ((index -= probe) >= 0) continue;
                index += length;
            } while ((state = states[index]) == 1 && set[index] != key);
        }
        if (state == 2) {
            int firstRemoved = index;
            while (state != 0 && (state == 2 || set[index] != key)) {
                if ((index -= probe) < 0) {
                    index += length;
                }
                state = states[index];
            }
            if (state == 1) {
                return - index - 1;
            }
            set[index] = key;
            states[index] = 1;
            return firstRemoved;
        }
        if (state == 1) {
            return - index - 1;
        }
        this.consumeFreeSlot = true;
        set[index] = key;
        states[index] = 1;
        return index;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        super.writeExternal(out);
        out.writeFloat(this.no_entry_key);
        out.writeLong(this.no_entry_value);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        this.no_entry_key = in.readFloat();
        this.no_entry_value = in.readLong();
    }
}


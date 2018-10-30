/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.procedure.TShortProcedure;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class TShortLongHash
extends TPrimitiveHash {
    static final long serialVersionUID = 1L;
    public transient short[] _set;
    protected short no_entry_key;
    protected long no_entry_value;
    protected boolean consumeFreeSlot;

    public TShortLongHash() {
        this.no_entry_key = 0;
        this.no_entry_value = 0L;
    }

    public TShortLongHash(int initialCapacity) {
        super(initialCapacity);
        this.no_entry_key = 0;
        this.no_entry_value = 0L;
    }

    public TShortLongHash(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = 0;
        this.no_entry_value = 0L;
    }

    public TShortLongHash(int initialCapacity, float loadFactor, short no_entry_key, long no_entry_value) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = no_entry_key;
        this.no_entry_value = no_entry_value;
    }

    public short getNoEntryKey() {
        return this.no_entry_key;
    }

    public long getNoEntryValue() {
        return this.no_entry_value;
    }

    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._set = new short[capacity];
        return capacity;
    }

    public boolean contains(short val) {
        return this.index(val) >= 0;
    }

    public boolean forEach(TShortProcedure procedure) {
        byte[] states = this._states;
        short[] set = this._set;
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

    protected int index(short key) {
        byte[] states = this._states;
        short[] set = this._set;
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

    int indexRehashed(short key, int index, int hash, byte state) {
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

    protected int insertKey(short val) {
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

    int insertKeyRehash(short val, int index, int hash, byte state) {
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

    void insertKeyAt(int index, short val) {
        this._set[index] = val;
        this._states[index] = 1;
    }

    protected int XinsertKey(short key) {
        byte[] states = this._states;
        short[] set = this._set;
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
        out.writeShort(this.no_entry_key);
        out.writeLong(this.no_entry_value);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        this.no_entry_key = in.readShort();
        this.no_entry_value = in.readLong();
    }
}


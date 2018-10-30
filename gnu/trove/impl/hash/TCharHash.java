/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.procedure.TCharProcedure;
import java.util.Arrays;

public abstract class TCharHash
extends TPrimitiveHash {
    static final long serialVersionUID = 1L;
    public transient char[] _set;
    protected char no_entry_value;
    protected boolean consumeFreeSlot;

    public TCharHash() {
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
        if (this.no_entry_value != '\u0000') {
            Arrays.fill(this._set, this.no_entry_value);
        }
    }

    public TCharHash(int initialCapacity) {
        super(initialCapacity);
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
        if (this.no_entry_value != '\u0000') {
            Arrays.fill(this._set, this.no_entry_value);
        }
    }

    public TCharHash(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
        if (this.no_entry_value != '\u0000') {
            Arrays.fill(this._set, this.no_entry_value);
        }
    }

    public TCharHash(int initialCapacity, float loadFactor, char no_entry_value) {
        super(initialCapacity, loadFactor);
        this.no_entry_value = no_entry_value;
        if (no_entry_value != '\u0000') {
            Arrays.fill(this._set, no_entry_value);
        }
    }

    public char getNoEntryValue() {
        return this.no_entry_value;
    }

    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._set = new char[capacity];
        return capacity;
    }

    public boolean contains(char val) {
        return this.index(val) >= 0;
    }

    public boolean forEach(TCharProcedure procedure) {
        byte[] states = this._states;
        char[] set = this._set;
        int i = set.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(set[i])) continue;
            return false;
        }
        return true;
    }

    protected void removeAt(int index) {
        this._set[index] = this.no_entry_value;
        super.removeAt(index);
    }

    protected int index(char val) {
        byte[] states = this._states;
        char[] set = this._set;
        int length = states.length;
        int hash = HashFunctions.hash(val) & Integer.MAX_VALUE;
        int index = hash % length;
        byte state = states[index];
        if (state == 0) {
            return -1;
        }
        if (state == 1 && set[index] == val) {
            return index;
        }
        return this.indexRehashed(val, index, hash, state);
    }

    int indexRehashed(char key, int index, int hash, byte state) {
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

    protected int insertKey(char val) {
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

    int insertKeyRehash(char val, int index, int hash, byte state) {
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

    void insertKeyAt(int index, char val) {
        this._set[index] = val;
        this._states[index] = 1;
    }
}


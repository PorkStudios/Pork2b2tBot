/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.hash;

import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.strategy.HashingStrategy;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TCustomObjectHash<T>
extends TObjectHash<T> {
    static final long serialVersionUID = 8766048185963756400L;
    protected HashingStrategy<? super T> strategy;

    public TCustomObjectHash() {
    }

    public TCustomObjectHash(HashingStrategy<? super T> strategy) {
        this.strategy = strategy;
    }

    public TCustomObjectHash(HashingStrategy<? super T> strategy, int initialCapacity) {
        super(initialCapacity);
        this.strategy = strategy;
    }

    public TCustomObjectHash(HashingStrategy<? super T> strategy, int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.strategy = strategy;
    }

    @Override
    protected int hash(Object obj) {
        return this.strategy.computeHashCode(obj);
    }

    @Override
    protected boolean equals(Object one, Object two) {
        return two != REMOVED && this.strategy.equals(one, two);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        super.writeExternal(out);
        out.writeObject(this.strategy);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        this.strategy = (HashingStrategy)in.readObject();
    }
}


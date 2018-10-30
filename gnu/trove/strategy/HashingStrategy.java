/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.strategy;

import java.io.Serializable;

public interface HashingStrategy<T>
extends Serializable {
    public static final long serialVersionUID = 5674097166776615540L;

    public int computeHashCode(T var1);

    public boolean equals(T var1, T var2);
}


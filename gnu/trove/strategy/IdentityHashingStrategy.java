/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.strategy;

import gnu.trove.strategy.HashingStrategy;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class IdentityHashingStrategy<K>
implements HashingStrategy<K> {
    static final long serialVersionUID = -5188534454583764904L;
    public static final IdentityHashingStrategy<Object> INSTANCE = new IdentityHashingStrategy<K>();

    @Override
    public int computeHashCode(K object) {
        return System.identityHashCode(object);
    }

    @Override
    public boolean equals(K o1, K o2) {
        return o1 == o2;
    }
}


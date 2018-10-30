/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableCharList;
import gnu.trove.list.TCharList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessCharList
extends TUnmodifiableCharList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessCharList(TCharList list) {
        super(list);
    }

    public TCharList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessCharList(this.list.subList(fromIndex, toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableCharList(this.list);
    }
}


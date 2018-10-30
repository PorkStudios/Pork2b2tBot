/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleList;
import gnu.trove.list.TDoubleList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessDoubleList
extends TUnmodifiableDoubleList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessDoubleList(TDoubleList list) {
        super(list);
    }

    public TDoubleList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessDoubleList(this.list.subList(fromIndex, toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableDoubleList(this.list);
    }
}


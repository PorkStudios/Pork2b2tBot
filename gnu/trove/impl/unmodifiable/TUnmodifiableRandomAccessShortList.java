/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableShortList;
import gnu.trove.list.TShortList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessShortList
extends TUnmodifiableShortList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessShortList(TShortList list) {
        super(list);
    }

    public TShortList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessShortList(this.list.subList(fromIndex, toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableShortList(this.list);
    }
}


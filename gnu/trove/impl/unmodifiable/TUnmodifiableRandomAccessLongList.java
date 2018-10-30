/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.impl.unmodifiable.TUnmodifiableLongList;
import gnu.trove.list.TLongList;
import java.util.RandomAccess;

public class TUnmodifiableRandomAccessLongList
extends TUnmodifiableLongList
implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;

    public TUnmodifiableRandomAccessLongList(TLongList list) {
        super(list);
    }

    public TLongList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableRandomAccessLongList(this.list.subList(fromIndex, toIndex));
    }

    private Object writeReplace() {
        return new TUnmodifiableLongList(this.list);
    }
}


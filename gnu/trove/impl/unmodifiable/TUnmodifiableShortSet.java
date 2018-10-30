/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TShortCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCollection;
import gnu.trove.set.TShortSet;
import java.io.Serializable;

public class TUnmodifiableShortSet
extends TUnmodifiableShortCollection
implements TShortSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableShortSet(TShortSet s) {
        super(s);
    }

    public boolean equals(Object o) {
        return o == this || this.c.equals(o);
    }

    public int hashCode() {
        return this.c.hashCode();
    }
}


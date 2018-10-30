/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TDoubleCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;

public class TUnmodifiableDoubleSet
extends TUnmodifiableDoubleCollection
implements TDoubleSet,
Serializable {
    private static final long serialVersionUID = -9215047833775013803L;

    public TUnmodifiableDoubleSet(TDoubleSet s) {
        super(s);
    }

    public boolean equals(Object o) {
        return o == this || this.c.equals(o);
    }

    public int hashCode() {
        return this.c.hashCode();
    }
}


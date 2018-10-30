/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.impl.sync.TSynchronizedShortCollection;
import gnu.trove.set.TShortSet;

public class TSynchronizedShortSet
extends TSynchronizedShortCollection
implements TShortSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedShortSet(TShortSet s) {
        super(s);
    }

    public TSynchronizedShortSet(TShortSet s, Object mutex) {
        super(s, mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.equals(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.hashCode();
        }
    }
}


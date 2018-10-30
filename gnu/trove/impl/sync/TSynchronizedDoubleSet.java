/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.set.TDoubleSet;

public class TSynchronizedDoubleSet
extends TSynchronizedDoubleCollection
implements TDoubleSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedDoubleSet(TDoubleSet s) {
        super(s);
    }

    public TSynchronizedDoubleSet(TDoubleSet s, Object mutex) {
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


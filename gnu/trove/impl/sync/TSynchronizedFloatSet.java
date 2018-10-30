/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.set.TFloatSet;

public class TSynchronizedFloatSet
extends TSynchronizedFloatCollection
implements TFloatSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedFloatSet(TFloatSet s) {
        super(s);
    }

    public TSynchronizedFloatSet(TFloatSet s, Object mutex) {
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


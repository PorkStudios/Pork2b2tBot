/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.impl.sync.TSynchronizedByteCollection;
import gnu.trove.set.TByteSet;

public class TSynchronizedByteSet
extends TSynchronizedByteCollection
implements TByteSet {
    private static final long serialVersionUID = 487447009682186044L;

    public TSynchronizedByteSet(TByteSet s) {
        super(s);
    }

    public TSynchronizedByteSet(TByteSet s, Object mutex) {
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


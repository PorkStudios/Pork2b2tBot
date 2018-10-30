/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedCharList;
import gnu.trove.list.TCharList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessCharList
extends TSynchronizedCharList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessCharList(TCharList list) {
        super(list);
    }

    public TSynchronizedRandomAccessCharList(TCharList list, Object mutex) {
        super(list, mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TCharList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedRandomAccessCharList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedCharList(this.list);
    }
}


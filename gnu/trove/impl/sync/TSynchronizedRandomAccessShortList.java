/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedShortList;
import gnu.trove.list.TShortList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessShortList
extends TSynchronizedShortList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessShortList(TShortList list) {
        super(list);
    }

    public TSynchronizedRandomAccessShortList(TShortList list, Object mutex) {
        super(list, mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TShortList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedRandomAccessShortList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedShortList(this.list);
    }
}


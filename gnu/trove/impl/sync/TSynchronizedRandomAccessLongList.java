/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedLongList;
import gnu.trove.list.TLongList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessLongList
extends TSynchronizedLongList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessLongList(TLongList list) {
        super(list);
    }

    public TSynchronizedRandomAccessLongList(TLongList list, Object mutex) {
        super(list, mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TLongList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedRandomAccessLongList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedLongList(this.list);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedByteList;
import gnu.trove.list.TByteList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessByteList
extends TSynchronizedByteList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessByteList(TByteList list) {
        super(list);
    }

    public TSynchronizedRandomAccessByteList(TByteList list, Object mutex) {
        super(list, mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TByteList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedRandomAccessByteList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedByteList(this.list);
    }
}


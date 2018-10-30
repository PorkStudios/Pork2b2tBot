/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedDoubleList;
import gnu.trove.list.TDoubleList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessDoubleList
extends TSynchronizedDoubleList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessDoubleList(TDoubleList list) {
        super(list);
    }

    public TSynchronizedRandomAccessDoubleList(TDoubleList list, Object mutex) {
        super(list, mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TDoubleList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedRandomAccessDoubleList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedDoubleList(this.list);
    }
}


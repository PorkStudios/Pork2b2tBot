/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.impl.sync.TSynchronizedIntList;
import gnu.trove.list.TIntList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessIntList
extends TSynchronizedIntList
implements RandomAccess {
    static final long serialVersionUID = 1530674583602358482L;

    public TSynchronizedRandomAccessIntList(TIntList list) {
        super(list);
    }

    public TSynchronizedRandomAccessIntList(TIntList list, Object mutex) {
        super(list, mutex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TIntList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedRandomAccessIntList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    private Object writeReplace() {
        return new TSynchronizedIntList(this.list);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.queue;

import gnu.trove.TLongCollection;

public interface TLongQueue
extends TLongCollection {
    public long element();

    public boolean offer(long var1);

    public long peek();

    public long poll();
}


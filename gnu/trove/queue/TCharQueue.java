/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.queue;

import gnu.trove.TCharCollection;

public interface TCharQueue
extends TCharCollection {
    public char element();

    public boolean offer(char var1);

    public char peek();

    public char poll();
}


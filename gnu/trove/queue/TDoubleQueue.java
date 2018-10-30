/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.queue;

import gnu.trove.TDoubleCollection;

public interface TDoubleQueue
extends TDoubleCollection {
    public double element();

    public boolean offer(double var1);

    public double peek();

    public double poll();
}


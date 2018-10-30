/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import java.util.Comparator;

@FunctionalInterface
public interface IntComparator
extends Comparator<Integer> {
    @Override
    public int compare(int var1, int var2);

    @Deprecated
    @Override
    default public int compare(Integer ok1, Integer ok2) {
        return this.compare((int)ok1, (int)ok2);
    }
}


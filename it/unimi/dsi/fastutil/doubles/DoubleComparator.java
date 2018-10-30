/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import java.util.Comparator;

@FunctionalInterface
public interface DoubleComparator
extends Comparator<Double> {
    @Override
    public int compare(double var1, double var3);

    @Deprecated
    @Override
    default public int compare(Double ok1, Double ok2) {
        return this.compare((double)ok1, (double)ok2);
    }
}


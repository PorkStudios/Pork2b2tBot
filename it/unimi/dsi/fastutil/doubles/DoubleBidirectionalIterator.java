/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface DoubleBidirectionalIterator
extends DoubleIterator,
ObjectBidirectionalIterator<Double> {
    public double previousDouble();

    @Deprecated
    @Override
    default public Double previous() {
        return this.previousDouble();
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousDouble();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return DoubleIterator.super.skip(n);
    }
}


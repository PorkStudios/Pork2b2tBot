/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public interface ObjectBidirectionalIterator<K>
extends ObjectIterator<K>,
BidirectionalIterator<K> {
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previous();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return ObjectIterator.super.skip(n);
    }
}


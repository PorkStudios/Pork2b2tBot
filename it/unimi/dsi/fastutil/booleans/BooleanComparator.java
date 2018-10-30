/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import java.util.Comparator;

@FunctionalInterface
public interface BooleanComparator
extends Comparator<Boolean> {
    @Override
    public int compare(boolean var1, boolean var2);

    @Deprecated
    @Override
    default public int compare(Boolean ok1, Boolean ok2) {
        return this.compare((boolean)ok1, (boolean)ok2);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Table;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
public interface RowSortedTable<R, C, V>
extends Table<R, C, V> {
    @Override
    public SortedSet<R> rowKeySet();

    @Override
    public SortedMap<R, Map<C, V>> rowMap();
}


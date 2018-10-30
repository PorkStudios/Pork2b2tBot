/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.FilteredMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

@GwtCompatible
interface FilteredSetMultimap<K, V>
extends FilteredMultimap<K, V>,
SetMultimap<K, V> {
    @Override
    public SetMultimap<K, V> unfiltered();
}


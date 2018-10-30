/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractSetMultimap;
import java.util.Collection;
import java.util.Map;

@GwtCompatible(emulated=true)
abstract class LinkedHashMultimapGwtSerializationDependencies<K, V>
extends AbstractSetMultimap<K, V> {
    LinkedHashMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
        super(map);
    }
}


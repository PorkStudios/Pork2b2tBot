/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Set;

public interface ObjectSet<K>
extends ObjectCollection<K>,
Set<K> {
    @Override
    public ObjectIterator<K> iterator();
}


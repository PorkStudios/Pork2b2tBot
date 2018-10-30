/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.Iterator;

public interface ObjectCollection<K>
extends Collection<K>,
ObjectIterable<K> {
    @Override
    public ObjectIterator<K> iterator();
}


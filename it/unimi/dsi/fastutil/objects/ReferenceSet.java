/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Iterator;
import java.util.Set;

public interface ReferenceSet<K>
extends ReferenceCollection<K>,
Set<K> {
    @Override
    public ObjectIterator<K> iterator();
}


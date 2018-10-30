/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;

public interface ObjectIterable<K>
extends Iterable<K> {
    @Override
    public ObjectIterator<K> iterator();
}


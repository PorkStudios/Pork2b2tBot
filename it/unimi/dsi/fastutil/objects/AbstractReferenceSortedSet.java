/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.util.Iterator;

public abstract class AbstractReferenceSortedSet<K>
extends AbstractReferenceSet<K>
implements ReferenceSortedSet<K> {
    protected AbstractReferenceSortedSet() {
    }

    @Override
    public abstract ObjectBidirectionalIterator<K> iterator();
}


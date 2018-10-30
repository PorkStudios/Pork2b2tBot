/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;

public interface ObjectBigList<K>
extends BigList<K>,
ObjectCollection<K>,
Comparable<BigList<? extends K>> {
    @Override
    public ObjectBigListIterator<K> iterator();

    @Override
    public ObjectBigListIterator<K> listIterator();

    @Override
    public ObjectBigListIterator<K> listIterator(long var1);

    @Override
    public ObjectBigList<K> subList(long var1, long var3);

    public void getElements(long var1, Object[][] var3, long var4, long var6);

    public void removeElements(long var1, long var3);

    public void addElements(long var1, K[][] var3);

    public void addElements(long var1, K[][] var3, long var4, long var6);
}


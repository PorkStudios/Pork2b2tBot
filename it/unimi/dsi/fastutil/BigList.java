/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.Size64;
import java.util.Collection;

public interface BigList<K>
extends Collection<K>,
Size64 {
    public K get(long var1);

    public K remove(long var1);

    public K set(long var1, K var3);

    public void add(long var1, K var3);

    public void size(long var1);

    public boolean addAll(long var1, Collection<? extends K> var3);

    public long indexOf(Object var1);

    public long lastIndexOf(Object var1);

    public BigListIterator<K> listIterator();

    public BigListIterator<K> listIterator(long var1);

    public BigList<K> subList(long var1, long var3);
}


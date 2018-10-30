/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class AbstractObjectCollection<K>
extends AbstractCollection<K>
implements ObjectCollection<K> {
    protected AbstractObjectCollection() {
    }

    @Override
    public abstract ObjectIterator<K> iterator();

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        Iterator i = this.iterator();
        int n = this.size();
        boolean first = true;
        s.append("{");
        while (n-- != 0) {
            if (first) {
                first = false;
            } else {
                s.append(", ");
            }
            E k = i.next();
            if (this == k) {
                s.append("(this collection)");
                continue;
            }
            s.append(String.valueOf(k));
        }
        s.append("}");
        return s.toString();
    }
}


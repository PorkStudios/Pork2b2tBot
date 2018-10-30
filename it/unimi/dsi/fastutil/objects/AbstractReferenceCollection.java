/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class AbstractReferenceCollection<K>
extends AbstractCollection<K>
implements ReferenceCollection<K> {
    protected AbstractReferenceCollection() {
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


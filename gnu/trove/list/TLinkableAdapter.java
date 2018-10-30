/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list;

import gnu.trove.list.TLinkable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TLinkableAdapter<T extends TLinkable>
implements TLinkable<T> {
    private volatile T next;
    private volatile T prev;

    @Override
    public T getNext() {
        return this.next;
    }

    @Override
    public void setNext(T next) {
        this.next = next;
    }

    @Override
    public T getPrevious() {
        return this.prev;
    }

    @Override
    public void setPrevious(T prev) {
        this.prev = prev;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list;

import java.io.Serializable;

public interface TLinkable<T extends TLinkable>
extends Serializable {
    public static final long serialVersionUID = 997545054865482562L;

    public T getNext();

    public T getPrevious();

    public void setNext(T var1);

    public void setPrevious(T var1);
}


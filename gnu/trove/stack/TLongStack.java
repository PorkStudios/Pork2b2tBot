/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.stack;

public interface TLongStack {
    public long getNoEntryValue();

    public void push(long var1);

    public long pop();

    public long peek();

    public int size();

    public void clear();

    public long[] toArray();

    public void toArray(long[] var1);
}


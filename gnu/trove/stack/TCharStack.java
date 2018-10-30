/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.stack;

public interface TCharStack {
    public char getNoEntryValue();

    public void push(char var1);

    public char pop();

    public char peek();

    public int size();

    public void clear();

    public char[] toArray();

    public void toArray(char[] var1);
}


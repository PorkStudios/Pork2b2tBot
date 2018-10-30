/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.stack;

public interface TDoubleStack {
    public double getNoEntryValue();

    public void push(double var1);

    public double pop();

    public double peek();

    public int size();

    public void clear();

    public double[] toArray();

    public void toArray(double[] var1);
}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

public interface IntHash {

    public static interface Strategy {
        public int hashCode(int var1);

        public boolean equals(int var1, int var2);
    }

}


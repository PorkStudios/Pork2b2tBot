/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

public interface ShortHash {

    public static interface Strategy {
        public int hashCode(short var1);

        public boolean equals(short var1, short var2);
    }

}


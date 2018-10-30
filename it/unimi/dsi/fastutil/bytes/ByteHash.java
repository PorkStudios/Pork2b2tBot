/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

public interface ByteHash {

    public static interface Strategy {
        public int hashCode(byte var1);

        public boolean equals(byte var1, byte var2);
    }

}


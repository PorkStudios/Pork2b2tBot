/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

public interface HashingStrategy<T> {
    public static final HashingStrategy JAVA_HASHER = new HashingStrategy(){

        public int hashCode(Object obj) {
            return obj != null ? obj.hashCode() : 0;
        }

        public boolean equals(Object a, Object b) {
            return a == b || a != null && a.equals(b);
        }
    };

    public int hashCode(T var1);

    public boolean equals(T var1, T var2);

}


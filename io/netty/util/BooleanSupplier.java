/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

public interface BooleanSupplier {
    public static final BooleanSupplier FALSE_SUPPLIER = new BooleanSupplier(){

        @Override
        public boolean get() {
            return false;
        }
    };
    public static final BooleanSupplier TRUE_SUPPLIER = new BooleanSupplier(){

        @Override
        public boolean get() {
            return true;
        }
    };

    public boolean get() throws Exception;

}


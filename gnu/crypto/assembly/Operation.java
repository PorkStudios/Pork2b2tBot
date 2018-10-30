/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

public final class Operation {
    public static final Operation PRE_PROCESSING = new Operation(1);
    public static final Operation POST_PROCESSING = new Operation(2);
    private int value;

    public final String toString() {
        return this == PRE_PROCESSING ? "pre-processing" : "post-processing";
    }

    private Operation(int value) {
        this.value = value;
    }
}


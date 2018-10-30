/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.procedure.array;

import gnu.trove.procedure.TObjectProcedure;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ToObjectArrayProceedure<T>
implements TObjectProcedure<T> {
    private final T[] target;
    private int pos = 0;

    public ToObjectArrayProceedure(T[] target) {
        this.target = target;
    }

    @Override
    public final boolean execute(T value) {
        this.target[this.pos++] = value;
        return true;
    }
}


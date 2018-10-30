/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.io.Serializable;

public abstract class AbstractInt2IntFunction
implements Int2IntFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected int defRetValue;

    protected AbstractInt2IntFunction() {
    }

    @Override
    public void defaultReturnValue(int rv) {
        this.defRetValue = rv;
    }

    @Override
    public int defaultReturnValue() {
        return this.defRetValue;
    }
}


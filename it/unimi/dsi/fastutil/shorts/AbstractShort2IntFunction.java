/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.Short2IntFunction;
import java.io.Serializable;

public abstract class AbstractShort2IntFunction
implements Short2IntFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected int defRetValue;

    protected AbstractShort2IntFunction() {
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


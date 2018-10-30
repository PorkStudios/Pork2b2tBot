/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.Short2FloatFunction;
import java.io.Serializable;

public abstract class AbstractShort2FloatFunction
implements Short2FloatFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected float defRetValue;

    protected AbstractShort2FloatFunction() {
    }

    @Override
    public void defaultReturnValue(float rv) {
        this.defRetValue = rv;
    }

    @Override
    public float defaultReturnValue() {
        return this.defRetValue;
    }
}


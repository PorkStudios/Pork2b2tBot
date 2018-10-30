/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.Float2BooleanFunction;
import java.io.Serializable;

public abstract class AbstractFloat2BooleanFunction
implements Float2BooleanFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected boolean defRetValue;

    protected AbstractFloat2BooleanFunction() {
    }

    @Override
    public void defaultReturnValue(boolean rv) {
        this.defRetValue = rv;
    }

    @Override
    public boolean defaultReturnValue() {
        return this.defRetValue;
    }
}


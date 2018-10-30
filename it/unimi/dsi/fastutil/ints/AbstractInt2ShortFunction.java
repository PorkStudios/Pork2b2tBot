/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2ShortFunction;
import java.io.Serializable;

public abstract class AbstractInt2ShortFunction
implements Int2ShortFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected short defRetValue;

    protected AbstractInt2ShortFunction() {
    }

    @Override
    public void defaultReturnValue(short rv) {
        this.defRetValue = rv;
    }

    @Override
    public short defaultReturnValue() {
        return this.defRetValue;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import java.io.Serializable;

public abstract class AbstractChar2BooleanFunction
implements Char2BooleanFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected boolean defRetValue;

    protected AbstractChar2BooleanFunction() {
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


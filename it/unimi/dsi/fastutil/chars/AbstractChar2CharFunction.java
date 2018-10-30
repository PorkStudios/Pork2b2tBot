/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import java.io.Serializable;

public abstract class AbstractChar2CharFunction
implements Char2CharFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected char defRetValue;

    protected AbstractChar2CharFunction() {
    }

    @Override
    public void defaultReturnValue(char rv) {
        this.defRetValue = rv;
    }

    @Override
    public char defaultReturnValue() {
        return this.defRetValue;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import java.io.Serializable;

public abstract class AbstractChar2DoubleFunction
implements Char2DoubleFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected double defRetValue;

    protected AbstractChar2DoubleFunction() {
    }

    @Override
    public void defaultReturnValue(double rv) {
        this.defRetValue = rv;
    }

    @Override
    public double defaultReturnValue() {
        return this.defRetValue;
    }
}


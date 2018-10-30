/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import java.io.Serializable;

public abstract class AbstractReference2IntFunction<K>
implements Reference2IntFunction<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected int defRetValue;

    protected AbstractReference2IntFunction() {
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


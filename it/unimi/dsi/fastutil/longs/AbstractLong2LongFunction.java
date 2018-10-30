/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import java.io.Serializable;

public abstract class AbstractLong2LongFunction
implements Long2LongFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected long defRetValue;

    protected AbstractLong2LongFunction() {
    }

    @Override
    public void defaultReturnValue(long rv) {
        this.defRetValue = rv;
    }

    @Override
    public long defaultReturnValue() {
        return this.defRetValue;
    }
}


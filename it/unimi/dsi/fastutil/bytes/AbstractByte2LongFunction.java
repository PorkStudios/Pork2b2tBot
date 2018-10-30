/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2LongFunction;
import java.io.Serializable;

public abstract class AbstractByte2LongFunction
implements Byte2LongFunction,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected long defRetValue;

    protected AbstractByte2LongFunction() {
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


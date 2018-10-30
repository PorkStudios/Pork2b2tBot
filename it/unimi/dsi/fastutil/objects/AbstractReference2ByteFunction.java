/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import java.io.Serializable;

public abstract class AbstractReference2ByteFunction<K>
implements Reference2ByteFunction<K>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected byte defRetValue;

    protected AbstractReference2ByteFunction() {
    }

    @Override
    public void defaultReturnValue(byte rv) {
        this.defRetValue = rv;
    }

    @Override
    public byte defaultReturnValue() {
        return this.defRetValue;
    }
}


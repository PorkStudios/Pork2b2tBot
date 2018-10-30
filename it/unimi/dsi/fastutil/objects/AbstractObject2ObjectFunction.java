/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import java.io.Serializable;

public abstract class AbstractObject2ObjectFunction<K, V>
implements Object2ObjectFunction<K, V>,
Serializable {
    private static final long serialVersionUID = -4940583368468432370L;
    protected V defRetValue;

    protected AbstractObject2ObjectFunction() {
    }

    @Override
    public void defaultReturnValue(V rv) {
        this.defRetValue = rv;
    }

    @Override
    public V defaultReturnValue() {
        return this.defRetValue;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.FinalizableReference;
import com.google.common.base.FinalizableReferenceQueue;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

@GwtIncompatible
public abstract class FinalizableSoftReference<T>
extends SoftReference<T>
implements FinalizableReference {
    protected FinalizableSoftReference(T referent, FinalizableReferenceQueue queue) {
        super(referent, queue.queue);
        queue.cleanUp();
    }
}


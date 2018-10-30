/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

@GwtIncompatible
public final class Defaults {
    private static final Double DOUBLE_DEFAULT = 0.0;
    private static final Float FLOAT_DEFAULT = Float.valueOf(0.0f);

    private Defaults() {
    }

    @Nullable
    public static <T> T defaultValue(Class<T> type) {
        Preconditions.checkNotNull(type);
        if (type == Boolean.TYPE) {
            return (T)Boolean.FALSE;
        }
        if (type == Character.TYPE) {
            return (T)Character.valueOf('\u0000');
        }
        if (type == Byte.TYPE) {
            return (byte)0;
        }
        if (type == Short.TYPE) {
            return (short)0;
        }
        if (type == Integer.TYPE) {
            return 0;
        }
        if (type == Long.TYPE) {
            return 0L;
        }
        if (type == Float.TYPE) {
            return (T)FLOAT_DEFAULT;
        }
        if (type == Double.TYPE) {
            return (T)DOUBLE_DEFAULT;
        }
        return null;
    }
}


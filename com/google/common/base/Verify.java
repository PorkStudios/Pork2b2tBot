/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.VerifyException;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class Verify {
    public static void verify(boolean expression) {
        if (!expression) {
            throw new VerifyException();
        }
    }

    public static /* varargs */ void verify(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object ... errorMessageArgs) {
        if (!expression) {
            throw new VerifyException(Preconditions.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    @CanIgnoreReturnValue
    public static <T> T verifyNotNull(@Nullable T reference) {
        return Verify.verifyNotNull(reference, "expected a non-null reference", new Object[0]);
    }

    @CanIgnoreReturnValue
    public static /* varargs */ <T> T verifyNotNull(@Nullable T reference, @Nullable String errorMessageTemplate, @Nullable Object ... errorMessageArgs) {
        Verify.verify(reference != null, errorMessageTemplate, errorMessageArgs);
        return reference;
    }

    private Verify() {
    }
}


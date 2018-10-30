/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import java.lang.reflect.AccessibleObject;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static Throwable trySetAccessible(AccessibleObject object) {
        try {
            object.setAccessible(true);
            return null;
        }
        catch (SecurityException e) {
            return e;
        }
        catch (RuntimeException e) {
            return ReflectionUtil.handleInaccessibleObjectException(e);
        }
    }

    private static RuntimeException handleInaccessibleObjectException(RuntimeException e) {
        if ("java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
            return e;
        }
        throw e;
    }
}


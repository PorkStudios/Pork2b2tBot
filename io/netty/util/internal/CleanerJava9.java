/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.internal.Cleaner;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import sun.misc.Unsafe;

final class CleanerJava9
implements Cleaner {
    private static final InternalLogger logger;
    private static final Method INVOKE_CLEANER;

    CleanerJava9() {
    }

    static boolean isSupported() {
        return INVOKE_CLEANER != null;
    }

    @Override
    public void freeDirectBuffer(ByteBuffer buffer) {
        try {
            INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, buffer);
        }
        catch (Throwable cause) {
            PlatformDependent0.throwException(cause);
        }
    }

    static {
        Throwable error;
        Method method;
        logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
        if (PlatformDependent0.hasUnsafe()) {
            Object maybeInvokeMethod;
            ByteBuffer buffer = ByteBuffer.allocateDirect(1);
            try {
                Method m = PlatformDependent0.UNSAFE.getClass().getDeclaredMethod("invokeCleaner", ByteBuffer.class);
                m.invoke(PlatformDependent0.UNSAFE, buffer);
                maybeInvokeMethod = m;
            }
            catch (NoSuchMethodException e) {
                maybeInvokeMethod = e;
            }
            catch (InvocationTargetException e) {
                maybeInvokeMethod = e;
            }
            catch (IllegalAccessException e) {
                maybeInvokeMethod = e;
            }
            if (maybeInvokeMethod instanceof Throwable) {
                method = null;
                error = (Throwable)maybeInvokeMethod;
            } else {
                method = (Method)maybeInvokeMethod;
                error = null;
            }
        } else {
            method = null;
            error = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
        }
        if (error == null) {
            logger.debug("java.nio.ByteBuffer.cleaner(): available");
        } else {
            logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
        }
        INVOKE_CLEANER = method;
    }
}


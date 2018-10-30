/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;

public final class PromiseNotificationUtil {
    private PromiseNotificationUtil() {
    }

    public static void tryCancel(Promise<?> p, InternalLogger logger) {
        if (!p.cancel(false) && logger != null) {
            Throwable err = p.cause();
            if (err == null) {
                logger.warn("Failed to cancel promise because it has succeeded already: {}", (Object)p);
            } else {
                logger.warn("Failed to cancel promise because it has failed already: {}, unnotified cause:", (Object)p, (Object)err);
            }
        }
    }

    public static <V> void trySuccess(Promise<? super V> p, V result, InternalLogger logger) {
        if (!p.trySuccess(result) && logger != null) {
            Throwable err = p.cause();
            if (err == null) {
                logger.warn("Failed to mark a promise as success because it has succeeded already: {}", (Object)p);
            } else {
                logger.warn("Failed to mark a promise as success because it has failed already: {}, unnotified cause:", (Object)p, (Object)err);
            }
        }
    }

    public static void tryFailure(Promise<?> p, Throwable cause, InternalLogger logger) {
        if (!p.tryFailure(cause) && logger != null) {
            Throwable err = p.cause();
            if (err == null) {
                logger.warn("Failed to mark a promise as failure because it has succeeded already: {}", (Object)p, (Object)cause);
            } else {
                logger.warn("Failed to mark a promise as failure because it has failed already: {}, unnotified cause: {}", p, ThrowableUtil.stackTraceToString(err), cause);
            }
        }
    }
}


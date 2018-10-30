/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class ThrowableUtil {
    private static final Method addSupressedMethod = ThrowableUtil.getAddSuppressed();

    private static Method getAddSuppressed() {
        if (PlatformDependent.javaVersion() < 7) {
            return null;
        }
        try {
            return Throwable.class.getDeclaredMethod("addSuppressed", Throwable.class);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private ThrowableUtil() {
    }

    public static <T extends Throwable> T unknownStackTrace(T cause, Class<?> clazz, String method) {
        cause.setStackTrace(new StackTraceElement[]{new StackTraceElement(clazz.getName(), method, null, -1)});
        return cause;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String stackTraceToString(Throwable cause) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        cause.printStackTrace(pout);
        pout.flush();
        try {
            String string = new String(out.toByteArray());
            return string;
        }
        finally {
            try {
                out.close();
            }
            catch (IOException iOException) {}
        }
    }

    public static boolean haveSuppressed() {
        return addSupressedMethod != null;
    }

    public static void addSuppressed(Throwable target, Throwable suppressed) {
        if (!ThrowableUtil.haveSuppressed()) {
            return;
        }
        try {
            addSupressedMethod.invoke(target, suppressed);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addSuppressedAndClear(Throwable target, List<Throwable> suppressed) {
        ThrowableUtil.addSuppressed(target, suppressed);
        suppressed.clear();
    }

    public static void addSuppressed(Throwable target, List<Throwable> suppressed) {
        for (Throwable t : suppressed) {
            ThrowableUtil.addSuppressed(target, t);
        }
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(emulated=true)
public final class Throwables {
    @GwtIncompatible
    private static final String JAVA_LANG_ACCESS_CLASSNAME = "sun.misc.JavaLangAccess";
    @GwtIncompatible
    @VisibleForTesting
    static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
    @Nullable
    @GwtIncompatible
    private static final Object jla = Throwables.getJLA();
    @Nullable
    @GwtIncompatible
    private static final Method getStackTraceElementMethod = jla == null ? null : Throwables.getGetMethod();
    @Nullable
    @GwtIncompatible
    private static final Method getStackTraceDepthMethod = jla == null ? null : Throwables.getSizeMethod();

    private Throwables() {
    }

    @GwtIncompatible
    public static <X extends Throwable> void throwIfInstanceOf(Throwable throwable, Class<X> declaredType) throws Throwable {
        Preconditions.checkNotNull(throwable);
        if (declaredType.isInstance(throwable)) {
            throw (Throwable)declaredType.cast(throwable);
        }
    }

    @Deprecated
    @GwtIncompatible
    public static <X extends Throwable> void propagateIfInstanceOf(@Nullable Throwable throwable, Class<X> declaredType) throws Throwable {
        if (throwable != null) {
            Throwables.throwIfInstanceOf(throwable, declaredType);
        }
    }

    public static void throwIfUnchecked(Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
    }

    @Deprecated
    @GwtIncompatible
    public static void propagateIfPossible(@Nullable Throwable throwable) {
        if (throwable != null) {
            Throwables.throwIfUnchecked(throwable);
        }
    }

    @GwtIncompatible
    public static <X extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X> declaredType) throws Throwable {
        Throwables.propagateIfInstanceOf(throwable, declaredType);
        Throwables.propagateIfPossible(throwable);
    }

    @GwtIncompatible
    public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X1> declaredType1, Class<X2> declaredType2) throws Throwable {
        Preconditions.checkNotNull(declaredType2);
        Throwables.propagateIfInstanceOf(throwable, declaredType1);
        Throwables.propagateIfPossible(throwable, declaredType2);
    }

    @Deprecated
    @CanIgnoreReturnValue
    @GwtIncompatible
    public static RuntimeException propagate(Throwable throwable) {
        Throwables.throwIfUnchecked(throwable);
        throw new RuntimeException(throwable);
    }

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause;
        Throwable slowPointer = throwable;
        boolean advanceSlowPointer = false;
        while ((cause = throwable.getCause()) != null) {
            throwable = cause;
            if (throwable == slowPointer) {
                throw new IllegalArgumentException("Loop in causal chain detected @ " + throwable);
            }
            if (advanceSlowPointer) {
                slowPointer = slowPointer.getCause();
            }
            advanceSlowPointer = !advanceSlowPointer;
        }
        return throwable;
    }

    @Beta
    public static List<Throwable> getCausalChain(Throwable throwable) {
        Throwable cause;
        Preconditions.checkNotNull(throwable);
        ArrayList<Throwable> causes = new ArrayList<Throwable>(4);
        causes.add(throwable);
        Throwable slowPointer = throwable;
        boolean advanceSlowPointer = false;
        while ((cause = throwable.getCause()) != null) {
            throwable = cause;
            causes.add(throwable);
            if (throwable == slowPointer) {
                throw new IllegalArgumentException("Loop in causal chain detected @ " + throwable);
            }
            if (advanceSlowPointer) {
                slowPointer = slowPointer.getCause();
            }
            advanceSlowPointer = !advanceSlowPointer;
        }
        return Collections.unmodifiableList(causes);
    }

    @Beta
    @GwtIncompatible
    public static <X extends Throwable> X getCauseAs(Throwable throwable, Class<X> expectedCauseType) {
        try {
            return (X)((Throwable)expectedCauseType.cast(throwable.getCause()));
        }
        catch (ClassCastException e) {
            e.initCause(throwable);
            throw e;
        }
    }

    @GwtIncompatible
    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    @Beta
    @GwtIncompatible
    public static List<StackTraceElement> lazyStackTrace(Throwable throwable) {
        return Throwables.lazyStackTraceIsLazy() ? Throwables.jlaStackTrace(throwable) : Collections.unmodifiableList(Arrays.asList(throwable.getStackTrace()));
    }

    @Beta
    @GwtIncompatible
    public static boolean lazyStackTraceIsLazy() {
        return getStackTraceElementMethod != null && getStackTraceDepthMethod != null;
    }

    @GwtIncompatible
    private static List<StackTraceElement> jlaStackTrace(final Throwable t) {
        Preconditions.checkNotNull(t);
        return new AbstractList<StackTraceElement>(){

            @Override
            public StackTraceElement get(int n) {
                return (StackTraceElement)Throwables.invokeAccessibleNonThrowingMethod(getStackTraceElementMethod, jla, new Object[]{t, n});
            }

            @Override
            public int size() {
                return (Integer)Throwables.invokeAccessibleNonThrowingMethod(getStackTraceDepthMethod, jla, new Object[]{t});
            }
        };
    }

    @GwtIncompatible
    private static /* varargs */ Object invokeAccessibleNonThrowingMethod(Method method, Object receiver, Object ... params) {
        try {
            return method.invoke(receiver, params);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e) {
            throw Throwables.propagate(e.getCause());
        }
    }

    @Nullable
    @GwtIncompatible
    private static Object getJLA() {
        try {
            Class<?> sharedSecrets = Class.forName(SHARED_SECRETS_CLASSNAME, false, null);
            Method langAccess = sharedSecrets.getMethod("getJavaLangAccess", new Class[0]);
            return langAccess.invoke(null, new Object[0]);
        }
        catch (ThreadDeath death) {
            throw death;
        }
        catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    @GwtIncompatible
    private static Method getGetMethod() {
        return Throwables.getJlaMethod("getStackTraceElement", Throwable.class, Integer.TYPE);
    }

    @Nullable
    @GwtIncompatible
    private static Method getSizeMethod() {
        return Throwables.getJlaMethod("getStackTraceDepth", Throwable.class);
    }

    @Nullable
    @GwtIncompatible
    private static /* varargs */ Method getJlaMethod(String name, Class<?> ... parameterTypes) throws ThreadDeath {
        try {
            return Class.forName(JAVA_LANG_ACCESS_CLASSNAME, false, null).getMethod(name, parameterTypes);
        }
        catch (ThreadDeath death) {
            throw death;
        }
        catch (Throwable t) {
            return null;
        }
    }

}


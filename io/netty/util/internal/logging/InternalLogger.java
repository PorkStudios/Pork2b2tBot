/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogLevel;

public interface InternalLogger {
    public String name();

    public boolean isTraceEnabled();

    public void trace(String var1);

    public void trace(String var1, Object var2);

    public void trace(String var1, Object var2, Object var3);

    public /* varargs */ void trace(String var1, Object ... var2);

    public void trace(String var1, Throwable var2);

    public void trace(Throwable var1);

    public boolean isDebugEnabled();

    public void debug(String var1);

    public void debug(String var1, Object var2);

    public void debug(String var1, Object var2, Object var3);

    public /* varargs */ void debug(String var1, Object ... var2);

    public void debug(String var1, Throwable var2);

    public void debug(Throwable var1);

    public boolean isInfoEnabled();

    public void info(String var1);

    public void info(String var1, Object var2);

    public void info(String var1, Object var2, Object var3);

    public /* varargs */ void info(String var1, Object ... var2);

    public void info(String var1, Throwable var2);

    public void info(Throwable var1);

    public boolean isWarnEnabled();

    public void warn(String var1);

    public void warn(String var1, Object var2);

    public /* varargs */ void warn(String var1, Object ... var2);

    public void warn(String var1, Object var2, Object var3);

    public void warn(String var1, Throwable var2);

    public void warn(Throwable var1);

    public boolean isErrorEnabled();

    public void error(String var1);

    public void error(String var1, Object var2);

    public void error(String var1, Object var2, Object var3);

    public /* varargs */ void error(String var1, Object ... var2);

    public void error(String var1, Throwable var2);

    public void error(Throwable var1);

    public boolean isEnabled(InternalLogLevel var1);

    public void log(InternalLogLevel var1, String var2);

    public void log(InternalLogLevel var1, String var2, Object var3);

    public void log(InternalLogLevel var1, String var2, Object var3, Object var4);

    public /* varargs */ void log(InternalLogLevel var1, String var2, Object ... var3);

    public void log(InternalLogLevel var1, String var2, Throwable var3);

    public void log(InternalLogLevel var1, Throwable var2);
}


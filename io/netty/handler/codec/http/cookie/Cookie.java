/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.cookie;

public interface Cookie
extends Comparable<Cookie> {
    public static final long UNDEFINED_MAX_AGE = Long.MIN_VALUE;

    public String name();

    public String value();

    public void setValue(String var1);

    public boolean wrap();

    public void setWrap(boolean var1);

    public String domain();

    public void setDomain(String var1);

    public String path();

    public void setPath(String var1);

    public long maxAge();

    public void setMaxAge(long var1);

    public boolean isSecure();

    public void setSecure(boolean var1);

    public boolean isHttpOnly();

    public void setHttpOnly(boolean var1);
}


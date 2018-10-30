/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import java.util.Set;

@Deprecated
public interface Cookie
extends io.netty.handler.codec.http.cookie.Cookie {
    @Deprecated
    public String getName();

    @Deprecated
    public String getValue();

    @Deprecated
    public String getDomain();

    @Deprecated
    public String getPath();

    @Deprecated
    public String getComment();

    @Deprecated
    public String comment();

    @Deprecated
    public void setComment(String var1);

    @Deprecated
    public long getMaxAge();

    @Deprecated
    @Override
    public long maxAge();

    @Deprecated
    @Override
    public void setMaxAge(long var1);

    @Deprecated
    public int getVersion();

    @Deprecated
    public int version();

    @Deprecated
    public void setVersion(int var1);

    @Deprecated
    public String getCommentUrl();

    @Deprecated
    public String commentUrl();

    @Deprecated
    public void setCommentUrl(String var1);

    @Deprecated
    public boolean isDiscard();

    @Deprecated
    public void setDiscard(boolean var1);

    @Deprecated
    public Set<Integer> getPorts();

    @Deprecated
    public Set<Integer> ports();

    @Deprecated
    public /* varargs */ void setPorts(int ... var1);

    @Deprecated
    public void setPorts(Iterable<Integer> var1);
}


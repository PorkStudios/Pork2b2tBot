/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.util.List;

public interface InterfaceHttpPostRequestDecoder {
    public boolean isMultipart();

    public void setDiscardThreshold(int var1);

    public int getDiscardThreshold();

    public List<InterfaceHttpData> getBodyHttpDatas();

    public List<InterfaceHttpData> getBodyHttpDatas(String var1);

    public InterfaceHttpData getBodyHttpData(String var1);

    public InterfaceHttpPostRequestDecoder offer(HttpContent var1);

    public boolean hasNext();

    public InterfaceHttpData next();

    public InterfaceHttpData currentPartialHttpData();

    public void destroy();

    public void cleanFiles();

    public void removeHttpDataFromClean(InterfaceHttpData var1);
}


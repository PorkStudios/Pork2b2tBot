/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.multipart.HttpData;
import java.io.IOException;

public interface Attribute
extends HttpData {
    public String getValue() throws IOException;

    public void setValue(String var1) throws IOException;

    @Override
    public Attribute copy();

    @Override
    public Attribute duplicate();

    @Override
    public Attribute retainedDuplicate();

    @Override
    public Attribute replace(ByteBuf var1);

    @Override
    public Attribute retain();

    @Override
    public Attribute retain(int var1);

    @Override
    public Attribute touch();

    @Override
    public Attribute touch(Object var1);
}


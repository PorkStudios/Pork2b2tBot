/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.multipart.HttpData;

public interface FileUpload
extends HttpData {
    public String getFilename();

    public void setFilename(String var1);

    public void setContentType(String var1);

    public String getContentType();

    public void setContentTransferEncoding(String var1);

    public String getContentTransferEncoding();

    @Override
    public FileUpload copy();

    @Override
    public FileUpload duplicate();

    @Override
    public FileUpload retainedDuplicate();

    @Override
    public FileUpload replace(ByteBuf var1);

    @Override
    public FileUpload retain();

    @Override
    public FileUpload retain(int var1);

    @Override
    public FileUpload touch();

    @Override
    public FileUpload touch(Object var1);
}


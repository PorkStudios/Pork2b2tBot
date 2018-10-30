/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.multipart.AbstractMemoryHttpData;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.FileUploadUtil;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.nio.charset.Charset;

public class MemoryFileUpload
extends AbstractMemoryHttpData
implements FileUpload {
    private String filename;
    private String contentType;
    private String contentTransferEncoding;

    public MemoryFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
        super(name, charset, size);
        this.setFilename(filename);
        this.setContentType(contentType);
        this.setContentTransferEncoding(contentTransferEncoding);
    }

    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.FileUpload;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public void setFilename(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename");
        }
        this.filename = filename;
    }

    public int hashCode() {
        return FileUploadUtil.hashCode(this);
    }

    public boolean equals(Object o) {
        return o instanceof FileUpload && FileUploadUtil.equals(this, (FileUpload)o);
    }

    @Override
    public int compareTo(InterfaceHttpData o) {
        if (!(o instanceof FileUpload)) {
            throw new ClassCastException("Cannot compare " + (Object)((Object)this.getHttpDataType()) + " with " + (Object)((Object)o.getHttpDataType()));
        }
        return this.compareTo((FileUpload)o);
    }

    @Override
    public int compareTo(FileUpload o) {
        return FileUploadUtil.compareTo(this, o);
    }

    @Override
    public void setContentType(String contentType) {
        if (contentType == null) {
            throw new NullPointerException("contentType");
        }
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getContentTransferEncoding() {
        return this.contentTransferEncoding;
    }

    @Override
    public void setContentTransferEncoding(String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }

    public String toString() {
        return HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + this.getName() + "\"; " + HttpHeaderValues.FILENAME + "=\"" + this.filename + "\"\r\n" + HttpHeaderNames.CONTENT_TYPE + ": " + this.contentType + (this.getCharset() != null ? new StringBuilder().append("; ").append((Object)HttpHeaderValues.CHARSET).append('=').append(this.getCharset().name()).append("\r\n").toString() : "\r\n") + HttpHeaderNames.CONTENT_LENGTH + ": " + this.length() + "\r\nCompleted: " + this.isCompleted() + "\r\nIsInMemory: " + this.isInMemory();
    }

    @Override
    public FileUpload copy() {
        ByteBuf content = this.content();
        return this.replace(content != null ? content.copy() : content);
    }

    @Override
    public FileUpload duplicate() {
        ByteBuf content = this.content();
        return this.replace(content != null ? content.duplicate() : content);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public FileUpload retainedDuplicate() {
        ByteBuf content = this.content();
        if (content != null) {
            content = content.retainedDuplicate();
            boolean success = false;
            try {
                FileUpload duplicate = this.replace(content);
                success = true;
                FileUpload fileUpload = duplicate;
                return fileUpload;
            }
            finally {
                if (!success) {
                    content.release();
                }
            }
        }
        return this.replace(null);
    }

    @Override
    public FileUpload replace(ByteBuf content) {
        MemoryFileUpload upload = new MemoryFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size);
        if (content != null) {
            try {
                upload.setContent(content);
                return upload;
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
        }
        return upload;
    }

    @Override
    public FileUpload retain() {
        super.retain();
        return this;
    }

    @Override
    public FileUpload retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public FileUpload touch() {
        super.touch();
        return this;
    }

    @Override
    public FileUpload touch(Object hint) {
        super.touch(hint);
        return this;
    }
}


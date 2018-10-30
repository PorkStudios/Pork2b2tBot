/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractHttpData
extends AbstractReferenceCounted
implements HttpData {
    private static final Pattern STRIP_PATTERN = Pattern.compile("(?:^\\s+|\\s+$|\\n)");
    private static final Pattern REPLACE_PATTERN = Pattern.compile("[\\r\\t]");
    private final String name;
    protected long definedSize;
    protected long size;
    private Charset charset = HttpConstants.DEFAULT_CHARSET;
    private boolean completed;
    private long maxSize = -1L;

    protected AbstractHttpData(String name, Charset charset, long size) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        name = REPLACE_PATTERN.matcher(name).replaceAll(" ");
        if ((name = STRIP_PATTERN.matcher(name).replaceAll("")).isEmpty()) {
            throw new IllegalArgumentException("empty name");
        }
        this.name = name;
        if (charset != null) {
            this.setCharset(charset);
        }
        this.definedSize = size;
    }

    @Override
    public long getMaxSize() {
        return this.maxSize;
    }

    @Override
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void checkSize(long newSize) throws IOException {
        if (this.maxSize >= 0L && newSize > this.maxSize) {
            throw new IOException("Size exceed allowed maximum capacity");
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    protected void setCompleted() {
        this.completed = true;
    }

    @Override
    public Charset getCharset() {
        return this.charset;
    }

    @Override
    public void setCharset(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }

    @Override
    public long length() {
        return this.size;
    }

    @Override
    public long definedLength() {
        return this.definedSize;
    }

    @Override
    public ByteBuf content() {
        try {
            return this.getByteBuf();
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    @Override
    protected void deallocate() {
        this.delete();
    }

    @Override
    public HttpData retain() {
        super.retain();
        return this;
    }

    @Override
    public HttpData retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public abstract HttpData touch();

    @Override
    public abstract HttpData touch(Object var1);
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public interface FileRegion
extends ReferenceCounted {
    public long position();

    @Deprecated
    public long transfered();

    public long transferred();

    public long count();

    public long transferTo(WritableByteChannel var1, long var2) throws IOException;

    @Override
    public FileRegion retain();

    @Override
    public FileRegion retain(int var1);

    @Override
    public FileRegion touch();

    @Override
    public FileRegion touch(Object var1);
}


/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import org.java_websocket.WrappedByteChannel;

public class AbstractWrappedByteChannel
implements WrappedByteChannel {
    private final ByteChannel channel;

    public AbstractWrappedByteChannel(ByteChannel towrap) {
        this.channel = towrap;
    }

    public AbstractWrappedByteChannel(WrappedByteChannel towrap) {
        this.channel = towrap;
    }

    public int read(ByteBuffer dst) throws IOException {
        return this.channel.read(dst);
    }

    public boolean isOpen() {
        return this.channel.isOpen();
    }

    public void close() throws IOException {
        this.channel.close();
    }

    public int write(ByteBuffer src) throws IOException {
        return this.channel.write(src);
    }

    public boolean isNeedWrite() {
        return this.channel instanceof WrappedByteChannel && ((WrappedByteChannel)this.channel).isNeedWrite();
    }

    public void writeMore() throws IOException {
        if (this.channel instanceof WrappedByteChannel) {
            ((WrappedByteChannel)this.channel).writeMore();
        }
    }

    public boolean isNeedRead() {
        return this.channel instanceof WrappedByteChannel && ((WrappedByteChannel)this.channel).isNeedRead();
    }

    public int readMore(ByteBuffer dst) throws IOException {
        return this.channel instanceof WrappedByteChannel ? ((WrappedByteChannel)this.channel).readMore(dst) : 0;
    }

    public boolean isBlocking() {
        if (this.channel instanceof SocketChannel) {
            return ((SocketChannel)this.channel).isBlocking();
        }
        if (this.channel instanceof WrappedByteChannel) {
            return ((WrappedByteChannel)this.channel).isBlocking();
        }
        return false;
    }
}


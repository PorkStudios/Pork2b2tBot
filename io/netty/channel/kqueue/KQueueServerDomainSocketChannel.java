/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.kqueue.AbstractKQueueChannel;
import io.netty.channel.kqueue.AbstractKQueueServerChannel;
import io.netty.channel.kqueue.BsdSocket;
import io.netty.channel.kqueue.KQueueChannelConfig;
import io.netty.channel.kqueue.KQueueDomainSocketChannel;
import io.netty.channel.kqueue.KQueueServerChannelConfig;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.ServerDomainSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.net.SocketAddress;

public final class KQueueServerDomainSocketChannel
extends AbstractKQueueServerChannel
implements ServerDomainSocketChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(KQueueServerDomainSocketChannel.class);
    private final KQueueServerChannelConfig config = new KQueueServerChannelConfig(this);
    private volatile DomainSocketAddress local;

    public KQueueServerDomainSocketChannel() {
        super(BsdSocket.newSocketDomain(), false);
    }

    public KQueueServerDomainSocketChannel(int fd) {
        this(new BsdSocket(fd), false);
    }

    KQueueServerDomainSocketChannel(BsdSocket socket, boolean active) {
        super(socket, active);
    }

    @Override
    protected Channel newChildChannel(int fd, byte[] addr, int offset, int len) throws Exception {
        return new KQueueDomainSocketChannel(this, new BsdSocket(fd));
    }

    @Override
    protected DomainSocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.socket.bind(localAddress);
        this.socket.listen(this.config.getBacklog());
        this.local = (DomainSocketAddress)localAddress;
        this.active = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doClose() throws Exception {
        try {
            super.doClose();
        }
        finally {
            boolean success;
            File socketFile;
            DomainSocketAddress local = this.local;
            if (local != null && !(success = (socketFile = new File(local.path())).delete()) && logger.isDebugEnabled()) {
                logger.debug("Failed to delete a domain socket file: {}", (Object)local.path());
            }
        }
    }

    @Override
    public KQueueServerChannelConfig config() {
        return this.config;
    }

    @Override
    public DomainSocketAddress remoteAddress() {
        return (DomainSocketAddress)super.remoteAddress();
    }

    @Override
    public DomainSocketAddress localAddress() {
        return (DomainSocketAddress)super.localAddress();
    }
}


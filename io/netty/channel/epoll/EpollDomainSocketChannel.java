/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.AbstractEpollStreamChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollDomainSocketChannelConfig;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.unix.DomainSocketReadMode;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.PeerCredentials;
import java.io.IOException;
import java.net.SocketAddress;

public final class EpollDomainSocketChannel
extends AbstractEpollStreamChannel
implements DomainSocketChannel {
    private final EpollDomainSocketChannelConfig config = new EpollDomainSocketChannelConfig(this);
    private volatile DomainSocketAddress local;
    private volatile DomainSocketAddress remote;

    public EpollDomainSocketChannel() {
        super(LinuxSocket.newSocketDomain(), false);
    }

    EpollDomainSocketChannel(Channel parent, FileDescriptor fd) {
        super(parent, new LinuxSocket(fd.intValue()));
    }

    public EpollDomainSocketChannel(int fd) {
        super(fd);
    }

    public EpollDomainSocketChannel(Channel parent, LinuxSocket fd) {
        super(parent, fd);
    }

    public EpollDomainSocketChannel(int fd, boolean active) {
        super(new LinuxSocket(fd), active);
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollDomainUnsafe();
    }

    @Override
    protected DomainSocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected DomainSocketAddress remoteAddress0() {
        return this.remote;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.socket.bind(localAddress);
        this.local = (DomainSocketAddress)localAddress;
    }

    @Override
    public EpollDomainSocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (super.doConnect(remoteAddress, localAddress)) {
            this.local = (DomainSocketAddress)localAddress;
            this.remote = (DomainSocketAddress)remoteAddress;
            return true;
        }
        return false;
    }

    @Override
    public DomainSocketAddress remoteAddress() {
        return (DomainSocketAddress)super.remoteAddress();
    }

    @Override
    public DomainSocketAddress localAddress() {
        return (DomainSocketAddress)super.localAddress();
    }

    @Override
    protected int doWriteSingle(ChannelOutboundBuffer in) throws Exception {
        Object msg = in.current();
        if (msg instanceof FileDescriptor && this.socket.sendFd(((FileDescriptor)msg).intValue()) > 0) {
            in.remove();
            return 1;
        }
        return super.doWriteSingle(in);
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (msg instanceof FileDescriptor) {
            return msg;
        }
        return super.filterOutboundMessage(msg);
    }

    public PeerCredentials peerCredentials() throws IOException {
        return this.socket.getPeerCredentials();
    }

    private final class EpollDomainUnsafe
    extends AbstractEpollStreamChannel.EpollStreamUnsafe {
        private EpollDomainUnsafe() {
            super(EpollDomainSocketChannel.this);
        }

        @Override
        void epollInReady() {
            switch (EpollDomainSocketChannel.this.config().getReadMode()) {
                case BYTES: {
                    super.epollInReady();
                    break;
                }
                case FILE_DESCRIPTORS: {
                    this.epollInReadFd();
                    break;
                }
                default: {
                    throw new Error();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        private void epollInReadFd() {
            if (EpollDomainSocketChannel.this.socket.isInputShutdown()) {
                this.clearEpollIn0();
                return;
            }
            EpollDomainSocketChannelConfig config = EpollDomainSocketChannel.this.config();
            EpollRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.edgeTriggered(EpollDomainSocketChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline pipeline = EpollDomainSocketChannel.this.pipeline();
            allocHandle.reset(config);
            this.epollInBefore();
            try {
                block10 : do {
                    allocHandle.lastBytesRead(EpollDomainSocketChannel.this.socket.recvFd());
                    switch (allocHandle.lastBytesRead()) {
                        case 0: {
                            break block10;
                        }
                        case -1: {
                            this.close(this.voidPromise());
                            return;
                        }
                    }
                    allocHandle.incMessagesRead(1);
                    this.readPending = false;
                    pipeline.fireChannelRead(new FileDescriptor(allocHandle.lastBytesRead()));
                } while (allocHandle.continueReading());
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                return;
            }
            catch (Throwable t) {
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                pipeline.fireExceptionCaught(t);
                return;
            }
            finally {
                this.epollInFinally(config);
            }
        }
    }

}


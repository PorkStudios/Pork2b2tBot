/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.EventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollDatagramChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.unix.DatagramSocketAddress;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public final class EpollDatagramChannel
extends AbstractEpollChannel
implements DatagramChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(true);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    private final EpollDatagramChannelConfig config = new EpollDatagramChannelConfig(this);
    private volatile boolean connected;

    public EpollDatagramChannel() {
        super(LinuxSocket.newSocketDgram(), Native.EPOLLIN);
    }

    public EpollDatagramChannel(int fd) {
        this(new LinuxSocket(fd));
    }

    EpollDatagramChannel(LinuxSocket fd) {
        super(null, fd, Native.EPOLLIN, true);
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public boolean isActive() {
        return this.socket.isOpen() && (this.config.getActiveOnOpen() && this.isRegistered() || this.active);
    }

    @Override
    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress) {
        return this.joinGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return this.joinGroup(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, promise);
        }
        catch (SocketException e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.joinGroup(multicastAddress, networkInterface, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        return this.joinGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.joinGroup(multicastAddress, networkInterface, source, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        if (multicastAddress == null) {
            throw new NullPointerException("multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException("networkInterface");
        }
        promise.setFailure(new UnsupportedOperationException("Multicast not supported"));
        return promise;
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress) {
        return this.leaveGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            return this.leaveGroup(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, promise);
        }
        catch (SocketException e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.leaveGroup(multicastAddress, networkInterface, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        return this.leaveGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.leaveGroup(multicastAddress, networkInterface, source, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        if (multicastAddress == null) {
            throw new NullPointerException("multicastAddress");
        }
        if (networkInterface == null) {
            throw new NullPointerException("networkInterface");
        }
        promise.setFailure(new UnsupportedOperationException("Multicast not supported"));
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
        return this.block(multicastAddress, networkInterface, sourceToBlock, this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
        if (multicastAddress == null) {
            throw new NullPointerException("multicastAddress");
        }
        if (sourceToBlock == null) {
            throw new NullPointerException("sourceToBlock");
        }
        if (networkInterface == null) {
            throw new NullPointerException("networkInterface");
        }
        promise.setFailure(new UnsupportedOperationException("Multicast not supported"));
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
        return this.block(multicastAddress, sourceToBlock, this.newPromise());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
        try {
            return this.block(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), sourceToBlock, promise);
        }
        catch (Throwable e) {
            promise.setFailure(e);
            return promise;
        }
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollDatagramChannelUnsafe();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        super.doBind(localAddress);
        this.active = true;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        block2 : do {
            Object msg;
            if ((msg = in.current()) == null) {
                this.clearFlag(Native.EPOLLOUT);
                break;
            }
            try {
                int cnt;
                NativeDatagramPacketArray array;
                if (Native.IS_SUPPORTING_SENDMMSG && in.size() > 1 && (cnt = (array = NativeDatagramPacketArray.getInstance(in)).count()) >= 1) {
                    int offset = 0;
                    NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
                    do {
                        if (cnt <= 0) continue block2;
                        int send = Native.sendmmsg(this.socket.intValue(), packets, offset, cnt);
                        if (send == 0) {
                            this.setFlag(Native.EPOLLOUT);
                            return;
                        }
                        for (int i = 0; i < send; ++i) {
                            in.remove();
                        }
                        cnt -= send;
                        offset += send;
                    } while (true);
                }
                boolean done = false;
                for (int i = this.config().getWriteSpinCount(); i > 0; --i) {
                    if (!this.doWriteMessage(msg)) continue;
                    done = true;
                    break;
                }
                if (done) {
                    in.remove();
                    continue;
                }
                this.setFlag(Native.EPOLLOUT);
            }
            catch (IOException e) {
                in.remove(e);
                continue;
            }
            break;
        } while (true);
    }

    private boolean doWriteMessage(Object msg) throws Exception {
        ByteBuf data;
        InetSocketAddress remoteAddress;
        long writtenBytes;
        if (msg instanceof AddressedEnvelope) {
            AddressedEnvelope envelope = (AddressedEnvelope)msg;
            data = (ByteBuf)envelope.content();
            remoteAddress = (InetSocketAddress)envelope.recipient();
        } else {
            data = (ByteBuf)msg;
            remoteAddress = null;
        }
        int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        if (data.hasMemoryAddress()) {
            long memoryAddress = data.memoryAddress();
            writtenBytes = remoteAddress == null ? (long)this.socket.writeAddress(memoryAddress, data.readerIndex(), data.writerIndex()) : (long)this.socket.sendToAddress(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress.getAddress(), remoteAddress.getPort());
        } else if (data.nioBufferCount() > 1) {
            IovArray array = ((EpollEventLoop)this.eventLoop()).cleanArray();
            array.add(data);
            int cnt = array.count();
            assert (cnt != 0);
            writtenBytes = remoteAddress == null ? this.socket.writevAddresses(array.memoryAddress(0), cnt) : (long)this.socket.sendToAddresses(array.memoryAddress(0), cnt, remoteAddress.getAddress(), remoteAddress.getPort());
        } else {
            ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
            writtenBytes = remoteAddress == null ? (long)this.socket.write(nioData, nioData.position(), nioData.limit()) : (long)this.socket.sendTo(nioData, nioData.position(), nioData.limit(), remoteAddress.getAddress(), remoteAddress.getPort());
        }
        return writtenBytes > 0L;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        AddressedEnvelope<ByteBuf, InetSocketAddress> e;
        if (msg instanceof DatagramPacket) {
            DatagramPacket packet = (DatagramPacket)msg;
            ByteBuf content = (ByteBuf)packet.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DatagramPacket(this.newDirectBuffer(packet, content), (InetSocketAddress)packet.recipient()) : msg;
        }
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
        }
        if (msg instanceof AddressedEnvelope && (e = (AddressedEnvelope<ByteBuf, InetSocketAddress>)msg).content() instanceof ByteBuf && (e.recipient() == null || e.recipient() instanceof InetSocketAddress)) {
            ByteBuf content = (ByteBuf)e.content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DefaultAddressedEnvelope<ByteBuf, InetSocketAddress>(this.newDirectBuffer(e, content), (InetSocketAddress)e.recipient()) : e;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    @Override
    public EpollDatagramChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.socket.disconnect();
        this.active = false;
        this.connected = false;
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (super.doConnect(remoteAddress, localAddress)) {
            this.connected = true;
            return true;
        }
        return false;
    }

    @Override
    protected void doClose() throws Exception {
        super.doClose();
        this.connected = false;
    }

    final class EpollDatagramChannelUnsafe
    extends AbstractEpollChannel.AbstractEpollUnsafe {
        EpollDatagramChannelUnsafe() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void epollInReady() {
            assert (EpollDatagramChannel.this.eventLoop().inEventLoop());
            EpollDatagramChannelConfig config = EpollDatagramChannel.this.config();
            if (EpollDatagramChannel.this.shouldBreakEpollInReady(config)) {
                this.clearEpollIn0();
                return;
            }
            EpollRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.edgeTriggered(EpollDatagramChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline pipeline = EpollDatagramChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            this.epollInBefore();
            Throwable exception = null;
            try {
                ByteBuf data = null;
                try {
                    do {
                        DatagramSocketAddress remoteAddress;
                        data = allocHandle.allocate(allocator);
                        allocHandle.attemptedBytesRead(data.writableBytes());
                        if (data.hasMemoryAddress()) {
                            remoteAddress = EpollDatagramChannel.this.socket.recvFromAddress(data.memoryAddress(), data.writerIndex(), data.capacity());
                        } else {
                            ByteBuffer nioData = data.internalNioBuffer(data.writerIndex(), data.writableBytes());
                            remoteAddress = EpollDatagramChannel.this.socket.recvFrom(nioData, nioData.position(), nioData.limit());
                        }
                        if (remoteAddress == null) {
                            allocHandle.lastBytesRead(-1);
                            data.release();
                            data = null;
                            break;
                        }
                        allocHandle.incMessagesRead(1);
                        allocHandle.lastBytesRead(remoteAddress.receivedAmount());
                        data.writerIndex(data.writerIndex() + allocHandle.lastBytesRead());
                        this.readPending = false;
                        pipeline.fireChannelRead(new DatagramPacket(data, (InetSocketAddress)this.localAddress(), remoteAddress));
                        data = null;
                    } while (allocHandle.continueReading());
                }
                catch (Throwable t) {
                    if (data != null) {
                        data.release();
                    }
                    exception = t;
                }
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    pipeline.fireExceptionCaught(exception);
                }
            }
            finally {
                this.epollInFinally(config);
            }
        }
    }

}


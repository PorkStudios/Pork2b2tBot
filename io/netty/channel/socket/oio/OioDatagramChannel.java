/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.oio.DefaultOioDatagramChannelConfig;
import io.netty.channel.socket.oio.OioDatagramChannelConfig;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.NotYetConnectedException;
import java.util.List;
import java.util.Locale;

public class OioDatagramChannel
extends AbstractOioMessageChannel
implements DatagramChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioDatagramChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(true);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(SocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    private final MulticastSocket socket;
    private final OioDatagramChannelConfig config;
    private final java.net.DatagramPacket tmpPacket;

    private static MulticastSocket newSocket() {
        try {
            return new MulticastSocket(null);
        }
        catch (Exception e) {
            throw new ChannelException("failed to create a new socket", e);
        }
    }

    public OioDatagramChannel() {
        this(OioDatagramChannel.newSocket());
    }

    public OioDatagramChannel(MulticastSocket socket) {
        super(null);
        this.tmpPacket = new java.net.DatagramPacket(EmptyArrays.EMPTY_BYTES, 0);
        boolean success = false;
        try {
            socket.setSoTimeout(1000);
            socket.setBroadcast(false);
            success = true;
        }
        catch (SocketException e) {
            throw new ChannelException("Failed to configure the datagram socket timeout.", e);
        }
        finally {
            if (!success) {
                socket.close();
            }
        }
        this.socket = socket;
        this.config = new DefaultOioDatagramChannelConfig(this, socket);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public DatagramChannelConfig config() {
        return this.config;
    }

    @Override
    public boolean isOpen() {
        return !this.socket.isClosed();
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && (this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) != false && this.isRegistered() || this.socket.isBound());
    }

    @Override
    public boolean isConnected() {
        return this.socket.isConnected();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.socket.getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.socket.getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.socket.bind(localAddress);
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.socket.bind(localAddress);
        }
        boolean success = false;
        try {
            this.socket.connect(remoteAddress);
            success = true;
        }
        finally {
            if (!success) {
                try {
                    this.socket.close();
                }
                catch (Throwable t) {
                    logger.warn("Failed to close a socket.", t);
                }
            }
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.socket.disconnect();
    }

    @Override
    protected void doClose() throws Exception {
        this.socket.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        DatagramChannelConfig config = this.config();
        RecvByteBufAllocator.Handle allocHandle = this.unsafe().recvBufAllocHandle();
        ByteBuf data = config.getAllocator().heapBuffer(allocHandle.guess());
        boolean free = true;
        try {
            this.tmpPacket.setAddress(null);
            this.tmpPacket.setData(data.array(), data.arrayOffset(), data.capacity());
            this.socket.receive(this.tmpPacket);
            InetSocketAddress remoteAddr = (InetSocketAddress)this.tmpPacket.getSocketAddress();
            allocHandle.lastBytesRead(this.tmpPacket.getLength());
            buf.add(new DatagramPacket(data.writerIndex(allocHandle.lastBytesRead()), this.localAddress(), remoteAddr));
            free = false;
            int n = 1;
            return n;
        }
        catch (SocketTimeoutException e) {
            int n = 0;
            return n;
        }
        catch (SocketException e) {
            if (!e.getMessage().toLowerCase(Locale.US).contains("socket closed")) {
                throw e;
            }
            int n = -1;
            return n;
        }
        catch (Throwable cause) {
            PlatformDependent.throwException(cause);
            int n = -1;
            return n;
        }
        finally {
            if (free) {
                data.release();
            }
        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        Object o;
        while ((o = in.current()) != null) {
            ByteBuf data;
            SocketAddress remoteAddress;
            if (o instanceof AddressedEnvelope) {
                AddressedEnvelope envelope = (AddressedEnvelope)o;
                remoteAddress = (SocketAddress)envelope.recipient();
                data = (ByteBuf)envelope.content();
            } else {
                data = (ByteBuf)o;
                remoteAddress = null;
            }
            int length = data.readableBytes();
            try {
                if (remoteAddress != null) {
                    this.tmpPacket.setSocketAddress(remoteAddress);
                } else {
                    if (!this.isConnected()) {
                        throw new NotYetConnectedException();
                    }
                    this.tmpPacket.setAddress(null);
                }
                if (data.hasArray()) {
                    this.tmpPacket.setData(data.array(), data.arrayOffset() + data.readerIndex(), length);
                } else {
                    byte[] tmp = new byte[length];
                    data.getBytes(data.readerIndex(), tmp);
                    this.tmpPacket.setData(tmp);
                }
                this.socket.send(this.tmpPacket);
                in.remove();
            }
            catch (Exception e) {
                in.remove(e);
            }
        }
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        AddressedEnvelope e;
        if (msg instanceof DatagramPacket || msg instanceof ByteBuf) {
            return msg;
        }
        if (msg instanceof AddressedEnvelope && (e = (AddressedEnvelope)msg).content() instanceof ByteBuf) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress) {
        return this.joinGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
        this.ensureBound();
        try {
            this.socket.joinGroup(multicastAddress);
            promise.setSuccess();
        }
        catch (IOException e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.joinGroup(multicastAddress, networkInterface, this.newPromise());
    }

    @Override
    public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        this.ensureBound();
        try {
            this.socket.joinGroup(multicastAddress, networkInterface);
            promise.setSuccess();
        }
        catch (IOException e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.newFailedFuture(new UnsupportedOperationException());
    }

    @Override
    public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        promise.setFailure(new UnsupportedOperationException());
        return promise;
    }

    private void ensureBound() {
        if (!this.isActive()) {
            throw new IllegalStateException(DatagramChannel.class.getName() + " must be bound to join a group.");
        }
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress) {
        return this.leaveGroup(multicastAddress, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
        try {
            this.socket.leaveGroup(multicastAddress);
            promise.setSuccess();
        }
        catch (IOException e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
        return this.leaveGroup(multicastAddress, networkInterface, this.newPromise());
    }

    @Override
    public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
        try {
            this.socket.leaveGroup(multicastAddress, networkInterface);
            promise.setSuccess();
        }
        catch (IOException e) {
            promise.setFailure(e);
        }
        return promise;
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
        return this.newFailedFuture(new UnsupportedOperationException());
    }

    @Override
    public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
        promise.setFailure(new UnsupportedOperationException());
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
        return this.newFailedFuture(new UnsupportedOperationException());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
        promise.setFailure(new UnsupportedOperationException());
        return promise;
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
        return this.newFailedFuture(new UnsupportedOperationException());
    }

    @Override
    public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
        promise.setFailure(new UnsupportedOperationException());
        return promise;
    }
}


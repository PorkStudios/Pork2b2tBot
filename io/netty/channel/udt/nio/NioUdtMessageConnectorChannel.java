/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  com.barchart.udt.SocketUDT
 *  com.barchart.udt.StatusUDT
 *  com.barchart.udt.TypeUDT
 *  com.barchart.udt.nio.ChannelUDT
 *  com.barchart.udt.nio.NioSocketUDT
 *  com.barchart.udt.nio.SocketChannelUDT
 */
package io.netty.channel.udt.nio;

import com.barchart.udt.SocketUDT;
import com.barchart.udt.StatusUDT;
import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.ChannelUDT;
import com.barchart.udt.nio.NioSocketUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.udt.DefaultUdtChannelConfig;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtChannelConfig;
import io.netty.channel.udt.UdtMessage;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;

@Deprecated
public class NioUdtMessageConnectorChannel
extends AbstractNioMessageChannel
implements UdtChannel {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioUdtMessageConnectorChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final UdtChannelConfig config;

    public NioUdtMessageConnectorChannel() {
        this(TypeUDT.DATAGRAM);
    }

    public NioUdtMessageConnectorChannel(Channel parent, SocketChannelUDT channelUDT) {
        super(parent, (SelectableChannel)channelUDT, 1);
        try {
            channelUDT.configureBlocking(false);
            switch (channelUDT.socketUDT().status()) {
                case INIT: 
                case OPENED: {
                    this.config = new DefaultUdtChannelConfig(this, (ChannelUDT)channelUDT, true);
                    break;
                }
                default: {
                    this.config = new DefaultUdtChannelConfig(this, (ChannelUDT)channelUDT, false);
                    break;
                }
            }
        }
        catch (Exception e) {
            block7 : {
                try {
                    channelUDT.close();
                }
                catch (Exception e2) {
                    if (!logger.isWarnEnabled()) break block7;
                    logger.warn("Failed to close channel.", e2);
                }
            }
            throw new ChannelException("Failed to configure channel.", e);
        }
    }

    public NioUdtMessageConnectorChannel(SocketChannelUDT channelUDT) {
        this(null, channelUDT);
    }

    public NioUdtMessageConnectorChannel(TypeUDT type) {
        this(NioUdtProvider.newConnectorChannelUDT(type));
    }

    @Override
    public UdtChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        NioUdtMessageConnectorChannel.privilegedBind(this.javaChannel(), localAddress);
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        this.doBind(localAddress != null ? localAddress : new InetSocketAddress(0));
        boolean success = false;
        try {
            boolean connected = SocketUtils.connect((SocketChannel)this.javaChannel(), remoteAddress);
            if (!connected) {
                this.selectionKey().interestOps(this.selectionKey().interestOps() | 8);
            }
            success = true;
            boolean bl = connected;
            return bl;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new Error("Provider error: failed to finish connect. Provider library should be upgraded.");
        }
        this.selectionKey().interestOps(this.selectionKey().interestOps() & -9);
    }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        int maximumMessageSize = this.config.getReceiveBufferSize();
        ByteBuf byteBuf = this.config.getAllocator().directBuffer(maximumMessageSize);
        int receivedMessageSize = byteBuf.writeBytes((ScatteringByteChannel)this.javaChannel(), maximumMessageSize);
        if (receivedMessageSize <= 0) {
            byteBuf.release();
            return 0;
        }
        if (receivedMessageSize >= maximumMessageSize) {
            this.javaChannel().close();
            throw new ChannelException("Invalid config : increase receive buffer size to avoid message truncation");
        }
        buf.add(new UdtMessage(byteBuf));
        return 1;
    }

    @Override
    protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
        UdtMessage message = (UdtMessage)msg;
        ByteBuf byteBuf = message.content();
        int messageSize = byteBuf.readableBytes();
        if (messageSize == 0) {
            return true;
        }
        long writtenBytes = byteBuf.nioBufferCount() == 1 ? (long)this.javaChannel().write(byteBuf.nioBuffer()) : this.javaChannel().write(byteBuf.nioBuffers());
        if (writtenBytes > 0L && writtenBytes != (long)messageSize) {
            throw new Error("Provider error: failed to write message. Provider library should be upgraded.");
        }
        return writtenBytes > 0L;
    }

    @Override
    public boolean isActive() {
        SocketChannelUDT channelUDT = this.javaChannel();
        return channelUDT.isOpen() && channelUDT.isConnectFinished();
    }

    protected SocketChannelUDT javaChannel() {
        return (SocketChannelUDT)super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    private static void privilegedBind(final SocketChannelUDT socketChannel, final SocketAddress localAddress) throws IOException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>(){

                @Override
                public Void run() throws IOException {
                    socketChannel.bind(localAddress);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }

}


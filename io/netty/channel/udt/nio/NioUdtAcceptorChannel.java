/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  com.barchart.udt.TypeUDT
 *  com.barchart.udt.nio.ChannelUDT
 *  com.barchart.udt.nio.NioServerSocketUDT
 *  com.barchart.udt.nio.ServerSocketChannelUDT
 *  com.barchart.udt.nio.SocketChannelUDT
 */
package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.ChannelUDT;
import com.barchart.udt.nio.NioServerSocketUDT;
import com.barchart.udt.nio.ServerSocketChannelUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.udt.DefaultUdtServerChannelConfig;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtChannelConfig;
import io.netty.channel.udt.UdtServerChannel;
import io.netty.channel.udt.UdtServerChannelConfig;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.List;

@Deprecated
public abstract class NioUdtAcceptorChannel
extends AbstractNioMessageChannel
implements UdtServerChannel {
    protected static final InternalLogger logger = InternalLoggerFactory.getInstance(NioUdtAcceptorChannel.class);
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
    private final UdtServerChannelConfig config;

    protected NioUdtAcceptorChannel(ServerSocketChannelUDT channelUDT) {
        super(null, (SelectableChannel)channelUDT, 16);
        try {
            channelUDT.configureBlocking(false);
            this.config = new DefaultUdtServerChannelConfig(this, (ChannelUDT)channelUDT, true);
        }
        catch (Exception e) {
            block4 : {
                try {
                    channelUDT.close();
                }
                catch (Exception e2) {
                    if (!logger.isWarnEnabled()) break block4;
                    logger.warn("Failed to close channel.", e2);
                }
            }
            throw new ChannelException("Failed to configure channel.", e);
        }
    }

    protected NioUdtAcceptorChannel(TypeUDT type) {
        this(NioUdtProvider.newAcceptorChannelUDT(type));
    }

    @Override
    public UdtServerChannelConfig config() {
        return this.config;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.javaChannel().socket().bind(localAddress, this.config.getBacklog());
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doFinishConnect() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final Object filterOutboundMessage(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isActive() {
        return this.javaChannel().socket().isBound();
    }

    protected ServerSocketChannelUDT javaChannel() {
        return (ServerSocketChannelUDT)super.javaChannel();
    }

    @Override
    protected SocketAddress localAddress0() {
        return SocketUtils.localSocketAddress((ServerSocket)this.javaChannel().socket());
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannelUDT channelUDT = (SocketChannelUDT)SocketUtils.accept((ServerSocketChannel)this.javaChannel());
        if (channelUDT == null) {
            return 0;
        }
        buf.add(this.newConnectorChannel(channelUDT));
        return 1;
    }

    protected abstract UdtChannel newConnectorChannel(SocketChannelUDT var1);
}


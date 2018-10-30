/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.epoll.EpollSocketChannelConfig;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

abstract class AbstractEpollChannel
extends AbstractChannel
implements UnixChannel {
    private static final ClosedChannelException DO_CLOSE_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollChannel.class, "doClose()");
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final int readFlag;
    final LinuxSocket socket;
    private ChannelPromise connectPromise;
    private ScheduledFuture<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;
    private volatile SocketAddress local;
    private volatile SocketAddress remote;
    protected int flags = Native.EPOLLET;
    boolean inputClosedSeenErrorOnRead;
    boolean epollInReadyRunnablePending;
    protected volatile boolean active;

    AbstractEpollChannel(LinuxSocket fd, int flag) {
        this(null, fd, flag, false);
    }

    AbstractEpollChannel(Channel parent, LinuxSocket fd, int flag, boolean active) {
        super(parent);
        this.socket = ObjectUtil.checkNotNull(fd, "fd");
        this.readFlag = flag;
        this.flags |= flag;
        this.active = active;
        if (active) {
            this.local = fd.localAddress();
            this.remote = fd.remoteAddress();
        }
    }

    AbstractEpollChannel(Channel parent, LinuxSocket fd, int flag, SocketAddress remote) {
        super(parent);
        this.socket = ObjectUtil.checkNotNull(fd, "fd");
        this.readFlag = flag;
        this.flags |= flag;
        this.active = true;
        this.remote = remote;
        this.local = fd.localAddress();
    }

    static boolean isSoErrorZero(Socket fd) {
        try {
            return fd.getSoError() == 0;
        }
        catch (IOException e) {
            throw new ChannelException(e);
        }
    }

    void setFlag(int flag) throws IOException {
        if (!this.isFlagSet(flag)) {
            this.flags |= flag;
            this.modifyEvents();
        }
    }

    void clearFlag(int flag) throws IOException {
        if (this.isFlagSet(flag)) {
            this.flags &= ~ flag;
            this.modifyEvents();
        }
    }

    boolean isFlagSet(int flag) {
        return (this.flags & flag) != 0;
    }

    @Override
    public final FileDescriptor fd() {
        return this.socket;
    }

    @Override
    public abstract EpollChannelConfig config();

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doClose() throws Exception {
        this.active = false;
        this.inputClosedSeenErrorOnRead = true;
        try {
            ScheduledFuture<?> future;
            ChannelPromise promise = this.connectPromise;
            if (promise != null) {
                promise.tryFailure(DO_CLOSE_CLOSED_CHANNEL_EXCEPTION);
                this.connectPromise = null;
            }
            if ((future = this.connectTimeoutFuture) != null) {
                future.cancel(false);
                this.connectTimeoutFuture = null;
            }
            if (this.isRegistered()) {
                EventLoop loop = this.eventLoop();
                if (loop.inEventLoop()) {
                    this.doDeregister();
                } else {
                    loop.execute(new Runnable(){

                        @Override
                        public void run() {
                            try {
                                AbstractEpollChannel.this.doDeregister();
                            }
                            catch (Throwable cause) {
                                AbstractEpollChannel.this.pipeline().fireExceptionCaught(cause);
                            }
                        }
                    });
                }
            }
        }
        finally {
            this.socket.close();
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }

    @Override
    public boolean isOpen() {
        return this.socket.isOpen();
    }

    @Override
    protected void doDeregister() throws Exception {
        ((EpollEventLoop)this.eventLoop()).remove(this);
    }

    @Override
    protected final void doBeginRead() throws Exception {
        AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
        unsafe.readPending = true;
        this.setFlag(this.readFlag);
        if (unsafe.maybeMoreDataToRead) {
            unsafe.executeEpollInReadyRunnable(this.config());
        }
    }

    final boolean shouldBreakEpollInReady(ChannelConfig config) {
        return this.socket.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !AbstractEpollChannel.isAllowHalfClosure(config));
    }

    private static boolean isAllowHalfClosure(ChannelConfig config) {
        return config instanceof EpollSocketChannelConfig && ((EpollSocketChannelConfig)config).isAllowHalfClosure();
    }

    final void clearEpollIn() {
        if (this.isRegistered()) {
            EventLoop loop = this.eventLoop();
            final AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
            if (loop.inEventLoop()) {
                unsafe.clearEpollIn0();
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        if (!unsafe.readPending && !AbstractEpollChannel.this.config().isAutoRead()) {
                            unsafe.clearEpollIn0();
                        }
                    }
                });
            }
        } else {
            this.flags &= ~ this.readFlag;
        }
    }

    private void modifyEvents() throws IOException {
        if (this.isOpen() && this.isRegistered()) {
            ((EpollEventLoop)this.eventLoop()).modify(this);
        }
    }

    @Override
    protected void doRegister() throws Exception {
        this.epollInReadyRunnablePending = false;
        ((EpollEventLoop)this.eventLoop()).add(this);
    }

    @Override
    protected abstract AbstractEpollUnsafe newUnsafe();

    protected final ByteBuf newDirectBuffer(ByteBuf buf) {
        return this.newDirectBuffer(buf, buf);
    }

    protected final ByteBuf newDirectBuffer(Object holder, ByteBuf buf) {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.release(holder);
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            return AbstractEpollChannel.newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf == null) {
            return AbstractEpollChannel.newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }

    private static ByteBuf newDirectBuffer0(Object holder, ByteBuf buf, ByteBufAllocator alloc, int capacity) {
        ByteBuf directBuf = alloc.directBuffer(capacity);
        directBuf.writeBytes(buf, buf.readerIndex(), capacity);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }

    protected static void checkResolvable(InetSocketAddress addr) {
        if (addr.isUnresolved()) {
            throw new UnresolvedAddressException();
        }
    }

    protected final int doReadBytes(ByteBuf byteBuf) throws Exception {
        int localReadAmount;
        int writerIndex = byteBuf.writerIndex();
        this.unsafe().recvBufAllocHandle().attemptedBytesRead(byteBuf.writableBytes());
        if (byteBuf.hasMemoryAddress()) {
            localReadAmount = this.socket.readAddress(byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
        } else {
            ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
            localReadAmount = this.socket.read(buf, buf.position(), buf.limit());
        }
        if (localReadAmount > 0) {
            byteBuf.writerIndex(writerIndex + localReadAmount);
        }
        return localReadAmount;
    }

    protected final int doWriteBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
        if (buf.hasMemoryAddress()) {
            int localFlushedAmount = this.socket.writeAddress(buf.memoryAddress(), buf.readerIndex(), buf.writerIndex());
            if (localFlushedAmount > 0) {
                in.removeBytes(localFlushedAmount);
                return 1;
            }
        } else {
            ByteBuffer nioBuf = buf.nioBufferCount() == 1 ? buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes()) : buf.nioBuffer();
            int localFlushedAmount = this.socket.write(nioBuf, nioBuf.position(), nioBuf.limit());
            if (localFlushedAmount > 0) {
                nioBuf.position(nioBuf.position() + localFlushedAmount);
                in.removeBytes(localFlushedAmount);
                return 1;
            }
        }
        return Integer.MAX_VALUE;
    }

    @Override
    protected void doBind(SocketAddress local) throws Exception {
        if (local instanceof InetSocketAddress) {
            AbstractEpollChannel.checkResolvable((InetSocketAddress)local);
        }
        this.socket.bind(local);
        this.local = this.socket.localAddress();
    }

    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        boolean connected;
        InetSocketAddress remoteSocketAddr;
        if (localAddress instanceof InetSocketAddress) {
            AbstractEpollChannel.checkResolvable((InetSocketAddress)localAddress);
        }
        InetSocketAddress inetSocketAddress = remoteSocketAddr = remoteAddress instanceof InetSocketAddress ? (InetSocketAddress)remoteAddress : null;
        if (remoteSocketAddr != null) {
            AbstractEpollChannel.checkResolvable(remoteSocketAddr);
        }
        if (this.remote != null) {
            throw new AlreadyConnectedException();
        }
        if (localAddress != null) {
            this.socket.bind(localAddress);
        }
        if (connected = this.doConnect0(remoteAddress)) {
            this.remote = remoteSocketAddr == null ? remoteAddress : UnixChannelUtil.computeRemoteAddr(remoteSocketAddr, this.socket.remoteAddress());
        }
        this.local = this.socket.localAddress();
        return connected;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean doConnect0(SocketAddress remote) throws Exception {
        boolean success = false;
        try {
            boolean connected = this.socket.connect(remote);
            if (!connected) {
                this.setFlag(Native.EPOLLOUT);
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
    protected SocketAddress localAddress0() {
        return this.local;
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.remote;
    }

    protected abstract class AbstractEpollUnsafe
    extends AbstractChannel.AbstractUnsafe {
        boolean readPending;
        boolean maybeMoreDataToRead;
        private EpollRecvByteAllocatorHandle allocHandle;
        private final Runnable epollInReadyRunnable = new Runnable(){

            @Override
            public void run() {
                AbstractEpollChannel.this.epollInReadyRunnablePending = false;
                AbstractEpollUnsafe.this.epollInReady();
            }
        };

        protected AbstractEpollUnsafe() {
        }

        abstract void epollInReady();

        final void epollInBefore() {
            this.maybeMoreDataToRead = false;
        }

        final void epollInFinally(ChannelConfig config) {
            boolean bl = this.maybeMoreDataToRead = this.allocHandle.isEdgeTriggered() && this.allocHandle.maybeMoreDataToRead();
            if (!this.readPending && !config.isAutoRead()) {
                AbstractEpollChannel.this.clearEpollIn();
            } else if (this.readPending && this.maybeMoreDataToRead) {
                this.executeEpollInReadyRunnable(config);
            }
        }

        final void executeEpollInReadyRunnable(ChannelConfig config) {
            if (AbstractEpollChannel.this.epollInReadyRunnablePending || !AbstractEpollChannel.this.isActive() || AbstractEpollChannel.this.shouldBreakEpollInReady(config)) {
                return;
            }
            AbstractEpollChannel.this.epollInReadyRunnablePending = true;
            AbstractEpollChannel.this.eventLoop().execute(this.epollInReadyRunnable);
        }

        final void epollRdHupReady() {
            this.recvBufAllocHandle().receivedRdHup();
            if (AbstractEpollChannel.this.isActive()) {
                this.epollInReady();
            } else {
                this.shutdownInput(true);
            }
            this.clearEpollRdHup();
        }

        private void clearEpollRdHup() {
            try {
                AbstractEpollChannel.this.clearFlag(Native.EPOLLRDHUP);
            }
            catch (IOException e) {
                AbstractEpollChannel.this.pipeline().fireExceptionCaught(e);
                this.close(this.voidPromise());
            }
        }

        void shutdownInput(boolean rdHup) {
            if (!AbstractEpollChannel.this.socket.isInputShutdown()) {
                if (AbstractEpollChannel.isAllowHalfClosure(AbstractEpollChannel.this.config())) {
                    try {
                        AbstractEpollChannel.this.socket.shutdown(true, false);
                    }
                    catch (IOException ignored2) {
                        this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
                        return;
                    }
                    catch (NotYetConnectedException ignored2) {
                        // empty catch block
                    }
                    AbstractEpollChannel.this.clearEpollIn();
                    AbstractEpollChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                } else {
                    this.close(this.voidPromise());
                }
            } else if (!rdHup) {
                AbstractEpollChannel.this.inputClosedSeenErrorOnRead = true;
                AbstractEpollChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
            }
        }

        private void fireEventAndClose(Object evt) {
            AbstractEpollChannel.this.pipeline().fireUserEventTriggered(evt);
            this.close(this.voidPromise());
        }

        @Override
        public EpollRecvByteAllocatorHandle recvBufAllocHandle() {
            if (this.allocHandle == null) {
                this.allocHandle = this.newEpollHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle());
            }
            return this.allocHandle;
        }

        EpollRecvByteAllocatorHandle newEpollHandle(RecvByteBufAllocator.ExtendedHandle handle) {
            return new EpollRecvByteAllocatorHandle(handle);
        }

        @Override
        protected void flush0() {
            if (AbstractEpollChannel.this.isFlagSet(Native.EPOLLOUT)) {
                return;
            }
            super.flush0();
        }

        final void epollOutReady() {
            if (AbstractEpollChannel.this.connectPromise != null) {
                this.finishConnect();
            } else if (!AbstractEpollChannel.this.socket.isOutputShutdown()) {
                super.flush0();
            }
        }

        protected final void clearEpollIn0() {
            assert (AbstractEpollChannel.this.eventLoop().inEventLoop());
            try {
                this.readPending = false;
                AbstractEpollChannel.this.clearFlag(AbstractEpollChannel.this.readFlag);
            }
            catch (IOException e) {
                AbstractEpollChannel.this.pipeline().fireExceptionCaught(e);
                AbstractEpollChannel.this.unsafe().close(AbstractEpollChannel.this.unsafe().voidPromise());
            }
        }

        @Override
        public void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            try {
                if (AbstractEpollChannel.this.connectPromise != null) {
                    throw new ConnectionPendingException();
                }
                boolean wasActive = AbstractEpollChannel.this.isActive();
                if (AbstractEpollChannel.this.doConnect(remoteAddress, localAddress)) {
                    this.fulfillConnectPromise(promise, wasActive);
                } else {
                    AbstractEpollChannel.this.connectPromise = promise;
                    AbstractEpollChannel.this.requestedRemoteAddress = remoteAddress;
                    int connectTimeoutMillis = AbstractEpollChannel.this.config().getConnectTimeoutMillis();
                    if (connectTimeoutMillis > 0) {
                        AbstractEpollChannel.this.connectTimeoutFuture = AbstractEpollChannel.this.eventLoop().schedule(new Runnable(){

                            @Override
                            public void run() {
                                ChannelPromise connectPromise = AbstractEpollChannel.this.connectPromise;
                                ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
                                if (connectPromise != null && connectPromise.tryFailure(cause)) {
                                    AbstractEpollUnsafe.this.close(AbstractEpollUnsafe.this.voidPromise());
                                }
                            }
                        }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                    }
                    promise.addListener(new ChannelFutureListener(){

                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isCancelled()) {
                                if (AbstractEpollChannel.this.connectTimeoutFuture != null) {
                                    AbstractEpollChannel.this.connectTimeoutFuture.cancel(false);
                                }
                                AbstractEpollChannel.this.connectPromise = null;
                                AbstractEpollUnsafe.this.close(AbstractEpollUnsafe.this.voidPromise());
                            }
                        }
                    });
                }
            }
            catch (Throwable t) {
                this.closeIfClosed();
                promise.tryFailure(this.annotateConnectException(t, remoteAddress));
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
            if (promise == null) {
                return;
            }
            AbstractEpollChannel.this.active = true;
            boolean active = AbstractEpollChannel.this.isActive();
            boolean promiseSet = promise.trySuccess();
            if (!wasActive && active) {
                AbstractEpollChannel.this.pipeline().fireChannelActive();
            }
            if (!promiseSet) {
                this.close(this.voidPromise());
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
            if (promise == null) {
                return;
            }
            promise.tryFailure(cause);
            this.closeIfClosed();
        }

        private void finishConnect() {
            assert (AbstractEpollChannel.this.eventLoop().inEventLoop());
            boolean connectStillInProgress = false;
            try {
                boolean wasActive = AbstractEpollChannel.this.isActive();
                if (!this.doFinishConnect()) {
                    connectStillInProgress = true;
                    return;
                }
                this.fulfillConnectPromise(AbstractEpollChannel.this.connectPromise, wasActive);
            }
            catch (Throwable t) {
                this.fulfillConnectPromise(AbstractEpollChannel.this.connectPromise, this.annotateConnectException(t, AbstractEpollChannel.this.requestedRemoteAddress));
            }
            finally {
                if (!connectStillInProgress) {
                    if (AbstractEpollChannel.this.connectTimeoutFuture != null) {
                        AbstractEpollChannel.this.connectTimeoutFuture.cancel(false);
                    }
                    AbstractEpollChannel.this.connectPromise = null;
                }
            }
        }

        private boolean doFinishConnect() throws Exception {
            if (AbstractEpollChannel.this.socket.finishConnect()) {
                AbstractEpollChannel.this.clearFlag(Native.EPOLLOUT);
                if (AbstractEpollChannel.this.requestedRemoteAddress instanceof InetSocketAddress) {
                    AbstractEpollChannel.this.remote = UnixChannelUtil.computeRemoteAddr((InetSocketAddress)AbstractEpollChannel.this.requestedRemoteAddress, AbstractEpollChannel.this.socket.remoteAddress());
                }
                AbstractEpollChannel.this.requestedRemoteAddress = null;
                return true;
            }
            AbstractEpollChannel.this.setFlag(Native.EPOLLOUT);
            return false;
        }

    }

}


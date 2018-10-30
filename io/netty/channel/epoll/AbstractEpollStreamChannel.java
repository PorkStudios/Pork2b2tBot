/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollChannelConfig;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.EpollRecvByteAllocatorHandle;
import io.netty.channel.epoll.EpollRecvByteAllocatorStreamingHandle;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.DuplexChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.SocketWritableByteChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.util.Queue;
import java.util.concurrent.Executor;

public abstract class AbstractEpollStreamChannel
extends AbstractEpollChannel
implements DuplexChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
    private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEpollStreamChannel.class);
    private static final ClosedChannelException CLEAR_SPLICE_QUEUE_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "clearSpliceQueue()");
    private static final ClosedChannelException SPLICE_TO_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "spliceTo(...)");
    private static final ClosedChannelException FAIL_SPLICE_IF_CLOSED_CLOSED_CHANNEL_EXCEPTION = ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "failSpliceIfClosed(...)");
    private final Runnable flushTask = new Runnable(){

        @Override
        public void run() {
            AbstractEpollStreamChannel.this.flush();
        }
    };
    private Queue<SpliceInTask> spliceQueue;
    private FileDescriptor pipeIn;
    private FileDescriptor pipeOut;
    private WritableByteChannel byteChannel;

    protected AbstractEpollStreamChannel(Channel parent, int fd) {
        this(parent, new LinuxSocket(fd));
    }

    protected AbstractEpollStreamChannel(int fd) {
        this(new LinuxSocket(fd));
    }

    AbstractEpollStreamChannel(LinuxSocket fd) {
        this(fd, AbstractEpollStreamChannel.isSoErrorZero(fd));
    }

    AbstractEpollStreamChannel(Channel parent, LinuxSocket fd) {
        super(parent, fd, Native.EPOLLIN, true);
        this.flags |= Native.EPOLLRDHUP;
    }

    AbstractEpollStreamChannel(Channel parent, LinuxSocket fd, SocketAddress remote) {
        super(parent, fd, Native.EPOLLIN, remote);
        this.flags |= Native.EPOLLRDHUP;
    }

    protected AbstractEpollStreamChannel(LinuxSocket fd, boolean active) {
        super(null, fd, Native.EPOLLIN, active);
        this.flags |= Native.EPOLLRDHUP;
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollStreamUnsafe();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    public final ChannelFuture spliceTo(AbstractEpollStreamChannel ch, int len) {
        return this.spliceTo(ch, len, this.newPromise());
    }

    public final ChannelFuture spliceTo(AbstractEpollStreamChannel ch, int len, ChannelPromise promise) {
        if (ch.eventLoop() != this.eventLoop()) {
            throw new IllegalArgumentException("EventLoops are not the same.");
        }
        if (len < 0) {
            throw new IllegalArgumentException("len: " + len + " (expected: >= 0)");
        }
        if (ch.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED || this.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
            throw new IllegalStateException("spliceTo() supported only when using " + (Object)((Object)EpollMode.LEVEL_TRIGGERED));
        }
        ObjectUtil.checkNotNull(promise, "promise");
        if (!this.isOpen()) {
            promise.tryFailure(SPLICE_TO_CLOSED_CHANNEL_EXCEPTION);
        } else {
            this.addToSpliceQueue(new SpliceInChannelTask(ch, len, promise));
            this.failSpliceIfClosed(promise);
        }
        return promise;
    }

    public final ChannelFuture spliceTo(FileDescriptor ch, int offset, int len) {
        return this.spliceTo(ch, offset, len, this.newPromise());
    }

    public final ChannelFuture spliceTo(FileDescriptor ch, int offset, int len, ChannelPromise promise) {
        if (len < 0) {
            throw new IllegalArgumentException("len: " + len + " (expected: >= 0)");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0 but was " + offset);
        }
        if (this.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
            throw new IllegalStateException("spliceTo() supported only when using " + (Object)((Object)EpollMode.LEVEL_TRIGGERED));
        }
        ObjectUtil.checkNotNull(promise, "promise");
        if (!this.isOpen()) {
            promise.tryFailure(SPLICE_TO_CLOSED_CHANNEL_EXCEPTION);
        } else {
            this.addToSpliceQueue(new SpliceFdTask(ch, offset, len, promise));
            this.failSpliceIfClosed(promise);
        }
        return promise;
    }

    private void failSpliceIfClosed(ChannelPromise promise) {
        if (!this.isOpen() && promise.tryFailure(FAIL_SPLICE_IF_CLOSED_CLOSED_CHANNEL_EXCEPTION)) {
            this.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    AbstractEpollStreamChannel.this.clearSpliceQueue();
                }
            });
        }
    }

    private int writeBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            in.remove();
            return 0;
        }
        if (buf.hasMemoryAddress() || buf.nioBufferCount() == 1) {
            return this.doWriteBytes(in, buf);
        }
        ByteBuffer[] nioBuffers = buf.nioBuffers();
        return this.writeBytesMultiple(in, nioBuffers, nioBuffers.length, readableBytes, this.config().getMaxBytesPerGatheringWrite());
    }

    private void adjustMaxBytesPerGatheringWrite(long attempted, long written, long oldMaxBytesPerGatheringWrite) {
        if (attempted == written) {
            if (attempted << 1 > oldMaxBytesPerGatheringWrite) {
                this.config().setMaxBytesPerGatheringWrite(attempted << 1);
            }
        } else if (attempted > 4096L && written < attempted >>> 1) {
            this.config().setMaxBytesPerGatheringWrite(attempted >>> 1);
        }
    }

    private int writeBytesMultiple(ChannelOutboundBuffer in, IovArray array) throws IOException {
        long expectedWrittenBytes = array.size();
        assert (expectedWrittenBytes != 0L);
        int cnt = array.count();
        assert (cnt != 0);
        long localWrittenBytes = this.socket.writevAddresses(array.memoryAddress(0), cnt);
        if (localWrittenBytes > 0L) {
            this.adjustMaxBytesPerGatheringWrite(expectedWrittenBytes, localWrittenBytes, array.maxBytes());
            in.removeBytes(localWrittenBytes);
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    private int writeBytesMultiple(ChannelOutboundBuffer in, ByteBuffer[] nioBuffers, int nioBufferCnt, long expectedWrittenBytes, long maxBytesPerGatheringWrite) throws IOException {
        long localWrittenBytes;
        assert (expectedWrittenBytes != 0L);
        if (expectedWrittenBytes > maxBytesPerGatheringWrite) {
            expectedWrittenBytes = maxBytesPerGatheringWrite;
        }
        if ((localWrittenBytes = this.socket.writev(nioBuffers, 0, nioBufferCnt, expectedWrittenBytes)) > 0L) {
            this.adjustMaxBytesPerGatheringWrite(expectedWrittenBytes, localWrittenBytes, maxBytesPerGatheringWrite);
            in.removeBytes(localWrittenBytes);
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    private int writeDefaultFileRegion(ChannelOutboundBuffer in, DefaultFileRegion region) throws Exception {
        long regionCount = region.count();
        if (region.transferred() >= regionCount) {
            in.remove();
            return 0;
        }
        long offset = region.transferred();
        long flushedAmount = this.socket.sendFile(region, region.position(), offset, regionCount - offset);
        if (flushedAmount > 0L) {
            in.progress(flushedAmount);
            if (region.transferred() >= regionCount) {
                in.remove();
            }
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    private int writeFileRegion(ChannelOutboundBuffer in, FileRegion region) throws Exception {
        long flushedAmount;
        if (region.transferred() >= region.count()) {
            in.remove();
            return 0;
        }
        if (this.byteChannel == null) {
            this.byteChannel = new EpollSocketWritableByteChannel();
        }
        if ((flushedAmount = region.transferTo(this.byteChannel, region.transferred())) > 0L) {
            in.progress(flushedAmount);
            if (region.transferred() >= region.count()) {
                in.remove();
            }
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        int writeSpinCount = this.config().getWriteSpinCount();
        do {
            int msgCount;
            if ((msgCount = in.size()) > 1 && in.current() instanceof ByteBuf) {
                writeSpinCount -= this.doWriteMultiple(in);
                continue;
            }
            if (msgCount == 0) {
                this.clearFlag(Native.EPOLLOUT);
                return;
            }
            writeSpinCount -= this.doWriteSingle(in);
        } while (writeSpinCount > 0);
        if (writeSpinCount == 0) {
            this.eventLoop().execute(this.flushTask);
        } else {
            this.setFlag(Native.EPOLLOUT);
        }
    }

    protected int doWriteSingle(ChannelOutboundBuffer in) throws Exception {
        Object msg = in.current();
        if (msg instanceof ByteBuf) {
            return this.writeBytes(in, (ByteBuf)msg);
        }
        if (msg instanceof DefaultFileRegion) {
            return this.writeDefaultFileRegion(in, (DefaultFileRegion)msg);
        }
        if (msg instanceof FileRegion) {
            return this.writeFileRegion(in, (FileRegion)msg);
        }
        if (msg instanceof SpliceOutTask) {
            if (!((SpliceOutTask)msg).spliceOut()) {
                return Integer.MAX_VALUE;
            }
            in.remove();
            return 1;
        }
        throw new Error();
    }

    private int doWriteMultiple(ChannelOutboundBuffer in) throws Exception {
        long maxBytesPerGatheringWrite = this.config().getMaxBytesPerGatheringWrite();
        if (PlatformDependent.hasUnsafe()) {
            IovArray array = ((EpollEventLoop)this.eventLoop()).cleanArray();
            array.maxBytes(maxBytesPerGatheringWrite);
            in.forEachFlushedMessage(array);
            if (array.count() >= 1) {
                return this.writeBytesMultiple(in, array);
            }
        } else {
            ByteBuffer[] buffers = in.nioBuffers();
            int cnt = in.nioBufferCount();
            if (cnt >= 1) {
                return this.writeBytesMultiple(in, buffers, cnt, in.nioBufferSize(), maxBytesPerGatheringWrite);
            }
        }
        in.removeBytes(0L);
        return 0;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;
            return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
        }
        if (msg instanceof FileRegion || msg instanceof SpliceOutTask) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    @Override
    protected final void doShutdownOutput() throws Exception {
        this.socket.shutdown(false, true);
    }

    private void shutdownInput0(ChannelPromise promise) {
        try {
            this.socket.shutdown(true, false);
            promise.setSuccess();
        }
        catch (Throwable cause) {
            promise.setFailure(cause);
        }
    }

    @Override
    public boolean isOutputShutdown() {
        return this.socket.isOutputShutdown();
    }

    @Override
    public boolean isInputShutdown() {
        return this.socket.isInputShutdown();
    }

    @Override
    public boolean isShutdown() {
        return this.socket.isShutdown();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }

    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise promise) {
        EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            ((AbstractChannel.AbstractUnsafe)this.unsafe()).shutdownOutput(promise);
        } else {
            loop.execute(new Runnable(){

                @Override
                public void run() {
                    ((AbstractChannel.AbstractUnsafe)AbstractEpollStreamChannel.this.unsafe()).shutdownOutput(promise);
                }
            });
        }
        return promise;
    }

    @Override
    public ChannelFuture shutdownInput() {
        return this.shutdownInput(this.newPromise());
    }

    @Override
    public ChannelFuture shutdownInput(final ChannelPromise promise) {
        Executor closeExecutor = ((EpollStreamUnsafe)this.unsafe()).prepareToClose();
        if (closeExecutor != null) {
            closeExecutor.execute(new Runnable(){

                @Override
                public void run() {
                    AbstractEpollStreamChannel.this.shutdownInput0(promise);
                }
            });
        } else {
            EventLoop loop = this.eventLoop();
            if (loop.inEventLoop()) {
                this.shutdownInput0(promise);
            } else {
                loop.execute(new Runnable(){

                    @Override
                    public void run() {
                        AbstractEpollStreamChannel.this.shutdownInput0(promise);
                    }
                });
            }
        }
        return promise;
    }

    @Override
    public ChannelFuture shutdown() {
        return this.shutdown(this.newPromise());
    }

    @Override
    public ChannelFuture shutdown(final ChannelPromise promise) {
        ChannelFuture shutdownOutputFuture = this.shutdownOutput();
        if (shutdownOutputFuture.isDone()) {
            this.shutdownOutputDone(shutdownOutputFuture, promise);
        } else {
            shutdownOutputFuture.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture shutdownOutputFuture) throws Exception {
                    AbstractEpollStreamChannel.this.shutdownOutputDone(shutdownOutputFuture, promise);
                }
            });
        }
        return promise;
    }

    private void shutdownOutputDone(final ChannelFuture shutdownOutputFuture, final ChannelPromise promise) {
        ChannelFuture shutdownInputFuture = this.shutdownInput();
        if (shutdownInputFuture.isDone()) {
            AbstractEpollStreamChannel.shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
        } else {
            shutdownInputFuture.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture shutdownInputFuture) throws Exception {
                    AbstractEpollStreamChannel.shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
                }
            });
        }
    }

    private static void shutdownDone(ChannelFuture shutdownOutputFuture, ChannelFuture shutdownInputFuture, ChannelPromise promise) {
        Throwable shutdownOutputCause = shutdownOutputFuture.cause();
        Throwable shutdownInputCause = shutdownInputFuture.cause();
        if (shutdownOutputCause != null) {
            if (shutdownInputCause != null) {
                logger.debug("Exception suppressed because a previous exception occurred.", shutdownInputCause);
            }
            promise.setFailure(shutdownOutputCause);
        } else if (shutdownInputCause != null) {
            promise.setFailure(shutdownInputCause);
        } else {
            promise.setSuccess();
        }
    }

    @Override
    protected void doClose() throws Exception {
        try {
            super.doClose();
        }
        finally {
            AbstractEpollStreamChannel.safeClosePipe(this.pipeIn);
            AbstractEpollStreamChannel.safeClosePipe(this.pipeOut);
            this.clearSpliceQueue();
        }
    }

    private void clearSpliceQueue() {
        SpliceInTask task;
        if (this.spliceQueue == null) {
            return;
        }
        while ((task = this.spliceQueue.poll()) != null) {
            task.promise.tryFailure(CLEAR_SPLICE_QUEUE_CLOSED_CHANNEL_EXCEPTION);
        }
    }

    private static void safeClosePipe(FileDescriptor fd) {
        block3 : {
            if (fd != null) {
                try {
                    fd.close();
                }
                catch (IOException e) {
                    if (!logger.isWarnEnabled()) break block3;
                    logger.warn("Error while closing a pipe", e);
                }
            }
        }
    }

    private void addToSpliceQueue(final SpliceInTask task) {
        EventLoop eventLoop = this.eventLoop();
        if (eventLoop.inEventLoop()) {
            this.addToSpliceQueue0(task);
        } else {
            eventLoop.execute(new Runnable(){

                @Override
                public void run() {
                    AbstractEpollStreamChannel.this.addToSpliceQueue0(task);
                }
            });
        }
    }

    private void addToSpliceQueue0(SpliceInTask task) {
        if (this.spliceQueue == null) {
            this.spliceQueue = PlatformDependent.newMpscQueue();
        }
        this.spliceQueue.add(task);
    }

    private final class EpollSocketWritableByteChannel
    extends SocketWritableByteChannel {
        EpollSocketWritableByteChannel() {
            super(AbstractEpollStreamChannel.this.socket);
        }

        @Override
        protected ByteBufAllocator alloc() {
            return AbstractEpollStreamChannel.this.alloc();
        }
    }

    private final class SpliceFdTask
    extends SpliceInTask {
        private final FileDescriptor fd;
        private final ChannelPromise promise;
        private final int offset;

        SpliceFdTask(FileDescriptor fd, int offset, int len, ChannelPromise promise) {
            super(len, promise);
            this.fd = fd;
            this.promise = promise;
            this.offset = offset;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public boolean spliceIn(RecvByteBufAllocator.Handle handle) {
            assert (AbstractEpollStreamChannel.this.eventLoop().inEventLoop());
            if (this.len == 0) {
                this.promise.setSuccess();
                return true;
            }
            try {
                FileDescriptor[] pipe = FileDescriptor.pipe();
                FileDescriptor pipeIn = pipe[0];
                FileDescriptor pipeOut = pipe[1];
                try {
                    boolean splicedOut;
                    int splicedIn = this.spliceIn(pipeOut, handle);
                    if (splicedIn > 0) {
                        if (this.len != Integer.MAX_VALUE) {
                            this.len -= splicedIn;
                        }
                        while ((splicedIn -= (splicedOut = Native.splice(pipeIn.intValue(), -1L, this.fd.intValue(), this.offset, splicedIn))) > 0) {
                        }
                        if (this.len == 0) {
                            this.promise.setSuccess();
                            splicedOut = true;
                            return splicedOut;
                        }
                    }
                    splicedOut = false;
                    return splicedOut;
                }
                finally {
                    AbstractEpollStreamChannel.safeClosePipe(pipeIn);
                    AbstractEpollStreamChannel.safeClosePipe(pipeOut);
                }
            }
            catch (Throwable cause) {
                this.promise.setFailure(cause);
                return true;
            }
        }
    }

    private final class SpliceOutTask {
        private final AbstractEpollStreamChannel ch;
        private final boolean autoRead;
        private int len;

        SpliceOutTask(AbstractEpollStreamChannel ch, int len, boolean autoRead) {
            this.ch = ch;
            this.len = len;
            this.autoRead = autoRead;
        }

        public boolean spliceOut() throws Exception {
            assert (this.ch.eventLoop().inEventLoop());
            try {
                int splicedOut = Native.splice(this.ch.pipeIn.intValue(), -1L, this.ch.socket.intValue(), -1L, this.len);
                this.len -= splicedOut;
                if (this.len == 0) {
                    if (this.autoRead) {
                        AbstractEpollStreamChannel.this.config().setAutoRead(true);
                    }
                    return true;
                }
                return false;
            }
            catch (IOException e) {
                if (this.autoRead) {
                    AbstractEpollStreamChannel.this.config().setAutoRead(true);
                }
                throw e;
            }
        }
    }

    private final class SpliceInChannelTask
    extends SpliceInTask
    implements ChannelFutureListener {
        private final AbstractEpollStreamChannel ch;

        SpliceInChannelTask(AbstractEpollStreamChannel ch, int len, ChannelPromise promise) {
            super(len, promise);
            this.ch = ch;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                this.promise.setFailure(future.cause());
            }
        }

        @Override
        public boolean spliceIn(RecvByteBufAllocator.Handle handle) {
            assert (this.ch.eventLoop().inEventLoop());
            if (this.len == 0) {
                this.promise.setSuccess();
                return true;
            }
            try {
                int splicedIn;
                FileDescriptor pipeOut = this.ch.pipeOut;
                if (pipeOut == null) {
                    FileDescriptor[] pipe = FileDescriptor.pipe();
                    this.ch.pipeIn = pipe[0];
                    pipeOut = this.ch.pipeOut = pipe[1];
                }
                if ((splicedIn = this.spliceIn(pipeOut, handle)) > 0) {
                    if (this.len != Integer.MAX_VALUE) {
                        this.len -= splicedIn;
                    }
                    ChannelPromise splicePromise = this.len == 0 ? this.promise : this.ch.newPromise().addListener(this);
                    boolean autoRead = AbstractEpollStreamChannel.this.config().isAutoRead();
                    this.ch.unsafe().write(new SpliceOutTask(this.ch, splicedIn, autoRead), splicePromise);
                    this.ch.unsafe().flush();
                    if (autoRead && !splicePromise.isDone()) {
                        AbstractEpollStreamChannel.this.config().setAutoRead(false);
                    }
                }
                return this.len == 0;
            }
            catch (Throwable cause) {
                this.promise.setFailure(cause);
                return true;
            }
        }
    }

    protected abstract class SpliceInTask {
        final ChannelPromise promise;
        int len;

        protected SpliceInTask(int len, ChannelPromise promise) {
            this.promise = promise;
            this.len = len;
        }

        abstract boolean spliceIn(RecvByteBufAllocator.Handle var1);

        protected final int spliceIn(FileDescriptor pipeOut, RecvByteBufAllocator.Handle handle) throws IOException {
            int localSplicedIn;
            int length = Math.min(handle.guess(), this.len);
            int splicedIn = 0;
            while ((localSplicedIn = Native.splice(AbstractEpollStreamChannel.this.socket.intValue(), -1L, pipeOut.intValue(), -1L, length)) != 0) {
                splicedIn += localSplicedIn;
                length -= localSplicedIn;
            }
            return splicedIn;
        }
    }

    class EpollStreamUnsafe
    extends AbstractEpollChannel.AbstractEpollUnsafe {
        EpollStreamUnsafe() {
        }

        @Override
        protected Executor prepareToClose() {
            return super.prepareToClose();
        }

        private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, EpollRecvByteAllocatorHandle allocHandle) {
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    this.readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                } else {
                    byteBuf.release();
                }
            }
            allocHandle.readComplete();
            pipeline.fireChannelReadComplete();
            pipeline.fireExceptionCaught(cause);
            if (close || cause instanceof IOException) {
                this.shutdownInput(false);
            }
        }

        @Override
        EpollRecvByteAllocatorHandle newEpollHandle(RecvByteBufAllocator.ExtendedHandle handle) {
            return new EpollRecvByteAllocatorStreamingHandle(handle);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void epollInReady() {
            EpollChannelConfig config = AbstractEpollStreamChannel.this.config();
            if (AbstractEpollStreamChannel.this.shouldBreakEpollInReady(config)) {
                this.clearEpollIn0();
                return;
            }
            EpollRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.edgeTriggered(AbstractEpollStreamChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline pipeline = AbstractEpollStreamChannel.this.pipeline();
            ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            this.epollInBefore();
            ByteBuf byteBuf = null;
            boolean close = false;
            try {
                do {
                    SpliceInTask spliceTask;
                    if (AbstractEpollStreamChannel.this.spliceQueue != null && (spliceTask = (SpliceInTask)AbstractEpollStreamChannel.this.spliceQueue.peek()) != null) {
                        if (!spliceTask.spliceIn(allocHandle)) break;
                        if (!AbstractEpollStreamChannel.this.isActive()) continue;
                        AbstractEpollStreamChannel.this.spliceQueue.remove();
                        continue;
                    }
                    byteBuf = allocHandle.allocate(allocator);
                    allocHandle.lastBytesRead(AbstractEpollStreamChannel.this.doReadBytes(byteBuf));
                    if (allocHandle.lastBytesRead() <= 0) {
                        byteBuf.release();
                        byteBuf = null;
                        boolean bl = close = allocHandle.lastBytesRead() < 0;
                        if (!close) break;
                        this.readPending = false;
                        break;
                    }
                    allocHandle.incMessagesRead(1);
                    this.readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                    byteBuf = null;
                    if (AbstractEpollStreamChannel.this.shouldBreakEpollInReady(config)) break;
                } while (allocHandle.continueReading());
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (close) {
                    this.shutdownInput(false);
                }
            }
            catch (Throwable t) {
                this.handleReadException(pipeline, byteBuf, t, close, allocHandle);
            }
            finally {
                this.epollInFinally(config);
            }
        }
    }

}


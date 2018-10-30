/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel.epoll;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.LinuxSocket;
import io.netty.channel.epoll.Native;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class AbstractEpollServerChannel
extends AbstractEpollChannel
implements ServerChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);

    protected AbstractEpollServerChannel(int fd) {
        this(new LinuxSocket(fd), false);
    }

    AbstractEpollServerChannel(LinuxSocket fd) {
        this(fd, AbstractEpollServerChannel.isSoErrorZero(fd));
    }

    AbstractEpollServerChannel(LinuxSocket fd, boolean active) {
        super(null, fd, Native.EPOLLIN, active);
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    protected boolean isCompatible(EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }

    @Override
    protected InetSocketAddress remoteAddress0() {
        return null;
    }

    @Override
    protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
        return new EpollServerSocketUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object filterOutboundMessage(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    abstract Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception;

    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    final class EpollServerSocketUnsafe
    extends AbstractEpollChannel.AbstractEpollUnsafe {
        private final byte[] acceptedAddress = new byte[26];

        EpollServerSocketUnsafe() {
        }

        @Override
        public void connect(SocketAddress socketAddress, SocketAddress socketAddress2, ChannelPromise channelPromise) {
            channelPromise.setFailure(new UnsupportedOperationException());
        }

        /*
         * Exception decompiling
         */
        @Override
        void epollInReady() {
            // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
            // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[DOLOOP]], but top level block is 8[SIMPLE_IF_TAKEN]
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:416)
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:468)
            // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:2960)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:818)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:196)
            // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:141)
            // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:95)
            // org.benf.cfr.reader.entities.Method.analyse(Method.java:372)
            // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:867)
            // org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:749)
            // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:847)
            // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:768)
            // org.benf.cfr.reader.Main.doJar(Main.java:141)
            // org.benf.cfr.reader.Main.main(Main.java:242)
            throw new IllegalStateException("Decompilation failed");
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import org.java_websocket.WrappedByteChannel;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SSLSocketChannel2
implements ByteChannel,
WrappedByteChannel {
    protected static ByteBuffer emptybuffer = ByteBuffer.allocate(0);
    protected ExecutorService exec;
    protected List<Future<?>> tasks;
    protected ByteBuffer inData;
    protected ByteBuffer outCrypt;
    protected ByteBuffer inCrypt;
    protected SocketChannel socketChannel;
    protected SelectionKey selectionKey;
    protected SSLEngine sslEngine;
    protected SSLEngineResult readEngineResult;
    protected SSLEngineResult writeEngineResult;
    protected int bufferallocations = 0;

    public SSLSocketChannel2(SocketChannel channel, SSLEngine sslEngine, ExecutorService exec, SelectionKey key) throws IOException {
        if (channel == null || sslEngine == null || exec == null) {
            throw new IllegalArgumentException("parameter must not be null");
        }
        this.socketChannel = channel;
        this.sslEngine = sslEngine;
        this.exec = exec;
        this.readEngineResult = this.writeEngineResult = new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, sslEngine.getHandshakeStatus(), 0, 0);
        this.tasks = new ArrayList(3);
        if (key != null) {
            key.interestOps(key.interestOps() | 4);
            this.selectionKey = key;
        }
        this.createBuffers(sslEngine.getSession());
        this.socketChannel.write(this.wrap(emptybuffer));
        this.processHandshake();
    }

    private void consumeFutureUninterruptible(Future<?> f) {
        try {
            boolean interrupted = false;
            do {
                try {
                    f.get();
                }
                catch (InterruptedException e) {
                    interrupted = true;
                    continue;
                }
                break;
            } while (true);
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void processHandshake() throws IOException {
        if (this.sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            return;
        }
        if (!this.tasks.isEmpty()) {
            Iterator<Future<?>> it = this.tasks.iterator();
            while (it.hasNext()) {
                Future<?> f = it.next();
                if (f.isDone()) {
                    it.remove();
                    continue;
                }
                if (this.isBlocking()) {
                    this.consumeFutureUninterruptible(f);
                }
                return;
            }
        }
        if (this.sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
            if (!this.isBlocking() || this.readEngineResult.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                this.inCrypt.compact();
                int read = this.socketChannel.read(this.inCrypt);
                if (read == -1) {
                    throw new IOException("connection closed unexpectedly by peer");
                }
                this.inCrypt.flip();
            }
            this.inData.compact();
            this.unwrap();
            if (this.readEngineResult.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                this.createBuffers(this.sslEngine.getSession());
                return;
            }
        }
        this.consumeDelegatedTasks();
        if (this.tasks.isEmpty() || this.sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
            this.socketChannel.write(this.wrap(emptybuffer));
            if (this.writeEngineResult.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                this.createBuffers(this.sslEngine.getSession());
                return;
            }
        }
        assert (this.sslEngine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING);
        this.bufferallocations = 1;
    }

    private synchronized ByteBuffer wrap(ByteBuffer b) throws SSLException {
        this.outCrypt.compact();
        this.writeEngineResult = this.sslEngine.wrap(b, this.outCrypt);
        this.outCrypt.flip();
        return this.outCrypt;
    }

    private synchronized ByteBuffer unwrap() throws SSLException {
        int rem;
        if (this.readEngineResult.getStatus() == SSLEngineResult.Status.CLOSED && this.sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            try {
                this.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        do {
            rem = this.inData.remaining();
            this.readEngineResult = this.sslEngine.unwrap(this.inCrypt, this.inData);
        } while (this.readEngineResult.getStatus() == SSLEngineResult.Status.OK && (rem != this.inData.remaining() || this.sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP));
        this.inData.flip();
        return this.inData;
    }

    protected void consumeDelegatedTasks() {
        Runnable task;
        while ((task = this.sslEngine.getDelegatedTask()) != null) {
            this.tasks.add(this.exec.submit(task));
        }
    }

    protected void createBuffers(SSLSession session) {
        int netBufferMax = session.getPacketBufferSize();
        int appBufferMax = Math.max(session.getApplicationBufferSize(), netBufferMax);
        if (this.inData == null) {
            this.inData = ByteBuffer.allocate(appBufferMax);
            this.outCrypt = ByteBuffer.allocate(netBufferMax);
            this.inCrypt = ByteBuffer.allocate(netBufferMax);
        } else {
            if (this.inData.capacity() != appBufferMax) {
                this.inData = ByteBuffer.allocate(appBufferMax);
            }
            if (this.outCrypt.capacity() != netBufferMax) {
                this.outCrypt = ByteBuffer.allocate(netBufferMax);
            }
            if (this.inCrypt.capacity() != netBufferMax) {
                this.inCrypt = ByteBuffer.allocate(netBufferMax);
            }
        }
        this.inData.rewind();
        this.inData.flip();
        this.inCrypt.rewind();
        this.inCrypt.flip();
        this.outCrypt.rewind();
        this.outCrypt.flip();
        ++this.bufferallocations;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        if (!this.isHandShakeComplete()) {
            this.processHandshake();
            return 0;
        }
        int num = this.socketChannel.write(this.wrap(src));
        if (this.writeEngineResult.getStatus() == SSLEngineResult.Status.CLOSED) {
            throw new EOFException("Connection is closed");
        }
        return num;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int transfered;
        do {
            int purged;
            if (!dst.hasRemaining()) {
                return 0;
            }
            if (!this.isHandShakeComplete()) {
                if (this.isBlocking()) {
                    while (!this.isHandShakeComplete()) {
                        this.processHandshake();
                    }
                } else {
                    this.processHandshake();
                    if (!this.isHandShakeComplete()) {
                        return 0;
                    }
                }
            }
            if ((purged = this.readRemaining(dst)) != 0) {
                return purged;
            }
            assert (this.inData.position() == 0);
            this.inData.clear();
            if (!this.inCrypt.hasRemaining()) {
                this.inCrypt.clear();
            } else {
                this.inCrypt.compact();
            }
            if ((this.isBlocking() || this.readEngineResult.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) && this.socketChannel.read(this.inCrypt) == -1) {
                return -1;
            }
            this.inCrypt.flip();
            this.unwrap();
            transfered = this.transfereTo(this.inData, dst);
        } while (transfered == 0 && this.isBlocking());
        return transfered;
    }

    private int readRemaining(ByteBuffer dst) throws SSLException {
        if (this.inData.hasRemaining()) {
            return this.transfereTo(this.inData, dst);
        }
        if (!this.inData.hasRemaining()) {
            this.inData.clear();
        }
        if (this.inCrypt.hasRemaining()) {
            this.unwrap();
            int amount = this.transfereTo(this.inData, dst);
            if (this.readEngineResult.getStatus() == SSLEngineResult.Status.CLOSED) {
                return -1;
            }
            if (amount > 0) {
                return amount;
            }
        }
        return 0;
    }

    public boolean isConnected() {
        return this.socketChannel.isConnected();
    }

    @Override
    public void close() throws IOException {
        this.sslEngine.closeOutbound();
        this.sslEngine.getSession().invalidate();
        if (this.socketChannel.isOpen()) {
            this.socketChannel.write(this.wrap(emptybuffer));
        }
        this.socketChannel.close();
    }

    private boolean isHandShakeComplete() {
        SSLEngineResult.HandshakeStatus status = this.sslEngine.getHandshakeStatus();
        return status == SSLEngineResult.HandshakeStatus.FINISHED || status == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }

    public SelectableChannel configureBlocking(boolean b) throws IOException {
        return this.socketChannel.configureBlocking(b);
    }

    public boolean connect(SocketAddress remote) throws IOException {
        return this.socketChannel.connect(remote);
    }

    public boolean finishConnect() throws IOException {
        return this.socketChannel.finishConnect();
    }

    public Socket socket() {
        return this.socketChannel.socket();
    }

    public boolean isInboundDone() {
        return this.sslEngine.isInboundDone();
    }

    @Override
    public boolean isOpen() {
        return this.socketChannel.isOpen();
    }

    @Override
    public boolean isNeedWrite() {
        return this.outCrypt.hasRemaining() || !this.isHandShakeComplete();
    }

    @Override
    public void writeMore() throws IOException {
        this.write(this.outCrypt);
    }

    @Override
    public boolean isNeedRead() {
        return this.inData.hasRemaining() || this.inCrypt.hasRemaining() && this.readEngineResult.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW && this.readEngineResult.getStatus() != SSLEngineResult.Status.CLOSED;
    }

    @Override
    public int readMore(ByteBuffer dst) throws SSLException {
        return this.readRemaining(dst);
    }

    private int transfereTo(ByteBuffer from, ByteBuffer to) {
        int toremain;
        int fremain = from.remaining();
        if (fremain > (toremain = to.remaining())) {
            int limit = Math.min(fremain, toremain);
            for (int i = 0; i < limit; ++i) {
                to.put(from.get());
            }
            return limit;
        }
        to.put(from);
        return fremain;
    }

    @Override
    public boolean isBlocking() {
        return this.socketChannel.isBlocking();
    }
}


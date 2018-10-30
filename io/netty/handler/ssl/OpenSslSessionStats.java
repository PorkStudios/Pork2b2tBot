/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.SSLContext
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.internal.tcnative.SSLContext;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class OpenSslSessionStats {
    private final ReferenceCountedOpenSslContext context;

    OpenSslSessionStats(ReferenceCountedOpenSslContext context) {
        this.context = context;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long number() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionNumber((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long connect() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionConnect((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long connectGood() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionConnectGood((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long connectRenegotiate() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionConnectRenegotiate((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long accept() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionAccept((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long acceptGood() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionAcceptGood((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long acceptRenegotiate() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionAcceptRenegotiate((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long hits() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionHits((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long cbHits() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionCbHits((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long misses() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionMisses((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long timeouts() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionTimeouts((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long cacheFull() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionCacheFull((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long ticketKeyFail() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionTicketKeyFail((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long ticketKeyNew() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionTicketKeyNew((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long ticketKeyRenew() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionTicketKeyRenew((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long ticketKeyResume() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.sessionTicketKeyResume((long)this.context.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }
}


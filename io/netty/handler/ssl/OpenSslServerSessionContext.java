/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.SSL
 *  io.netty.internal.tcnative.SSLContext
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class OpenSslServerSessionContext
extends OpenSslSessionContext {
    OpenSslServerSessionContext(ReferenceCountedOpenSslContext context) {
        super(context);
    }

    @Override
    public void setSessionTimeout(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException();
        }
        Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setSessionCacheTimeout((long)this.context.ctx, (long)seconds);
        }
        finally {
            writerLock.unlock();
        }
    }

    @Override
    public int getSessionTimeout() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            int n = (int)SSLContext.getSessionCacheTimeout((long)this.context.ctx);
            return n;
        }
        finally {
            readerLock.unlock();
        }
    }

    @Override
    public void setSessionCacheSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setSessionCacheSize((long)this.context.ctx, (long)size);
        }
        finally {
            writerLock.unlock();
        }
    }

    @Override
    public int getSessionCacheSize() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            int n = (int)SSLContext.getSessionCacheSize((long)this.context.ctx);
            return n;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSessionCacheEnabled(boolean enabled) {
        long mode = enabled ? SSL.SSL_SESS_CACHE_SERVER : SSL.SSL_SESS_CACHE_OFF;
        Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setSessionCacheMode((long)this.context.ctx, (long)mode);
        }
        finally {
            writerLock.unlock();
        }
    }

    @Override
    public boolean isSessionCacheEnabled() {
        Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            boolean bl = SSLContext.getSessionCacheMode((long)this.context.ctx) == SSL.SSL_SESS_CACHE_SERVER;
            return bl;
        }
        finally {
            readerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setSessionIdContext(byte[] sidCtx) {
        Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            boolean bl = SSLContext.setSessionIdContext((long)this.context.ctx, (byte[])sidCtx);
            return bl;
        }
        finally {
            writerLock.unlock();
        }
    }
}


/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.SSL
 *  io.netty.internal.tcnative.SSLContext
 *  io.netty.internal.tcnative.SessionTicketKey
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslSessionStats;
import io.netty.handler.ssl.OpenSslSessionTicketKey;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SessionTicketKey;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;

public abstract class OpenSslSessionContext
implements SSLSessionContext {
    private static final Enumeration<byte[]> EMPTY = new EmptyEnumeration();
    private final OpenSslSessionStats stats;
    final ReferenceCountedOpenSslContext context;

    OpenSslSessionContext(ReferenceCountedOpenSslContext context) {
        this.context = context;
        this.stats = new OpenSslSessionStats(context);
    }

    @Override
    public SSLSession getSession(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes");
        }
        return null;
    }

    @Override
    public Enumeration<byte[]> getIds() {
        return EMPTY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public void setTicketKeys(byte[] keys) {
        if (keys.length % 48 != 0) {
            throw new IllegalArgumentException("keys.length % 48 != 0");
        }
        SessionTicketKey[] tickets = new SessionTicketKey[keys.length / 48];
        int a = 0;
        for (int i = 0; i < tickets.length; ++i) {
            byte[] name = Arrays.copyOfRange(keys, a, 16);
            byte[] hmacKey = Arrays.copyOfRange(keys, a += 16, 16);
            byte[] aesKey = Arrays.copyOfRange(keys, a, 16);
            a += 16;
            tickets[i += 16] = new SessionTicketKey(name, hmacKey, aesKey);
        }
        Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.clearOptions((long)this.context.ctx, (int)SSL.SSL_OP_NO_TICKET);
            SSLContext.setSessionTicketKeys((long)this.context.ctx, (SessionTicketKey[])tickets);
        }
        finally {
            writerLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public /* varargs */ void setTicketKeys(OpenSslSessionTicketKey ... keys) {
        ObjectUtil.checkNotNull(keys, "keys");
        SessionTicketKey[] ticketKeys = new SessionTicketKey[keys.length];
        for (int i = 0; i < ticketKeys.length; ++i) {
            ticketKeys[i] = keys[i].key;
        }
        Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.clearOptions((long)this.context.ctx, (int)SSL.SSL_OP_NO_TICKET);
            SSLContext.setSessionTicketKeys((long)this.context.ctx, (SessionTicketKey[])ticketKeys);
        }
        finally {
            writerLock.unlock();
        }
    }

    public abstract void setSessionCacheEnabled(boolean var1);

    public abstract boolean isSessionCacheEnabled();

    public OpenSslSessionStats stats() {
        return this.stats;
    }

    private static final class EmptyEnumeration
    implements Enumeration<byte[]> {
        private EmptyEnumeration() {
        }

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public byte[] nextElement() {
            throw new NoSuchElementException();
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.dns.AbstractDnsOptPseudoRrRecord;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import io.netty.resolver.dns.DnsQueryContextManager;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

final class DnsQueryContext {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsQueryContext.class);
    private final DnsNameResolver parent;
    private final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise;
    private final int id;
    private final DnsQuestion question;
    private final DnsRecord[] additionals;
    private final DnsRecord optResource;
    private final InetSocketAddress nameServerAddr;
    private final boolean recursionDesired;
    private volatile ScheduledFuture<?> timeoutFuture;

    DnsQueryContext(DnsNameResolver parent, InetSocketAddress nameServerAddr, DnsQuestion question, DnsRecord[] additionals, Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise) {
        this.parent = ObjectUtil.checkNotNull(parent, "parent");
        this.nameServerAddr = ObjectUtil.checkNotNull(nameServerAddr, "nameServerAddr");
        this.question = ObjectUtil.checkNotNull(question, "question");
        this.additionals = ObjectUtil.checkNotNull(additionals, "additionals");
        this.promise = ObjectUtil.checkNotNull(promise, "promise");
        this.recursionDesired = parent.isRecursionDesired();
        this.id = parent.queryContextManager.add(this);
        this.optResource = parent.isOptResourceEnabled() ? new AbstractDnsOptPseudoRrRecord(parent.maxPayloadSize(), 0, 0){} : null;
    }

    InetSocketAddress nameServerAddr() {
        return this.nameServerAddr;
    }

    DnsQuestion question() {
        return this.question;
    }

    void query(ChannelPromise writePromise) {
        DnsQuestion question = this.question();
        InetSocketAddress nameServerAddr = this.nameServerAddr();
        DatagramDnsQuery query = new DatagramDnsQuery(null, nameServerAddr, this.id);
        query.setRecursionDesired(this.recursionDesired);
        query.addRecord(DnsSection.QUESTION, question);
        for (DnsRecord record : this.additionals) {
            query.addRecord(DnsSection.ADDITIONAL, record);
        }
        if (this.optResource != null) {
            query.addRecord(DnsSection.ADDITIONAL, this.optResource);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("{} WRITE: [{}: {}], {}", this.parent.ch, this.id, nameServerAddr, question);
        }
        this.sendQuery(query, writePromise);
    }

    private void sendQuery(final DnsQuery query, final ChannelPromise writePromise) {
        if (this.parent.channelFuture.isDone()) {
            this.writeQuery(query, writePromise);
        } else {
            this.parent.channelFuture.addListener((GenericFutureListener<Future<Channel>>)new GenericFutureListener<Future<? super Channel>>(){

                @Override
                public void operationComplete(Future<? super Channel> future) throws Exception {
                    if (future.isSuccess()) {
                        DnsQueryContext.this.writeQuery(query, writePromise);
                    } else {
                        Throwable cause = future.cause();
                        DnsQueryContext.this.promise.tryFailure(cause);
                        writePromise.setFailure(cause);
                    }
                }
            });
        }
    }

    private void writeQuery(DnsQuery query, ChannelPromise writePromise) {
        final ChannelFuture writeFuture = this.parent.ch.writeAndFlush(query, writePromise);
        if (writeFuture.isDone()) {
            this.onQueryWriteCompletion(writeFuture);
        } else {
            writeFuture.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    DnsQueryContext.this.onQueryWriteCompletion(writeFuture);
                }
            });
        }
    }

    private void onQueryWriteCompletion(ChannelFuture writeFuture) {
        if (!writeFuture.isSuccess()) {
            this.setFailure("failed to send a query", writeFuture.cause());
            return;
        }
        final long queryTimeoutMillis = this.parent.queryTimeoutMillis();
        if (queryTimeoutMillis > 0L) {
            this.timeoutFuture = this.parent.ch.eventLoop().schedule(new Runnable(){

                @Override
                public void run() {
                    if (DnsQueryContext.this.promise.isDone()) {
                        return;
                    }
                    DnsQueryContext.this.setFailure("query timed out after " + queryTimeoutMillis + " milliseconds", null);
                }
            }, queryTimeoutMillis, TimeUnit.MILLISECONDS);
        }
    }

    void finish(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> envelope) {
        DnsResponse res = envelope.content();
        if (res.count(DnsSection.QUESTION) != 1) {
            logger.warn("Received a DNS response with invalid number of questions: {}", (Object)envelope);
            return;
        }
        if (!this.question().equals(res.recordAt(DnsSection.QUESTION))) {
            logger.warn("Received a mismatching DNS response: {}", (Object)envelope);
            return;
        }
        this.setSuccess(envelope);
    }

    private void setSuccess(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> envelope) {
        AddressedEnvelope<? extends DnsResponse, InetSocketAddress> castResponse;
        Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise;
        this.parent.queryContextManager.remove(this.nameServerAddr(), this.id);
        ScheduledFuture<?> timeoutFuture = this.timeoutFuture;
        if (timeoutFuture != null) {
            timeoutFuture.cancel(false);
        }
        if ((promise = this.promise).setUncancellable() && !promise.trySuccess(castResponse = envelope.retain())) {
            envelope.release();
        }
    }

    private void setFailure(String message, Throwable cause) {
        InetSocketAddress nameServerAddr = this.nameServerAddr();
        this.parent.queryContextManager.remove(nameServerAddr, this.id);
        StringBuilder buf = new StringBuilder(message.length() + 64);
        buf.append('[').append(nameServerAddr).append("] ").append(message).append(" (no stack trace available)");
        DnsNameResolverException e = cause == null ? new DnsNameResolverTimeoutException(nameServerAddr, this.question(), buf.toString()) : new DnsNameResolverException(nameServerAddr, this.question(), buf.toString(), cause);
        this.promise.tryFailure(e);
    }

}


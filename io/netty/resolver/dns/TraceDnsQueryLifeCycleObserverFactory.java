/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.resolver.dns.DnsQueryLifecycleObserver;
import io.netty.resolver.dns.DnsQueryLifecycleObserverFactory;
import io.netty.resolver.dns.TraceDnsQueryLifecycleObserver;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

final class TraceDnsQueryLifeCycleObserverFactory
implements DnsQueryLifecycleObserverFactory {
    private static final InternalLogger DEFAULT_LOGGER = InternalLoggerFactory.getInstance(TraceDnsQueryLifeCycleObserverFactory.class);
    private static final InternalLogLevel DEFAULT_LEVEL = InternalLogLevel.DEBUG;
    private final InternalLogger logger;
    private final InternalLogLevel level;

    TraceDnsQueryLifeCycleObserverFactory() {
        this(DEFAULT_LOGGER, DEFAULT_LEVEL);
    }

    TraceDnsQueryLifeCycleObserverFactory(InternalLogger logger, InternalLogLevel level) {
        this.logger = ObjectUtil.checkNotNull(logger, "logger");
        this.level = ObjectUtil.checkNotNull(level, "level");
    }

    @Override
    public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion question) {
        return new TraceDnsQueryLifecycleObserver(question, this.logger, this.level);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.resolver.dns.DnsQueryLifecycleObserver;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import java.net.InetSocketAddress;
import java.util.List;

final class TraceDnsQueryLifecycleObserver
implements DnsQueryLifecycleObserver {
    private final InternalLogger logger;
    private final InternalLogLevel level;
    private final DnsQuestion question;
    private InetSocketAddress dnsServerAddress;

    TraceDnsQueryLifecycleObserver(DnsQuestion question, InternalLogger logger, InternalLogLevel level) {
        this.question = ObjectUtil.checkNotNull(question, "question");
        this.logger = ObjectUtil.checkNotNull(logger, "logger");
        this.level = ObjectUtil.checkNotNull(level, "level");
    }

    @Override
    public void queryWritten(InetSocketAddress dnsServerAddress, ChannelFuture future) {
        this.dnsServerAddress = dnsServerAddress;
    }

    @Override
    public void queryCancelled(int queriesRemaining) {
        if (this.dnsServerAddress != null) {
            this.logger.log(this.level, "from {} : {} cancelled with {} queries remaining", this.dnsServerAddress, this.question, queriesRemaining);
        } else {
            this.logger.log(this.level, "{} query never written and cancelled with {} queries remaining", (Object)this.question, (Object)queriesRemaining);
        }
    }

    @Override
    public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> nameServers) {
        this.logger.log(this.level, "from {} : {} redirected", (Object)this.dnsServerAddress, (Object)this.question);
        return this;
    }

    @Override
    public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion cnameQuestion) {
        this.logger.log(this.level, "from {} : {} CNAME question {}", this.dnsServerAddress, this.question, cnameQuestion);
        return this;
    }

    @Override
    public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode code) {
        this.logger.log(this.level, "from {} : {} no answer {}", this.dnsServerAddress, this.question, code);
        return this;
    }

    @Override
    public void queryFailed(Throwable cause) {
        if (this.dnsServerAddress != null) {
            this.logger.log(this.level, "from {} : {} failure", this.dnsServerAddress, this.question, cause);
        } else {
            this.logger.log(this.level, "{} query never written and failed", (Object)this.question, (Object)cause);
        }
    }

    @Override
    public void querySucceed() {
    }
}


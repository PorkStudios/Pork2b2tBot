/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ipfilter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class RuleBasedIpFilter
extends AbstractRemoteAddressFilter<InetSocketAddress> {
    private final IpFilterRule[] rules;

    public /* varargs */ RuleBasedIpFilter(IpFilterRule ... rules) {
        if (rules == null) {
            throw new NullPointerException("rules");
        }
        this.rules = rules;
    }

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
        for (IpFilterRule rule : this.rules) {
            if (rule == null) break;
            if (!rule.matches(remoteAddress)) continue;
            return rule.ruleType() == IpFilterRuleType.ACCEPT;
        }
        return true;
    }
}


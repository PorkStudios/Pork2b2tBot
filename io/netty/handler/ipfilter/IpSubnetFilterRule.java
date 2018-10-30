/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ipfilter;

import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.util.internal.SocketUtils;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class IpSubnetFilterRule
implements IpFilterRule {
    private final IpFilterRule filterRule;

    public IpSubnetFilterRule(String ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
        try {
            this.filterRule = IpSubnetFilterRule.selectFilterRule(SocketUtils.addressByName(ipAddress), cidrPrefix, ruleType);
        }
        catch (UnknownHostException e) {
            throw new IllegalArgumentException("ipAddress", e);
        }
    }

    public IpSubnetFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
        this.filterRule = IpSubnetFilterRule.selectFilterRule(ipAddress, cidrPrefix, ruleType);
    }

    private static IpFilterRule selectFilterRule(InetAddress ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
        if (ipAddress == null) {
            throw new NullPointerException("ipAddress");
        }
        if (ruleType == null) {
            throw new NullPointerException("ruleType");
        }
        if (ipAddress instanceof Inet4Address) {
            return new Ip4SubnetFilterRule((Inet4Address)ipAddress, cidrPrefix, ruleType);
        }
        if (ipAddress instanceof Inet6Address) {
            return new Ip6SubnetFilterRule((Inet6Address)ipAddress, cidrPrefix, ruleType);
        }
        throw new IllegalArgumentException("Only IPv4 and IPv6 addresses are supported");
    }

    @Override
    public boolean matches(InetSocketAddress remoteAddress) {
        return this.filterRule.matches(remoteAddress);
    }

    @Override
    public IpFilterRuleType ruleType() {
        return this.filterRule.ruleType();
    }

    private static final class Ip6SubnetFilterRule
    implements IpFilterRule {
        private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1L);
        private final BigInteger networkAddress;
        private final BigInteger subnetMask;
        private final IpFilterRuleType ruleType;

        private Ip6SubnetFilterRule(Inet6Address ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
            if (cidrPrefix < 0 || cidrPrefix > 128) {
                throw new IllegalArgumentException(String.format("IPv6 requires the subnet prefix to be in range of [0,128]. The prefix was: %d", cidrPrefix));
            }
            this.subnetMask = Ip6SubnetFilterRule.prefixToSubnetMask(cidrPrefix);
            this.networkAddress = Ip6SubnetFilterRule.ipToInt(ipAddress).and(this.subnetMask);
            this.ruleType = ruleType;
        }

        @Override
        public boolean matches(InetSocketAddress remoteAddress) {
            InetAddress inetAddress = remoteAddress.getAddress();
            if (inetAddress instanceof Inet6Address) {
                BigInteger ipAddress = Ip6SubnetFilterRule.ipToInt((Inet6Address)inetAddress);
                return ipAddress.and(this.subnetMask).equals(this.networkAddress);
            }
            return false;
        }

        @Override
        public IpFilterRuleType ruleType() {
            return this.ruleType;
        }

        private static BigInteger ipToInt(Inet6Address ipAddress) {
            byte[] octets = ipAddress.getAddress();
            assert (octets.length == 16);
            return new BigInteger(octets);
        }

        private static BigInteger prefixToSubnetMask(int cidrPrefix) {
            return MINUS_ONE.shiftLeft(128 - cidrPrefix);
        }
    }

    private static final class Ip4SubnetFilterRule
    implements IpFilterRule {
        private final int networkAddress;
        private final int subnetMask;
        private final IpFilterRuleType ruleType;

        private Ip4SubnetFilterRule(Inet4Address ipAddress, int cidrPrefix, IpFilterRuleType ruleType) {
            if (cidrPrefix < 0 || cidrPrefix > 32) {
                throw new IllegalArgumentException(String.format("IPv4 requires the subnet prefix to be in range of [0,32]. The prefix was: %d", cidrPrefix));
            }
            this.subnetMask = Ip4SubnetFilterRule.prefixToSubnetMask(cidrPrefix);
            this.networkAddress = Ip4SubnetFilterRule.ipToInt(ipAddress) & this.subnetMask;
            this.ruleType = ruleType;
        }

        @Override
        public boolean matches(InetSocketAddress remoteAddress) {
            InetAddress inetAddress = remoteAddress.getAddress();
            if (inetAddress instanceof Inet4Address) {
                int ipAddress = Ip4SubnetFilterRule.ipToInt((Inet4Address)inetAddress);
                return (ipAddress & this.subnetMask) == this.networkAddress;
            }
            return false;
        }

        @Override
        public IpFilterRuleType ruleType() {
            return this.ruleType;
        }

        private static int ipToInt(Inet4Address ipAddress) {
            byte[] octets = ipAddress.getAddress();
            assert (octets.length == 4);
            return (octets[0] & 255) << 24 | (octets[1] & 255) << 16 | (octets[2] & 255) << 8 | octets[3] & 255;
        }

        private static int prefixToSubnetMask(int cidrPrefix) {
            return (int)(-1L << 32 - cidrPrefix & -1L);
        }
    }

}


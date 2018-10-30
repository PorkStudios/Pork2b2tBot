/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddresses;
import io.netty.util.NetUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

public final class DefaultDnsServerAddressStreamProvider
implements DnsServerAddressStreamProvider {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultDnsServerAddressStreamProvider.class);
    public static final DefaultDnsServerAddressStreamProvider INSTANCE = new DefaultDnsServerAddressStreamProvider();
    private static final List<InetSocketAddress> DEFAULT_NAME_SERVER_LIST;
    private static final InetSocketAddress[] DEFAULT_NAME_SERVER_ARRAY;
    private static final DnsServerAddresses DEFAULT_NAME_SERVERS;
    static final int DNS_PORT = 53;

    private DefaultDnsServerAddressStreamProvider() {
    }

    @Override
    public DnsServerAddressStream nameServerAddressStream(String hostname) {
        return DEFAULT_NAME_SERVERS.stream();
    }

    public static List<InetSocketAddress> defaultAddressList() {
        return DEFAULT_NAME_SERVER_LIST;
    }

    public static DnsServerAddresses defaultAddresses() {
        return DEFAULT_NAME_SERVERS;
    }

    static InetSocketAddress[] defaultAddressArray() {
        return (InetSocketAddress[])DEFAULT_NAME_SERVER_ARRAY.clone();
    }

    static {
        ArrayList<InetSocketAddress> defaultNameServers = new ArrayList<InetSocketAddress>(2);
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns://");
        try {
            InitialDirContext ctx = new InitialDirContext(env);
            String dnsUrls = (String)ctx.getEnvironment().get("java.naming.provider.url");
            if (dnsUrls != null && !dnsUrls.isEmpty()) {
                String[] servers;
                for (String server : servers = dnsUrls.split(" ")) {
                    try {
                        int port;
                        URI uri = new URI(server);
                        String host = new URI(server).getHost();
                        if (host == null || host.isEmpty()) {
                            logger.debug("Skipping a nameserver URI as host portion could not be extracted: {}", (Object)server);
                            continue;
                        }
                        defaultNameServers.add(SocketUtils.socketAddress(uri.getHost(), (port = uri.getPort()) == -1 ? 53 : port));
                    }
                    catch (URISyntaxException e) {
                        logger.debug("Skipping a malformed nameserver URI: {}", (Object)server, (Object)e);
                    }
                }
            }
        }
        catch (NamingException ctx) {
            // empty catch block
        }
        if (defaultNameServers.isEmpty()) {
            try {
                Class<?> configClass = Class.forName("sun.net.dns.ResolverConfiguration");
                Method open = configClass.getMethod("open", new Class[0]);
                Method nameservers = configClass.getMethod("nameservers", new Class[0]);
                Object instance = open.invoke(null, new Object[0]);
                List list = (List)nameservers.invoke(instance, new Object[0]);
                for (String a : list) {
                    if (a == null) continue;
                    defaultNameServers.add(new InetSocketAddress(SocketUtils.addressByName(a), 53));
                }
            }
            catch (Exception configClass) {
                // empty catch block
            }
        }
        if (!defaultNameServers.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Default DNS servers: {} (sun.net.dns.ResolverConfiguration)", (Object)defaultNameServers);
            }
        } else {
            if (NetUtil.isIpV6AddressesPreferred() || NetUtil.LOCALHOST instanceof Inet6Address && !NetUtil.isIpV4StackPreferred()) {
                Collections.addAll(defaultNameServers, SocketUtils.socketAddress("2001:4860:4860::8888", 53), SocketUtils.socketAddress("2001:4860:4860::8844", 53));
            } else {
                Collections.addAll(defaultNameServers, SocketUtils.socketAddress("8.8.8.8", 53), SocketUtils.socketAddress("8.8.4.4", 53));
            }
            if (logger.isWarnEnabled()) {
                logger.warn("Default DNS servers: {} (Google Public DNS as a fallback)", (Object)defaultNameServers);
            }
        }
        DEFAULT_NAME_SERVER_LIST = Collections.unmodifiableList(defaultNameServers);
        DEFAULT_NAME_SERVER_ARRAY = defaultNameServers.toArray(new InetSocketAddress[defaultNameServers.size()]);
        DEFAULT_NAME_SERVERS = DnsServerAddresses.sequential(DEFAULT_NAME_SERVER_ARRAY);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddresses;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class UnixResolverDnsServerAddressStreamProvider
implements DnsServerAddressStreamProvider {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnixResolverDnsServerAddressStreamProvider.class);
    private static final String ETC_RESOLV_CONF_FILE = "/etc/resolv.conf";
    private static final String ETC_RESOLVER_DIR = "/etc/resolver";
    private static final String NAMESERVER_ROW_LABEL = "nameserver";
    private static final String SORTLIST_ROW_LABEL = "sortlist";
    private static final String OPTIONS_ROW_LABEL = "options";
    private static final String DOMAIN_ROW_LABEL = "domain";
    private static final String PORT_ROW_LABEL = "port";
    private static final String NDOTS_LABEL = "ndots:";
    static final int DEFAULT_NDOTS = 1;
    private final DnsServerAddresses defaultNameServerAddresses;
    private final Map<String, DnsServerAddresses> domainToNameServerStreamMap;

    static DnsServerAddressStreamProvider parseSilently() {
        try {
            UnixResolverDnsServerAddressStreamProvider nameServerCache = new UnixResolverDnsServerAddressStreamProvider(ETC_RESOLV_CONF_FILE, ETC_RESOLVER_DIR);
            return nameServerCache.mayOverrideNameServers() ? nameServerCache : DefaultDnsServerAddressStreamProvider.INSTANCE;
        }
        catch (Exception e) {
            logger.debug("failed to parse {} and/or {}", ETC_RESOLV_CONF_FILE, ETC_RESOLVER_DIR, e);
            return DefaultDnsServerAddressStreamProvider.INSTANCE;
        }
    }

    public /* varargs */ UnixResolverDnsServerAddressStreamProvider(File etcResolvConf, File ... etcResolverFiles) throws IOException {
        Map<String, DnsServerAddresses> etcResolvConfMap = UnixResolverDnsServerAddressStreamProvider.parse(ObjectUtil.checkNotNull(etcResolvConf, "etcResolvConf"));
        boolean useEtcResolverFiles = etcResolverFiles != null && etcResolverFiles.length != 0;
        this.domainToNameServerStreamMap = useEtcResolverFiles ? UnixResolverDnsServerAddressStreamProvider.parse(etcResolverFiles) : etcResolvConfMap;
        DnsServerAddresses defaultNameServerAddresses = etcResolvConfMap.get(etcResolvConf.getName());
        if (defaultNameServerAddresses == null) {
            Collection<DnsServerAddresses> values = etcResolvConfMap.values();
            if (values.isEmpty()) {
                throw new IllegalArgumentException(etcResolvConf + " didn't provide any name servers");
            }
            this.defaultNameServerAddresses = values.iterator().next();
        } else {
            this.defaultNameServerAddresses = defaultNameServerAddresses;
        }
        if (useEtcResolverFiles) {
            this.domainToNameServerStreamMap.putAll(etcResolvConfMap);
        }
    }

    public UnixResolverDnsServerAddressStreamProvider(String etcResolvConf, String etcResolverDir) throws IOException {
        this(etcResolvConf == null ? null : new File(etcResolvConf), etcResolverDir == null ? null : new File(etcResolverDir).listFiles());
    }

    @Override
    public DnsServerAddressStream nameServerAddressStream(String hostname) {
        int i;
        while ((i = hostname.indexOf(46, 1)) >= 0 && i != hostname.length() - 1) {
            DnsServerAddresses addresses = this.domainToNameServerStreamMap.get(hostname);
            if (addresses != null) {
                return addresses.stream();
            }
            hostname = hostname.substring(i + 1);
        }
        return this.defaultNameServerAddresses.stream();
    }

    private boolean mayOverrideNameServers() {
        return !this.domainToNameServerStreamMap.isEmpty() || this.defaultNameServerAddresses.stream().next() != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static /* varargs */ Map<String, DnsServerAddresses> parse(File ... etcResolverFiles) throws IOException {
        HashMap<String, DnsServerAddresses> domainToNameServerStreamMap = new HashMap<String, DnsServerAddresses>(etcResolverFiles.length << 1);
        for (File etcResolverFile : etcResolverFiles) {
            if (!etcResolverFile.isFile()) continue;
            FileReader fr = new FileReader(etcResolverFile);
            BufferedReader br = null;
            try {
                String line;
                br = new BufferedReader(fr);
                ArrayList<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(2);
                String domainName = etcResolverFile.getName();
                int port = 53;
                while ((line = br.readLine()) != null) {
                    int i;
                    char c;
                    if ((line = line.trim()).isEmpty() || (c = line.charAt(0)) == '#' || c == ';') continue;
                    if (line.startsWith(NAMESERVER_ROW_LABEL)) {
                        i = StringUtil.indexOfNonWhiteSpace(line, NAMESERVER_ROW_LABEL.length());
                        if (i < 0) {
                            throw new IllegalArgumentException("error parsing label nameserver in file " + etcResolverFile + ". value: " + line);
                        }
                        String maybeIP = line.substring(i);
                        if (!NetUtil.isValidIpV4Address(maybeIP) && !NetUtil.isValidIpV6Address(maybeIP)) {
                            i = maybeIP.lastIndexOf(46);
                            if (i + 1 >= maybeIP.length()) {
                                throw new IllegalArgumentException("error parsing label nameserver in file " + etcResolverFile + ". invalid IP value: " + line);
                            }
                            port = Integer.parseInt(maybeIP.substring(i + 1));
                            maybeIP = maybeIP.substring(0, i);
                        }
                        addresses.add(SocketUtils.socketAddress(maybeIP, port));
                        continue;
                    }
                    if (line.startsWith(DOMAIN_ROW_LABEL)) {
                        i = StringUtil.indexOfNonWhiteSpace(line, DOMAIN_ROW_LABEL.length());
                        if (i < 0) {
                            throw new IllegalArgumentException("error parsing label domain in file " + etcResolverFile + " value: " + line);
                        }
                        domainName = line.substring(i);
                        if (!addresses.isEmpty()) {
                            UnixResolverDnsServerAddressStreamProvider.putIfAbsent(domainToNameServerStreamMap, domainName, addresses);
                        }
                        addresses = new ArrayList(2);
                        continue;
                    }
                    if (line.startsWith(PORT_ROW_LABEL)) {
                        i = StringUtil.indexOfNonWhiteSpace(line, PORT_ROW_LABEL.length());
                        if (i < 0) {
                            throw new IllegalArgumentException("error parsing label port in file " + etcResolverFile + " value: " + line);
                        }
                        port = Integer.parseInt(line.substring(i));
                        continue;
                    }
                    if (!line.startsWith(SORTLIST_ROW_LABEL)) continue;
                    logger.info("row type {} not supported. ignoring line: {}", (Object)SORTLIST_ROW_LABEL, (Object)line);
                }
                if (addresses.isEmpty()) continue;
                UnixResolverDnsServerAddressStreamProvider.putIfAbsent(domainToNameServerStreamMap, domainName, addresses);
            }
            finally {
                if (br == null) {
                    fr.close();
                } else {
                    br.close();
                }
            }
        }
        return domainToNameServerStreamMap;
    }

    private static void putIfAbsent(Map<String, DnsServerAddresses> domainToNameServerStreamMap, String domainName, List<InetSocketAddress> addresses) {
        UnixResolverDnsServerAddressStreamProvider.putIfAbsent(domainToNameServerStreamMap, domainName, DnsServerAddresses.sequential(addresses));
    }

    private static void putIfAbsent(Map<String, DnsServerAddresses> domainToNameServerStreamMap, String domainName, DnsServerAddresses addresses) {
        DnsServerAddresses existingAddresses = domainToNameServerStreamMap.put(domainName, addresses);
        if (existingAddresses != null) {
            domainToNameServerStreamMap.put(domainName, existingAddresses);
            logger.debug("Domain name {} already maps to addresses {} so new addresses {} will be discarded", domainName, existingAddresses, addresses);
        }
    }

    static int parseEtcResolverFirstNdots() throws IOException {
        return UnixResolverDnsServerAddressStreamProvider.parseEtcResolverFirstNdots(new File(ETC_RESOLV_CONF_FILE));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static int parseEtcResolverFirstNdots(File etcResolvConf) throws IOException {
        FileReader fr = new FileReader(etcResolvConf);
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(OPTIONS_ROW_LABEL)) continue;
                int i = line.indexOf(NDOTS_LABEL);
                if (i < 0) break;
                int j = line.indexOf(32, i += NDOTS_LABEL.length());
                int n = Integer.parseInt(line.substring(i, j < 0 ? line.length() : j));
                return n;
            }
        }
        finally {
            if (br == null) {
                fr.close();
            } else {
                br.close();
            }
        }
        return 1;
    }
}


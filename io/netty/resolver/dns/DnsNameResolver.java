/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DatagramDnsResponseDecoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.InetNameResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.resolver.dns.BiDnsQueryLifecycleObserverFactory;
import io.netty.resolver.dns.DnsCache;
import io.netty.resolver.dns.DnsCacheEntry;
import io.netty.resolver.dns.DnsNameResolverContext;
import io.netty.resolver.dns.DnsNameResolverException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import io.netty.resolver.dns.DnsQueryContext;
import io.netty.resolver.dns.DnsQueryContextManager;
import io.netty.resolver.dns.DnsQueryLifecycleObserverFactory;
import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsServerAddresses;
import io.netty.resolver.dns.NoopDnsQueryLifecycleObserverFactory;
import io.netty.resolver.dns.TraceDnsQueryLifeCycleObserverFactory;
import io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DnsNameResolver
extends InetNameResolver {
    private static final InternalLogger logger;
    private static final String LOCALHOST = "localhost";
    private static final InetAddress LOCALHOST_ADDRESS;
    private static final DnsRecord[] EMPTY_ADDITIONALS;
    private static final DnsRecordType[] IPV4_ONLY_RESOLVED_RECORD_TYPES;
    private static final InternetProtocolFamily[] IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
    private static final DnsRecordType[] IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
    private static final InternetProtocolFamily[] IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
    private static final DnsRecordType[] IPV6_ONLY_RESOLVED_RECORD_TYPES;
    private static final InternetProtocolFamily[] IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
    private static final DnsRecordType[] IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
    private static final InternetProtocolFamily[] IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
    static final ResolvedAddressTypes DEFAULT_RESOLVE_ADDRESS_TYPES;
    static final String[] DEFAULT_SEARCH_DOMAINS;
    private static final int DEFAULT_NDOTS;
    private static final DatagramDnsResponseDecoder DECODER;
    private static final DatagramDnsQueryEncoder ENCODER;
    final Future<Channel> channelFuture;
    final DatagramChannel ch;
    final DnsQueryContextManager queryContextManager = new DnsQueryContextManager();
    private final DnsCache resolveCache;
    private final DnsCache authoritativeDnsServerCache;
    private final FastThreadLocal<DnsServerAddressStream> nameServerAddrStream = new FastThreadLocal<DnsServerAddressStream>(){

        @Override
        protected DnsServerAddressStream initialValue() throws Exception {
            return DnsNameResolver.this.dnsServerAddressStreamProvider.nameServerAddressStream("");
        }
    };
    private final long queryTimeoutMillis;
    private final int maxQueriesPerResolve;
    private final ResolvedAddressTypes resolvedAddressTypes;
    private final InternetProtocolFamily[] resolvedInternetProtocolFamilies;
    private final boolean recursionDesired;
    private final int maxPayloadSize;
    private final boolean optResourceEnabled;
    private final HostsFileEntriesResolver hostsFileEntriesResolver;
    private final DnsServerAddressStreamProvider dnsServerAddressStreamProvider;
    private final String[] searchDomains;
    private final int ndots;
    private final boolean supportsAAAARecords;
    private final boolean supportsARecords;
    private final InternetProtocolFamily preferredAddressType;
    private final DnsRecordType[] resolveRecordTypes;
    private final boolean decodeIdn;
    private final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory;

    public DnsNameResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, final DnsCache resolveCache, DnsCache authoritativeDnsServerCache, DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory, long queryTimeoutMillis, ResolvedAddressTypes resolvedAddressTypes, boolean recursionDesired, int maxQueriesPerResolve, boolean traceEnabled, int maxPayloadSize, boolean optResourceEnabled, HostsFileEntriesResolver hostsFileEntriesResolver, DnsServerAddressStreamProvider dnsServerAddressStreamProvider, String[] searchDomains, int ndots, boolean decodeIdn) {
        super(eventLoop);
        this.queryTimeoutMillis = ObjectUtil.checkPositive(queryTimeoutMillis, "queryTimeoutMillis");
        this.resolvedAddressTypes = resolvedAddressTypes != null ? resolvedAddressTypes : DEFAULT_RESOLVE_ADDRESS_TYPES;
        this.recursionDesired = recursionDesired;
        this.maxQueriesPerResolve = ObjectUtil.checkPositive(maxQueriesPerResolve, "maxQueriesPerResolve");
        this.maxPayloadSize = ObjectUtil.checkPositive(maxPayloadSize, "maxPayloadSize");
        this.optResourceEnabled = optResourceEnabled;
        this.hostsFileEntriesResolver = ObjectUtil.checkNotNull(hostsFileEntriesResolver, "hostsFileEntriesResolver");
        this.dnsServerAddressStreamProvider = ObjectUtil.checkNotNull(dnsServerAddressStreamProvider, "dnsServerAddressStreamProvider");
        this.resolveCache = ObjectUtil.checkNotNull(resolveCache, "resolveCache");
        this.authoritativeDnsServerCache = ObjectUtil.checkNotNull(authoritativeDnsServerCache, "authoritativeDnsServerCache");
        this.dnsQueryLifecycleObserverFactory = traceEnabled ? (dnsQueryLifecycleObserverFactory instanceof NoopDnsQueryLifecycleObserverFactory ? new TraceDnsQueryLifeCycleObserverFactory() : new BiDnsQueryLifecycleObserverFactory(new TraceDnsQueryLifeCycleObserverFactory(), dnsQueryLifecycleObserverFactory)) : ObjectUtil.checkNotNull(dnsQueryLifecycleObserverFactory, "dnsQueryLifecycleObserverFactory");
        this.searchDomains = searchDomains != null ? (String[])searchDomains.clone() : DEFAULT_SEARCH_DOMAINS;
        this.ndots = ndots >= 0 ? ndots : DEFAULT_NDOTS;
        this.decodeIdn = decodeIdn;
        switch (this.resolvedAddressTypes) {
            case IPV4_ONLY: {
                this.supportsAAAARecords = false;
                this.supportsARecords = true;
                this.resolveRecordTypes = IPV4_ONLY_RESOLVED_RECORD_TYPES;
                this.resolvedInternetProtocolFamilies = IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
                this.preferredAddressType = InternetProtocolFamily.IPv4;
                break;
            }
            case IPV4_PREFERRED: {
                this.supportsAAAARecords = true;
                this.supportsARecords = true;
                this.resolveRecordTypes = IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
                this.resolvedInternetProtocolFamilies = IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
                this.preferredAddressType = InternetProtocolFamily.IPv4;
                break;
            }
            case IPV6_ONLY: {
                this.supportsAAAARecords = true;
                this.supportsARecords = false;
                this.resolveRecordTypes = IPV6_ONLY_RESOLVED_RECORD_TYPES;
                this.resolvedInternetProtocolFamilies = IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
                this.preferredAddressType = InternetProtocolFamily.IPv6;
                break;
            }
            case IPV6_PREFERRED: {
                this.supportsAAAARecords = true;
                this.supportsARecords = true;
                this.resolveRecordTypes = IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
                this.resolvedInternetProtocolFamilies = IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
                this.preferredAddressType = InternetProtocolFamily.IPv6;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + (Object)((Object)resolvedAddressTypes));
            }
        }
        Bootstrap b = new Bootstrap();
        b.group(this.executor());
        b.channelFactory(channelFactory);
        b.option(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, true);
        final DnsResponseHandler responseHandler = new DnsResponseHandler(this.executor().newPromise());
        b.handler(new ChannelInitializer<DatagramChannel>(){

            @Override
            protected void initChannel(DatagramChannel ch) throws Exception {
                ch.pipeline().addLast(DECODER, ENCODER, responseHandler);
            }
        });
        this.channelFuture = responseHandler.channelActivePromise;
        this.ch = (DatagramChannel)b.register().channel();
        this.ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(maxPayloadSize));
        this.ch.closeFuture().addListener(new ChannelFutureListener(){

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                resolveCache.clear();
            }
        });
    }

    int dnsRedirectPort(InetAddress server) {
        return 53;
    }

    final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory() {
        return this.dnsQueryLifecycleObserverFactory;
    }

    protected DnsServerAddressStream uncachedRedirectDnsServerStream(List<InetSocketAddress> nameServers) {
        return DnsServerAddresses.sequential(nameServers).stream();
    }

    public DnsCache resolveCache() {
        return this.resolveCache;
    }

    public DnsCache authoritativeDnsServerCache() {
        return this.authoritativeDnsServerCache;
    }

    public long queryTimeoutMillis() {
        return this.queryTimeoutMillis;
    }

    public ResolvedAddressTypes resolvedAddressTypes() {
        return this.resolvedAddressTypes;
    }

    InternetProtocolFamily[] resolvedInternetProtocolFamiliesUnsafe() {
        return this.resolvedInternetProtocolFamilies;
    }

    final String[] searchDomains() {
        return this.searchDomains;
    }

    final int ndots() {
        return this.ndots;
    }

    final boolean supportsAAAARecords() {
        return this.supportsAAAARecords;
    }

    final boolean supportsARecords() {
        return this.supportsARecords;
    }

    final InternetProtocolFamily preferredAddressType() {
        return this.preferredAddressType;
    }

    final DnsRecordType[] resolveRecordTypes() {
        return this.resolveRecordTypes;
    }

    final boolean isDecodeIdn() {
        return this.decodeIdn;
    }

    public boolean isRecursionDesired() {
        return this.recursionDesired;
    }

    public int maxQueriesPerResolve() {
        return this.maxQueriesPerResolve;
    }

    public int maxPayloadSize() {
        return this.maxPayloadSize;
    }

    public boolean isOptResourceEnabled() {
        return this.optResourceEnabled;
    }

    public HostsFileEntriesResolver hostsFileEntriesResolver() {
        return this.hostsFileEntriesResolver;
    }

    @Override
    public void close() {
        if (this.ch.isOpen()) {
            this.ch.close();
        }
    }

    @Override
    protected EventLoop executor() {
        return (EventLoop)super.executor();
    }

    private InetAddress resolveHostsFileEntry(String hostname) {
        if (this.hostsFileEntriesResolver == null) {
            return null;
        }
        InetAddress address = this.hostsFileEntriesResolver.address(hostname, this.resolvedAddressTypes);
        if (address == null && PlatformDependent.isWindows() && LOCALHOST.equalsIgnoreCase(hostname)) {
            return LOCALHOST_ADDRESS;
        }
        return address;
    }

    @Override
    public final Future<InetAddress> resolve(String inetHost, Iterable<DnsRecord> additionals) {
        return this.resolve(inetHost, additionals, this.executor().newPromise());
    }

    public final Future<InetAddress> resolve(String inetHost, Iterable<DnsRecord> additionals, Promise<InetAddress> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        DnsRecord[] additionalsArray = DnsNameResolver.toArray(additionals, true);
        try {
            this.doResolve(inetHost, additionalsArray, promise, this.resolveCache);
            return promise;
        }
        catch (Exception e) {
            return promise.setFailure(e);
        }
    }

    public final Future<List<InetAddress>> resolveAll(String inetHost, Iterable<DnsRecord> additionals) {
        return this.resolveAll(inetHost, additionals, this.executor().newPromise());
    }

    public final Future<List<InetAddress>> resolveAll(String inetHost, Iterable<DnsRecord> additionals, Promise<List<InetAddress>> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        DnsRecord[] additionalsArray = DnsNameResolver.toArray(additionals, true);
        try {
            this.doResolveAll(inetHost, additionalsArray, promise, this.resolveCache);
            return promise;
        }
        catch (Exception e) {
            return promise.setFailure(e);
        }
    }

    @Override
    protected void doResolve(String inetHost, Promise<InetAddress> promise) throws Exception {
        this.doResolve(inetHost, EMPTY_ADDITIONALS, promise, this.resolveCache);
    }

    private static DnsRecord[] toArray(Iterable<DnsRecord> additionals, boolean validateType) {
        ObjectUtil.checkNotNull(additionals, "additionals");
        if (additionals instanceof Collection) {
            Collection records = (Collection)additionals;
            for (DnsRecord r : additionals) {
                DnsNameResolver.validateAdditional(r, validateType);
            }
            return records.toArray(new DnsRecord[records.size()]);
        }
        Iterator<DnsRecord> additionalsIt = additionals.iterator();
        if (!additionalsIt.hasNext()) {
            return EMPTY_ADDITIONALS;
        }
        ArrayList<DnsRecord> records = new ArrayList<DnsRecord>();
        do {
            DnsRecord r = additionalsIt.next();
            DnsNameResolver.validateAdditional(r, validateType);
            records.add(r);
        } while (additionalsIt.hasNext());
        return records.toArray(new DnsRecord[records.size()]);
    }

    private static void validateAdditional(DnsRecord record, boolean validateType) {
        ObjectUtil.checkNotNull(record, "record");
        if (validateType && record instanceof DnsRawRecord) {
            throw new IllegalArgumentException("DnsRawRecord implementations not allowed: " + record);
        }
    }

    private InetAddress loopbackAddress() {
        return this.preferredAddressType().localhost();
    }

    protected void doResolve(String inetHost, DnsRecord[] additionals, Promise<InetAddress> promise, DnsCache resolveCache) throws Exception {
        if (inetHost == null || inetHost.isEmpty()) {
            promise.setSuccess(this.loopbackAddress());
            return;
        }
        byte[] bytes = NetUtil.createByteArrayFromIpAddressString(inetHost);
        if (bytes != null) {
            promise.setSuccess(InetAddress.getByAddress(bytes));
            return;
        }
        String hostname = DnsNameResolver.hostname(inetHost);
        InetAddress hostsFileEntry = this.resolveHostsFileEntry(hostname);
        if (hostsFileEntry != null) {
            promise.setSuccess(hostsFileEntry);
            return;
        }
        if (!this.doResolveCached(hostname, additionals, promise, resolveCache)) {
            this.doResolveUncached(hostname, additionals, promise, resolveCache);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean doResolveCached(String hostname, DnsRecord[] additionals, Promise<InetAddress> promise, DnsCache resolveCache) {
        List<? extends DnsCacheEntry> cachedEntries = resolveCache.get(hostname, additionals);
        if (cachedEntries == null || cachedEntries.isEmpty()) {
            return false;
        }
        InetAddress address = null;
        Throwable cause = null;
        List<? extends DnsCacheEntry> list = cachedEntries;
        synchronized (list) {
            int numEntries = cachedEntries.size();
            assert (numEntries > 0);
            if (cachedEntries.get(0).cause() != null) {
                cause = cachedEntries.get(0).cause();
            } else {
                block3 : for (InternetProtocolFamily f : this.resolvedInternetProtocolFamilies) {
                    for (int i = 0; i < numEntries; ++i) {
                        DnsCacheEntry e = cachedEntries.get(i);
                        if (!f.addressType().isInstance(e.address())) continue;
                        address = e.address();
                        continue block3;
                    }
                }
            }
        }
        if (address != null) {
            DnsNameResolver.trySuccess(promise, address);
            return true;
        }
        if (cause != null) {
            DnsNameResolver.tryFailure(promise, cause);
            return true;
        }
        return false;
    }

    private static <T> void trySuccess(Promise<T> promise, T result) {
        if (!promise.trySuccess(result)) {
            logger.warn("Failed to notify success ({}) to a promise: {}", (Object)result, (Object)promise);
        }
    }

    private static void tryFailure(Promise<?> promise, Throwable cause) {
        if (!promise.tryFailure(cause)) {
            logger.warn("Failed to notify failure to a promise: {}", (Object)promise, (Object)cause);
        }
    }

    private void doResolveUncached(String hostname, DnsRecord[] additionals, Promise<InetAddress> promise, DnsCache resolveCache) {
        new SingleResolverContext(this, hostname, additionals, resolveCache, this.dnsServerAddressStreamProvider.nameServerAddressStream(hostname)).resolve(promise);
    }

    @Override
    protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) throws Exception {
        this.doResolveAll(inetHost, EMPTY_ADDITIONALS, promise, this.resolveCache);
    }

    protected void doResolveAll(String inetHost, DnsRecord[] additionals, Promise<List<InetAddress>> promise, DnsCache resolveCache) throws Exception {
        if (inetHost == null || inetHost.isEmpty()) {
            promise.setSuccess(Collections.singletonList(this.loopbackAddress()));
            return;
        }
        byte[] bytes = NetUtil.createByteArrayFromIpAddressString(inetHost);
        if (bytes != null) {
            promise.setSuccess(Collections.singletonList(InetAddress.getByAddress(bytes)));
            return;
        }
        String hostname = DnsNameResolver.hostname(inetHost);
        InetAddress hostsFileEntry = this.resolveHostsFileEntry(hostname);
        if (hostsFileEntry != null) {
            promise.setSuccess(Collections.singletonList(hostsFileEntry));
            return;
        }
        if (!this.doResolveAllCached(hostname, additionals, promise, resolveCache)) {
            this.doResolveAllUncached(hostname, additionals, promise, resolveCache);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean doResolveAllCached(String hostname, DnsRecord[] additionals, Promise<List<InetAddress>> promise, DnsCache resolveCache) {
        List<? extends DnsCacheEntry> cachedEntries = resolveCache.get(hostname, additionals);
        if (cachedEntries == null || cachedEntries.isEmpty()) {
            return false;
        }
        ArrayList<InetAddress> result = null;
        Throwable cause = null;
        List<? extends DnsCacheEntry> list = cachedEntries;
        synchronized (list) {
            int numEntries = cachedEntries.size();
            assert (numEntries > 0);
            if (cachedEntries.get(0).cause() != null) {
                cause = cachedEntries.get(0).cause();
            } else {
                for (InternetProtocolFamily f : this.resolvedInternetProtocolFamilies) {
                    for (int i = 0; i < numEntries; ++i) {
                        DnsCacheEntry e = cachedEntries.get(i);
                        if (!f.addressType().isInstance(e.address())) continue;
                        if (result == null) {
                            result = new ArrayList<InetAddress>(numEntries);
                        }
                        result.add(e.address());
                    }
                }
            }
        }
        if (result != null) {
            DnsNameResolver.trySuccess(promise, result);
            return true;
        }
        if (cause != null) {
            DnsNameResolver.tryFailure(promise, cause);
            return true;
        }
        return false;
    }

    private void doResolveAllUncached(String hostname, DnsRecord[] additionals, Promise<List<InetAddress>> promise, DnsCache resolveCache) {
        new ListResolverContext(this, hostname, additionals, resolveCache, this.dnsServerAddressStreamProvider.nameServerAddressStream(hostname)).resolve(promise);
    }

    private static String hostname(String inetHost) {
        String hostname = IDN.toASCII(inetHost);
        if (StringUtil.endsWith(inetHost, '.') && !StringUtil.endsWith(hostname, '.')) {
            hostname = hostname + ".";
        }
        return hostname;
    }

    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question) {
        return this.query(this.nextNameServerAddress(), question);
    }

    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question, Iterable<DnsRecord> additionals) {
        return this.query(this.nextNameServerAddress(), question, additionals);
    }

    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion question, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        return this.query(this.nextNameServerAddress(), question, Collections.<DnsRecord>emptyList(), promise);
    }

    private InetSocketAddress nextNameServerAddress() {
        return this.nameServerAddrStream.get().next();
    }

    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question) {
        return this.query0(nameServerAddr, question, EMPTY_ADDITIONALS, this.ch.eventLoop().newPromise());
    }

    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Iterable<DnsRecord> additionals) {
        return this.query0(nameServerAddr, question, DnsNameResolver.toArray(additionals, false), this.ch.eventLoop().newPromise());
    }

    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        return this.query0(nameServerAddr, question, EMPTY_ADDITIONALS, promise);
    }

    public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress nameServerAddr, DnsQuestion question, Iterable<DnsRecord> additionals, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        return this.query0(nameServerAddr, question, DnsNameResolver.toArray(additionals, false), promise);
    }

    public static boolean isTransportOrTimeoutError(Throwable cause) {
        return cause != null && cause.getCause() instanceof DnsNameResolverException;
    }

    public static boolean isTimeoutError(Throwable cause) {
        return cause != null && cause.getCause() instanceof DnsNameResolverTimeoutException;
    }

    final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query0(InetSocketAddress nameServerAddr, DnsQuestion question, DnsRecord[] additionals, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        return this.query0(nameServerAddr, question, additionals, this.ch.newPromise(), promise);
    }

    final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query0(InetSocketAddress nameServerAddr, DnsQuestion question, DnsRecord[] additionals, ChannelPromise writePromise, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> promise) {
        assert (!writePromise.isVoid());
        Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> castPromise = DnsNameResolver.cast(ObjectUtil.checkNotNull(promise, "promise"));
        try {
            new DnsQueryContext(this, nameServerAddr, question, additionals, castPromise).query(writePromise);
            return castPromise;
        }
        catch (Exception e) {
            return castPromise.setFailure(e);
        }
    }

    private static Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> cast(Promise<?> promise) {
        return promise;
    }

    static {
        String[] searchDomains;
        int ndots;
        logger = InternalLoggerFactory.getInstance(DnsNameResolver.class);
        EMPTY_ADDITIONALS = new DnsRecord[0];
        IPV4_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.A};
        IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv4};
        IPV4_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.A, DnsRecordType.AAAA};
        IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv4, InternetProtocolFamily.IPv6};
        IPV6_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.AAAA};
        IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv6};
        IPV6_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.AAAA, DnsRecordType.A};
        IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv6, InternetProtocolFamily.IPv4};
        if (NetUtil.isIpV4StackPreferred()) {
            DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_ONLY;
            LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
        } else if (NetUtil.isIpV6AddressesPreferred()) {
            DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV6_PREFERRED;
            LOCALHOST_ADDRESS = NetUtil.LOCALHOST6;
        } else {
            DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_PREFERRED;
            LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
        }
        try {
            Class<?> configClass = Class.forName("sun.net.dns.ResolverConfiguration");
            Method open = configClass.getMethod("open", new Class[0]);
            Method nameservers = configClass.getMethod("searchlist", new Class[0]);
            Object instance = open.invoke(null, new Object[0]);
            List list = (List)nameservers.invoke(instance, new Object[0]);
            searchDomains = list.toArray(new String[list.size()]);
        }
        catch (Exception ignore) {
            searchDomains = EmptyArrays.EMPTY_STRINGS;
        }
        DEFAULT_SEARCH_DOMAINS = searchDomains;
        try {
            ndots = UnixResolverDnsServerAddressStreamProvider.parseEtcResolverFirstNdots();
        }
        catch (Exception ignore) {
            ndots = 1;
        }
        DEFAULT_NDOTS = ndots;
        DECODER = new DatagramDnsResponseDecoder();
        ENCODER = new DatagramDnsQueryEncoder();
    }

    private final class DnsResponseHandler
    extends ChannelInboundHandlerAdapter {
        private final Promise<Channel> channelActivePromise;

        DnsResponseHandler(Promise<Channel> channelActivePromise) {
            this.channelActivePromise = channelActivePromise;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                DnsQueryContext qCtx;
                DatagramDnsResponse res = (DatagramDnsResponse)msg;
                int queryId = res.id();
                if (logger.isDebugEnabled()) {
                    logger.debug("{} RECEIVED: [{}: {}], {}", DnsNameResolver.this.ch, queryId, res.sender(), res);
                }
                if ((qCtx = DnsNameResolver.this.queryContextManager.get(res.sender(), queryId)) == null) {
                    logger.warn("{} Received a DNS response with an unknown ID: {}", (Object)DnsNameResolver.this.ch, (Object)queryId);
                    return;
                }
                qCtx.finish(res);
            }
            finally {
                ReferenceCountUtil.safeRelease(msg);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            this.channelActivePromise.setSuccess(ctx.channel());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.warn("{} Unexpected exception: ", (Object)DnsNameResolver.this.ch, (Object)cause);
        }
    }

    static final class ListResolverContext
    extends DnsNameResolverContext<List<InetAddress>> {
        ListResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache, DnsServerAddressStream nameServerAddrs) {
            super(parent, hostname, additionals, resolveCache, nameServerAddrs);
        }

        @Override
        DnsNameResolverContext<List<InetAddress>> newResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache, DnsServerAddressStream nameServerAddrs) {
            return new ListResolverContext(parent, hostname, additionals, resolveCache, nameServerAddrs);
        }

        @Override
        boolean finishResolve(Class<? extends InetAddress> addressType, List<DnsCacheEntry> resolvedEntries, Promise<List<InetAddress>> promise) {
            ArrayList<InetAddress> result = null;
            int numEntries = resolvedEntries.size();
            for (int i = 0; i < numEntries; ++i) {
                InetAddress a = resolvedEntries.get(i).address();
                if (!addressType.isInstance(a)) continue;
                if (result == null) {
                    result = new ArrayList<InetAddress>(numEntries);
                }
                result.add(a);
            }
            if (result != null) {
                promise.trySuccess(result);
                return true;
            }
            return false;
        }
    }

    static final class SingleResolverContext
    extends DnsNameResolverContext<InetAddress> {
        SingleResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache, DnsServerAddressStream nameServerAddrs) {
            super(parent, hostname, additionals, resolveCache, nameServerAddrs);
        }

        @Override
        DnsNameResolverContext<InetAddress> newResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache, DnsServerAddressStream nameServerAddrs) {
            return new SingleResolverContext(parent, hostname, additionals, resolveCache, nameServerAddrs);
        }

        @Override
        boolean finishResolve(Class<? extends InetAddress> addressType, List<DnsCacheEntry> resolvedEntries, Promise<InetAddress> promise) {
            int numEntries = resolvedEntries.size();
            for (int i = 0; i < numEntries; ++i) {
                InetAddress a = resolvedEntries.get(i).address();
                if (!addressType.isInstance(a)) continue;
                DnsNameResolver.trySuccess(promise, a);
                return true;
            }
            return false;
        }
    }

}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.resolver.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsRecordDecoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.resolver.dns.DnsCache;
import io.netty.resolver.dns.DnsCacheEntry;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsQueryLifecycleObserver;
import io.netty.resolver.dns.DnsQueryLifecycleObserverFactory;
import io.netty.resolver.dns.DnsServerAddressStream;
import io.netty.resolver.dns.DnsServerAddresses;
import io.netty.resolver.dns.NoopDnsQueryLifecycleObserver;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThrowableUtil;
import java.net.IDN;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

abstract class DnsNameResolverContext<T> {
    private static final int INADDRSZ4 = 4;
    private static final int INADDRSZ6 = 16;
    private static final FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>> RELEASE_RESPONSE = new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>(){

        @Override
        public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
            if (future.isSuccess()) {
                future.getNow().release();
            }
        }
    };
    private static final RuntimeException NXDOMAIN_QUERY_FAILED_EXCEPTION = ThrowableUtil.unknownStackTrace(new RuntimeException("No answer found and NXDOMAIN response code returned"), DnsNameResolverContext.class, "onResponse(..)");
    private static final RuntimeException CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION = ThrowableUtil.unknownStackTrace(new RuntimeException("No matching CNAME record found"), DnsNameResolverContext.class, "onResponseCNAME(..)");
    private static final RuntimeException NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION = ThrowableUtil.unknownStackTrace(new RuntimeException("No matching record type found"), DnsNameResolverContext.class, "onResponseAorAAAA(..)");
    private static final RuntimeException UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION = ThrowableUtil.unknownStackTrace(new RuntimeException("Response type was unrecognized"), DnsNameResolverContext.class, "onResponse(..)");
    private static final RuntimeException NAME_SERVERS_EXHAUSTED_EXCEPTION = ThrowableUtil.unknownStackTrace(new RuntimeException("No name servers returned an answer"), DnsNameResolverContext.class, "tryToFinishResolve(..)");
    private final DnsNameResolver parent;
    private final DnsServerAddressStream nameServerAddrs;
    private final String hostname;
    private final DnsCache resolveCache;
    private final int maxAllowedQueries;
    private final InternetProtocolFamily[] resolvedInternetProtocolFamilies;
    private final DnsRecord[] additionals;
    private final Set<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> queriesInProgress = Collections.newSetFromMap(new IdentityHashMap());
    private List<DnsCacheEntry> resolvedEntries;
    private int allowedQueries;
    private boolean triedCNAME;

    DnsNameResolverContext(DnsNameResolver parent, String hostname, DnsRecord[] additionals, DnsCache resolveCache, DnsServerAddressStream nameServerAddrs) {
        this.parent = parent;
        this.hostname = hostname;
        this.additionals = additionals;
        this.resolveCache = resolveCache;
        this.nameServerAddrs = ObjectUtil.checkNotNull(nameServerAddrs, "nameServerAddrs");
        this.maxAllowedQueries = parent.maxQueriesPerResolve();
        this.resolvedInternetProtocolFamilies = parent.resolvedInternetProtocolFamiliesUnsafe();
        this.allowedQueries = this.maxAllowedQueries;
    }

    void resolve(final Promise<T> promise) {
        final String[] searchDomains = this.parent.searchDomains();
        if (searchDomains.length == 0 || this.parent.ndots() == 0 || StringUtil.endsWith(this.hostname, '.')) {
            this.internalResolve(promise);
        } else {
            final boolean startWithoutSearchDomain = this.hasNDots();
            String initialHostname = startWithoutSearchDomain ? this.hostname : this.hostname + '.' + searchDomains[0];
            final int initialSearchDomainIdx = startWithoutSearchDomain ? 0 : 1;
            this.doSearchDomainQuery(initialHostname, new FutureListener<T>(){
                private int searchDomainIdx;
                {
                    this.searchDomainIdx = initialSearchDomainIdx;
                }

                @Override
                public void operationComplete(Future<T> future) throws Exception {
                    Throwable cause = future.cause();
                    if (cause == null) {
                        promise.trySuccess(future.getNow());
                    } else if (DnsNameResolver.isTransportOrTimeoutError(cause)) {
                        promise.tryFailure(new SearchDomainUnknownHostException(cause, DnsNameResolverContext.this.hostname));
                    } else if (this.searchDomainIdx < searchDomains.length) {
                        DnsNameResolverContext.this.doSearchDomainQuery(DnsNameResolverContext.this.hostname + '.' + searchDomains[this.searchDomainIdx++], this);
                    } else if (!startWithoutSearchDomain) {
                        DnsNameResolverContext.this.internalResolve(promise);
                    } else {
                        promise.tryFailure(new SearchDomainUnknownHostException(cause, DnsNameResolverContext.this.hostname));
                    }
                }
            });
        }
    }

    private boolean hasNDots() {
        int dots = 0;
        for (int idx = this.hostname.length() - 1; idx >= 0; --idx) {
            if (this.hostname.charAt(idx) != '.' || ++dots < this.parent.ndots()) continue;
            return true;
        }
        return false;
    }

    private void doSearchDomainQuery(String hostname, FutureListener<T> listener) {
        DnsNameResolverContext<T> nextContext = this.newResolverContext(this.parent, hostname, this.additionals, this.resolveCache, this.nameServerAddrs);
        Promise nextPromise = this.parent.executor().newPromise();
        DnsNameResolverContext.super.internalResolve(nextPromise);
        nextPromise.addListener(listener);
    }

    private void internalResolve(Promise<T> promise) {
        DnsServerAddressStream nameServerAddressStream = this.getNameServers(this.hostname);
        DnsRecordType[] recordTypes = this.parent.resolveRecordTypes();
        assert (recordTypes.length > 0);
        int end = recordTypes.length - 1;
        for (int i = 0; i < end; ++i) {
            if (this.query(this.hostname, recordTypes[i], nameServerAddressStream.duplicate(), promise, null)) continue;
            return;
        }
        this.query(this.hostname, recordTypes[end], nameServerAddressStream, promise, null);
    }

    private void addNameServerToCache(AuthoritativeNameServer name, InetAddress resolved, long ttl) {
        if (!name.isRootServer()) {
            this.parent.authoritativeDnsServerCache().cache(name.domainName(), this.additionals, resolved, ttl, this.parent.ch.eventLoop());
        }
    }

    private DnsServerAddressStream getNameServersFromCache(String hostname) {
        List<? extends DnsCacheEntry> entries;
        int idx;
        int len = hostname.length();
        if (len == 0) {
            return null;
        }
        if (hostname.charAt(len - 1) != '.') {
            hostname = hostname + ".";
        }
        if ((idx = hostname.indexOf(46)) == hostname.length() - 1) {
            return null;
        }
        do {
            int idx2;
            if ((idx2 = (hostname = hostname.substring(idx + 1)).indexOf(46)) <= 0 || idx2 == hostname.length() - 1) {
                return null;
            }
            idx = idx2;
        } while ((entries = this.parent.authoritativeDnsServerCache().get(hostname, this.additionals)) == null || entries.isEmpty());
        return DnsServerAddresses.sequential(new DnsCacheIterable(entries)).stream();
    }

    private void query(DnsServerAddressStream nameServerAddrStream, int nameServerAddrStreamIndex, DnsQuestion question, Promise<T> promise, Throwable cause) {
        this.query(nameServerAddrStream, nameServerAddrStreamIndex, question, this.parent.dnsQueryLifecycleObserverFactory().newDnsQueryLifecycleObserver(question), promise, cause);
    }

    private void query(final DnsServerAddressStream nameServerAddrStream, final int nameServerAddrStreamIndex, final DnsQuestion question, final DnsQueryLifecycleObserver queryLifecycleObserver, final Promise<T> promise, Throwable cause) {
        if (nameServerAddrStreamIndex >= nameServerAddrStream.size() || this.allowedQueries == 0 || promise.isCancelled()) {
            this.tryToFinishResolve(nameServerAddrStream, nameServerAddrStreamIndex, question, queryLifecycleObserver, promise, cause);
            return;
        }
        --this.allowedQueries;
        InetSocketAddress nameServerAddr = nameServerAddrStream.next();
        ChannelPromise writePromise = this.parent.ch.newPromise();
        Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> f = this.parent.query0(nameServerAddr, question, this.additionals, writePromise, this.parent.ch.eventLoop().newPromise());
        this.queriesInProgress.add(f);
        queryLifecycleObserver.queryWritten(nameServerAddr, writePromise);
        f.addListener((GenericFutureListener<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>>)new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>(){

            @Override
            public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
                DnsNameResolverContext.this.queriesInProgress.remove(future);
                if (promise.isDone() || future.isCancelled()) {
                    queryLifecycleObserver.queryCancelled(DnsNameResolverContext.this.allowedQueries);
                    return;
                }
                Throwable queryCause = future.cause();
                try {
                    if (queryCause == null) {
                        DnsNameResolverContext.this.onResponse(nameServerAddrStream, nameServerAddrStreamIndex, question, future.getNow(), queryLifecycleObserver, promise);
                    } else {
                        queryLifecycleObserver.queryFailed(queryCause);
                        DnsNameResolverContext.this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, promise, queryCause);
                    }
                }
                finally {
                    DnsNameResolverContext.this.tryToFinishResolve(nameServerAddrStream, nameServerAddrStreamIndex, question, NoopDnsQueryLifecycleObserver.INSTANCE, promise, queryCause);
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void onResponse(DnsServerAddressStream nameServerAddrStream, int nameServerAddrStreamIndex, DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<T> promise) {
        try {
            DnsResponse res = envelope.content();
            DnsResponseCode code = res.code();
            if (code == DnsResponseCode.NOERROR) {
                if (this.handleRedirect(question, envelope, queryLifecycleObserver, promise)) {
                    return;
                }
                DnsRecordType type = question.type();
                if (type == DnsRecordType.A || type == DnsRecordType.AAAA) {
                    this.onResponseAorAAAA(type, question, envelope, queryLifecycleObserver, promise);
                } else if (type == DnsRecordType.CNAME) {
                    this.onResponseCNAME(question, envelope, queryLifecycleObserver, promise);
                } else {
                    queryLifecycleObserver.queryFailed(UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION);
                }
                return;
            }
            if (code != DnsResponseCode.NXDOMAIN) {
                this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, queryLifecycleObserver.queryNoAnswer(code), promise, null);
            } else {
                queryLifecycleObserver.queryFailed(NXDOMAIN_QUERY_FAILED_EXCEPTION);
            }
        }
        finally {
            ReferenceCountUtil.safeRelease(envelope);
        }
    }

    private boolean handleRedirect(DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<T> promise) {
        AuthoritativeNameServerList serverNames;
        DnsResponse res = envelope.content();
        if (res.count(DnsSection.ANSWER) == 0 && (serverNames = DnsNameResolverContext.extractAuthoritativeNameServers(question.name(), res)) != null) {
            ArrayList<InetSocketAddress> nameServers = new ArrayList<InetSocketAddress>(serverNames.size());
            int additionalCount = res.count(DnsSection.ADDITIONAL);
            for (int i = 0; i < additionalCount; ++i) {
                AuthoritativeNameServer authoritativeNameServer;
                String recordName;
                InetAddress resolved;
                Object r = res.recordAt(DnsSection.ADDITIONAL, i);
                if (r.type() == DnsRecordType.A && !this.parent.supportsARecords() || r.type() == DnsRecordType.AAAA && !this.parent.supportsAAAARecords() || (authoritativeNameServer = serverNames.remove(recordName = r.name())) == null || (resolved = this.parseAddress((DnsRecord)r, recordName)) == null) continue;
                nameServers.add(new InetSocketAddress(resolved, this.parent.dnsRedirectPort(resolved)));
                this.addNameServerToCache(authoritativeNameServer, resolved, r.timeToLive());
            }
            if (!nameServers.isEmpty()) {
                this.query(this.parent.uncachedRedirectDnsServerStream(nameServers), 0, question, queryLifecycleObserver.queryRedirected(Collections.unmodifiableList(nameServers)), promise, null);
                return true;
            }
        }
        return false;
    }

    private static AuthoritativeNameServerList extractAuthoritativeNameServers(String questionName, DnsResponse res) {
        int authorityCount = res.count(DnsSection.AUTHORITY);
        if (authorityCount == 0) {
            return null;
        }
        AuthoritativeNameServerList serverNames = new AuthoritativeNameServerList(questionName);
        for (int i = 0; i < authorityCount; ++i) {
            serverNames.add((DnsRecord)res.recordAt(DnsSection.AUTHORITY, i));
        }
        return serverNames;
    }

    private void onResponseAorAAAA(DnsRecordType qType, DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<T> promise) {
        DnsResponse response = envelope.content();
        Map<String, String> cnames = DnsNameResolverContext.buildAliasMap(response);
        int answerCount = response.count(DnsSection.ANSWER);
        boolean found = false;
        for (int i = 0; i < answerCount; ++i) {
            Object resolved;
            Object r = response.recordAt(DnsSection.ANSWER, i);
            DnsRecordType type = r.type();
            if (type != DnsRecordType.A && type != DnsRecordType.AAAA) continue;
            String questionName = question.name().toLowerCase(Locale.US);
            String recordName = r.name().toLowerCase(Locale.US);
            if (!recordName.equals(questionName)) {
                resolved = questionName;
                while (!recordName.equals(resolved = cnames.get(resolved)) && resolved != null) {
                }
                if (resolved == null) continue;
            }
            if ((resolved = this.parseAddress((DnsRecord)r, this.hostname)) == null) continue;
            if (this.resolvedEntries == null) {
                this.resolvedEntries = new ArrayList<DnsCacheEntry>(8);
            }
            this.resolvedEntries.add(this.resolveCache.cache(this.hostname, this.additionals, (InetAddress)resolved, r.timeToLive(), this.parent.ch.eventLoop()));
            found = true;
        }
        if (found) {
            queryLifecycleObserver.querySucceed();
            return;
        }
        if (cnames.isEmpty()) {
            queryLifecycleObserver.queryFailed(NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION);
        } else {
            this.onResponseCNAME(question, envelope, cnames, queryLifecycleObserver, promise);
        }
    }

    private InetAddress parseAddress(DnsRecord r, String name) {
        if (!(r instanceof DnsRawRecord)) {
            return null;
        }
        ByteBuf content = ((ByteBufHolder)((Object)r)).content();
        int contentLen = content.readableBytes();
        if (contentLen != 4 && contentLen != 16) {
            return null;
        }
        byte[] addrBytes = new byte[contentLen];
        content.getBytes(content.readerIndex(), addrBytes);
        try {
            return InetAddress.getByAddress(this.parent.isDecodeIdn() ? IDN.toUnicode(name) : name, addrBytes);
        }
        catch (UnknownHostException e) {
            throw new Error(e);
        }
    }

    private void onResponseCNAME(DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> envelope, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<T> promise) {
        this.onResponseCNAME(question, envelope, DnsNameResolverContext.buildAliasMap(envelope.content()), queryLifecycleObserver, promise);
    }

    private void onResponseCNAME(DnsQuestion question, AddressedEnvelope<DnsResponse, InetSocketAddress> response, Map<String, String> cnames, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<T> promise) {
        String next;
        String name;
        String resolved = name = question.name().toLowerCase(Locale.US);
        boolean found = false;
        while (!cnames.isEmpty() && (next = cnames.remove(resolved)) != null) {
            found = true;
            resolved = next;
        }
        if (found) {
            this.followCname(resolved, queryLifecycleObserver, promise);
        } else {
            queryLifecycleObserver.queryFailed(CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION);
        }
    }

    private static Map<String, String> buildAliasMap(DnsResponse response) {
        int answerCount = response.count(DnsSection.ANSWER);
        Map<String, String> cnames = null;
        for (int i = 0; i < answerCount; ++i) {
            ByteBuf recordContent;
            String domainName;
            Object r = response.recordAt(DnsSection.ANSWER, i);
            DnsRecordType type = r.type();
            if (type != DnsRecordType.CNAME || !(r instanceof DnsRawRecord) || (domainName = DnsNameResolverContext.decodeDomainName(recordContent = ((ByteBufHolder)r).content())) == null) continue;
            if (cnames == null) {
                cnames = new HashMap<String, String>(Math.min(8, answerCount));
            }
            cnames.put(r.name().toLowerCase(Locale.US), domainName.toLowerCase(Locale.US));
        }
        return cnames != null ? cnames : Collections.emptyMap();
    }

    void tryToFinishResolve(DnsServerAddressStream nameServerAddrStream, int nameServerAddrStreamIndex, DnsQuestion question, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<T> promise, Throwable cause) {
        if (!this.queriesInProgress.isEmpty()) {
            queryLifecycleObserver.queryCancelled(this.allowedQueries);
            if (this.gotPreferredAddress()) {
                this.finishResolve(promise, cause);
            }
            return;
        }
        if (this.resolvedEntries == null) {
            if (nameServerAddrStreamIndex < nameServerAddrStream.size()) {
                if (queryLifecycleObserver == NoopDnsQueryLifecycleObserver.INSTANCE) {
                    this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, promise, cause);
                } else {
                    this.query(nameServerAddrStream, nameServerAddrStreamIndex + 1, question, queryLifecycleObserver, promise, cause);
                }
                return;
            }
            queryLifecycleObserver.queryFailed(NAME_SERVERS_EXHAUSTED_EXCEPTION);
            if (cause == null && !this.triedCNAME) {
                this.triedCNAME = true;
                this.query(this.hostname, DnsRecordType.CNAME, this.getNameServers(this.hostname), promise, null);
                return;
            }
        } else {
            queryLifecycleObserver.queryCancelled(this.allowedQueries);
        }
        this.finishResolve(promise, cause);
    }

    private boolean gotPreferredAddress() {
        if (this.resolvedEntries == null) {
            return false;
        }
        int size = this.resolvedEntries.size();
        Class<? extends InetAddress> inetAddressType = this.parent.preferredAddressType().addressType();
        for (int i = 0; i < size; ++i) {
            InetAddress address = this.resolvedEntries.get(i).address();
            if (!inetAddressType.isInstance(address)) continue;
            return true;
        }
        return false;
    }

    private void finishResolve(Promise<T> promise, Throwable cause) {
        if (!this.queriesInProgress.isEmpty()) {
            InternetProtocolFamily[] i = this.queriesInProgress.iterator();
            while (i.hasNext()) {
                Future f = (Future)i.next();
                i.remove();
                if (f.cancel(false)) continue;
                f.addListener(RELEASE_RESPONSE);
            }
        }
        if (this.resolvedEntries != null) {
            for (InternetProtocolFamily f : this.resolvedInternetProtocolFamilies) {
                if (!this.finishResolve(f.addressType(), this.resolvedEntries, promise)) continue;
                return;
            }
        }
        int tries = this.maxAllowedQueries - this.allowedQueries;
        StringBuilder buf = new StringBuilder(64);
        buf.append("failed to resolve '").append(this.hostname).append('\'');
        if (tries > 1) {
            if (tries < this.maxAllowedQueries) {
                buf.append(" after ").append(tries).append(" queries ");
            } else {
                buf.append(". Exceeded max queries per resolve ").append(this.maxAllowedQueries).append(' ');
            }
        }
        UnknownHostException unknownHostException = new UnknownHostException(buf.toString());
        if (cause == null) {
            this.resolveCache.cache(this.hostname, this.additionals, unknownHostException, this.parent.ch.eventLoop());
        } else {
            unknownHostException.initCause(cause);
        }
        promise.tryFailure(unknownHostException);
    }

    abstract boolean finishResolve(Class<? extends InetAddress> var1, List<DnsCacheEntry> var2, Promise<T> var3);

    abstract DnsNameResolverContext<T> newResolverContext(DnsNameResolver var1, String var2, DnsRecord[] var3, DnsCache var4, DnsServerAddressStream var5);

    static String decodeDomainName(ByteBuf in) {
        in.markReaderIndex();
        try {
            String string = DefaultDnsRecordDecoder.decodeName(in);
            return string;
        }
        catch (CorruptedFrameException e) {
            String string = null;
            return string;
        }
        finally {
            in.resetReaderIndex();
        }
    }

    private DnsServerAddressStream getNameServers(String hostname) {
        DnsServerAddressStream stream = this.getNameServersFromCache(hostname);
        return stream == null ? this.nameServerAddrs : stream;
    }

    private void followCname(String cname, DnsQueryLifecycleObserver queryLifecycleObserver, Promise<T> promise) {
        DnsServerAddressStream stream = DnsServerAddresses.singleton(this.getNameServers(cname).next()).stream();
        DnsQuestion cnameQuestion = null;
        if (this.parent.supportsARecords()) {
            try {
                cnameQuestion = DnsNameResolverContext.newQuestion(cname, DnsRecordType.A);
                if (cnameQuestion == null) {
                    return;
                }
            }
            catch (Throwable cause) {
                queryLifecycleObserver.queryFailed(cause);
                PlatformDependent.throwException(cause);
            }
            this.query(stream, 0, cnameQuestion, queryLifecycleObserver.queryCNAMEd(cnameQuestion), promise, null);
        }
        if (this.parent.supportsAAAARecords()) {
            try {
                cnameQuestion = DnsNameResolverContext.newQuestion(cname, DnsRecordType.AAAA);
                if (cnameQuestion == null) {
                    return;
                }
            }
            catch (Throwable cause) {
                queryLifecycleObserver.queryFailed(cause);
                PlatformDependent.throwException(cause);
            }
            this.query(stream, 0, cnameQuestion, queryLifecycleObserver.queryCNAMEd(cnameQuestion), promise, null);
        }
    }

    private boolean query(String hostname, DnsRecordType type, DnsServerAddressStream dnsServerAddressStream, Promise<T> promise, Throwable cause) {
        DnsQuestion question = DnsNameResolverContext.newQuestion(hostname, type);
        if (question == null) {
            return false;
        }
        this.query(dnsServerAddressStream, 0, question, promise, cause);
        return true;
    }

    private static DnsQuestion newQuestion(String hostname, DnsRecordType type) {
        try {
            return new DefaultDnsQuestion(hostname, type);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    static final class AuthoritativeNameServer {
        final int dots;
        final String nsName;
        final String domainName;
        AuthoritativeNameServer next;
        boolean removed;

        AuthoritativeNameServer(int dots, String domainName, String nsName) {
            this.dots = dots;
            this.nsName = nsName;
            this.domainName = domainName;
        }

        boolean isRootServer() {
            return this.dots == 1;
        }

        String domainName() {
            return this.domainName;
        }
    }

    private static final class AuthoritativeNameServerList {
        private final String questionName;
        private AuthoritativeNameServer head;
        private int count;

        AuthoritativeNameServerList(String questionName) {
            this.questionName = questionName.toLowerCase(Locale.US);
        }

        void add(DnsRecord r) {
            if (r.type() != DnsRecordType.NS || !(r instanceof DnsRawRecord)) {
                return;
            }
            if (this.questionName.length() < r.name().length()) {
                return;
            }
            String recordName = r.name().toLowerCase(Locale.US);
            int dots = 0;
            int a = recordName.length() - 1;
            int b = this.questionName.length() - 1;
            while (a >= 0) {
                char c = recordName.charAt(a);
                if (this.questionName.charAt(b) != c) {
                    return;
                }
                if (c == '.') {
                    ++dots;
                }
                --a;
                --b;
            }
            if (this.head != null && this.head.dots > dots) {
                return;
            }
            ByteBuf recordContent = ((ByteBufHolder)((Object)r)).content();
            String domainName = DnsNameResolverContext.decodeDomainName(recordContent);
            if (domainName == null) {
                return;
            }
            if (this.head == null || this.head.dots < dots) {
                this.count = 1;
                this.head = new AuthoritativeNameServer(dots, recordName, domainName);
            } else if (this.head.dots == dots) {
                AuthoritativeNameServer serverName = this.head;
                while (serverName.next != null) {
                    serverName = serverName.next;
                }
                serverName.next = new AuthoritativeNameServer(dots, recordName, domainName);
                ++this.count;
            }
        }

        AuthoritativeNameServer remove(String nsName) {
            AuthoritativeNameServer serverName = this.head;
            while (serverName != null) {
                if (!serverName.removed && serverName.nsName.equalsIgnoreCase(nsName)) {
                    serverName.removed = true;
                    return serverName;
                }
                serverName = serverName.next;
            }
            return null;
        }

        int size() {
            return this.count;
        }
    }

    private final class DnsCacheIterable
    implements Iterable<InetSocketAddress> {
        private final List<? extends DnsCacheEntry> entries;

        DnsCacheIterable(List<? extends DnsCacheEntry> entries) {
            this.entries = entries;
        }

        @Override
        public Iterator<InetSocketAddress> iterator() {
            return new Iterator<InetSocketAddress>(){
                Iterator<? extends DnsCacheEntry> entryIterator;
                {
                    this.entryIterator = DnsCacheIterable.this.entries.iterator();
                }

                @Override
                public boolean hasNext() {
                    return this.entryIterator.hasNext();
                }

                @Override
                public InetSocketAddress next() {
                    InetAddress address = this.entryIterator.next().address();
                    return new InetSocketAddress(address, DnsNameResolverContext.this.parent.dnsRedirectPort(address));
                }

                @Override
                public void remove() {
                    this.entryIterator.remove();
                }
            };
        }

    }

    private static final class SearchDomainUnknownHostException
    extends UnknownHostException {
        private static final long serialVersionUID = -8573510133644997085L;

        SearchDomainUnknownHostException(Throwable cause, String originalHostname) {
            super("Search domain query failed. Original hostname: '" + originalHostname + "' " + cause.getMessage());
            this.setStackTrace(cause.getStackTrace());
            this.initCause(cause.getCause());
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

}


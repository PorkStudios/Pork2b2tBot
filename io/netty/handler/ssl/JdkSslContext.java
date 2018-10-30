/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkDefaultApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkNpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

public class JdkSslContext
extends SslContext {
    private static final InternalLogger logger;
    static final String PROTOCOL = "TLS";
    private static final String[] DEFAULT_PROTOCOLS;
    private static final List<String> DEFAULT_CIPHERS;
    private static final Set<String> SUPPORTED_CIPHERS;
    private final String[] protocols;
    private final String[] cipherSuites;
    private final List<String> unmodifiableCipherSuites;
    private final JdkApplicationProtocolNegotiator apn;
    private final ClientAuth clientAuth;
    private final SSLContext sslContext;
    private final boolean isClient;

    public JdkSslContext(SSLContext sslContext, boolean isClient, ClientAuth clientAuth) {
        this(sslContext, isClient, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, clientAuth, null, false);
    }

    public JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, ClientAuth clientAuth) {
        this(sslContext, isClient, ciphers, cipherFilter, JdkSslContext.toNegotiator(apn, !isClient), clientAuth, null, false);
    }

    JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, ClientAuth clientAuth, String[] protocols, boolean startTls) {
        super(startTls);
        this.apn = ObjectUtil.checkNotNull(apn, "apn");
        this.clientAuth = ObjectUtil.checkNotNull(clientAuth, "clientAuth");
        this.cipherSuites = ObjectUtil.checkNotNull(cipherFilter, "cipherFilter").filterCipherSuites(ciphers, DEFAULT_CIPHERS, SUPPORTED_CIPHERS);
        this.protocols = protocols == null ? DEFAULT_PROTOCOLS : protocols;
        this.unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));
        this.sslContext = ObjectUtil.checkNotNull(sslContext, "sslContext");
        this.isClient = isClient;
    }

    public final SSLContext context() {
        return this.sslContext;
    }

    @Override
    public final boolean isClient() {
        return this.isClient;
    }

    @Override
    public final SSLSessionContext sessionContext() {
        if (this.isServer()) {
            return this.context().getServerSessionContext();
        }
        return this.context().getClientSessionContext();
    }

    @Override
    public final List<String> cipherSuites() {
        return this.unmodifiableCipherSuites;
    }

    @Override
    public final long sessionCacheSize() {
        return this.sessionContext().getSessionCacheSize();
    }

    @Override
    public final long sessionTimeout() {
        return this.sessionContext().getSessionTimeout();
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc) {
        return this.configureAndWrapEngine(this.context().createSSLEngine(), alloc);
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return this.configureAndWrapEngine(this.context().createSSLEngine(peerHost, peerPort), alloc);
    }

    private SSLEngine configureAndWrapEngine(SSLEngine engine, ByteBufAllocator alloc) {
        JdkApplicationProtocolNegotiator.SslEngineWrapperFactory factory;
        engine.setEnabledCipherSuites(this.cipherSuites);
        engine.setEnabledProtocols(this.protocols);
        engine.setUseClientMode(this.isClient());
        if (this.isServer()) {
            switch (this.clientAuth) {
                case OPTIONAL: {
                    engine.setWantClientAuth(true);
                    break;
                }
                case REQUIRE: {
                    engine.setNeedClientAuth(true);
                    break;
                }
                case NONE: {
                    break;
                }
                default: {
                    throw new Error("Unknown auth " + (Object)((Object)this.clientAuth));
                }
            }
        }
        if ((factory = this.apn.wrapperFactory()) instanceof JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory) {
            return ((JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory)factory).wrapSslEngine(engine, alloc, this.apn, this.isServer());
        }
        return factory.wrapSslEngine(engine, this.apn, this.isServer());
    }

    @Override
    public final JdkApplicationProtocolNegotiator applicationProtocolNegotiator() {
        return this.apn;
    }

    static JdkApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig config, boolean isServer) {
        if (config == null) {
            return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
        }
        switch (config.protocol()) {
            case NONE: {
                return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
            }
            case ALPN: {
                if (isServer) {
                    switch (config.selectorFailureBehavior()) {
                        case FATAL_ALERT: {
                            return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                        }
                        case NO_ADVERTISE: {
                            return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                        }
                    }
                    throw new UnsupportedOperationException("JDK provider does not support " + (Object)((Object)config.selectorFailureBehavior()) + " failure behavior");
                }
                switch (config.selectedListenerFailureBehavior()) {
                    case ACCEPT: {
                        return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                    }
                    case FATAL_ALERT: {
                        return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                    }
                }
                throw new UnsupportedOperationException("JDK provider does not support " + (Object)((Object)config.selectedListenerFailureBehavior()) + " failure behavior");
            }
            case NPN: {
                if (isServer) {
                    switch (config.selectedListenerFailureBehavior()) {
                        case ACCEPT: {
                            return new JdkNpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                        }
                        case FATAL_ALERT: {
                            return new JdkNpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                        }
                    }
                    throw new UnsupportedOperationException("JDK provider does not support " + (Object)((Object)config.selectedListenerFailureBehavior()) + " failure behavior");
                }
                switch (config.selectorFailureBehavior()) {
                    case FATAL_ALERT: {
                        return new JdkNpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                    }
                    case NO_ADVERTISE: {
                        return new JdkNpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                    }
                }
                throw new UnsupportedOperationException("JDK provider does not support " + (Object)((Object)config.selectorFailureBehavior()) + " failure behavior");
            }
        }
        throw new UnsupportedOperationException("JDK provider does not support " + (Object)((Object)config.protocol()) + " protocol");
    }

    @Deprecated
    protected static KeyManagerFactory buildKeyManagerFactory(File certChainFile, File keyFile, String keyPassword, KeyManagerFactory kmf) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        return JdkSslContext.buildKeyManagerFactory(certChainFile, algorithm, keyFile, keyPassword, kmf);
    }

    @Deprecated
    protected static KeyManagerFactory buildKeyManagerFactory(File certChainFile, String keyAlgorithm, File keyFile, String keyPassword, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
        return JdkSslContext.buildKeyManagerFactory(JdkSslContext.toX509Certificates(certChainFile), keyAlgorithm, JdkSslContext.toPrivateKey(keyFile, keyPassword), keyPassword, kmf);
    }

    static {
        int i;
        SSLContext context;
        logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
        try {
            context = SSLContext.getInstance(PROTOCOL);
            context.init(null, null, null);
        }
        catch (Exception e) {
            throw new Error("failed to initialize the default SSL context", e);
        }
        SSLEngine engine = context.createSSLEngine();
        String[] supportedProtocols = engine.getSupportedProtocols();
        HashSet<String> supportedProtocolsSet = new HashSet<String>(supportedProtocols.length);
        for (i = 0; i < supportedProtocols.length; ++i) {
            supportedProtocolsSet.add(supportedProtocols[i]);
        }
        ArrayList<String> protocols = new ArrayList<String>();
        SslUtils.addIfSupported(supportedProtocolsSet, protocols, "TLSv1.2", "TLSv1.1", "TLSv1");
        DEFAULT_PROTOCOLS = !protocols.isEmpty() ? protocols.toArray(new String[protocols.size()]) : engine.getEnabledProtocols();
        String[] supportedCiphers = engine.getSupportedCipherSuites();
        SUPPORTED_CIPHERS = new HashSet<String>(supportedCiphers.length);
        for (i = 0; i < supportedCiphers.length; ++i) {
            String supportedCipher = supportedCiphers[i];
            SUPPORTED_CIPHERS.add(supportedCipher);
            if (!supportedCipher.startsWith("SSL_")) continue;
            SUPPORTED_CIPHERS.add("TLS_" + supportedCipher.substring("SSL_".length()));
        }
        ArrayList<String> ciphers = new ArrayList<String>();
        SslUtils.addIfSupported(SUPPORTED_CIPHERS, ciphers, SslUtils.DEFAULT_CIPHER_SUITES);
        SslUtils.useFallbackCiphersIfDefaultIsEmpty(ciphers, engine.getEnabledCipherSuites());
        DEFAULT_CIPHERS = Collections.unmodifiableList(ciphers);
        if (logger.isDebugEnabled()) {
            logger.debug("Default protocols (JDK): {} ", (Object)Arrays.asList(DEFAULT_PROTOCOLS));
            logger.debug("Default cipher suites (JDK): {}", (Object)DEFAULT_CIPHERS);
        }
    }

}


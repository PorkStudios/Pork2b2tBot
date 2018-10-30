/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkDefaultApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslContext;
import java.io.File;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

@Deprecated
public final class JdkSslClientContext
extends JdkSslContext {
    @Deprecated
    public JdkSslClientContext() throws SSLException {
        this(null, null);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile) throws SSLException {
        this(certChainFile, null);
    }

    @Deprecated
    public JdkSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, trustManagerFactory);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        this(certChainFile, trustManagerFactory, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, trustManagerFactory, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, JdkSslClientContext.toNegotiator(JdkSslClientContext.toApplicationProtocolConfig(nextProtocols), false), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, trustManagerFactory, ciphers, cipherFilter, JdkSslClientContext.toNegotiator(apn, false), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, certChainFile, trustManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }

    JdkSslClientContext(Provider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(JdkSslClientContext.newSSLContext(provider, JdkSslClientContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, null, null, null, null, sessionCacheSize, sessionTimeout), true, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }

    @Deprecated
    public JdkSslClientContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(trustCertCollectionFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword, keyManagerFactory, ciphers, cipherFilter, JdkSslClientContext.toNegotiator(apn, false), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public JdkSslClientContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(JdkSslClientContext.newSSLContext(null, JdkSslClientContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, JdkSslClientContext.toX509CertificatesInternal(keyCertChainFile), JdkSslClientContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout), true, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }

    JdkSslClientContext(Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(JdkSslClientContext.newSSLContext(sslContextProvider, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout), true, ciphers, cipherFilter, JdkSslClientContext.toNegotiator(apn, false), ClientAuth.NONE, protocols, false);
    }

    private static SSLContext newSSLContext(Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, long sessionCacheSize, long sessionTimeout) throws SSLException {
        try {
            if (trustCertCollection != null) {
                trustManagerFactory = JdkSslClientContext.buildTrustManagerFactory(trustCertCollection, trustManagerFactory);
            }
            if (keyCertChain != null) {
                keyManagerFactory = JdkSslClientContext.buildKeyManagerFactory(keyCertChain, key, keyPassword, keyManagerFactory);
            }
            SSLContext ctx = sslContextProvider == null ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", sslContextProvider);
            ctx.init(keyManagerFactory == null ? null : keyManagerFactory.getKeyManagers(), trustManagerFactory == null ? null : trustManagerFactory.getTrustManagers(), null);
            SSLSessionContext sessCtx = ctx.getClientSessionContext();
            if (sessionCacheSize > 0L) {
                sessCtx.setSessionCacheSize((int)Math.min(sessionCacheSize, Integer.MAX_VALUE));
            }
            if (sessionTimeout > 0L) {
                sessCtx.setSessionTimeout((int)Math.min(sessionTimeout, Integer.MAX_VALUE));
            }
            return ctx;
        }
        catch (Exception e) {
            if (e instanceof SSLException) {
                throw (SSLException)e;
            }
            throw new SSLException("failed to initialize the client-side SSL context", e);
        }
    }
}


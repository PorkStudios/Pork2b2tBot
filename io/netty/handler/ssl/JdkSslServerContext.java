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
public final class JdkSslServerContext
extends JdkSslContext {
    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile) throws SSLException {
        this(certChainFile, keyFile, null);
    }

    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        this(certChainFile, keyFile, keyPassword, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
    }

    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, keyFile, keyPassword, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, JdkSslServerContext.toNegotiator(JdkSslServerContext.toApplicationProtocolConfig(nextProtocols), true), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, keyFile, keyPassword, ciphers, cipherFilter, JdkSslServerContext.toNegotiator(apn, true), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, certChainFile, keyFile, keyPassword, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }

    JdkSslServerContext(Provider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(JdkSslServerContext.newSSLContext(provider, null, null, JdkSslServerContext.toX509CertificatesInternal(certChainFile), JdkSslServerContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, null, sessionCacheSize, sessionTimeout), false, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }

    @Deprecated
    public JdkSslServerContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(trustCertCollectionFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword, keyManagerFactory, ciphers, cipherFilter, JdkSslServerContext.toNegotiator(apn, true), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public JdkSslServerContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super(JdkSslServerContext.newSSLContext(null, JdkSslServerContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, JdkSslServerContext.toX509CertificatesInternal(keyCertChainFile), JdkSslServerContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout), false, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }

    JdkSslServerContext(Provider provider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls) throws SSLException {
        super(JdkSslServerContext.newSSLContext(provider, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout), false, ciphers, cipherFilter, JdkSslServerContext.toNegotiator(apn, true), clientAuth, protocols, startTls);
    }

    private static SSLContext newSSLContext(Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, long sessionCacheSize, long sessionTimeout) throws SSLException {
        if (key == null && keyManagerFactory == null) {
            throw new NullPointerException("key, keyManagerFactory");
        }
        try {
            if (trustCertCollection != null) {
                trustManagerFactory = JdkSslServerContext.buildTrustManagerFactory(trustCertCollection, trustManagerFactory);
            }
            if (key != null) {
                keyManagerFactory = JdkSslServerContext.buildKeyManagerFactory(keyCertChain, key, keyPassword, keyManagerFactory);
            }
            SSLContext ctx = sslContextProvider == null ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", sslContextProvider);
            ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory == null ? null : trustManagerFactory.getTrustManagers(), null);
            SSLSessionContext sessCtx = ctx.getServerSessionContext();
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
            throw new SSLException("failed to initialize the server-side SSL context", e);
        }
    }
}


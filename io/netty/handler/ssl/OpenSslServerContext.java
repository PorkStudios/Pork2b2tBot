/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslContext;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.OpenSslServerSessionContext;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslServerContext;
import java.io.File;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

public final class OpenSslServerContext
extends OpenSslContext {
    private final OpenSslServerSessionContext sessionContext;
    private final OpenSslKeyMaterialManager keyMaterialManager;

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile) throws SSLException {
        this(certChainFile, keyFile, null);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        this(certChainFile, keyFile, keyPassword, null, IdentityCipherSuiteFilter.INSTANCE, ApplicationProtocolConfig.DISABLED, 0L, 0L);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, keyFile, keyPassword, ciphers, IdentityCipherSuiteFilter.INSTANCE, apn, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, keyFile, keyPassword, ciphers, OpenSslServerContext.toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, ApplicationProtocolConfig config, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(certChainFile, keyFile, keyPassword, trustManagerFactory, ciphers, OpenSslServerContext.toNegotiator(config), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, null, apn, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, null, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig config, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(trustCertCollectionFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword, keyManagerFactory, ciphers, cipherFilter, OpenSslServerContext.toNegotiator(config), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig config, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, OpenSslServerContext.toNegotiator(config), sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(OpenSslServerContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, OpenSslServerContext.toX509CertificatesInternal(keyCertChainFile), OpenSslServerContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, ClientAuth.NONE, null, false, false);
    }

    OpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp) throws SSLException {
        this(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, OpenSslServerContext.toNegotiator(apn), sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private OpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp) throws SSLException {
        super(ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, 1, (Certificate[])keyCertChain, clientAuth, protocols, startTls, enableOcsp);
        boolean success = false;
        try {
            ReferenceCountedOpenSslServerContext.ServerContext context = ReferenceCountedOpenSslServerContext.newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory);
            this.sessionContext = context.sessionContext;
            this.keyMaterialManager = context.keyMaterialManager;
            success = true;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }

    @Override
    public OpenSslServerSessionContext sessionContext() {
        return this.sessionContext;
    }

    @Override
    OpenSslKeyMaterialManager keyMaterialManager() {
        return this.keyMaterialManager;
    }
}


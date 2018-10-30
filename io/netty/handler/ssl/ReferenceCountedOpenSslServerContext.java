/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.CertificateVerifier
 *  io.netty.internal.tcnative.SSLContext
 *  io.netty.internal.tcnative.SniHostNameMatcher
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslExtendedKeyMaterialManager;
import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.OpenSslServerSessionContext;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.internal.tcnative.CertificateVerifier;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SniHostNameMatcher;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public final class ReferenceCountedOpenSslServerContext
extends ReferenceCountedOpenSslContext {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslServerContext.class);
    private static final byte[] ID = new byte[]{110, 101, 116, 116, 121};
    private final OpenSslServerSessionContext sessionContext;
    private final OpenSslKeyMaterialManager keyMaterialManager;

    ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp) throws SSLException {
        this(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, ReferenceCountedOpenSslServerContext.toNegotiator(apn), sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp) throws SSLException {
        super(ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, 1, (Certificate[])keyCertChain, clientAuth, protocols, startTls, enableOcsp, true);
        boolean success = false;
        try {
            ServerContext context = ReferenceCountedOpenSslServerContext.newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static ServerContext newSessionContext(ReferenceCountedOpenSslContext thiz, long ctx, OpenSslEngineMap engineMap, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory) throws SSLException {
        ServerContext result = new ServerContext();
        try {
            SSLContext.setVerify((long)ctx, (int)0, (int)10);
            if (!OpenSsl.useKeyManagerFactory()) {
                if (keyManagerFactory != null) {
                    throw new IllegalArgumentException("KeyManagerFactory not supported");
                }
                ObjectUtil.checkNotNull(keyCertChain, "keyCertChain");
                ReferenceCountedOpenSslServerContext.setKeyMaterial(ctx, keyCertChain, key, keyPassword);
            } else {
                X509KeyManager keyManager;
                if (keyManagerFactory == null) {
                    keyManagerFactory = ReferenceCountedOpenSslServerContext.buildKeyManagerFactory(keyCertChain, key, keyPassword, keyManagerFactory);
                }
                result.keyMaterialManager = ReferenceCountedOpenSslServerContext.useExtendedKeyManager(keyManager = ReferenceCountedOpenSslServerContext.chooseX509KeyManager(keyManagerFactory.getKeyManagers())) ? new OpenSslExtendedKeyMaterialManager((X509ExtendedKeyManager)keyManager, keyPassword) : new OpenSslKeyMaterialManager(keyManager, keyPassword);
            }
        }
        catch (Exception e) {
            throw new SSLException("failed to set certificate and key", e);
        }
        try {
            if (trustCertCollection != null) {
                trustManagerFactory = ReferenceCountedOpenSslServerContext.buildTrustManagerFactory(trustCertCollection, trustManagerFactory);
            } else if (trustManagerFactory == null) {
                trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore)null);
            }
            X509TrustManager manager = ReferenceCountedOpenSslServerContext.chooseTrustManager(trustManagerFactory.getTrustManagers());
            if (ReferenceCountedOpenSslServerContext.useExtendedTrustManager(manager)) {
                SSLContext.setCertVerifyCallback((long)ctx, (CertificateVerifier)new ExtendedTrustManagerVerifyCallback(engineMap, (X509ExtendedTrustManager)manager));
            } else {
                SSLContext.setCertVerifyCallback((long)ctx, (CertificateVerifier)new TrustManagerVerifyCallback(engineMap, manager));
            }
            X509Certificate[] issuers = manager.getAcceptedIssuers();
            if (issuers != null && issuers.length > 0) {
                long bio = 0L;
                try {
                    bio = ReferenceCountedOpenSslServerContext.toBIO(issuers);
                    if (!SSLContext.setCACertificateBio((long)ctx, (long)bio)) {
                        throw new SSLException("unable to setup accepted issuers for trustmanager " + manager);
                    }
                }
                finally {
                    ReferenceCountedOpenSslServerContext.freeBio(bio);
                }
            }
            if (PlatformDependent.javaVersion() >= 8) {
                SSLContext.setSniHostnameMatcher((long)ctx, (SniHostNameMatcher)new OpenSslSniHostnameMatcher(engineMap));
            }
        }
        catch (SSLException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SSLException("unable to setup trustmanager", e);
        }
        result.sessionContext = new OpenSslServerSessionContext(thiz);
        result.sessionContext.setSessionIdContext(ID);
        return result;
    }

    private static final class OpenSslSniHostnameMatcher
    implements SniHostNameMatcher {
        private final OpenSslEngineMap engineMap;

        OpenSslSniHostnameMatcher(OpenSslEngineMap engineMap) {
            this.engineMap = engineMap;
        }

        public boolean match(long ssl, String hostname) {
            ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
            if (engine != null) {
                return engine.checkSniHostnameMatch(hostname);
            }
            logger.warn("No ReferenceCountedOpenSslEngine found for SSL pointer: {}", (Object)ssl);
            return false;
        }
    }

    private static final class ExtendedTrustManagerVerifyCallback
    extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
        private final X509ExtendedTrustManager manager;

        ExtendedTrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509ExtendedTrustManager manager) {
            super(engineMap);
            this.manager = manager;
        }

        @Override
        void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
            this.manager.checkClientTrusted(peerCerts, auth, engine);
        }
    }

    private static final class TrustManagerVerifyCallback
    extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
        private final X509TrustManager manager;

        TrustManagerVerifyCallback(OpenSslEngineMap engineMap, X509TrustManager manager) {
            super(engineMap);
            this.manager = manager;
        }

        @Override
        void verify(ReferenceCountedOpenSslEngine engine, X509Certificate[] peerCerts, String auth) throws Exception {
            this.manager.checkClientTrusted(peerCerts, auth);
        }
    }

    static final class ServerContext {
        OpenSslServerSessionContext sessionContext;
        OpenSslKeyMaterialManager keyMaterialManager;

        ServerContext() {
        }
    }

}


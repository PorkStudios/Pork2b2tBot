/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.CertificateRequestedCallback
 *  io.netty.internal.tcnative.CertificateRequestedCallback$KeyMaterial
 *  io.netty.internal.tcnative.CertificateVerifier
 *  io.netty.internal.tcnative.SSLContext
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslExtendedKeyMaterialManager;
import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.internal.tcnative.CertificateRequestedCallback;
import io.netty.internal.tcnative.CertificateVerifier;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public final class ReferenceCountedOpenSslClientContext
extends ReferenceCountedOpenSslContext {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslClientContext.class);
    private final OpenSslSessionContext sessionContext;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ReferenceCountedOpenSslClientContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, boolean enableOcsp) throws SSLException {
        super(ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, 0, (Certificate[])keyCertChain, ClientAuth.NONE, protocols, false, enableOcsp, true);
        boolean success = false;
        try {
            this.sessionContext = ReferenceCountedOpenSslClientContext.newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory);
            success = true;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }

    @Override
    OpenSslKeyMaterialManager keyMaterialManager() {
        return null;
    }

    @Override
    public OpenSslSessionContext sessionContext() {
        return this.sessionContext;
    }

    static OpenSslSessionContext newSessionContext(ReferenceCountedOpenSslContext thiz, long ctx, OpenSslEngineMap engineMap, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory) throws SSLException {
        if (key == null && keyCertChain != null || key != null && keyCertChain == null) {
            throw new IllegalArgumentException("Either both keyCertChain and key needs to be null or none of them");
        }
        try {
            if (!OpenSsl.useKeyManagerFactory()) {
                if (keyManagerFactory != null) {
                    throw new IllegalArgumentException("KeyManagerFactory not supported");
                }
                if (keyCertChain != null) {
                    ReferenceCountedOpenSslClientContext.setKeyMaterial(ctx, keyCertChain, key, keyPassword);
                }
            } else {
                if (keyManagerFactory == null && keyCertChain != null) {
                    keyManagerFactory = ReferenceCountedOpenSslClientContext.buildKeyManagerFactory(keyCertChain, key, keyPassword, keyManagerFactory);
                }
                if (keyManagerFactory != null) {
                    X509KeyManager keyManager = ReferenceCountedOpenSslClientContext.chooseX509KeyManager(keyManagerFactory.getKeyManagers());
                    OpenSslKeyMaterialManager materialManager = ReferenceCountedOpenSslClientContext.useExtendedKeyManager(keyManager) ? new OpenSslExtendedKeyMaterialManager((X509ExtendedKeyManager)keyManager, keyPassword) : new OpenSslKeyMaterialManager(keyManager, keyPassword);
                    SSLContext.setCertRequestedCallback((long)ctx, (CertificateRequestedCallback)new OpenSslCertificateRequestedCallback(engineMap, materialManager));
                }
            }
        }
        catch (Exception e) {
            throw new SSLException("failed to set certificate and key", e);
        }
        SSLContext.setVerify((long)ctx, (int)0, (int)10);
        try {
            if (trustCertCollection != null) {
                trustManagerFactory = ReferenceCountedOpenSslClientContext.buildTrustManagerFactory(trustCertCollection, trustManagerFactory);
            } else if (trustManagerFactory == null) {
                trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore)null);
            }
            X509TrustManager manager = ReferenceCountedOpenSslClientContext.chooseTrustManager(trustManagerFactory.getTrustManagers());
            if (ReferenceCountedOpenSslClientContext.useExtendedTrustManager(manager)) {
                SSLContext.setCertVerifyCallback((long)ctx, (CertificateVerifier)new ExtendedTrustManagerVerifyCallback(engineMap, (X509ExtendedTrustManager)manager));
            } else {
                SSLContext.setCertVerifyCallback((long)ctx, (CertificateVerifier)new TrustManagerVerifyCallback(engineMap, manager));
            }
        }
        catch (Exception e) {
            throw new SSLException("unable to setup trustmanager", e);
        }
        return new OpenSslClientSessionContext(thiz);
    }

    private static final class OpenSslCertificateRequestedCallback
    implements CertificateRequestedCallback {
        private final OpenSslEngineMap engineMap;
        private final OpenSslKeyMaterialManager keyManagerHolder;

        OpenSslCertificateRequestedCallback(OpenSslEngineMap engineMap, OpenSslKeyMaterialManager keyManagerHolder) {
            this.engineMap = engineMap;
            this.keyManagerHolder = keyManagerHolder;
        }

        public CertificateRequestedCallback.KeyMaterial requested(long ssl, byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals) {
            ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
            try {
                X500Principal[] issuers;
                Set<String> keyTypesSet = OpenSslCertificateRequestedCallback.supportedClientKeyTypes(keyTypeBytes);
                String[] keyTypes = keyTypesSet.toArray(new String[keyTypesSet.size()]);
                if (asn1DerEncodedPrincipals == null) {
                    issuers = null;
                } else {
                    issuers = new X500Principal[asn1DerEncodedPrincipals.length];
                    for (int i = 0; i < asn1DerEncodedPrincipals.length; ++i) {
                        issuers[i] = new X500Principal(asn1DerEncodedPrincipals[i]);
                    }
                }
                return this.keyManagerHolder.keyMaterial(engine, keyTypes, issuers);
            }
            catch (Throwable cause) {
                logger.debug("request of key failed", cause);
                SSLHandshakeException e = new SSLHandshakeException("General OpenSslEngine problem");
                e.initCause(cause);
                engine.handshakeException = e;
                return null;
            }
        }

        private static Set<String> supportedClientKeyTypes(byte[] clientCertificateTypes) {
            HashSet<String> result = new HashSet<String>(clientCertificateTypes.length);
            for (byte keyTypeCode : clientCertificateTypes) {
                String keyType = OpenSslCertificateRequestedCallback.clientKeyType(keyTypeCode);
                if (keyType == null) continue;
                result.add(keyType);
            }
            return result;
        }

        private static String clientKeyType(byte clientCertificateType) {
            switch (clientCertificateType) {
                case 1: {
                    return "RSA";
                }
                case 3: {
                    return "DH_RSA";
                }
                case 64: {
                    return "EC";
                }
                case 65: {
                    return "EC_RSA";
                }
                case 66: {
                    return "EC_EC";
                }
            }
            return null;
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
            this.manager.checkServerTrusted(peerCerts, auth, engine);
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
            this.manager.checkServerTrusted(peerCerts, auth);
        }
    }

    static final class OpenSslClientSessionContext
    extends OpenSslSessionContext {
        OpenSslClientSessionContext(ReferenceCountedOpenSslContext context) {
            super(context);
        }

        @Override
        public void setSessionTimeout(int seconds) {
            if (seconds < 0) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public int getSessionTimeout() {
            return 0;
        }

        @Override
        public void setSessionCacheSize(int size) {
            if (size < 0) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public int getSessionCacheSize() {
            return 0;
        }

        @Override
        public void setSessionCacheEnabled(boolean enabled) {
        }

        @Override
        public boolean isSessionCacheEnabled() {
            return false;
        }
    }

}


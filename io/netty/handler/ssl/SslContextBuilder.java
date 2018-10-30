/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public final class SslContextBuilder {
    private final boolean forServer;
    private SslProvider provider;
    private Provider sslContextProvider;
    private X509Certificate[] trustCertCollection;
    private TrustManagerFactory trustManagerFactory;
    private X509Certificate[] keyCertChain;
    private PrivateKey key;
    private String keyPassword;
    private KeyManagerFactory keyManagerFactory;
    private Iterable<String> ciphers;
    private CipherSuiteFilter cipherFilter = IdentityCipherSuiteFilter.INSTANCE;
    private ApplicationProtocolConfig apn;
    private long sessionCacheSize;
    private long sessionTimeout;
    private ClientAuth clientAuth = ClientAuth.NONE;
    private String[] protocols;
    private boolean startTls;
    private boolean enableOcsp;

    public static SslContextBuilder forClient() {
        return new SslContextBuilder(false);
    }

    public static SslContextBuilder forServer(File keyCertChainFile, File keyFile) {
        return new SslContextBuilder(true).keyManager(keyCertChainFile, keyFile);
    }

    public static SslContextBuilder forServer(InputStream keyCertChainInputStream, InputStream keyInputStream) {
        return new SslContextBuilder(true).keyManager(keyCertChainInputStream, keyInputStream);
    }

    public static /* varargs */ SslContextBuilder forServer(PrivateKey key, X509Certificate ... keyCertChain) {
        return new SslContextBuilder(true).keyManager(key, keyCertChain);
    }

    public static SslContextBuilder forServer(File keyCertChainFile, File keyFile, String keyPassword) {
        return new SslContextBuilder(true).keyManager(keyCertChainFile, keyFile, keyPassword);
    }

    public static SslContextBuilder forServer(InputStream keyCertChainInputStream, InputStream keyInputStream, String keyPassword) {
        return new SslContextBuilder(true).keyManager(keyCertChainInputStream, keyInputStream, keyPassword);
    }

    public static /* varargs */ SslContextBuilder forServer(PrivateKey key, String keyPassword, X509Certificate ... keyCertChain) {
        return new SslContextBuilder(true).keyManager(key, keyPassword, keyCertChain);
    }

    public static SslContextBuilder forServer(KeyManagerFactory keyManagerFactory) {
        return new SslContextBuilder(true).keyManager(keyManagerFactory);
    }

    private SslContextBuilder(boolean forServer) {
        this.forServer = forServer;
    }

    public SslContextBuilder sslProvider(SslProvider provider) {
        this.provider = provider;
        return this;
    }

    public SslContextBuilder sslContextProvider(Provider sslContextProvider) {
        this.sslContextProvider = sslContextProvider;
        return this;
    }

    public SslContextBuilder trustManager(File trustCertCollectionFile) {
        try {
            return this.trustManager(SslContext.toX509Certificates(trustCertCollectionFile));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid certificates: " + trustCertCollectionFile, e);
        }
    }

    public SslContextBuilder trustManager(InputStream trustCertCollectionInputStream) {
        try {
            return this.trustManager(SslContext.toX509Certificates(trustCertCollectionInputStream));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Input stream does not contain valid certificates.", e);
        }
    }

    public /* varargs */ SslContextBuilder trustManager(X509Certificate ... trustCertCollection) {
        this.trustCertCollection = trustCertCollection != null ? (X509Certificate[])trustCertCollection.clone() : null;
        this.trustManagerFactory = null;
        return this;
    }

    public SslContextBuilder trustManager(TrustManagerFactory trustManagerFactory) {
        this.trustCertCollection = null;
        this.trustManagerFactory = trustManagerFactory;
        return this;
    }

    public SslContextBuilder keyManager(File keyCertChainFile, File keyFile) {
        return this.keyManager(keyCertChainFile, keyFile, null);
    }

    public SslContextBuilder keyManager(InputStream keyCertChainInputStream, InputStream keyInputStream) {
        return this.keyManager(keyCertChainInputStream, keyInputStream, null);
    }

    public /* varargs */ SslContextBuilder keyManager(PrivateKey key, X509Certificate ... keyCertChain) {
        return this.keyManager(key, (String)null, keyCertChain);
    }

    public SslContextBuilder keyManager(File keyCertChainFile, File keyFile, String keyPassword) {
        PrivateKey key;
        X509Certificate[] keyCertChain;
        try {
            keyCertChain = SslContext.toX509Certificates(keyCertChainFile);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid certificates: " + keyCertChainFile, e);
        }
        try {
            key = SslContext.toPrivateKey(keyFile, keyPassword);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("File does not contain valid private key: " + keyFile, e);
        }
        return this.keyManager(key, keyPassword, keyCertChain);
    }

    public SslContextBuilder keyManager(InputStream keyCertChainInputStream, InputStream keyInputStream, String keyPassword) {
        PrivateKey key;
        X509Certificate[] keyCertChain;
        try {
            keyCertChain = SslContext.toX509Certificates(keyCertChainInputStream);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Input stream not contain valid certificates.", e);
        }
        try {
            key = SslContext.toPrivateKey(keyInputStream, keyPassword);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Input stream does not contain valid private key.", e);
        }
        return this.keyManager(key, keyPassword, keyCertChain);
    }

    public /* varargs */ SslContextBuilder keyManager(PrivateKey key, String keyPassword, X509Certificate ... keyCertChain) {
        if (this.forServer) {
            ObjectUtil.checkNotNull(keyCertChain, "keyCertChain required for servers");
            if (keyCertChain.length == 0) {
                throw new IllegalArgumentException("keyCertChain must be non-empty");
            }
            ObjectUtil.checkNotNull(key, "key required for servers");
        }
        if (keyCertChain == null || keyCertChain.length == 0) {
            this.keyCertChain = null;
        } else {
            for (X509Certificate cert : keyCertChain) {
                if (cert != null) continue;
                throw new IllegalArgumentException("keyCertChain contains null entry");
            }
            this.keyCertChain = (X509Certificate[])keyCertChain.clone();
        }
        this.key = key;
        this.keyPassword = keyPassword;
        this.keyManagerFactory = null;
        return this;
    }

    public SslContextBuilder keyManager(KeyManagerFactory keyManagerFactory) {
        if (this.forServer) {
            ObjectUtil.checkNotNull(keyManagerFactory, "keyManagerFactory required for servers");
        }
        this.keyCertChain = null;
        this.key = null;
        this.keyPassword = null;
        this.keyManagerFactory = keyManagerFactory;
        return this;
    }

    public SslContextBuilder ciphers(Iterable<String> ciphers) {
        return this.ciphers(ciphers, IdentityCipherSuiteFilter.INSTANCE);
    }

    public SslContextBuilder ciphers(Iterable<String> ciphers, CipherSuiteFilter cipherFilter) {
        ObjectUtil.checkNotNull(cipherFilter, "cipherFilter");
        this.ciphers = ciphers;
        this.cipherFilter = cipherFilter;
        return this;
    }

    public SslContextBuilder applicationProtocolConfig(ApplicationProtocolConfig apn) {
        this.apn = apn;
        return this;
    }

    public SslContextBuilder sessionCacheSize(long sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
        return this;
    }

    public SslContextBuilder sessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public SslContextBuilder clientAuth(ClientAuth clientAuth) {
        this.clientAuth = ObjectUtil.checkNotNull(clientAuth, "clientAuth");
        return this;
    }

    public /* varargs */ SslContextBuilder protocols(String ... protocols) {
        this.protocols = protocols == null ? null : (String[])protocols.clone();
        return this;
    }

    public SslContextBuilder startTls(boolean startTls) {
        this.startTls = startTls;
        return this;
    }

    public SslContextBuilder enableOcsp(boolean enableOcsp) {
        this.enableOcsp = enableOcsp;
        return this;
    }

    public SslContext build() throws SSLException {
        if (this.forServer) {
            return SslContext.newServerContextInternal(this.provider, this.sslContextProvider, this.trustCertCollection, this.trustManagerFactory, this.keyCertChain, this.key, this.keyPassword, this.keyManagerFactory, this.ciphers, this.cipherFilter, this.apn, this.sessionCacheSize, this.sessionTimeout, this.clientAuth, this.protocols, this.startTls, this.enableOcsp);
        }
        return SslContext.newClientContextInternal(this.provider, this.sslContextProvider, this.trustCertCollection, this.trustManagerFactory, this.keyCertChain, this.key, this.keyPassword, this.keyManagerFactory, this.ciphers, this.cipherFilter, this.apn, this.protocols, this.sessionCacheSize, this.sessionTimeout, this.enableOcsp);
    }
}


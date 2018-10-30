/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.X509TrustManagerWrapper;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

public abstract class SimpleTrustManagerFactory
extends TrustManagerFactory {
    private static final Provider PROVIDER = new Provider("", 0.0, ""){
        private static final long serialVersionUID = -2680540247105807895L;
    };
    private static final FastThreadLocal<SimpleTrustManagerFactorySpi> CURRENT_SPI = new FastThreadLocal<SimpleTrustManagerFactorySpi>(){

        @Override
        protected SimpleTrustManagerFactorySpi initialValue() {
            return new SimpleTrustManagerFactorySpi();
        }
    };

    protected SimpleTrustManagerFactory() {
        this("");
    }

    protected SimpleTrustManagerFactory(String name) {
        super(CURRENT_SPI.get(), PROVIDER, name);
        CURRENT_SPI.get().init(this);
        CURRENT_SPI.remove();
        if (name == null) {
            throw new NullPointerException("name");
        }
    }

    protected abstract void engineInit(KeyStore var1) throws Exception;

    protected abstract void engineInit(ManagerFactoryParameters var1) throws Exception;

    protected abstract TrustManager[] engineGetTrustManagers();

    static final class SimpleTrustManagerFactorySpi
    extends TrustManagerFactorySpi {
        private SimpleTrustManagerFactory parent;
        private volatile TrustManager[] trustManagers;

        SimpleTrustManagerFactorySpi() {
        }

        void init(SimpleTrustManagerFactory parent) {
            this.parent = parent;
        }

        @Override
        protected void engineInit(KeyStore keyStore) throws KeyStoreException {
            try {
                this.parent.engineInit(keyStore);
            }
            catch (KeyStoreException e) {
                throw e;
            }
            catch (Exception e) {
                throw new KeyStoreException(e);
            }
        }

        @Override
        protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            try {
                this.parent.engineInit(managerFactoryParameters);
            }
            catch (InvalidAlgorithmParameterException e) {
                throw e;
            }
            catch (Exception e) {
                throw new InvalidAlgorithmParameterException(e);
            }
        }

        @Override
        protected TrustManager[] engineGetTrustManagers() {
            TrustManager[] trustManagers = this.trustManagers;
            if (trustManagers == null) {
                trustManagers = this.parent.engineGetTrustManagers();
                if (PlatformDependent.javaVersion() >= 7) {
                    for (int i = 0; i < trustManagers.length; ++i) {
                        TrustManager tm = trustManagers[i];
                        if (!(tm instanceof X509TrustManager) || tm instanceof X509ExtendedTrustManager) continue;
                        trustManagers[i] = new X509TrustManagerWrapper((X509TrustManager)tm);
                    }
                }
                this.trustManagers = trustManagers;
            }
            return (TrustManager[])trustManagers.clone();
        }
    }

}


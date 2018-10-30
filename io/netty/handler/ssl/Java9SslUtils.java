/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;

final class Java9SslUtils {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Java9SslUtils.class);
    private static final Method SET_APPLICATION_PROTOCOLS;
    private static final Method GET_APPLICATION_PROTOCOL;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
    private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;

    private Java9SslUtils() {
    }

    static boolean supportsAlpn() {
        return GET_APPLICATION_PROTOCOL != null;
    }

    static String getApplicationProtocol(SSLEngine sslEngine) {
        try {
            return (String)GET_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static String getHandshakeApplicationProtocol(SSLEngine sslEngine) {
        try {
            return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static void setApplicationProtocols(SSLEngine engine, List<String> supportedProtocols) {
        SSLParameters parameters = engine.getSSLParameters();
        String[] protocolArray = supportedProtocols.toArray(EmptyArrays.EMPTY_STRINGS);
        try {
            SET_APPLICATION_PROTOCOLS.invoke(parameters, new Object[]{protocolArray});
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        engine.setSSLParameters(parameters);
    }

    static void setHandshakeApplicationProtocolSelector(SSLEngine engine, BiFunction<SSLEngine, List<String>, String> selector) {
        try {
            SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, selector);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine engine) {
        try {
            return (BiFunction)GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, new Object[0]);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    static {
        Method getHandshakeApplicationProtocol = null;
        Method getApplicationProtocol = null;
        Method setApplicationProtocols = null;
        Method setHandshakeApplicationProtocolSelector = null;
        Method getHandshakeApplicationProtocolSelector = null;
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            SSLEngine engine = context.createSSLEngine();
            getHandshakeApplicationProtocol = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return SSLEngine.class.getMethod("getHandshakeApplicationProtocol", new Class[0]);
                }
            });
            getHandshakeApplicationProtocol.invoke(engine, new Object[0]);
            getApplicationProtocol = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return SSLEngine.class.getMethod("getApplicationProtocol", new Class[0]);
                }
            });
            getApplicationProtocol.invoke(engine, new Object[0]);
            setApplicationProtocols = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
                }
            });
            setApplicationProtocols.invoke(engine.getSSLParameters(), new Object[]{EmptyArrays.EMPTY_STRINGS});
            setHandshakeApplicationProtocolSelector = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return SSLEngine.class.getMethod("setHandshakeApplicationProtocolSelector", BiFunction.class);
                }
            });
            setHandshakeApplicationProtocolSelector.invoke(engine, new BiFunction<SSLEngine, List<String>, String>(){

                @Override
                public String apply(SSLEngine sslEngine, List<String> strings) {
                    return null;
                }
            });
            getHandshakeApplicationProtocolSelector = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    return SSLEngine.class.getMethod("getHandshakeApplicationProtocolSelector", new Class[0]);
                }
            });
            getHandshakeApplicationProtocolSelector.invoke(engine, new Object[0]);
        }
        catch (Throwable t) {
            logger.error("Unable to initialize Java9SslUtils, but the detected javaVersion was: {}", (Object)PlatformDependent.javaVersion(), (Object)t);
            getHandshakeApplicationProtocol = null;
            getApplicationProtocol = null;
            setApplicationProtocols = null;
            setHandshakeApplicationProtocolSelector = null;
            getHandshakeApplicationProtocolSelector = null;
        }
        GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
        GET_APPLICATION_PROTOCOL = getApplicationProtocol;
        SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
        SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
        GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
    }

}


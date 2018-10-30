/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.Buffer
 *  io.netty.internal.tcnative.Library
 *  io.netty.internal.tcnative.SSL
 *  io.netty.internal.tcnative.SSLContext
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.handler.ssl.CipherSuiteConverter;
import io.netty.handler.ssl.OpenSslEngine;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.SslUtils;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.Library;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class OpenSsl {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);
    private static final Throwable UNAVAILABILITY_CAUSE;
    static final List<String> DEFAULT_CIPHERS;
    static final Set<String> AVAILABLE_CIPHER_SUITES;
    private static final Set<String> AVAILABLE_OPENSSL_CIPHER_SUITES;
    private static final Set<String> AVAILABLE_JAVA_CIPHER_SUITES;
    private static final boolean SUPPORTS_KEYMANAGER_FACTORY;
    private static final boolean SUPPORTS_HOSTNAME_VALIDATION;
    private static final boolean USE_KEYMANAGER_FACTORY;
    private static final boolean SUPPORTS_OCSP;
    static final Set<String> SUPPORTED_PROTOCOLS_SET;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean doesSupportOcsp() {
        boolean supportsOcsp;
        block6 : {
            supportsOcsp = false;
            if ((long)OpenSsl.version() >= 268443648L) {
                long sslCtx = -1L;
                try {
                    sslCtx = SSLContext.make((int)16, (int)1);
                    SSLContext.enableOcsp((long)sslCtx, (boolean)false);
                    supportsOcsp = true;
                    break block6;
                }
                catch (Exception exception) {}
                break block6;
                finally {
                    if (sslCtx != -1L) {
                        SSLContext.free((long)sslCtx);
                    }
                }
            }
        }
        return supportsOcsp;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean doesSupportProtocol(int protocol) {
        long sslCtx = -1L;
        try {
            sslCtx = SSLContext.make((int)protocol, (int)2);
            boolean bl = true;
            return bl;
        }
        catch (Exception ignore) {
            boolean bl = false;
            return bl;
        }
        finally {
            if (sslCtx != -1L) {
                SSLContext.free((long)sslCtx);
            }
        }
    }

    public static boolean isAvailable() {
        return UNAVAILABILITY_CAUSE == null;
    }

    public static boolean isAlpnSupported() {
        return (long)OpenSsl.version() >= 268443648L;
    }

    public static boolean isOcspSupported() {
        return SUPPORTS_OCSP;
    }

    public static int version() {
        return OpenSsl.isAvailable() ? SSL.version() : -1;
    }

    public static String versionString() {
        return OpenSsl.isAvailable() ? SSL.versionString() : null;
    }

    public static void ensureAvailability() {
        if (UNAVAILABILITY_CAUSE != null) {
            throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(UNAVAILABILITY_CAUSE);
        }
    }

    public static Throwable unavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    @Deprecated
    public static Set<String> availableCipherSuites() {
        return OpenSsl.availableOpenSslCipherSuites();
    }

    public static Set<String> availableOpenSslCipherSuites() {
        return AVAILABLE_OPENSSL_CIPHER_SUITES;
    }

    public static Set<String> availableJavaCipherSuites() {
        return AVAILABLE_JAVA_CIPHER_SUITES;
    }

    public static boolean isCipherSuiteAvailable(String cipherSuite) {
        String converted = CipherSuiteConverter.toOpenSsl(cipherSuite);
        if (converted != null) {
            cipherSuite = converted;
        }
        return AVAILABLE_OPENSSL_CIPHER_SUITES.contains(cipherSuite);
    }

    public static boolean supportsKeyManagerFactory() {
        return SUPPORTS_KEYMANAGER_FACTORY;
    }

    public static boolean supportsHostnameValidation() {
        return SUPPORTS_HOSTNAME_VALIDATION;
    }

    static boolean useKeyManagerFactory() {
        return USE_KEYMANAGER_FACTORY;
    }

    static long memoryAddress(ByteBuf buf) {
        assert (buf.isDirect());
        return buf.hasMemoryAddress() ? buf.memoryAddress() : Buffer.address((ByteBuffer)buf.nioBuffer());
    }

    private OpenSsl() {
    }

    private static void loadTcNative() throws Exception {
        String os = PlatformDependent.normalizedOs();
        String arch = PlatformDependent.normalizedArch();
        LinkedHashSet<String> libNames = new LinkedHashSet<String>(4);
        String staticLibName = "netty_tcnative";
        libNames.add(staticLibName + "_" + os + '_' + arch);
        if ("linux".equalsIgnoreCase(os)) {
            libNames.add(staticLibName + "_" + os + '_' + arch + "_fedora");
        }
        libNames.add(staticLibName + "_" + arch);
        libNames.add(staticLibName);
        NativeLibraryLoader.loadFirstAvailable(SSL.class.getClassLoader(), libNames.toArray(new String[libNames.size()]));
    }

    private static boolean initializeTcNative() throws Exception {
        return Library.initialize();
    }

    static void releaseIfNeeded(ReferenceCounted counted) {
        if (counted.refCnt() > 0) {
            ReferenceCountUtil.safeRelease(counted);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        Throwable cause = null;
        try {
            Class.forName("io.netty.internal.tcnative.SSL", false, OpenSsl.class.getClassLoader());
        }
        catch (ClassNotFoundException t) {
            cause = t;
            logger.debug("netty-tcnative not in the classpath; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.");
        }
        if (cause == null) {
            try {
                OpenSsl.loadTcNative();
            }
            catch (Throwable t) {
                cause = t;
                logger.debug("Failed to load netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable, unless the application has already loaded the symbols by some other means. See http://netty.io/wiki/forked-tomcat-native.html for more information.", t);
            }
            try {
                OpenSsl.initializeTcNative();
                cause = null;
            }
            catch (Throwable t) {
                if (cause == null) {
                    cause = t;
                }
                logger.debug("Failed to initialize netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable. See http://netty.io/wiki/forked-tomcat-native.html for more information.", t);
            }
        }
        UNAVAILABILITY_CAUSE = cause;
        if (cause == null) {
            ArrayList<String> defaultCiphers;
            boolean useKeyManagerFactory;
            boolean supportsKeyManagerFactory;
            boolean supportsHostNameValidation;
            LinkedHashSet<String> availableOpenSslCipherSuites;
            logger.debug("netty-tcnative using native library: {}", (Object)SSL.versionString());
            defaultCiphers = new ArrayList<String>();
            availableOpenSslCipherSuites = new LinkedHashSet<String>(128);
            supportsKeyManagerFactory = false;
            useKeyManagerFactory = false;
            supportsHostNameValidation = false;
            try {
                long sslCtx = SSLContext.make((int)31, (int)1);
                long certBio = 0L;
                SelfSignedCertificate cert = null;
                try {
                    SSLContext.setCipherSuite((long)sslCtx, (String)"ALL");
                    long ssl = SSL.newSSL((long)sslCtx, (boolean)true);
                    try {
                        for (String c : SSL.getCiphers((long)ssl)) {
                            if (c == null || c.isEmpty() || availableOpenSslCipherSuites.contains(c)) continue;
                            availableOpenSslCipherSuites.add(c);
                        }
                        try {
                            SSL.setHostNameValidation((long)ssl, (int)0, (String)"netty.io");
                            supportsHostNameValidation = true;
                        }
                        catch (Throwable ignore) {
                            logger.debug("Hostname Verification not supported.");
                        }
                        try {
                            cert = new SelfSignedCertificate();
                            certBio = ReferenceCountedOpenSslContext.toBIO(cert.cert());
                            SSL.setCertificateChainBio((long)ssl, (long)certBio, (boolean)false);
                            supportsKeyManagerFactory = true;
                            try {
                                useKeyManagerFactory = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

                                    @Override
                                    public Boolean run() {
                                        return SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.useKeyManagerFactory", true);
                                    }
                                });
                            }
                            catch (Throwable ignore) {
                                logger.debug("Failed to get useKeyManagerFactory system property.");
                            }
                        }
                        catch (Throwable ignore) {
                            logger.debug("KeyManagerFactory not supported.");
                        }
                    }
                    finally {
                        SSL.freeSSL((long)ssl);
                        if (certBio != 0L) {
                            SSL.freeBIO((long)certBio);
                        }
                        if (cert != null) {
                            cert.delete();
                        }
                    }
                }
                finally {
                    SSLContext.free((long)sslCtx);
                }
            }
            catch (Exception e) {
                logger.warn("Failed to get the list of available OpenSSL cipher suites.", e);
            }
            AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.unmodifiableSet(availableOpenSslCipherSuites);
            LinkedHashSet<String> availableJavaCipherSuites = new LinkedHashSet<String>(AVAILABLE_OPENSSL_CIPHER_SUITES.size() * 2);
            for (String cipher : AVAILABLE_OPENSSL_CIPHER_SUITES) {
                availableJavaCipherSuites.add(CipherSuiteConverter.toJava(cipher, "TLS"));
                availableJavaCipherSuites.add(CipherSuiteConverter.toJava(cipher, "SSL"));
            }
            SslUtils.addIfSupported(availableJavaCipherSuites, defaultCiphers, SslUtils.DEFAULT_CIPHER_SUITES);
            SslUtils.useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, availableJavaCipherSuites);
            DEFAULT_CIPHERS = Collections.unmodifiableList(defaultCiphers);
            AVAILABLE_JAVA_CIPHER_SUITES = Collections.unmodifiableSet(availableJavaCipherSuites);
            LinkedHashSet<String> availableCipherSuites = new LinkedHashSet<String>(AVAILABLE_OPENSSL_CIPHER_SUITES.size() + AVAILABLE_JAVA_CIPHER_SUITES.size());
            availableCipherSuites.addAll(AVAILABLE_OPENSSL_CIPHER_SUITES);
            availableCipherSuites.addAll(AVAILABLE_JAVA_CIPHER_SUITES);
            AVAILABLE_CIPHER_SUITES = availableCipherSuites;
            SUPPORTS_KEYMANAGER_FACTORY = supportsKeyManagerFactory;
            SUPPORTS_HOSTNAME_VALIDATION = supportsHostNameValidation;
            USE_KEYMANAGER_FACTORY = useKeyManagerFactory;
            LinkedHashSet<String> protocols = new LinkedHashSet<String>(6);
            protocols.add("SSLv2Hello");
            if (OpenSsl.doesSupportProtocol(1)) {
                protocols.add("SSLv2");
            }
            if (OpenSsl.doesSupportProtocol(2)) {
                protocols.add("SSLv3");
            }
            if (OpenSsl.doesSupportProtocol(4)) {
                protocols.add("TLSv1");
            }
            if (OpenSsl.doesSupportProtocol(8)) {
                protocols.add("TLSv1.1");
            }
            if (OpenSsl.doesSupportProtocol(16)) {
                protocols.add("TLSv1.2");
            }
            SUPPORTED_PROTOCOLS_SET = Collections.unmodifiableSet(protocols);
            SUPPORTS_OCSP = OpenSsl.doesSupportOcsp();
            if (logger.isDebugEnabled()) {
                logger.debug("Supported protocols (OpenSSL): {} ", (Object)Arrays.asList(SUPPORTED_PROTOCOLS_SET));
                logger.debug("Default cipher suites (OpenSSL): {}", (Object)DEFAULT_CIPHERS);
            }
        } else {
            DEFAULT_CIPHERS = Collections.emptyList();
            AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.emptySet();
            AVAILABLE_JAVA_CIPHER_SUITES = Collections.emptySet();
            AVAILABLE_CIPHER_SUITES = Collections.emptySet();
            SUPPORTS_KEYMANAGER_FACTORY = false;
            SUPPORTS_HOSTNAME_VALIDATION = false;
            USE_KEYMANAGER_FACTORY = false;
            SUPPORTED_PROTOCOLS_SET = Collections.emptySet();
            SUPPORTS_OCSP = false;
        }
    }

}


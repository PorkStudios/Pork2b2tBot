/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.ssl.util.BouncyCastleSelfSignedCertGenerator;
import io.netty.handler.ssl.util.OpenJdkSelfSignedCertGenerator;
import io.netty.handler.ssl.util.ThreadLocalInsecureRandom;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public final class SelfSignedCertificate {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);
    private static final Date DEFAULT_NOT_BEFORE = new Date(SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotBefore", System.currentTimeMillis() - 31536000000L));
    private static final Date DEFAULT_NOT_AFTER = new Date(SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotAfter", 253402300799000L));
    private final File certificate;
    private final File privateKey;
    private final X509Certificate cert;
    private final PrivateKey key;

    public SelfSignedCertificate() throws CertificateException {
        this(DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
    }

    public SelfSignedCertificate(Date notBefore, Date notAfter) throws CertificateException {
        this("example.com", notBefore, notAfter);
    }

    public SelfSignedCertificate(String fqdn) throws CertificateException {
        this(fqdn, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
    }

    public SelfSignedCertificate(String fqdn, Date notBefore, Date notAfter) throws CertificateException {
        this(fqdn, ThreadLocalInsecureRandom.current(), 1024, notBefore, notAfter);
    }

    public SelfSignedCertificate(String fqdn, SecureRandom random, int bits) throws CertificateException {
        this(fqdn, random, bits, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
    }

    public SelfSignedCertificate(String fqdn, SecureRandom random, int bits, Date notBefore, Date notAfter) throws CertificateException {
        String[] paths;
        KeyPair keypair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(bits, random);
            keypair = keyGen.generateKeyPair();
        }
        catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        }
        try {
            paths = OpenJdkSelfSignedCertGenerator.generate(fqdn, keypair, random, notBefore, notAfter);
        }
        catch (Throwable t) {
            logger.debug("Failed to generate a self-signed X.509 certificate using sun.security.x509:", t);
            try {
                paths = BouncyCastleSelfSignedCertGenerator.generate(fqdn, keypair, random, notBefore, notAfter);
            }
            catch (Throwable t2) {
                logger.debug("Failed to generate a self-signed X.509 certificate using Bouncy Castle:", t2);
                throw new CertificateException("No provider succeeded to generate a self-signed certificate. See debug log for the root cause.", t2);
            }
        }
        this.certificate = new File(paths[0]);
        this.privateKey = new File(paths[1]);
        this.key = keypair.getPrivate();
        FileInputStream certificateInput = null;
        try {
            certificateInput = new FileInputStream(this.certificate);
            this.cert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(certificateInput);
        }
        catch (Exception e) {
            throw new CertificateEncodingException(e);
        }
        finally {
            if (certificateInput != null) {
                try {
                    certificateInput.close();
                }
                catch (IOException e) {
                    logger.warn("Failed to close a file: " + this.certificate, e);
                }
            }
        }
    }

    public File certificate() {
        return this.certificate;
    }

    public File privateKey() {
        return this.privateKey;
    }

    public X509Certificate cert() {
        return this.cert;
    }

    public PrivateKey key() {
        return this.key;
    }

    public void delete() {
        SelfSignedCertificate.safeDelete(this.certificate);
        SelfSignedCertificate.safeDelete(this.privateKey);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String[] newSelfSignedCertificate(String fqdn, PrivateKey key, X509Certificate cert) throws IOException, CertificateEncodingException {
        File keyFile;
        File certFile;
        String certText;
        ByteBuf wrappedBuf;
        ByteBuf encodedBuf;
        String keyText;
        wrappedBuf = Unpooled.wrappedBuffer(key.getEncoded());
        try {
            encodedBuf = Base64.encode(wrappedBuf, true);
            try {
                keyText = "-----BEGIN PRIVATE KEY-----\n" + encodedBuf.toString(CharsetUtil.US_ASCII) + "\n-----END PRIVATE KEY-----\n";
            }
            finally {
                encodedBuf.release();
            }
        }
        finally {
            wrappedBuf.release();
        }
        keyFile = File.createTempFile("keyutil_" + fqdn + '_', ".key");
        keyFile.deleteOnExit();
        FileOutputStream keyOut = new FileOutputStream(keyFile);
        try {
            keyOut.write(keyText.getBytes(CharsetUtil.US_ASCII));
            keyOut.close();
            keyOut = null;
        }
        finally {
            if (keyOut != null) {
                SelfSignedCertificate.safeClose(keyFile, keyOut);
                SelfSignedCertificate.safeDelete(keyFile);
            }
        }
        wrappedBuf = Unpooled.wrappedBuffer(cert.getEncoded());
        try {
            encodedBuf = Base64.encode(wrappedBuf, true);
            try {
                certText = "-----BEGIN CERTIFICATE-----\n" + encodedBuf.toString(CharsetUtil.US_ASCII) + "\n-----END CERTIFICATE-----\n";
            }
            finally {
                encodedBuf.release();
            }
        }
        finally {
            wrappedBuf.release();
        }
        certFile = File.createTempFile("keyutil_" + fqdn + '_', ".crt");
        certFile.deleteOnExit();
        FileOutputStream certOut = new FileOutputStream(certFile);
        try {
            certOut.write(certText.getBytes(CharsetUtil.US_ASCII));
            certOut.close();
            certOut = null;
        }
        finally {
            if (certOut != null) {
                SelfSignedCertificate.safeClose(certFile, certOut);
                SelfSignedCertificate.safeDelete(certFile);
                SelfSignedCertificate.safeDelete(keyFile);
            }
        }
        return new String[]{certFile.getPath(), keyFile.getPath()};
    }

    private static void safeDelete(File certFile) {
        if (!certFile.delete()) {
            logger.warn("Failed to delete a file: " + certFile);
        }
    }

    private static void safeClose(File keyFile, OutputStream keyOut) {
        try {
            keyOut.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close a file: " + keyFile, e);
        }
    }
}


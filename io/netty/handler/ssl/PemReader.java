/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PemReader {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PemReader.class);
    private static final Pattern CERT_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
    private static final Pattern KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);

    static ByteBuf[] readCertificates(File file) throws CertificateException {
        ByteBuf[] arrbyteBuf;
        FileInputStream in = new FileInputStream(file);
        try {
            arrbyteBuf = PemReader.readCertificates(in);
        }
        catch (Throwable throwable) {
            try {
                PemReader.safeClose(in);
                throw throwable;
            }
            catch (FileNotFoundException e) {
                throw new CertificateException("could not find certificate file: " + file);
            }
        }
        PemReader.safeClose(in);
        return arrbyteBuf;
    }

    static ByteBuf[] readCertificates(InputStream in) throws CertificateException {
        String content;
        try {
            content = PemReader.readContent(in);
        }
        catch (IOException e) {
            throw new CertificateException("failed to read certificate input stream", e);
        }
        ArrayList<ByteBuf> certs = new ArrayList<ByteBuf>();
        Matcher m = CERT_PATTERN.matcher(content);
        int start = 0;
        while (m.find(start)) {
            ByteBuf base64 = Unpooled.copiedBuffer(m.group(1), CharsetUtil.US_ASCII);
            ByteBuf der = Base64.decode(base64);
            base64.release();
            certs.add(der);
            start = m.end();
        }
        if (certs.isEmpty()) {
            throw new CertificateException("found no certificates in input stream");
        }
        return certs.toArray(new ByteBuf[certs.size()]);
    }

    static ByteBuf readPrivateKey(File file) throws KeyException {
        ByteBuf byteBuf;
        FileInputStream in = new FileInputStream(file);
        try {
            byteBuf = PemReader.readPrivateKey(in);
        }
        catch (Throwable throwable) {
            try {
                PemReader.safeClose(in);
                throw throwable;
            }
            catch (FileNotFoundException e) {
                throw new KeyException("could not find key file: " + file);
            }
        }
        PemReader.safeClose(in);
        return byteBuf;
    }

    static ByteBuf readPrivateKey(InputStream in) throws KeyException {
        String content;
        try {
            content = PemReader.readContent(in);
        }
        catch (IOException e) {
            throw new KeyException("failed to read key input stream", e);
        }
        Matcher m = KEY_PATTERN.matcher(content);
        if (!m.find()) {
            throw new KeyException("could not find a PKCS #8 private key in input stream (see http://netty.io/wiki/sslcontextbuilder-and-private-key.html for more information)");
        }
        ByteBuf base64 = Unpooled.copiedBuffer(m.group(1), CharsetUtil.US_ASCII);
        ByteBuf der = Base64.decode(base64);
        base64.release();
        return der;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String readContent(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int ret22;
            byte[] buf = new byte[8192];
            while ((ret22 = in.read(buf)) >= 0) {
                out.write(buf, 0, ret22);
            }
            String ret22 = out.toString(CharsetUtil.US_ASCII.name());
            return ret22;
        }
        finally {
            PemReader.safeClose(out);
        }
    }

    private static void safeClose(InputStream in) {
        try {
            in.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close a stream.", e);
        }
    }

    private static void safeClose(OutputStream out) {
        try {
            out.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close a stream.", e);
        }
    }

    private PemReader() {
    }
}


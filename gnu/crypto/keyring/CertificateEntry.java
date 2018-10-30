/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.MeteredInputStream;
import gnu.crypto.keyring.PrimitiveEntry;
import gnu.crypto.keyring.Properties;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;

public final class CertificateEntry
extends PrimitiveEntry {
    public static final int TYPE = 5;
    private Certificate certificate;

    public static final CertificateEntry decode(DataInputStream in) throws IOException {
        CertificateEntry entry = new CertificateEntry();
        entry.properties = new Properties();
        entry.properties.decode(in);
        entry.makeCreationDate();
        String type = entry.properties.get("type");
        if (type == null) {
            throw new MalformedKeyringException("no certificate type");
        }
        int len = in.readInt();
        MeteredInputStream in2 = new MeteredInputStream(in, len);
        try {
            CertificateFactory fact = CertificateFactory.getInstance(type);
            entry.certificate = fact.generateCertificate(in2);
        }
        catch (CertificateException ce) {
            throw new MalformedKeyringException(ce.toString());
        }
        if (!in2.limitReached()) {
            throw new MalformedKeyringException("extra data at end of payload");
        }
        return entry;
    }

    public final Certificate getCertificate() {
        return this.certificate;
    }

    protected final void encodePayload() throws IOException {
        try {
            this.payload = this.certificate.getEncoded();
        }
        catch (CertificateEncodingException cee) {
            throw new IOException(cee.toString());
        }
    }

    public CertificateEntry(Certificate certificate, Date creationDate, Properties properties) {
        super(5, creationDate, properties);
        if (certificate == null) {
            throw new IllegalArgumentException("no certificate");
        }
        this.certificate = certificate;
        this.properties.put("type", certificate.getType());
    }

    private CertificateEntry() {
    }
}


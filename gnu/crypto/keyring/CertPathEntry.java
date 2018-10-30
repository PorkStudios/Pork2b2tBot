/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.MeteredInputStream;
import gnu.crypto.keyring.PrimitiveEntry;
import gnu.crypto.keyring.Properties;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Date;

public final class CertPathEntry
extends PrimitiveEntry {
    public static final int TYPE = 8;
    private Certificate[] path;

    public static final CertPathEntry decode(DataInputStream in) throws IOException {
        CertPathEntry entry = new CertPathEntry();
        entry.properties = new Properties();
        entry.properties.decode(in);
        entry.makeCreationDate();
        int len = in.readInt();
        MeteredInputStream in2 = new MeteredInputStream(in, len);
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            entry.path = fact.generateCertificates(in2).toArray(new Certificate[0]);
        }
        catch (CertificateException ce) {
            throw new MalformedKeyringException(ce.toString());
        }
        return entry;
    }

    public final Certificate[] getCertPath() {
        return this.path;
    }

    protected final void encodePayload() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
        Object enc = null;
        try {
            int i = 0;
            while (i < this.path.length) {
                bout.write(this.path[i].getEncoded());
                ++i;
            }
        }
        catch (CertificateEncodingException cee) {
            throw new IOException(cee.toString());
        }
        this.payload = bout.toByteArray();
    }

    public CertPathEntry(Certificate[] path, Date creationDate, Properties properties) {
        super(8, creationDate, properties);
        if (path == null || path.length == 0) {
            throw new IllegalArgumentException("no certificate path");
        }
        this.path = (Certificate[])path.clone();
    }

    private CertPathEntry() {
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.ssl.PemEncoded;
import io.netty.handler.ssl.PemValue;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public final class PemX509Certificate
extends X509Certificate
implements PemEncoded {
    private static final byte[] BEGIN_CERT = "-----BEGIN CERTIFICATE-----\n".getBytes(CharsetUtil.US_ASCII);
    private static final byte[] END_CERT = "\n-----END CERTIFICATE-----\n".getBytes(CharsetUtil.US_ASCII);
    private final ByteBuf content;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static /* varargs */ PemEncoded toPEM(ByteBufAllocator allocator, boolean useDirect, X509Certificate ... chain) throws CertificateEncodingException {
        X509Certificate first;
        if (chain == null || chain.length == 0) {
            throw new IllegalArgumentException("X.509 certificate chain can't be null or empty");
        }
        if (chain.length == 1 && (first = chain[0]) instanceof PemEncoded) {
            return ((PemEncoded)((Object)first)).retain();
        }
        boolean success = false;
        ReferenceCounted pem = null;
        try {
            for (X509Certificate cert : chain) {
                if (cert == null) {
                    throw new IllegalArgumentException("Null element in chain: " + Arrays.toString(chain));
                }
                pem = cert instanceof PemEncoded ? PemX509Certificate.append(allocator, useDirect, (PemEncoded)((Object)cert), chain.length, (ByteBuf)pem) : PemX509Certificate.append(allocator, useDirect, cert, chain.length, (ByteBuf)pem);
            }
            PemValue value = new PemValue((ByteBuf)pem, false);
            success = true;
            PemValue pemValue = value;
            return pemValue;
        }
        finally {
            if (!success && pem != null) {
                pem.release();
            }
        }
    }

    private static ByteBuf append(ByteBufAllocator allocator, boolean useDirect, PemEncoded encoded, int count, ByteBuf pem) {
        ByteBuf content = encoded.content();
        if (pem == null) {
            pem = PemX509Certificate.newBuffer(allocator, useDirect, content.readableBytes() * count);
        }
        pem.writeBytes(content.slice());
        return pem;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static ByteBuf append(ByteBufAllocator allocator, boolean useDirect, X509Certificate cert, int count, ByteBuf pem) throws CertificateEncodingException {
        ByteBuf encoded = Unpooled.wrappedBuffer(cert.getEncoded());
        try {
            ByteBuf base64 = SslUtils.toBase64(allocator, encoded);
            try {
                if (pem == null) {
                    pem = PemX509Certificate.newBuffer(allocator, useDirect, (BEGIN_CERT.length + base64.readableBytes() + END_CERT.length) * count);
                }
                pem.writeBytes(BEGIN_CERT);
                pem.writeBytes(base64);
                pem.writeBytes(END_CERT);
            }
            finally {
                base64.release();
            }
        }
        finally {
            encoded.release();
        }
        return pem;
    }

    private static ByteBuf newBuffer(ByteBufAllocator allocator, boolean useDirect, int initialCapacity) {
        return useDirect ? allocator.directBuffer(initialCapacity) : allocator.buffer(initialCapacity);
    }

    public static PemX509Certificate valueOf(byte[] key) {
        return PemX509Certificate.valueOf(Unpooled.wrappedBuffer(key));
    }

    public static PemX509Certificate valueOf(ByteBuf key) {
        return new PemX509Certificate(key);
    }

    private PemX509Certificate(ByteBuf content) {
        this.content = ObjectUtil.checkNotNull(content, "content");
    }

    @Override
    public boolean isSensitive() {
        return false;
    }

    @Override
    public int refCnt() {
        return this.content.refCnt();
    }

    @Override
    public ByteBuf content() {
        int count = this.refCnt();
        if (count <= 0) {
            throw new IllegalReferenceCountException(count);
        }
        return this.content;
    }

    @Override
    public PemX509Certificate copy() {
        return this.replace(this.content.copy());
    }

    @Override
    public PemX509Certificate duplicate() {
        return this.replace(this.content.duplicate());
    }

    @Override
    public PemX509Certificate retainedDuplicate() {
        return this.replace(this.content.retainedDuplicate());
    }

    @Override
    public PemX509Certificate replace(ByteBuf content) {
        return new PemX509Certificate(content);
    }

    @Override
    public PemX509Certificate retain() {
        this.content.retain();
        return this;
    }

    @Override
    public PemX509Certificate retain(int increment) {
        this.content.retain(increment);
        return this;
    }

    @Override
    public PemX509Certificate touch() {
        this.content.touch();
        return this;
    }

    @Override
    public PemX509Certificate touch(Object hint) {
        this.content.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.content.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.content.release(decrement);
    }

    @Override
    public byte[] getEncoded() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkValidity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkValidity(Date date) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger getSerialNumber() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Principal getIssuerDN() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Principal getSubjectDN() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getNotBefore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getNotAfter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getTBSCertificate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getSignature() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSigAlgName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSigAlgOID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getSigAlgParams() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean[] getKeyUsage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBasicConstraints() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void verify(PublicKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void verify(PublicKey key, String sigProvider) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PublicKey getPublicKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PemX509Certificate)) {
            return false;
        }
        PemX509Certificate other = (PemX509Certificate)o;
        return this.content.equals(other.content);
    }

    @Override
    public int hashCode() {
        return this.content.hashCode();
    }

    @Override
    public String toString() {
        return this.content.toString(CharsetUtil.UTF_8);
    }
}


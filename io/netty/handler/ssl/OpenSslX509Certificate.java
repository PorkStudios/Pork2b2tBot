/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.SslContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

final class OpenSslX509Certificate
extends X509Certificate {
    private final byte[] bytes;
    private X509Certificate wrapped;

    public OpenSslX509Certificate(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.unwrap().checkValidity();
    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        this.unwrap().checkValidity(date);
    }

    @Override
    public int getVersion() {
        return this.unwrap().getVersion();
    }

    @Override
    public BigInteger getSerialNumber() {
        return this.unwrap().getSerialNumber();
    }

    @Override
    public Principal getIssuerDN() {
        return this.unwrap().getIssuerDN();
    }

    @Override
    public Principal getSubjectDN() {
        return this.unwrap().getSubjectDN();
    }

    @Override
    public Date getNotBefore() {
        return this.unwrap().getNotBefore();
    }

    @Override
    public Date getNotAfter() {
        return this.unwrap().getNotAfter();
    }

    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        return this.unwrap().getTBSCertificate();
    }

    @Override
    public byte[] getSignature() {
        return this.unwrap().getSignature();
    }

    @Override
    public String getSigAlgName() {
        return this.unwrap().getSigAlgName();
    }

    @Override
    public String getSigAlgOID() {
        return this.unwrap().getSigAlgOID();
    }

    @Override
    public byte[] getSigAlgParams() {
        return this.unwrap().getSigAlgParams();
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        return this.unwrap().getIssuerUniqueID();
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        return this.unwrap().getSubjectUniqueID();
    }

    @Override
    public boolean[] getKeyUsage() {
        return this.unwrap().getKeyUsage();
    }

    @Override
    public int getBasicConstraints() {
        return this.unwrap().getBasicConstraints();
    }

    @Override
    public byte[] getEncoded() {
        return (byte[])this.bytes.clone();
    }

    @Override
    public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.unwrap().verify(key);
    }

    @Override
    public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.unwrap().verify(key, sigProvider);
    }

    @Override
    public String toString() {
        return this.unwrap().toString();
    }

    @Override
    public PublicKey getPublicKey() {
        return this.unwrap().getPublicKey();
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return this.unwrap().hasUnsupportedCriticalExtension();
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        return this.unwrap().getCriticalExtensionOIDs();
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        return this.unwrap().getNonCriticalExtensionOIDs();
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        return this.unwrap().getExtensionValue(oid);
    }

    private X509Certificate unwrap() {
        X509Certificate wrapped = this.wrapped;
        if (wrapped == null) {
            try {
                wrapped = this.wrapped = (X509Certificate)SslContext.X509_CERT_FACTORY.generateCertificate(new ByteArrayInputStream(this.bytes));
            }
            catch (CertificateException e) {
                throw new IllegalStateException(e);
            }
        }
        return wrapped;
    }
}


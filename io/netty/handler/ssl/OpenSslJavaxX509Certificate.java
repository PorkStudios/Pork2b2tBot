/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Date;
import javax.security.cert.CertificateException;
import javax.security.cert.CertificateExpiredException;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.X509Certificate;

final class OpenSslJavaxX509Certificate
extends X509Certificate {
    private final byte[] bytes;
    private X509Certificate wrapped;

    public OpenSslJavaxX509Certificate(byte[] bytes) {
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

    private X509Certificate unwrap() {
        X509Certificate wrapped = this.wrapped;
        if (wrapped == null) {
            try {
                wrapped = this.wrapped = X509Certificate.getInstance(this.bytes);
            }
            catch (CertificateException e) {
                throw new IllegalStateException(e);
            }
        }
        return wrapped;
    }
}


/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.CertificateVerifier
 */
package io.netty.handler.ssl;

import io.netty.internal.tcnative.CertificateVerifier;
import java.security.cert.CertificateException;

public final class OpenSslCertificateException
extends CertificateException {
    private static final long serialVersionUID = 5542675253797129798L;
    private final int errorCode;

    public OpenSslCertificateException(int errorCode) {
        this((String)null, errorCode);
    }

    public OpenSslCertificateException(String msg, int errorCode) {
        super(msg);
        this.errorCode = OpenSslCertificateException.checkErrorCode(errorCode);
    }

    public OpenSslCertificateException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = OpenSslCertificateException.checkErrorCode(errorCode);
    }

    public OpenSslCertificateException(Throwable cause, int errorCode) {
        this(null, cause, errorCode);
    }

    public int errorCode() {
        return this.errorCode;
    }

    private static int checkErrorCode(int errorCode) {
        if (!CertificateVerifier.isValid((int)errorCode)) {
            throw new IllegalArgumentException("errorCode '" + errorCode + "' invalid, see https://www.openssl.org/docs/man1.0.2/apps/verify.html.");
        }
        return errorCode;
    }
}


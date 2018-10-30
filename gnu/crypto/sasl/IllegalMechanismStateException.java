/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import javax.security.sasl.AuthenticationException;

public class IllegalMechanismStateException
extends AuthenticationException {
    public IllegalMechanismStateException() {
    }

    public IllegalMechanismStateException(String detail) {
        super(detail);
    }

    public IllegalMechanismStateException(String detail, Throwable ex) {
        super(detail, ex);
    }
}


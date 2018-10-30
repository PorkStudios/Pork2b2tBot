/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import javax.security.sasl.SaslException;

public class UserAlreadyExistsException
extends SaslException {
    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(String userName) {
        super(userName);
    }
}


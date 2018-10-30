/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.IKeyring;
import java.security.cert.Certificate;

public interface IPublicKeyring
extends IKeyring {
    public boolean containsCertificate(String var1);

    public Certificate getCertificate(String var1);

    public void putCertificate(String var1, Certificate var2);
}


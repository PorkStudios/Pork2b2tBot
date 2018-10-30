/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.keyring.IKeyring;
import java.security.Key;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

public interface IPrivateKeyring
extends IKeyring {
    public boolean containsPrivateKey(String var1);

    public Key getPrivateKey(String var1, char[] var2) throws UnrecoverableKeyException;

    public void putPrivateKey(String var1, Key var2, char[] var3);

    public boolean containsPublicKey(String var1);

    public PublicKey getPublicKey(String var1);

    public void putPublicKey(String var1, PublicKey var2);

    public boolean containsCertPath(String var1);

    public Certificate[] getCertPath(String var1);

    public void putCertPath(String var1, Certificate[] var2);
}


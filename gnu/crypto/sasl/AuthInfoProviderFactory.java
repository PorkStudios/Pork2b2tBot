/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.IAuthInfoProviderFactory;
import gnu.crypto.sasl.crammd5.CramMD5AuthInfoProvider;
import gnu.crypto.sasl.plain.PlainAuthInfoProvider;
import gnu.crypto.sasl.srp.SRPAuthInfoProvider;

public class AuthInfoProviderFactory
implements IAuthInfoProviderFactory {
    public IAuthInfoProvider getInstance(String mechanism) {
        if (mechanism == null) {
            return null;
        }
        if ((mechanism = mechanism.trim().toUpperCase()).startsWith("SRP")) {
            return new SRPAuthInfoProvider();
        }
        if (mechanism.equals("CRAM-MD5")) {
            return new CramMD5AuthInfoProvider();
        }
        if (mechanism.equals("PLAIN")) {
            return new PlainAuthInfoProvider();
        }
        return null;
    }
}


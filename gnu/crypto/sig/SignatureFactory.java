/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sig;

import gnu.crypto.sig.BaseSignature;
import gnu.crypto.sig.ISignature;
import gnu.crypto.sig.dss.DSSSignature;
import gnu.crypto.sig.rsa.RSAPKCS1V1_5Signature;
import gnu.crypto.sig.rsa.RSAPSSSignature;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SignatureFactory {
    public static final ISignature getInstance(String ssa) {
        if (ssa == null) {
            return null;
        }
        ssa = ssa.trim();
        BaseSignature result = null;
        if (ssa.equalsIgnoreCase("dsa") || ssa.equals("dss")) {
            result = new DSSSignature();
        } else if (ssa.equalsIgnoreCase("rsa-pss")) {
            result = new RSAPSSSignature();
        } else if (ssa.equalsIgnoreCase("rsa-pkcs1-v1.5")) {
            result = new RSAPKCS1V1_5Signature();
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.add("dss");
        hs.add("rsa-pss");
        hs.add("rsa-pkcs1-v1.5");
        return Collections.unmodifiableSet(hs);
    }

    private SignatureFactory() {
    }
}


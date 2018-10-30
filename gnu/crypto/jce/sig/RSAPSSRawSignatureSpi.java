/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.sig;

import gnu.crypto.jce.sig.SignatureAdapter;
import gnu.crypto.sig.ISignatureCodec;
import gnu.crypto.sig.rsa.RSAPSSSignatureRawCodec;

public class RSAPSSRawSignatureSpi
extends SignatureAdapter {
    public RSAPSSRawSignatureSpi() {
        super("rsa-pss", (ISignatureCodec)new RSAPSSSignatureRawCodec());
    }
}


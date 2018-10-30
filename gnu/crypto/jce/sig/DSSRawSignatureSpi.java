/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.sig;

import gnu.crypto.jce.sig.SignatureAdapter;
import gnu.crypto.sig.ISignatureCodec;
import gnu.crypto.sig.dss.DSSSignatureRawCodec;

public class DSSRawSignatureSpi
extends SignatureAdapter {
    public DSSRawSignatureSpi() {
        super("dss", (ISignatureCodec)new DSSSignatureRawCodec());
    }
}


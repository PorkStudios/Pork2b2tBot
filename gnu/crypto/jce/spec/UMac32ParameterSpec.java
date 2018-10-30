/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.spec;

import java.security.spec.AlgorithmParameterSpec;

public class UMac32ParameterSpec
implements AlgorithmParameterSpec {
    protected byte[] nonce;

    public byte[] getNonce() {
        return this.nonce;
    }

    public UMac32ParameterSpec(byte[] nonce) {
        this.nonce = nonce;
    }
}


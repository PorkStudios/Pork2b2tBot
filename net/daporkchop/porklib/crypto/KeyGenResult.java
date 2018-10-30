/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyGenResult {
    public final PublicKey publicKey;
    public final PrivateKey privateKey;

    KeyGenResult(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}


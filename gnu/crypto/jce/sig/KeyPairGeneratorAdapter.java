/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.sig;

import gnu.crypto.key.IKeyPairGenerator;
import gnu.crypto.key.KeyPairGeneratorFactory;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

abstract class KeyPairGeneratorAdapter
extends KeyPairGeneratorSpi {
    protected IKeyPairGenerator adaptee;

    public abstract void initialize(int var1, SecureRandom var2);

    public abstract void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException;

    public KeyPair generateKeyPair() {
        return this.adaptee.generate();
    }

    protected KeyPairGeneratorAdapter(String kpgName) {
        this.adaptee = KeyPairGeneratorFactory.getInstance(kpgName);
    }
}


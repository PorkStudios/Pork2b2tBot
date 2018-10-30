/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.cipher;

import gnu.crypto.jce.cipher.CipherAdapter;
import gnu.crypto.jce.spec.BlockCipherParameterSpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public final class AESSpi
extends CipherAdapter {
    static /* synthetic */ Class class$gnu$crypto$jce$spec$BlockCipherParameterSpec;

    protected final void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params instanceof BlockCipherParameterSpec && ((BlockCipherParameterSpec)params).getBlockSize() != 16) {
            throw new InvalidAlgorithmParameterException("AES block size must be 16 bytes");
        }
        super.engineInit(opmode, key, params, random);
    }

    protected final void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec spec;
        spec = null;
        try {
            if (params != null) {
                Class class_ = class$gnu$crypto$jce$spec$BlockCipherParameterSpec;
                if (class_ == null) {
                    class_ = class$gnu$crypto$jce$spec$BlockCipherParameterSpec = AESSpi.class$("[Lgnu.crypto.jce.spec.BlockCipherParameterSpec;", false);
                }
                spec = (AlgorithmParameterSpec)params.getParameterSpec(class_);
            }
        }
        catch (InvalidParameterSpecException invalidParameterSpecException) {}
        this.engineInit(opmode, key, spec, random);
    }

    static /* synthetic */ Class class$(String string, boolean bl) {
        try {
            Class<?> class_ = Class.forName(string);
            if (!bl) {
                class_ = class_.getComponentType();
            }
            return class_;
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError().initCause(classNotFoundException);
        }
    }

    public AESSpi() {
        super("aes", 16);
    }
}


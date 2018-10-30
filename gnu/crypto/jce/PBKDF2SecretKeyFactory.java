/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce;

import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.PRNGFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class PBKDF2SecretKeyFactory
extends SecretKeyFactorySpi {
    private static final int DEFAULT_ITERATION_COUNT = 1000;
    private static final int DEFAULT_KEY_LEN = 32;
    protected String macName;

    protected SecretKey engineGenerateSecret(KeySpec spec) throws InvalidKeySpecException {
        if (!(spec instanceof PBEKeySpec)) {
            throw new InvalidKeySpecException("not a PBEKeySpec");
        }
        IRandom kdf = PRNGFactory.getInstance("PBKDF2-" + this.macName);
        HashMap<String, Object> attr = new HashMap<String, Object>();
        attr.put("gnu.crypto.pbe.password", ((PBEKeySpec)spec).getPassword());
        byte[] salt = ((PBEKeySpec)spec).getSalt();
        if (salt == null) {
            salt = new byte[]{};
        }
        attr.put("gnu.crypto.pbe.salt", salt);
        int ic = ((PBEKeySpec)spec).getIterationCount();
        if (ic <= 0) {
            ic = 1000;
        }
        attr.put("gnu.crypto.pbe.iteration.count", new Integer(ic));
        kdf.init(attr);
        int len = ((PBEKeySpec)spec).getKeyLength();
        if (len <= 0) {
            len = 32;
        }
        byte[] dk = new byte[len];
        try {
            kdf.nextBytes(dk, 0, len);
        }
        catch (LimitReachedException lre) {
            throw new IllegalArgumentException(lre.toString());
        }
        return new SecretKeySpec(dk, "PBKDF2");
    }

    protected KeySpec engineGetKeySpec(SecretKey key, Class clazz) throws InvalidKeySpecException {
        throw new InvalidKeySpecException("not supported");
    }

    protected SecretKey engineTranslateKey(SecretKey key) {
        return new SecretKeySpec(key.getEncoded(), key.getAlgorithm());
    }

    protected PBKDF2SecretKeyFactory(String macName) {
        this.macName = macName;
    }

    public static class HMacHaval
    extends PBKDF2SecretKeyFactory {
        public HMacHaval() {
            super("HMAC-HAVAL");
        }
    }

    public static class HMacMD2
    extends PBKDF2SecretKeyFactory {
        public HMacMD2() {
            super("HMAC-MD2");
        }
    }

    public static class HMacMD4
    extends PBKDF2SecretKeyFactory {
        public HMacMD4() {
            super("HMAC-MD4");
        }
    }

    public static class HMacMD5
    extends PBKDF2SecretKeyFactory {
        public HMacMD5() {
            super("HMAC-MD5");
        }
    }

    public static class HMacRipeMD128
    extends PBKDF2SecretKeyFactory {
        public HMacRipeMD128() {
            super("HMAC-RIPEMD128");
        }
    }

    public static class HMacRipeMD160
    extends PBKDF2SecretKeyFactory {
        public HMacRipeMD160() {
            super("HMAC-RIPEMD160");
        }
    }

    public static class HMacSHA1
    extends PBKDF2SecretKeyFactory {
        public HMacSHA1() {
            super("HMAC-SHA1");
        }
    }

    public static class HMacSHA256
    extends PBKDF2SecretKeyFactory {
        public HMacSHA256() {
            super("HMAC-SHA256");
        }
    }

    public static class HMacSHA384
    extends PBKDF2SecretKeyFactory {
        public HMacSHA384() {
            super("HMAC-SHA384");
        }
    }

    public static class HMacSHA512
    extends PBKDF2SecretKeyFactory {
        public HMacSHA512() {
            super("HMAC-SHA512");
        }
    }

    public static class HMacTiger
    extends PBKDF2SecretKeyFactory {
        public HMacTiger() {
            super("HMAC-TIGER");
        }
    }

    public static class HMacWhirlpool
    extends PBKDF2SecretKeyFactory {
        public HMacWhirlpool() {
            super("HMAC-WHIRLPOOL");
        }
    }

}


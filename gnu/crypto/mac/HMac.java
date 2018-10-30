/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.hash.MD5;
import gnu.crypto.mac.BaseMac;
import gnu.crypto.util.Util;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class HMac
extends BaseMac {
    public static final String USE_WITH_PKCS5_V2 = "gnu.crypto.hmac.pkcs5";
    private static final byte IPAD_BYTE = 54;
    private static final byte OPAD_BYTE = 92;
    private static Boolean valid;
    protected int macSize;
    protected int blockSize;
    protected IMessageDigest ipadHash;
    protected IMessageDigest opadHash;
    protected byte[] ipad;

    public Object clone() {
        HMac result = new HMac((IMessageDigest)this.underlyingHash.clone());
        result.truncatedSize = this.truncatedSize;
        if (this.ipadHash != null) {
            result.ipadHash = (IMessageDigest)this.ipadHash.clone();
        }
        if (this.opadHash != null) {
            result.opadHash = (IMessageDigest)this.opadHash.clone();
        }
        if (this.ipad != null) {
            result.ipad = (byte[])this.ipad.clone();
        }
        return result;
    }

    public void init(Map attributes) throws InvalidKeyException, IllegalStateException {
        Integer ts = (Integer)attributes.get("gnu.crypto.mac.truncated.size");
        int n = this.truncatedSize = ts == null ? this.macSize : ts;
        if (this.truncatedSize < this.macSize / 2) {
            throw new IllegalArgumentException("Truncated size too small");
        }
        if (this.truncatedSize < 10) {
            throw new IllegalArgumentException("Truncated size less than 80 bits");
        }
        byte[] K = (byte[])attributes.get("gnu.crypto.mac.key.material");
        if (K == null) {
            if (this.ipadHash == null) {
                throw new InvalidKeyException("Null key");
            }
            this.underlyingHash = (IMessageDigest)this.ipadHash.clone();
            return;
        }
        Boolean pkcs5 = (Boolean)attributes.get(USE_WITH_PKCS5_V2);
        if (pkcs5 == null) {
            pkcs5 = Boolean.FALSE;
        }
        if (K.length < this.macSize && !pkcs5.booleanValue()) {
            throw new InvalidKeyException("Key too short");
        }
        if (K.length > this.blockSize) {
            this.underlyingHash.update(K, 0, K.length);
            K = this.underlyingHash.digest();
        }
        if (K.length < this.blockSize) {
            int limit = K.length > this.blockSize ? this.blockSize : K.length;
            byte[] newK = new byte[this.blockSize];
            System.arraycopy(K, 0, newK, 0, limit);
            K = newK;
        }
        this.underlyingHash.reset();
        this.opadHash = (IMessageDigest)this.underlyingHash.clone();
        if (this.ipad == null) {
            this.ipad = new byte[this.blockSize];
        }
        int i = 0;
        while (i < this.blockSize) {
            this.ipad[i] = (byte)(K[i] ^ 54);
            ++i;
        }
        i = 0;
        while (i < this.blockSize) {
            this.opadHash.update((byte)(K[i] ^ 92));
            ++i;
        }
        this.underlyingHash.update(this.ipad, 0, this.blockSize);
        this.ipadHash = (IMessageDigest)this.underlyingHash.clone();
        K = null;
    }

    public void reset() {
        super.reset();
        if (this.ipad != null) {
            this.underlyingHash.update(this.ipad, 0, this.blockSize);
            this.ipadHash = (IMessageDigest)this.underlyingHash.clone();
        }
    }

    public byte[] digest() {
        if (this.ipadHash == null) {
            throw new IllegalStateException("HMAC not initialised");
        }
        byte[] out = this.underlyingHash.digest();
        this.underlyingHash = (IMessageDigest)this.opadHash.clone();
        this.underlyingHash.update(out, 0, this.macSize);
        out = this.underlyingHash.digest();
        if (this.truncatedSize == this.macSize) {
            return out;
        }
        byte[] result = new byte[this.truncatedSize];
        System.arraycopy(out, 0, result, 0, this.truncatedSize);
        return result;
    }

    public boolean selfTest() {
        if (valid == null) {
            try {
                HMac mac = new HMac(new MD5());
                String tv1 = "9294727A3638BB1C13F48EF8158BFC9D";
                String tv3 = "56BE34521D144C88DBB8C733F0E8B3F6";
                byte[] k1 = new byte[]{11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11};
                byte[] k3 = new byte[]{-86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86, -86};
                byte[] data = new byte[50];
                int i = 0;
                while (i < 50) {
                    data[i++] = -35;
                }
                HashMap<String, byte[]> map = new HashMap<String, byte[]>();
                map.put("gnu.crypto.mac.key.material", k1);
                mac.init(map);
                mac.update("Hi There".getBytes("ASCII"), 0, 8);
                if (!tv1.equals(Util.toString(mac.digest()))) {
                    valid = Boolean.FALSE;
                }
                map.put("gnu.crypto.mac.key.material", k3);
                mac.init(map);
                mac.update(data, 0, 50);
                if (!tv3.equals(Util.toString(mac.digest()))) {
                    valid = Boolean.FALSE;
                }
                valid = Boolean.TRUE;
            }
            catch (Exception x) {
                x.printStackTrace(System.err);
                valid = Boolean.FALSE;
            }
        }
        return valid;
    }

    protected HMac(IMessageDigest underlyingHash) {
        super("hmac-" + underlyingHash.name(), underlyingHash);
        this.blockSize = underlyingHash.blockSize();
        this.macSize = underlyingHash.hashSize();
        this.opadHash = null;
        this.ipadHash = null;
    }
}


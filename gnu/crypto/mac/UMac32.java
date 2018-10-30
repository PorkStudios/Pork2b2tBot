/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mac.BaseMac;
import gnu.crypto.mac.UHash32;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.UMacGenerator;
import gnu.crypto.util.Util;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class UMac32
extends BaseMac {
    public static final String NONCE_MATERIAL = "gnu.crypto.umac.nonce.material";
    private static final String TV1 = "455ED214A6909F20";
    private static final BigInteger MAX_NONCE_ITERATIONS = BigInteger.ONE.shiftLeft(128);
    static final int OUTPUT_LEN = 8;
    static final int L1_KEY_LEN = 1024;
    static final int KEY_LEN = 16;
    private static Boolean valid;
    private byte[] nonce;
    private UHash32 uhash32;
    private BigInteger nonceReuseCount;
    private transient byte[] K;

    public Object clone() {
        return new UMac32(this);
    }

    public int macSize() {
        return 8;
    }

    public void init(Map attributes) throws InvalidKeyException, IllegalStateException {
        boolean newNonce;
        byte[] key = (byte[])attributes.get("gnu.crypto.mac.key.material");
        byte[] n = (byte[])attributes.get(NONCE_MATERIAL);
        boolean bl = false;
        if (key != null) {
            bl = true;
        }
        boolean newKey = bl;
        boolean bl2 = false;
        if (n != null) {
            bl2 = newNonce = true;
        }
        if (newKey) {
            if (key.length != 16) {
                throw new InvalidKeyException("Key length: " + String.valueOf(key.length));
            }
            this.K = key;
        } else if (this.K == null) {
            throw new InvalidKeyException("Null Key");
        }
        if (newNonce) {
            if (n.length < 1 || n.length > 16) {
                throw new IllegalArgumentException("Invalid Nonce length: " + String.valueOf(n.length));
            }
            if (n.length < 16) {
                byte[] newN = new byte[16];
                System.arraycopy(n, 0, newN, 0, n.length);
                this.nonce = newN;
            } else {
                this.nonce = n;
            }
            this.nonceReuseCount = BigInteger.ZERO;
        } else if (this.nonce == null) {
            this.nonce = new byte[16];
            this.nonceReuseCount = BigInteger.ZERO;
        } else if (!newKey) {
            this.nonceReuseCount = this.nonceReuseCount.add(BigInteger.ONE);
            if (this.nonceReuseCount.compareTo(MAX_NONCE_ITERATIONS) >= 0) {
                throw new InvalidKeyException("Null Key and unusable old Nonce");
            }
            BigInteger N = new BigInteger(1, this.nonce);
            n = (N = N.add(BigInteger.ONE).mod(MAX_NONCE_ITERATIONS)).toByteArray();
            if (n.length == 16) {
                this.nonce = n;
            } else if (n.length < 16) {
                this.nonce = new byte[16];
                System.arraycopy(n, 0, this.nonce, 16 - n.length, n.length);
            } else {
                this.nonce = new byte[16];
                System.arraycopy(n, n.length - 16, this.nonce, 0, 16);
            }
        } else {
            this.nonceReuseCount = BigInteger.ZERO;
        }
        if (this.uhash32 == null) {
            this.uhash32 = new UHash32();
        }
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();
        map.put("gnu.crypto.mac.key.material", this.K);
        this.uhash32.init(map);
    }

    public void update(byte b) {
        this.uhash32.update(b);
    }

    public void update(byte[] b, int offset, int len) {
        this.uhash32.update(b, offset, len);
    }

    public byte[] digest() {
        byte[] result = this.uhash32.digest();
        byte[] pad = this.pdf();
        int i = 0;
        while (i < 8) {
            result[i] = (byte)(result[i] ^ pad[i]);
            ++i;
        }
        return result;
    }

    public void reset() {
        if (this.uhash32 != null) {
            this.uhash32.reset();
        }
    }

    public boolean selfTest() {
        if (valid == null) {
            byte[] key;
            try {
                key = "abcdefghijklmnop".getBytes("ASCII");
            }
            catch (UnsupportedEncodingException x) {
                throw new RuntimeException("ASCII not supported");
            }
            byte[] arrby = new byte[8];
            arrby[1] = 1;
            arrby[2] = 2;
            arrby[3] = 3;
            arrby[4] = 4;
            arrby[5] = 5;
            arrby[6] = 6;
            arrby[7] = 7;
            byte[] nonce = arrby;
            UMac32 mac = new UMac32();
            HashMap<String, byte[]> attributes = new HashMap<String, byte[]>();
            attributes.put("gnu.crypto.mac.key.material", key);
            attributes.put(NONCE_MATERIAL, nonce);
            try {
                mac.init(attributes);
            }
            catch (InvalidKeyException x) {
                x.printStackTrace(System.err);
                return false;
            }
            byte[] data = new byte[128];
            data[0] = -128;
            mac.update(data, 0, 128);
            byte[] result = mac.digest();
            valid = new Boolean(TV1.equals(Util.toString(result)));
        }
        return valid;
    }

    private final byte[] pdf() {
        BigInteger Nonce = new BigInteger(1, this.nonce);
        int nlowbitsnum = Nonce.testBit(0);
        Nonce = Nonce.clearBit(0);
        UMacGenerator kdf = new UMacGenerator();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("gnu.crypto.cipher.key.material", this.K);
        map.put("gnu.crypto.prng.umac.index", new Integer(128));
        kdf.init(map);
        byte[] Kp = new byte[16];
        try {
            kdf.nextBytes(Kp, 0, 16);
        }
        catch (IllegalStateException x) {
            x.printStackTrace(System.err);
            throw new RuntimeException(String.valueOf(x));
        }
        catch (LimitReachedException x) {
            x.printStackTrace(System.err);
            throw new RuntimeException(String.valueOf(x));
        }
        IBlockCipher aes = CipherFactory.getInstance("aes");
        map.put("gnu.crypto.cipher.key.material", Kp);
        try {
            aes.init(map);
        }
        catch (InvalidKeyException x) {
            x.printStackTrace(System.err);
            throw new RuntimeException(String.valueOf(x));
        }
        catch (IllegalStateException x) {
            x.printStackTrace(System.err);
            throw new RuntimeException(String.valueOf(x));
        }
        byte[] T = new byte[16];
        aes.encryptBlock(this.nonce, 0, T, 0);
        byte[] result = new byte[8];
        System.arraycopy(T, nlowbitsnum, result, 0, 8);
        return result;
    }

    public UMac32() {
        super("umac32");
    }

    private UMac32(UMac32 that) {
        this();
        if (that.K != null) {
            this.K = (byte[])that.K.clone();
        }
        if (that.nonce != null) {
            this.nonce = (byte[])that.nonce.clone();
        }
        if (that.uhash32 != null) {
            this.uhash32 = (UHash32)that.uhash32.clone();
        }
        this.nonceReuseCount = that.nonceReuseCount;
    }
}


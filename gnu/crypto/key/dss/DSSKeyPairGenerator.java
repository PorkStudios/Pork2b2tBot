/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.key.dss;

import gnu.crypto.hash.Sha160;
import gnu.crypto.key.IKeyPairGenerator;
import gnu.crypto.key.dss.DSSPrivateKey;
import gnu.crypto.key.dss.DSSPublicKey;
import gnu.crypto.key.dss.FIPS186;
import gnu.crypto.util.PRNG;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.DSAParameterSpec;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class DSSKeyPairGenerator
implements IKeyPairGenerator {
    private static final String NAME = "dss";
    private static final boolean DEBUG = false;
    private static final int debuglevel = 5;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    private static final BigInteger TWO = new BigInteger("2");
    public static final String MODULUS_LENGTH = "gnu.crypto.dss.L";
    public static final String USE_DEFAULTS = "gnu.crypto.dss.use.defaults";
    public static final String SOURCE_OF_RANDOMNESS = "gnu.crypto.dss.prng";
    public static final String DSS_PARAMETERS = "gnu.crypto.dss.params";
    private static final int DEFAULT_MODULUS_LENGTH = 1024;
    private static final int[] T_SHS = new int[]{1732584193, -271733879, -1732584194, 271733878, -1009589776};
    public static final DSAParameterSpec KEY_PARAMS_512 = new DSAParameterSpec(new BigInteger("fca682ce8e12caba26efccf7110e526db078b05edecbcd1eb4a208f3ae1617ae01f35b91a47e6df63413c5e12ed0899bcd132acd50d99151bdc43ee737592e17", 16), new BigInteger("962eddcc369cba8ebb260ee6b6a126d9346e38c5", 16), new BigInteger("678471b27a9cf44ee91a49c5147db1a9aaf244f05a434d6486931d2d14271b9e35030b71fd73da179069b32e2935630e1c2062354d0da20a6c416e50be794ca4", 16));
    public static final DSAParameterSpec KEY_PARAMS_768 = new DSAParameterSpec(new BigInteger("e9e642599d355f37c97ffd3567120b8e25c9cd43e927b3a9670fbec5d890141922d2c3b3ad2480093799869d1e846aab49fab0ad26d2ce6a22219d470bce7d777d4a21fbe9c270b57f607002f3cef8393694cf45ee3688c11a8c56ab127a3daf", 16), new BigInteger("9cdbd84c9f1ac2f38d0f80f42ab952e7338bf511", 16), new BigInteger("30470ad5a005fb14ce2d9dcd87e38bc7d1b1c5facbaecbe95f190aa7a31d23c4dbbcbe06174544401a5b2c020965d8c2bd2171d3668445771f74ba084d2029d83c1c158547f3a9f1a2715be23d51ae4d3e5a1f6a7064f316933a346d3f529252", 16));
    public static final DSAParameterSpec KEY_PARAMS_1024 = new DSAParameterSpec(new BigInteger("fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c7", 16), new BigInteger("9760508f15230bccb292b982a2eb840bf0581cf5", 16), new BigInteger("f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a", 16));
    private static final BigInteger TWO_POW_160 = TWO.pow(160);
    private int L;
    private SecureRandom rnd;
    private BigInteger seed;
    private BigInteger counter;
    private BigInteger p;
    private BigInteger q;
    private BigInteger e;
    private BigInteger g;
    private BigInteger XKEY;

    private static final void debug(String s) {
        err.println(">>> dss: " + s);
    }

    public String name() {
        return NAME;
    }

    public void setup(Map attributes) {
        DSAParameterSpec params;
        Integer l = (Integer)attributes.get(MODULUS_LENGTH);
        int n = this.L = l == null ? 1024 : l;
        if (this.L % 64 != 0 || this.L < 512 || this.L > 1024) {
            throw new IllegalArgumentException(MODULUS_LENGTH);
        }
        Boolean useDefaults = (Boolean)attributes.get(USE_DEFAULTS);
        if (useDefaults == null) {
            useDefaults = Boolean.TRUE;
        }
        if ((params = (DSAParameterSpec)attributes.get(DSS_PARAMETERS)) != null) {
            this.p = params.getP();
            this.q = params.getQ();
            this.g = params.getG();
        } else if (useDefaults.equals(Boolean.TRUE)) {
            switch (this.L) {
                case 512: {
                    this.p = KEY_PARAMS_512.getP();
                    this.q = KEY_PARAMS_512.getQ();
                    this.g = KEY_PARAMS_512.getG();
                    break;
                }
                case 768: {
                    this.p = KEY_PARAMS_768.getP();
                    this.q = KEY_PARAMS_768.getQ();
                    this.g = KEY_PARAMS_768.getG();
                    break;
                }
                case 1024: {
                    this.p = KEY_PARAMS_1024.getP();
                    this.q = KEY_PARAMS_1024.getQ();
                    this.g = KEY_PARAMS_1024.getG();
                    break;
                }
                default: {
                    this.p = null;
                    this.q = null;
                    this.g = null;
                    break;
                }
            }
        } else {
            this.p = null;
            this.q = null;
            this.g = null;
        }
        this.rnd = (SecureRandom)attributes.get(SOURCE_OF_RANDOMNESS);
        byte[] kb = new byte[20];
        this.nextRandomBytes(kb);
        this.XKEY = new BigInteger(1, kb).setBit(159).setBit(0);
    }

    public KeyPair generate() {
        if (this.p == null) {
            BigInteger[] params = new FIPS186(this.L, this.rnd).generateParameters();
            this.seed = params[0];
            this.counter = params[1];
            this.q = params[2];
            this.p = params[3];
            this.e = params[4];
            this.g = params[5];
        }
        BigInteger x = this.nextX();
        BigInteger y = this.g.modPow(x, this.p);
        DSSPublicKey pubK = new DSSPublicKey(this.p, this.q, this.g, y);
        DSSPrivateKey secK = new DSSPrivateKey(this.p, this.q, this.g, x);
        return new KeyPair(pubK, secK);
    }

    private final synchronized BigInteger nextX() {
        byte[] xk = this.XKEY.toByteArray();
        byte[] in = new byte[64];
        System.arraycopy(xk, 0, in, 0, xk.length);
        int[] H = Sha160.G(T_SHS[0], T_SHS[1], T_SHS[2], T_SHS[3], T_SHS[4], in, 0);
        byte[] h = new byte[20];
        int i = 0;
        int j = 0;
        while (i < 5) {
            h[j++] = (byte)(H[i] >>> 24);
            h[j++] = (byte)(H[i] >>> 16);
            h[j++] = (byte)(H[i] >>> 8);
            h[j++] = (byte)H[i];
            ++i;
        }
        BigInteger result = new BigInteger(1, h).mod(this.q);
        this.XKEY = this.XKEY.add(result).add(BigInteger.ONE).mod(TWO_POW_160);
        return result;
    }

    private final void nextRandomBytes(byte[] buffer) {
        if (this.rnd != null) {
            this.rnd.nextBytes(buffer);
        } else {
            PRNG.nextBytes(buffer);
        }
    }

    private final /* synthetic */ void this() {
        this.rnd = null;
    }

    public DSSKeyPairGenerator() {
        this.this();
    }
}


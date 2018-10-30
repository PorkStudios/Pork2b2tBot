/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.mac.BaseMac;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.UMacGenerator;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class UHash32
extends BaseMac {
    private static final BigInteger PRIME_19 = BigInteger.valueOf(524287L);
    private static final BigInteger PRIME_32 = BigInteger.valueOf(0xFFFFFFFBL);
    private static final BigInteger PRIME_36 = BigInteger.valueOf(0xFFFFFFFFBL);
    private static final BigInteger PRIME_64 = new BigInteger(1, new byte[]{-1, -1, -1, -1, -1, -1, -1, -59});
    private static final BigInteger PRIME_128 = new BigInteger(1, new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 97});
    static final BigInteger TWO = BigInteger.valueOf(2);
    static final long BOUNDARY = TWO.shiftLeft(17).longValue();
    static final BigInteger LOWER_RANGE = TWO.pow(64).subtract(TWO.pow(32));
    static final BigInteger UPPER_RANGE = TWO.pow(128).subtract(TWO.pow(96));
    static final byte[] ALL_ZEROES = new byte[32];
    int streams;
    L1Hash32[] l1hash;

    static final BigInteger prime(int n) {
        switch (n) {
            case 19: {
                return PRIME_19;
            }
            case 32: {
                return PRIME_32;
            }
            case 36: {
                return PRIME_36;
            }
            case 64: {
                return PRIME_64;
            }
            case 128: {
                return PRIME_128;
            }
        }
        throw new IllegalArgumentException("Undefined prime(" + String.valueOf(n) + ')');
    }

    public Object clone() {
        return new UHash32(this);
    }

    public int macSize() {
        return 8;
    }

    public void init(Map attributes) throws InvalidKeyException, IllegalStateException {
        byte[] K = (byte[])attributes.get("gnu.crypto.mac.key.material");
        if (K == null) {
            throw new InvalidKeyException("Null Key");
        }
        if (K.length != 16) {
            throw new InvalidKeyException("Invalid Key length: " + String.valueOf(K.length));
        }
        this.streams = 2;
        UMacGenerator kdf1 = new UMacGenerator();
        UMacGenerator kdf2 = new UMacGenerator();
        UMacGenerator kdf3 = new UMacGenerator();
        UMacGenerator kdf4 = new UMacGenerator();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("gnu.crypto.cipher.key.material", K);
        map.put("gnu.crypto.prng.umac.index", new Integer(0));
        kdf1.init(map);
        map.put("gnu.crypto.prng.umac.index", new Integer(1));
        kdf2.init(map);
        map.put("gnu.crypto.prng.umac.index", new Integer(2));
        kdf3.init(map);
        map.put("gnu.crypto.prng.umac.index", new Integer(3));
        kdf4.init(map);
        byte[] L1Key = new byte[1024 + (this.streams - 1) * 16];
        try {
            kdf1.nextBytes(L1Key, 0, L1Key.length);
        }
        catch (LimitReachedException x) {
            x.printStackTrace(System.err);
            throw new RuntimeException("KDF for L1Key reached limit");
        }
        this.l1hash = new L1Hash32[this.streams];
        int i = 0;
        while (i < this.streams) {
            byte[] k1 = new byte[1024];
            System.arraycopy(L1Key, i * 16, k1, 0, 1024);
            byte[] k2 = new byte[24];
            try {
                kdf2.nextBytes(k2, 0, 24);
            }
            catch (LimitReachedException x) {
                x.printStackTrace(System.err);
                throw new RuntimeException("KDF for L2Key reached limit");
            }
            byte[] k31 = new byte[64];
            try {
                kdf3.nextBytes(k31, 0, 64);
            }
            catch (LimitReachedException x) {
                x.printStackTrace(System.err);
                throw new RuntimeException("KDF for L3Key1 reached limit");
            }
            byte[] k32 = new byte[4];
            try {
                kdf4.nextBytes(k32, 0, 4);
            }
            catch (LimitReachedException x) {
                x.printStackTrace(System.err);
                throw new RuntimeException("KDF for L3Key2 reached limit");
            }
            L1Hash32 mac = new L1Hash32();
            mac.init(k1, k2, k31, k32);
            this.l1hash[i] = mac;
            ++i;
        }
    }

    public void update(byte b) {
        int i = 0;
        while (i < this.streams) {
            this.l1hash[i].update(b);
            ++i;
        }
    }

    public void update(byte[] b, int offset, int len) {
        int i = 0;
        while (i < len) {
            this.update(b[offset + i]);
            ++i;
        }
    }

    public byte[] digest() {
        byte[] result = new byte[8];
        int i = 0;
        while (i < this.streams) {
            byte[] partialResult = this.l1hash[i].digest();
            System.arraycopy(partialResult, 0, result, 4 * i, 4);
            ++i;
        }
        this.reset();
        return result;
    }

    public void reset() {
        int i = 0;
        while (i < this.streams) {
            this.l1hash[i].reset();
            ++i;
        }
    }

    public boolean selfTest() {
        return true;
    }

    public UHash32() {
        super("uhash32");
    }

    private UHash32(UHash32 that) {
        this();
        this.streams = that.streams;
        if (that.l1hash != null) {
            this.l1hash = new L1Hash32[that.streams];
            int i = 0;
            while (i < that.streams) {
                if (that.l1hash[i] != null) {
                    this.l1hash[i] = (L1Hash32)that.l1hash[i].clone();
                }
                ++i;
            }
        }
    }

    class L1Hash32
    implements Cloneable {
        private int[] key = new int[256];
        private byte[] buffer = new byte[1024];
        private int count = 0;
        private ByteArrayOutputStream Y = new ByteArrayOutputStream();
        private long totalCount = 0L;
        private L2Hash32 l2hash;
        private L3Hash32 l3hash;

        public Object clone() {
            return new L1Hash32(this);
        }

        public void init(byte[] k1, byte[] k2, byte[] k31, byte[] k32) {
            int i = 0;
            int j = 0;
            while (i < 256) {
                this.key[i] = k1[j++] << 24 | (k1[j++] & 255) << 16 | (k1[j++] & 255) << 8 | k1[j++] & 255;
                ++i;
            }
            this.l2hash = new L2Hash32(k2);
            this.l3hash = new L3Hash32(k31, k32);
        }

        public void update(byte b) {
            this.buffer[this.count] = b;
            ++this.count;
            ++this.totalCount;
            if (this.count >= 1024) {
                byte[] y = this.nh32(1024);
                this.Y.write(y, 0, 8);
                this.count = 0;
                if (this.Y.size() == 16) {
                    byte[] A = this.Y.toByteArray();
                    this.Y.reset();
                    this.l2hash.update(A, 0, 16);
                }
            }
        }

        public byte[] digest() {
            byte[] B;
            if (this.count != 0) {
                if (this.count % 32 != 0) {
                    int limit = 32 * ((this.count + 31) / 32);
                    System.arraycopy(UHash32.ALL_ZEROES, 0, this.buffer, this.count, limit - this.count);
                    this.count += limit - this.count;
                }
                byte[] y = this.nh32(this.count);
                this.Y.write(y, 0, 8);
            }
            byte[] A = this.Y.toByteArray();
            this.Y.reset();
            if (this.totalCount <= 1024L) {
                if (A.length == 0) {
                    B = this.l2hash.digest();
                } else {
                    B = new byte[16];
                    System.arraycopy(A, 0, B, 8, 8);
                }
            } else {
                if (A.length != 0) {
                    this.l2hash.update(A, 0, A.length);
                }
                B = this.l2hash.digest();
            }
            byte[] result = this.l3hash.digest(B);
            this.reset();
            return result;
        }

        public void reset() {
            this.count = 0;
            this.Y.reset();
            this.totalCount = 0L;
            if (this.l2hash != null) {
                this.l2hash.reset();
            }
        }

        private final byte[] nh32(int len) {
            int t = len / 4;
            int[] m = new int[t];
            int j = 0;
            int i = 0;
            j = 0;
            while (i < t) {
                m[i] = this.buffer[j++] << 24 | (this.buffer[j++] & 255) << 16 | (this.buffer[j++] & 255) << 8 | this.buffer[j++] & 255;
                ++i;
            }
            long result = (long)len * 8L;
            i = 0;
            while (i < t) {
                result += ((long)(m[i] + this.key[i]) & 0xFFFFFFFFL) * ((long)(m[i + 4] + this.key[i + 4]) & 0xFFFFFFFFL);
                result += ((long)(m[i + 1] + this.key[i + 1]) & 0xFFFFFFFFL) * ((long)(m[i + 5] + this.key[i + 5]) & 0xFFFFFFFFL);
                result += ((long)(m[i + 2] + this.key[i + 2]) & 0xFFFFFFFFL) * ((long)(m[i + 6] + this.key[i + 6]) & 0xFFFFFFFFL);
                result += ((long)(m[i + 3] + this.key[i + 3]) & 0xFFFFFFFFL) * ((long)(m[i + 7] + this.key[i + 7]) & 0xFFFFFFFFL);
                i += 8;
            }
            return new byte[]{(byte)(result >>> 56), (byte)(result >>> 48), (byte)(result >>> 40), (byte)(result >>> 32), (byte)(result >>> 24), (byte)(result >>> 16), (byte)(result >>> 8), (byte)result};
        }

        L1Hash32() {
        }

        private L1Hash32(L1Hash32 that) {
            this();
            System.arraycopy(that.key, 0, this.key, 0, that.key.length);
            System.arraycopy(that.buffer, 0, this.buffer, 0, that.count);
            this.count = that.count;
            byte[] otherY = that.Y.toByteArray();
            this.Y.write(otherY, 0, otherY.length);
            this.totalCount = that.totalCount;
            if (that.l2hash != null) {
                this.l2hash = (L2Hash32)that.l2hash.clone();
            }
            if (that.l3hash != null) {
                this.l3hash = (L3Hash32)that.l3hash.clone();
            }
        }
    }

    class L2Hash32
    implements Cloneable {
        private BigInteger k64;
        private BigInteger k128;
        private BigInteger y;
        private boolean highBound;
        private long bytesSoFar;
        private ByteArrayOutputStream buffer;

        public Object clone() {
            return new L2Hash32(this);
        }

        void update(byte[] b, int offset, int len) {
            if (len == 0) {
                return;
            }
            if (!this.highBound) {
                this.poly(64, UHash32.LOWER_RANGE, this.k64, b, offset, 8);
                this.bytesSoFar += 8L;
                boolean bl = false;
                if (this.bytesSoFar > UHash32.BOUNDARY) {
                    bl = this.highBound = true;
                }
                if (this.highBound) {
                    this.poly(128, UHash32.UPPER_RANGE, this.k128, this.yTo16bytes(), 0, 16);
                    this.buffer = new ByteArrayOutputStream();
                }
                this.update(b, offset + 8, len - 8);
            } else {
                this.buffer.write(b, offset, len);
                if (this.buffer.size() > 16) {
                    byte[] bb = this.buffer.toByteArray();
                    this.poly(128, UHash32.UPPER_RANGE, this.k128, bb, 0, 16);
                    if (bb.length > 16) {
                        this.buffer.write(bb, 16, bb.length - 16);
                    }
                }
            }
        }

        byte[] digest() {
            if (this.highBound) {
                byte[] bb = this.buffer.toByteArray();
                byte[] lastBlock = new byte[16];
                System.arraycopy(bb, 0, lastBlock, 0, bb.length);
                lastBlock[bb.length] = -128;
                this.poly(128, UHash32.UPPER_RANGE, this.k128, lastBlock, 0, 16);
            }
            byte[] result = this.yTo16bytes();
            this.reset();
            return result;
        }

        void reset() {
            this.y = BigInteger.ONE;
            this.highBound = false;
            this.bytesSoFar = 0L;
            if (this.buffer != null) {
                this.buffer.reset();
            }
        }

        private final byte[] yTo16bytes() {
            byte[] yy = this.y.toByteArray();
            byte[] result = new byte[16];
            if (yy.length > 16) {
                System.arraycopy(yy, yy.length - 16, result, 0, 16);
            } else {
                System.arraycopy(yy, 0, result, 16 - yy.length, yy.length);
            }
            return result;
        }

        private final void poly(int wordbits, BigInteger maxwordrange, BigInteger k, byte[] M, int off, int len) {
            byte[] mag = new byte[len];
            System.arraycopy(M, off, mag, 0, len);
            BigInteger p = UHash32.prime(wordbits);
            BigInteger offset = UHash32.TWO.pow(wordbits).subtract(p);
            BigInteger marker = p.subtract(BigInteger.ONE);
            BigInteger m = new BigInteger(1, mag);
            if (m.compareTo(maxwordrange) >= 0) {
                this.y = this.y.multiply(k).add(marker).mod(p);
                this.y = this.y.multiply(k).add(m.subtract(offset)).mod(p);
            } else {
                this.y = this.y.multiply(k).add(m).mod(p);
            }
        }

        L2Hash32(byte[] K) {
            if (K.length != 24) {
                throw new ExceptionInInitializerError("K length is not 24");
            }
            int i = 0;
            byte[] arrby = new byte[]{(byte)(K[i++] & 1), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 1), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 255)};
            this.k64 = new BigInteger(1, arrby);
            byte[] arrby2 = new byte[]{(byte)(K[i++] & 1), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 1), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 1), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 1), (byte)(K[i++] & 255), (byte)(K[i++] & 255), (byte)(K[i++] & 255)};
            this.k128 = new BigInteger(1, arrby2);
            this.y = BigInteger.ONE;
            this.highBound = false;
            this.bytesSoFar = 0L;
        }

        private L2Hash32(L2Hash32 that) {
            this.k64 = that.k64;
            this.k128 = that.k128;
            this.y = that.y;
            this.highBound = that.highBound;
            this.bytesSoFar = that.bytesSoFar;
            if (that.buffer != null) {
                byte[] thatbuffer = that.buffer.toByteArray();
                this.buffer = new ByteArrayOutputStream();
                this.buffer.write(thatbuffer, 0, thatbuffer.length);
            }
        }
    }

    /*
     * Illegal identifiers - consider using --renameillegalidents true
     */
    class L3Hash32
    implements Cloneable {
        private static final long PRIME_36 = 0xFFFFFFFFBL;
        private int[] k;

        public Object clone() {
            return new L3Hash32((int[])this.k.clone());
        }

        byte[] digest(byte[] M) {
            if (M.length != 16) {
                throw new IllegalArgumentException("M length is not 16");
            }
            long y = 0L;
            int i = 0;
            int j = 0;
            while (i < 8) {
                long m = ((long)M[j++] & 255L) << 8 | (long)M[j++] & 255L;
                y += m * ((long)this.k[i] & 0xFFFFFFFFL) % 0xFFFFFFFFBL;
                ++i;
            }
            int Y = (int)y ^ this.k[8];
            return new byte[]{(byte)(Y >>> 24), (byte)(Y >>> 16), (byte)(Y >>> 8), (byte)Y};
        }

        private final /* synthetic */ void this() {
            this.k = new int[9];
        }

        L3Hash32(byte[] K1, byte[] K2) {
            this.this();
            if (K1.length != 64) {
                throw new ExceptionInInitializerError("K1 length is not 64");
            }
            if (K2.length != 4) {
                throw new ExceptionInInitializerError("K2 length is not 4");
            }
            int i = 0;
            int j = 0;
            while (i < 8) {
                long kk = ((long)K1[j++] & 255L) << 56 | ((long)K1[j++] & 255L) << 48 | ((long)K1[j++] & 255L) << 40 | ((long)K1[j++] & 255L) << 32 | ((long)K1[j++] & 255L) << 24 | ((long)K1[j++] & 255L) << 16 | ((long)K1[j++] & 255L) << 8 | (long)K1[j++] & 255L;
                this.k[i] = (int)(kk % 0xFFFFFFFFBL);
                ++i;
            }
            this.k[8] = K2[0] << 24 | (K2[1] & 255) << 16 | (K2[2] & 255) << 8 | K2[3] & 255;
        }

        private L3Hash32(int[] k) {
            this.this();
            this.k = k;
        }
    }

}


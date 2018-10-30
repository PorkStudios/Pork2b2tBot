/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.prng;

import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.prng.BasePRNG;
import gnu.crypto.prng.LimitReachedException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class ICMGenerator
extends BasePRNG {
    public static final String CIPHER = "gnu.crypto.prng.icm.cipher.name";
    public static final String BLOCK_INDEX_LENGTH = "gnu.crypto.prng.icm.block.index.length";
    public static final String SEGMENT_INDEX_LENGTH = "gnu.crypto.prng.icm.segment.index.length";
    public static final String OFFSET = "gnu.crypto.prng.icm.offset";
    public static final String SEGMENT_INDEX = "gnu.crypto.prng.icm.segment.index";
    private static final BigInteger TWO_FIFTY_SIX = new BigInteger("256");
    private IBlockCipher cipher;
    private int blockNdxLength;
    private int segmentNdxLength;
    private BigInteger blockNdx;
    private BigInteger segmentNdx;
    private BigInteger C0;

    public Object clone() {
        return new ICMGenerator(this);
    }

    public void setup(Map attributes) {
        BigInteger r;
        boolean newCipher = true;
        String underlyingCipher = (String)attributes.get(CIPHER);
        if (underlyingCipher == null) {
            if (this.cipher == null) {
                this.cipher = CipherFactory.getInstance("rijndael");
            } else {
                newCipher = false;
            }
        } else {
            this.cipher = CipherFactory.getInstance(underlyingCipher);
        }
        int cipherBlockSize = 0;
        Integer bs = (Integer)attributes.get("gnu.crypto.cipher.block.size");
        if (bs != null) {
            cipherBlockSize = bs;
        } else if (newCipher) {
            cipherBlockSize = this.cipher.defaultBlockSize();
        }
        byte[] key = (byte[])attributes.get("gnu.crypto.cipher.key.material");
        if (key == null) {
            throw new IllegalArgumentException("gnu.crypto.cipher.key.material");
        }
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        if (cipherBlockSize != 0) {
            map.put("gnu.crypto.cipher.block.size", new Integer(cipherBlockSize));
        }
        map.put("gnu.crypto.cipher.key.material", (Integer)key);
        try {
            this.cipher.init(map);
        }
        catch (InvalidKeyException x) {
            throw new IllegalArgumentException("gnu.crypto.cipher.key.material");
        }
        cipherBlockSize = this.cipher.currentBlockSize();
        BigInteger counterRange = TWO_FIFTY_SIX.pow(cipherBlockSize);
        Object obj = attributes.get(OFFSET);
        if (obj instanceof BigInteger) {
            r = (BigInteger)obj;
        } else {
            byte[] offset = (byte[])obj;
            if (offset.length != cipherBlockSize) {
                throw new IllegalArgumentException(OFFSET);
            }
            r = new BigInteger(1, offset);
        }
        int wantBlockNdxLength = -1;
        Integer i = (Integer)attributes.get(BLOCK_INDEX_LENGTH);
        if (i != null && (wantBlockNdxLength = i.intValue()) < 1) {
            throw new IllegalArgumentException(BLOCK_INDEX_LENGTH);
        }
        int wantSegmentNdxLength = -1;
        i = (Integer)attributes.get(SEGMENT_INDEX_LENGTH);
        if (i != null && (wantSegmentNdxLength = i.intValue()) < 1) {
            throw new IllegalArgumentException(SEGMENT_INDEX_LENGTH);
        }
        if (wantBlockNdxLength == -1 && wantSegmentNdxLength == -1) {
            if (this.blockNdxLength == -1) {
                throw new IllegalArgumentException("gnu.crypto.prng.icm.block.index.length, gnu.crypto.prng.icm.segment.index.length");
            }
        } else {
            int limit = cipherBlockSize / 2;
            if (wantBlockNdxLength == -1) {
                wantBlockNdxLength = limit - wantSegmentNdxLength;
            } else if (wantSegmentNdxLength == -1) {
                wantSegmentNdxLength = limit - wantBlockNdxLength;
            } else if (wantSegmentNdxLength + wantBlockNdxLength > limit) {
                throw new IllegalArgumentException("gnu.crypto.prng.icm.block.index.length, gnu.crypto.prng.icm.segment.index.length");
            }
            this.blockNdxLength = wantBlockNdxLength;
            this.segmentNdxLength = wantSegmentNdxLength;
        }
        BigInteger s = (BigInteger)attributes.get(SEGMENT_INDEX);
        if (s == null) {
            if (this.segmentNdx == null) {
                throw new IllegalArgumentException(SEGMENT_INDEX);
            }
            if (this.segmentNdx.compareTo(TWO_FIFTY_SIX.pow(this.segmentNdxLength)) > 0) {
                throw new IllegalArgumentException(SEGMENT_INDEX);
            }
        } else {
            if (s.compareTo(TWO_FIFTY_SIX.pow(this.segmentNdxLength)) > 0) {
                throw new IllegalArgumentException(SEGMENT_INDEX);
            }
            this.segmentNdx = s;
        }
        this.C0 = this.segmentNdx.multiply(TWO_FIFTY_SIX.pow(this.blockNdxLength)).add(r).modPow(BigInteger.ONE, counterRange);
    }

    public void fillBlock() throws LimitReachedException {
        if (this.C0 == null) {
            throw new IllegalStateException();
        }
        if (this.blockNdx.compareTo(TWO_FIFTY_SIX.pow(this.blockNdxLength)) >= 0) {
            throw new LimitReachedException();
        }
        int cipherBlockSize = this.cipher.currentBlockSize();
        BigInteger counterRange = TWO_FIFTY_SIX.pow(cipherBlockSize);
        BigInteger Ci = this.C0.add(this.blockNdx).modPow(BigInteger.ONE, counterRange);
        this.buffer = Ci.toByteArray();
        int limit = this.buffer.length;
        if (limit < cipherBlockSize) {
            byte[] data = new byte[cipherBlockSize];
            System.arraycopy(this.buffer, 0, data, cipherBlockSize - limit, limit);
            this.buffer = data;
        } else if (limit > cipherBlockSize) {
            byte[] data = new byte[cipherBlockSize];
            System.arraycopy(this.buffer, limit - cipherBlockSize, data, 0, cipherBlockSize);
            this.buffer = data;
        }
        this.cipher.encryptBlock(this.buffer, 0, this.buffer, 0);
        this.blockNdx = this.blockNdx.add(BigInteger.ONE);
    }

    private final /* synthetic */ void this() {
        this.blockNdxLength = -1;
        this.segmentNdxLength = -1;
        this.blockNdx = BigInteger.ZERO;
    }

    public ICMGenerator() {
        super("icm");
        this.this();
    }

    private ICMGenerator(ICMGenerator that) {
        this();
        this.cipher = that.cipher == null ? null : (IBlockCipher)that.cipher.clone();
        this.blockNdxLength = that.blockNdxLength;
        this.segmentNdxLength = that.segmentNdxLength;
        this.blockNdx = that.blockNdx;
        this.segmentNdx = that.segmentNdx;
        this.buffer = (byte[])that.buffer.clone();
        this.ndx = that.ndx;
        this.initialised = that.initialised;
    }
}


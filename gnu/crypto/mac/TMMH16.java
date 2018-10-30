/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.mac;

import gnu.crypto.mac.BaseMac;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import java.security.InvalidKeyException;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class TMMH16
extends BaseMac {
    public static final String TAG_LENGTH = "gnu.crypto.mac.tmmh.tag.length";
    public static final String KEYSTREAM = "gnu.crypto.mac.tmmh.keystream";
    public static final String PREFIX = "gnu.crypto.mac.tmmh.prefix";
    private static final int P = 65537;
    private static Boolean valid;
    private int tagWords;
    private IRandom keystream;
    private byte[] prefix;
    private long keyWords;
    private long msgLength;
    private long msgWords;
    private int[] context;
    private int[] K0;
    private int[] Ki;
    private int Mi;

    public Object clone() {
        return new TMMH16(this);
    }

    public int macSize() {
        return this.tagWords * 2;
    }

    public void init(Map attributes) throws InvalidKeyException, IllegalStateException {
        int wantTagLength = 0;
        Integer tagLength = (Integer)attributes.get(TAG_LENGTH);
        if (tagLength == null) {
            if (this.tagWords == 0) {
                throw new IllegalArgumentException(TAG_LENGTH);
            }
        } else {
            wantTagLength = tagLength;
            if (wantTagLength < 2 || wantTagLength % 2 != 0) {
                throw new IllegalArgumentException(TAG_LENGTH);
            }
            if (wantTagLength > 64) {
                throw new IllegalArgumentException(TAG_LENGTH);
            }
            this.tagWords = wantTagLength / 2;
            this.K0 = new int[this.tagWords];
            this.Ki = new int[this.tagWords];
            this.context = new int[this.tagWords];
        }
        this.prefix = (byte[])attributes.get(PREFIX);
        if (this.prefix == null) {
            this.prefix = new byte[this.tagWords * 2];
        } else if (this.prefix.length != this.tagWords * 2) {
            throw new IllegalArgumentException(PREFIX);
        }
        IRandom prng = (IRandom)attributes.get(KEYSTREAM);
        if (prng == null) {
            if (this.keystream == null) {
                throw new IllegalArgumentException(KEYSTREAM);
            }
        } else {
            this.keystream = prng;
        }
        this.reset();
        int i = 0;
        while (i < this.tagWords) {
            this.Ki[i] = this.K0[i] = this.getNextKeyWord(this.keystream);
            ++i;
        }
    }

    public void update(byte b) {
        this.update(b, this.keystream);
    }

    public void update(byte[] b, int offset, int len) {
        int i = 0;
        while (i < len) {
            this.update(b[offset + i], this.keystream);
            ++i;
        }
    }

    public byte[] digest() {
        return this.digest(this.keystream);
    }

    public void reset() {
        this.keyWords = 0L;
        this.msgWords = 0L;
        this.msgLength = 0L;
        this.Mi = 0;
        int i = 0;
        while (i < this.tagWords) {
            this.context[i] = 0;
            ++i;
        }
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = Boolean.TRUE;
        }
        return valid;
    }

    public void update(byte b, IRandom prng) {
        this.Mi <<= 8;
        this.Mi |= b & 255;
        ++this.msgLength;
        if (this.msgLength % (long)2 == 0L) {
            ++this.msgWords;
            System.arraycopy(this.Ki, 1, this.Ki, 0, this.tagWords - 1);
            this.Ki[this.tagWords - 1] = this.getNextKeyWord(prng);
            int i = 0;
            while (i < this.tagWords) {
                long t = (long)this.context[i] & 0xFFFFFFFFL;
                this.context[i] = (int)(t += (long)(this.Ki[i] * this.Mi));
                ++i;
            }
            this.Mi = 0;
        }
    }

    public void update(byte[] b, int offset, int len, IRandom prng) {
        int i = 0;
        while (i < len) {
            this.update(b[offset + i], prng);
            ++i;
        }
    }

    public byte[] digest(IRandom prng) {
        this.doFinalRound(prng);
        byte[] result = new byte[this.tagWords * 2];
        int i = 0;
        int j = 0;
        while (i < this.tagWords) {
            result[j] = (byte)(this.context[i] >>> 8 ^ this.prefix[j]);
            result[++j] = (byte)(this.context[i] ^ this.prefix[j]);
            ++j;
            ++i;
        }
        this.reset();
        return result;
    }

    private final int getNextKeyWord(IRandom prng) {
        int result = 0;
        try {
            result = (prng.nextByte() & 255) << 8 | prng.nextByte() & 255;
        }
        catch (LimitReachedException x) {
            throw new RuntimeException(String.valueOf(x));
        }
        ++this.keyWords;
        return result;
    }

    private final void doFinalRound(IRandom prng) {
        long limit = this.msgLength;
        while (this.msgLength % (long)2 != 0L) {
            this.update((byte)0, prng);
        }
        int i = 0;
        while (i < this.tagWords) {
            long t = (long)this.context[i] & 0xFFFFFFFFL;
            t += (long)this.K0[i] * limit;
            this.context[i] = (int)(t %= 65537L);
            ++i;
        }
    }

    private final /* synthetic */ void this() {
        this.tagWords = 0;
        this.keystream = null;
    }

    public TMMH16() {
        super("tmmh16");
        this.this();
    }

    private TMMH16(TMMH16 that) {
        this();
        this.tagWords = that.tagWords;
        this.keystream = (IRandom)that.keystream.clone();
        this.keyWords = that.keyWords;
        this.msgLength = that.msgLength;
        this.msgWords = that.msgWords;
        this.context = (int[])that.context.clone();
        this.prefix = (byte[])that.prefix.clone();
        this.K0 = (int[])that.K0.clone();
        this.Ki = (int[])that.Ki.clone();
        this.Mi = that.Mi;
    }
}


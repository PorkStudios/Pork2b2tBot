/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.exp.ust;

import gnu.crypto.mac.TMMH16;
import gnu.crypto.prng.ICMGenerator;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.PRNGFactory;
import gnu.crypto.prng.UMacGenerator;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class UST {
    public static final String INDEX_LENGTH = "gnu.crypto.ust.index.length";
    public static final String KEYSTREAM = "gnu.crypto.ust.keystream.name";
    public static final String CIPHER = "gnu.crypto.ust.cipher.name";
    public static final String KEY_MATERIAL = "gnu.crypto.ust.key";
    public static final String TAG_LENGTH = "gnu.crypto.ust.tag.length";
    public static final String CONFIDENTIALITY = "gnu.crypto.ust.confidentiality";
    public static final String INTEGRITY = "gnu.crypto.ust.integrity";
    private static Boolean valid;
    private IRandom keystream;
    private IRandom cpStream;
    private IRandom ipStream;
    private TMMH16 mac;
    private HashMap kAttributes;
    private HashMap cpAttributes;
    private HashMap ipAttributes;
    private HashMap macAttributes;
    private boolean wantIntegrity;
    private boolean wantConfidentiality;
    private int keysize;
    private BigInteger index;
    private BigInteger maxIndex;
    private int macLength;
    private boolean ready;
    private Object lock;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void init(Map attributes) {
        Object object = this.lock;
        synchronized (object) {
            byte[] key;
            Boolean integrity;
            Integer blockSize;
            String keystreamName = (String)attributes.get(KEYSTREAM);
            if (keystreamName == null) {
                throw new IllegalArgumentException(KEYSTREAM);
            }
            this.keystream = PRNGFactory.getInstance(keystreamName);
            this.kAttributes.clear();
            this.cpAttributes.clear();
            this.ipAttributes.clear();
            String underlyingCipher = (String)attributes.get(CIPHER);
            if (underlyingCipher != null) {
                this.cpAttributes.put("gnu.crypto.prng.icm.cipher.name", underlyingCipher);
                this.ipAttributes.put("gnu.crypto.prng.icm.cipher.name", underlyingCipher);
                if (this.keystream instanceof ICMGenerator) {
                    this.kAttributes.put("gnu.crypto.prng.icm.cipher.name", underlyingCipher);
                } else {
                    if (!(this.keystream instanceof UMacGenerator)) {
                        throw new IllegalArgumentException(KEYSTREAM);
                    }
                    this.kAttributes.put("gnu.crypto.prng.umac.cipher.name", underlyingCipher);
                }
            }
            if ((blockSize = (Integer)attributes.get("gnu.crypto.cipher.block.size")) != null) {
                this.kAttributes.put("gnu.crypto.cipher.block.size", blockSize);
                this.cpAttributes.put("gnu.crypto.cipher.block.size", blockSize);
                this.ipAttributes.put("gnu.crypto.cipher.block.size", blockSize);
            }
            if ((key = (byte[])attributes.get(KEY_MATERIAL)) == null) {
                throw new IllegalArgumentException(KEY_MATERIAL);
            }
            this.keysize = key.length;
            if (this.keystream instanceof ICMGenerator) {
                int limit = key.length;
                if (limit < 2) {
                    throw new IllegalArgumentException(KEY_MATERIAL);
                }
                if ((limit & 1) != 0) {
                    throw new IllegalArgumentException(KEY_MATERIAL);
                }
                byte[] cipherKey = new byte[limit /= 2];
                byte[] offset = new byte[limit];
                System.arraycopy(key, 0, cipherKey, 0, limit);
                System.arraycopy(key, limit, offset, 0, limit);
                this.kAttributes.put("gnu.crypto.cipher.key.material", cipherKey);
                this.kAttributes.put("gnu.crypto.prng.icm.offset", offset);
            } else {
                this.kAttributes.put("gnu.crypto.cipher.key.material", key);
            }
            Integer ndxLength = (Integer)attributes.get(INDEX_LENGTH);
            if (ndxLength != null) {
                if (this.keystream instanceof ICMGenerator) {
                    this.kAttributes.put("gnu.crypto.prng.icm.segment.index.length", ndxLength);
                    this.maxIndex = BigInteger.valueOf(2).pow(8 * ndxLength).subtract(BigInteger.ONE);
                } else {
                    if (ndxLength != 1) {
                        throw new IllegalArgumentException(INDEX_LENGTH);
                    }
                    this.maxIndex = BigInteger.valueOf(255L);
                }
            } else {
                if (this.keystream instanceof ICMGenerator) {
                    throw new IllegalArgumentException(INDEX_LENGTH);
                }
                this.maxIndex = BigInteger.valueOf(255L);
            }
            if (this.keystream instanceof ICMGenerator) {
                this.kAttributes.put("gnu.crypto.prng.icm.segment.index", BigInteger.ZERO);
            } else {
                this.kAttributes.put("gnu.crypto.prng.umac.index", new Integer(0));
            }
            this.keystream.init(this.kAttributes);
            this.index = BigInteger.valueOf(-1);
            Boolean confidentiality = (Boolean)attributes.get(CONFIDENTIALITY);
            this.wantConfidentiality = confidentiality == null ? false : confidentiality;
            if (this.wantConfidentiality) {
                this.cpStream = PRNGFactory.getInstance(keystreamName);
            }
            this.wantIntegrity = (integrity = (Boolean)attributes.get(INTEGRITY)) == null ? true : integrity;
            if (this.wantIntegrity) {
                if (this.cpStream == null) {
                    this.cpStream = PRNGFactory.getInstance(keystreamName);
                }
                this.ipStream = PRNGFactory.getInstance(keystreamName);
                Integer tagLength = (Integer)attributes.get(TAG_LENGTH);
                if (tagLength == null) {
                    throw new IllegalArgumentException(TAG_LENGTH);
                }
                this.macAttributes.put("gnu.crypto.mac.tmmh.tag.length", tagLength);
                this.macLength = tagLength;
            }
            this.ready = false;
            return;
        }
    }

    public byte[] beginMessage() throws LimitReachedException, InvalidKeyException {
        this.beginMessageWithIndex(this.index.add(BigInteger.ONE));
        return this.index.toByteArray();
    }

    public void beginMessageWithIndex(int ndx) throws LimitReachedException, InvalidKeyException {
        this.beginMessageWithIndex(ndx);
    }

    public void beginMessageWithIndex(BigInteger ndx) throws LimitReachedException, InvalidKeyException {
        if (ndx.compareTo(this.maxIndex) > 0) {
            throw new LimitReachedException();
        }
        this.index = ndx;
        if (this.wantConfidentiality || this.wantIntegrity) {
            byte[] cpKey = new byte[this.keysize];
            this.keystream.nextBytes(cpKey, 0, this.keysize);
            this.cpAttributes.put("gnu.crypto.cipher.key.material", cpKey);
            if (this.cpStream instanceof ICMGenerator) {
                this.cpAttributes.put("gnu.crypto.prng.icm.segment.index", this.index);
            } else {
                this.cpAttributes.put("gnu.crypto.prng.umac.index", new Integer(this.index.intValue()));
            }
            this.cpStream.init(this.cpAttributes);
        }
        if (this.wantIntegrity) {
            byte[] ipKey = new byte[this.keysize];
            this.keystream.nextBytes(ipKey, 0, this.keysize);
            this.ipAttributes.put("gnu.crypto.cipher.key.material", ipKey);
            if (this.ipStream instanceof ICMGenerator) {
                this.ipAttributes.put("gnu.crypto.prng.icm.segment.index", this.index);
            } else {
                this.ipAttributes.put("gnu.crypto.prng.umac.index", new Integer(this.index.intValue()));
            }
            this.ipStream.init(this.ipAttributes);
            byte[] prefix = new byte[this.macLength];
            this.cpStream.nextBytes(prefix, 0, this.macLength);
            this.macAttributes.put("gnu.crypto.mac.tmmh.prefix", prefix);
            this.mac = new TMMH16();
            this.macAttributes.put("gnu.crypto.mac.tmmh.keystream", this.ipStream);
            this.mac.init(this.macAttributes);
        }
        this.ready = true;
    }

    public void doClear(byte[] in, int offset, int length) {
        if (!this.ready) {
            throw new IllegalStateException();
        }
        if (!this.wantIntegrity) {
            throw new IllegalStateException();
        }
        this.mac.update(in, offset, length);
    }

    public void doOpaque(byte[] in, int inOffset, int length, byte[] out, int outOffset) throws LimitReachedException {
        if (!this.ready) {
            throw new IllegalStateException();
        }
        if (this.wantIntegrity) {
            this.mac.update(in, inOffset, length);
        }
        if (this.wantConfidentiality) {
            byte[] suffix = new byte[length];
            this.cpStream.nextBytes(suffix, 0, length);
            int i = 0;
            while (i < length) {
                out[outOffset++] = (byte)(in[inOffset++] ^ suffix[i++]);
            }
        } else {
            System.arraycopy(in, inOffset, out, outOffset, length);
        }
    }

    public byte[] endMessage() {
        if (!this.ready) {
            throw new IllegalStateException();
        }
        if (!this.wantIntegrity) {
            return new byte[0];
        }
        byte[] result = this.mac.digest();
        this.reset();
        return result;
    }

    public void reset() {
        this.ready = false;
        if (this.wantIntegrity) {
            this.mac.reset();
        }
    }

    public boolean selfTest() {
        if (valid == null) {
            try {
                UST ust = new UST();
                HashMap<String, Object> attributes = new HashMap<String, Object>();
                attributes.put(KEYSTREAM, "umac-kdf");
                attributes.put(TAG_LENGTH, new Integer(4));
                attributes.put(KEY_MATERIAL, "abcdefghijklmnop".getBytes("ASCII"));
                attributes.put(CONFIDENTIALITY, Boolean.TRUE);
                attributes.put(INTEGRITY, Boolean.TRUE);
                ust.init(attributes);
                ust.beginMessage();
                ust.doClear("Giambattista Bodoni".getBytes("ASCII"), 0, 19);
                byte[] out = new byte[17];
                ust.doOpaque("Que du magnifique".getBytes("ASCII"), 0, 17, out, 0);
                byte[] tag = ust.endMessage();
                valid = Boolean.TRUE;
            }
            catch (Exception x) {
                x.printStackTrace(System.err);
                valid = Boolean.FALSE;
            }
        }
        return valid;
    }

    private final /* synthetic */ void this() {
        this.keystream = null;
        this.cpStream = null;
        this.ipStream = null;
        this.mac = null;
        this.kAttributes = new HashMap(5);
        this.cpAttributes = new HashMap(5);
        this.ipAttributes = new HashMap(5);
        this.macAttributes = new HashMap(2);
        this.wantIntegrity = true;
        this.wantConfidentiality = false;
        this.ready = false;
        this.lock = new Object();
    }

    public UST() {
        this.this();
    }
}


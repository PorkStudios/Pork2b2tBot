/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.prng;

import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.prng.BasePRNG;
import gnu.crypto.prng.LimitReachedException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UMacGenerator
extends BasePRNG {
    public static final String INDEX = "gnu.crypto.prng.umac.index";
    public static final String CIPHER = "gnu.crypto.prng.umac.cipher.name";
    private IBlockCipher cipher;

    public Object clone() {
        return new UMacGenerator(this);
    }

    public void setup(Map attributes) {
        boolean newCipher = true;
        String cipherName = (String)attributes.get(CIPHER);
        if (cipherName == null) {
            if (this.cipher == null) {
                this.cipher = CipherFactory.getInstance("rijndael");
            } else {
                newCipher = false;
            }
        } else {
            this.cipher = CipherFactory.getInstance(cipherName);
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
        int keyLength = key.length;
        boolean ok = false;
        Iterator it = this.cipher.keySizes();
        while (it.hasNext()) {
            boolean bl = false;
            if (keyLength == (Integer)it.next()) {
                bl = true;
            }
            if (ok = bl) break;
        }
        if (!ok) {
            throw new IllegalArgumentException("key length");
        }
        int index = -1;
        Integer i = (Integer)attributes.get(INDEX);
        if (i != null && ((index = i.intValue()) < 0 || index > 255)) {
            throw new IllegalArgumentException(INDEX);
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
        this.buffer = new byte[this.cipher.currentBlockSize()];
        this.buffer[this.cipher.currentBlockSize() - 1] = (byte)index;
        try {
            this.fillBlock();
        }
        catch (LimitReachedException limitReachedException) {}
    }

    public void fillBlock() throws LimitReachedException {
        this.cipher.encryptBlock(this.buffer, 0, this.buffer, 0);
    }

    public UMacGenerator() {
        super("umac-kdf");
    }

    private UMacGenerator(UMacGenerator that) {
        this();
        this.cipher = that.cipher == null ? null : (IBlockCipher)that.cipher.clone();
        this.initialised = that.initialised;
        this.buffer = (byte[])that.buffer.clone();
        this.ndx = that.ndx;
    }
}


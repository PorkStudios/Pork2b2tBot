/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.prng;

import gnu.crypto.mac.IMac;
import gnu.crypto.prng.BasePRNG;
import gnu.crypto.prng.LimitReachedException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PBKDF2
extends BasePRNG
implements Cloneable {
    private byte[] in;
    private int iterationCount;
    private byte[] salt;
    private IMac mac;
    private long count;

    public Object clone() {
        PBKDF2 that = new PBKDF2((IMac)this.mac.clone());
        that.iterationCount = this.iterationCount;
        that.salt = this.salt != null ? (byte[])this.salt.clone() : null;
        that.count = this.count;
        return that;
    }

    public void setup(Map attributes) {
        char[] password;
        HashMap<String, Boolean> macAttrib = new HashMap<String, Boolean>();
        macAttrib.put("gnu.crypto.hmac.pkcs5", Boolean.TRUE);
        byte[] s = (byte[])attributes.get("gnu.crypto.pbe.salt");
        if (s == null) {
            if (this.salt == null) {
                throw new IllegalArgumentException("no salt specified");
            }
        } else {
            this.salt = s;
        }
        if ((password = (char[])attributes.get("gnu.crypto.pbe.password")) != null) {
            try {
                macAttrib.put("gnu.crypto.mac.key.material", (Boolean)new String(password).getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException uee) {
                throw new Error(uee.getMessage());
            }
        } else if (!this.initialised) {
            throw new IllegalArgumentException("no password specified");
        }
        try {
            this.mac.init(macAttrib);
        }
        catch (Exception x) {
            throw new IllegalArgumentException(x.getMessage());
        }
        Integer ic = (Integer)attributes.get("gnu.crypto.pbe.iteration.count");
        if (ic != null) {
            this.iterationCount = ic;
        }
        if (this.iterationCount <= 0) {
            throw new IllegalArgumentException("bad iteration count");
        }
        this.count = 0L;
        this.buffer = new byte[this.mac.macSize()];
        try {
            this.fillBlock();
        }
        catch (LimitReachedException x) {
            throw new Error(x.getMessage());
        }
    }

    public void fillBlock() throws LimitReachedException {
        if (++this.count > 0xFFFFFFFFL) {
            throw new LimitReachedException();
        }
        Arrays.fill(this.buffer, (byte)0);
        int limit = this.salt.length;
        this.in = new byte[limit + 4];
        System.arraycopy(this.salt, 0, this.in, 0, this.salt.length);
        this.in[limit++] = (byte)(this.count >>> 24);
        this.in[limit++] = (byte)(this.count >>> 16);
        this.in[limit++] = (byte)(this.count >>> 8);
        this.in[limit] = (byte)this.count;
        int i = 0;
        while (i < this.iterationCount) {
            this.mac.reset();
            this.mac.update(this.in, 0, this.in.length);
            this.in = this.mac.digest();
            int j = 0;
            while (j < this.buffer.length) {
                byte[] arrby = this.buffer;
                int n = j;
                arrby[n] = (byte)(arrby[n] ^ this.in[j]);
                ++j;
            }
            ++i;
        }
    }

    public PBKDF2(IMac mac) {
        super("PBKDF2-" + mac.name());
        this.mac = mac;
        this.iterationCount = -1;
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.params;

import gnu.crypto.jce.params.DEREncodingException;
import java.math.BigInteger;

class DERReader {
    static final int UNIVERSAL = 1;
    static final int APPLICATION = 2;
    static final int CONTEXT_SPECIFIC = 3;
    static final int PRIVATE = 4;
    byte[] source;
    int pos;

    public void init(String source) {
        this.init(source.getBytes());
    }

    public void init(byte[] source) {
        this.source = source;
        this.pos = 0;
    }

    public boolean hasMorePrimitives() {
        boolean bl = false;
        if (this.pos < this.source.length) {
            bl = true;
        }
        return bl;
    }

    public BigInteger getBigInteger() throws DEREncodingException {
        return new BigInteger(this.getPrimitive());
    }

    private final byte[] getPrimitive() throws DEREncodingException {
        byte identifier;
        int tmp = this.pos;
        if ((32 & (identifier = this.source[tmp++])) != 0) {
            throw new DEREncodingException();
        }
        int type = this.translateLeadIdentifierByte(identifier);
        int tag = 31 & identifier;
        int len = this.source[tmp];
        long length = 127 & len;
        if ((128 & len) != 0) {
            len = (byte)(len & 127);
            length = 0L;
            int i = 0;
            while (i < len) {
                length <<= 8;
                length += (long)(this.source[++tmp] < 0 ? 256 + this.source[tmp] : this.source[tmp]);
                ++i;
            }
            ++tmp;
        } else {
            ++tmp;
        }
        byte[] tmpb = new byte[(int)length];
        System.arraycopy(this.source, tmp, tmpb, 0, (int)length);
        this.pos = (int)((long)tmp + length);
        return tmpb;
    }

    private final int translateLeadIdentifierByte(byte b) {
        if ((63 & b) == b) {
            return 1;
        }
        if ((127 & b) == b) {
            return 2;
        }
        if ((191 & b) == b) {
            return 3;
        }
        return 4;
    }

    private final int getIdentifier(int tpos) {
        while ((128 & this.source[tpos]) != 0) {
            ++tpos;
        }
        return tpos;
    }

    public DERReader() {
        this.source = null;
        this.pos = 0;
    }

    public DERReader(byte[] source) {
        this.init(source);
    }
}


/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.params;

import java.math.BigInteger;

class DERWriter {
    static final int UNIVERSAL = 1;
    static final int APPLICATION = 2;
    static final int CONTEXT_SPECIFIC = 3;
    static final int PRIVATE = 4;

    public byte[] writeBigInteger(BigInteger i) {
        return this.writePrimitive(2, 1, (int)Math.ceil((double)i.bitLength() / 8.0), i.toByteArray());
    }

    private final byte[] writePrimitive(int identifier, int identifierencoding, int length, byte[] contents) {
        return this.joinarrays(this.generateIdentifier(identifier, identifierencoding), this.generateLength(length), contents);
    }

    public byte[] joinarrays(byte[] a, byte[] b) {
        byte[] d = new byte[a.length + b.length];
        System.arraycopy(a, 0, d, 0, a.length);
        System.arraycopy(b, 0, d, a.length, b.length);
        return d;
    }

    public byte[] joinarrays(byte[] a, byte[] b, byte[] c) {
        byte[] d = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, d, 0, a.length);
        System.arraycopy(b, 0, d, a.length, b.length);
        System.arraycopy(c, 0, d, a.length + b.length, c.length);
        return d;
    }

    private final byte[] generateIdentifier(int identifier, int identifierencoding) {
        if (identifier > 31) {
            int count = (int)(Math.log(identifier) / Math.log(256.0));
            byte[] b = new byte[count + 1];
            b[0] = (byte)(this.translateLeadIdentifierByte(identifierencoding) | 31);
            int i = 1;
            while (i < count + 1) {
                b[i] = (byte)(127 & identifier >> 7 * (count - i));
                byte[] arrby = b;
                int n = i++;
                arrby[n] = (byte)(arrby[n] | 128);
            }
            byte[] arrby = b;
            int n = i - 1;
            arrby[n] = (byte)(arrby[n] ^ 128);
            return b;
        }
        byte[] b = new byte[]{(byte)((this.translateLeadIdentifierByte(identifierencoding) | (byte)(identifier & 31)) & 223)};
        return b;
    }

    private final byte translateLeadIdentifierByte(int b) {
        if (b == 1) {
            return 63;
        }
        if (b == 2) {
            return 127;
        }
        if (b == 3) {
            return -65;
        }
        return -64;
    }

    private final byte[] generateLength(int length) {
        if (length > 127) {
            int count = (int)Math.ceil(Math.log(length) / Math.log(256.0));
            byte[] b = new byte[count + 1];
            b[0] = (byte)(count & 127 | 128);
            int i = 1;
            while (i < count + 1) {
                b[i] = (byte)(length >>> 8 * (count - i));
                ++i;
            }
            return b;
        }
        byte[] b = new byte[]{(byte)(length & 127)};
        return b;
    }
}


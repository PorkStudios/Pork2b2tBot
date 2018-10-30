/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.sasl.SaslEncodingException;
import gnu.crypto.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class OutputBuffer {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public void setScalar(int count, int b) throws IOException {
        if (count < 0 || count > 4) {
            throw new SaslEncodingException("Invalid SASL scalar octet count: " + String.valueOf(count));
        }
        byte[] element = new byte[count];
        int i = count;
        while (--i >= 0) {
            element[i] = (byte)b;
            b >>>= 8;
        }
        this.out.write(element);
    }

    public void setOS(byte[] b) throws IOException {
        int length = b.length;
        if (length > 255) {
            throw new SaslEncodingException("SASL octet-sequence too long");
        }
        this.out.write(length & 255);
        this.out.write(b);
    }

    public void setEOS(byte[] b) throws IOException {
        int length = b.length;
        if (length > (char)-1) {
            throw new SaslEncodingException("SASL extended octet-sequence too long");
        }
        byte[] lengthBytes = new byte[]{(byte)(length >>> 8), (byte)length};
        this.out.write(lengthBytes);
        this.out.write(b);
    }

    public void setMPI(BigInteger val) throws IOException {
        byte[] b = Util.trim(val);
        int length = b.length;
        if (length > (char)-1) {
            throw new SaslEncodingException("SASL multi-precision integer too long");
        }
        byte[] lengthBytes = new byte[]{(byte)(length >>> 8), (byte)length};
        this.out.write(lengthBytes);
        this.out.write(b);
    }

    public void setText(String str) throws IOException {
        byte[] b = str.getBytes("UTF8");
        int length = b.length;
        if (length > (char)-1) {
            throw new SaslEncodingException("SASL text too long");
        }
        byte[] lengthBytes = new byte[]{(byte)(length >>> 8), (byte)length};
        this.out.write(lengthBytes);
        this.out.write(b);
    }

    public byte[] encode() throws SaslEncodingException {
        byte[] buffer = this.wrap();
        int length = buffer.length;
        byte[] result = new byte[length + 4];
        result[0] = (byte)(length >>> 24);
        result[1] = (byte)(length >>> 16);
        result[2] = (byte)(length >>> 8);
        result[3] = (byte)length;
        System.arraycopy(buffer, 0, result, 4, length);
        return result;
    }

    public byte[] wrap() throws SaslEncodingException {
        int length = this.out.size();
        if (length > 2147483643 || length < 0) {
            throw new SaslEncodingException("SASL buffer too long");
        }
        return this.out.toByteArray();
    }
}


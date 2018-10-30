/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.sasl.SaslEncodingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class InputBuffer {
    protected ByteArrayInputStream in;
    protected int length;

    public static InputBuffer getInstance(byte[] raw) {
        return InputBuffer.getInstance(raw, 0, raw.length);
    }

    public static InputBuffer getInstance(byte[] raw, int offset, int len) {
        InputBuffer result = new InputBuffer();
        result.in = new ByteArrayInputStream(raw, offset, len);
        return result;
    }

    public static int twoBytesToLength(byte[] b) throws SaslEncodingException {
        int result = (b[0] & 255) << 8 | b[1] & 255;
        if (result > (char)-1) {
            throw new SaslEncodingException("SASL MPI/Text size limit exceeded");
        }
        return result;
    }

    public boolean hasMoreElements() {
        boolean bl = false;
        if (this.in.available() > 0) {
            bl = true;
        }
        return bl;
    }

    public long getScalar(int count) throws IOException {
        if (count < 0 || count > 4) {
            throw new SaslEncodingException("Invalid SASL scalar octet count: " + String.valueOf(count));
        }
        if (!this.hasMoreElements()) {
            throw new SaslEncodingException("Not enough bytes for a scalar in buffer");
        }
        if (this.in.available() < count) {
            throw new SaslEncodingException("Illegal SASL scalar encoding");
        }
        byte[] element = new byte[count];
        this.in.read(element);
        long result = 0L;
        int i = 0;
        while (i < count) {
            result <<= 8;
            result |= (long)element[i] & 255L;
            ++i;
        }
        return result;
    }

    public byte[] getOS() throws IOException {
        if (!this.hasMoreElements()) {
            throw new SaslEncodingException("Not enough bytes for an octet-sequence in buffer");
        }
        int elementLength = this.in.read();
        if (elementLength > 255) {
            throw new SaslEncodingException("SASL octet-sequence size limit exceeded");
        }
        if (this.in.available() < elementLength) {
            throw new SaslEncodingException("Illegal SASL octet-sequence encoding");
        }
        byte[] result = new byte[elementLength];
        this.in.read(result);
        return result;
    }

    public byte[] getEOS() throws IOException {
        if (this.in.available() < 2) {
            throw new SaslEncodingException("Not enough bytes for an extended octet-sequence in buffer");
        }
        byte[] elementLengthBytes = new byte[2];
        this.in.read(elementLengthBytes);
        int elementLength = InputBuffer.twoBytesToLength(elementLengthBytes);
        if (this.in.available() < elementLength) {
            throw new SaslEncodingException("Illegal SASL extended octet-sequence encoding");
        }
        byte[] result = new byte[elementLength];
        this.in.read(result);
        return result;
    }

    public BigInteger getMPI() throws IOException {
        if (this.in.available() < 2) {
            throw new SaslEncodingException("Not enough bytes for an MPI in buffer");
        }
        byte[] elementLengthBytes = new byte[2];
        this.in.read(elementLengthBytes);
        int elementLength = InputBuffer.twoBytesToLength(elementLengthBytes);
        if (this.in.available() < elementLength) {
            throw new SaslEncodingException("Illegal SASL multi-precision integer encoding");
        }
        byte[] element = new byte[elementLength];
        this.in.read(element);
        return new BigInteger(1, element);
    }

    public String getText() throws IOException {
        if (this.in.available() < 2) {
            throw new SaslEncodingException("Not enough bytes for a text in buffer");
        }
        byte[] elementLengthBytes = new byte[2];
        this.in.read(elementLengthBytes);
        int elementLength = InputBuffer.twoBytesToLength(elementLengthBytes);
        if (this.in.available() < elementLength) {
            throw new SaslEncodingException("Illegal SASL text encoding");
        }
        byte[] element = new byte[elementLength];
        this.in.read(element);
        return new String(element, "UTF8");
    }

    public InputBuffer(byte[] frame) throws SaslEncodingException {
        this();
        if (frame.length < 4) {
            throw new SaslEncodingException("SASL buffer header too short");
        }
        this.length = (frame[0] & 255) << 24 | (frame[1] & 255) << 16 | (frame[2] & 255) << 8 | frame[3] & 255;
        if (this.length > 2147483643 || this.length < 0) {
            throw new SaslEncodingException("SASL buffer size limit exceeded");
        }
        this.in = new ByteArrayInputStream(frame, 4, this.length);
    }

    private InputBuffer() {
    }
}


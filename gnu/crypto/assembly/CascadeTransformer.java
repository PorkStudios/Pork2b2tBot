/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Cascade;
import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.Transformer;
import gnu.crypto.assembly.TransformerException;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.util.Map;

class CascadeTransformer
extends Transformer {
    private Cascade delegate;
    private int blockSize;

    void initDelegate(Map attributes) throws TransformerException {
        attributes.put("gnu.crypto.assembly.cascade.direction", this.wired);
        try {
            this.delegate.init(attributes);
        }
        catch (InvalidKeyException x) {
            throw new TransformerException("initDelegate()", x);
        }
        this.blockSize = this.delegate.currentBlockSize();
    }

    int delegateBlockSize() {
        return this.blockSize;
    }

    void resetDelegate() {
        this.delegate.reset();
        this.blockSize = 0;
    }

    byte[] updateDelegate(byte[] in, int offset, int length) throws TransformerException {
        byte[] result = this.updateInternal(in, offset, length);
        return result;
    }

    byte[] lastUpdateDelegate() throws TransformerException {
        if (this.inBuffer.size() != 0) {
            throw new TransformerException("lastUpdateDelegate()", new IllegalStateException("Cascade transformer, after last update, must be empty but isn't"));
        }
        return new byte[0];
    }

    private final byte[] updateInternal(byte[] in, int offset, int length) {
        byte[] result;
        int i = 0;
        while (i < length) {
            this.inBuffer.write(in[offset++] & 255);
            if (this.inBuffer.size() >= this.blockSize) {
                result = this.inBuffer.toByteArray();
                this.inBuffer.reset();
                this.delegate.update(result, 0, result, 0);
                this.outBuffer.write(result, 0, this.blockSize);
            }
            ++i;
        }
        result = this.outBuffer.toByteArray();
        this.outBuffer.reset();
        return result;
    }

    CascadeTransformer(Cascade delegate) {
        this.delegate = delegate;
    }
}


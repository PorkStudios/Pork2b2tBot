/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.Transformer;
import gnu.crypto.assembly.TransformerException;
import gnu.crypto.pad.IPad;
import gnu.crypto.pad.WrongPaddingException;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
class PaddingTransformer
extends Transformer {
    private IPad delegate;
    private int outputBlockSize;

    void initDelegate(Map attributes) throws TransformerException {
        if (this.tail == null) {
            throw new TransformerException("initDelegate()", new IllegalStateException("Padding transformer missing its tail!"));
        }
        this.outputBlockSize = this.tail.currentBlockSize();
        this.delegate.init(this.outputBlockSize);
    }

    int delegateBlockSize() {
        return this.outputBlockSize;
    }

    void resetDelegate() {
        this.delegate.reset();
        this.outputBlockSize = 1;
    }

    byte[] updateDelegate(byte[] in, int offset, int length) throws TransformerException {
        byte[] result;
        this.inBuffer.write(in, offset, length);
        byte[] tmp = this.inBuffer.toByteArray();
        this.inBuffer.reset();
        if (this.wired == Direction.FORWARD) {
            if (tmp.length < this.outputBlockSize) {
                this.inBuffer.write(tmp, 0, tmp.length);
                result = new byte[]{};
            } else {
                int newlen = this.outputBlockSize * (tmp.length / this.outputBlockSize);
                this.inBuffer.write(tmp, newlen, tmp.length - newlen);
                result = new byte[newlen];
                System.arraycopy(tmp, 0, result, 0, newlen);
            }
        } else if (tmp.length < this.outputBlockSize) {
            this.inBuffer.write(tmp, 0, tmp.length);
            result = new byte[]{};
        } else {
            result = new byte[tmp.length - this.outputBlockSize];
            System.arraycopy(tmp, 0, result, 0, result.length);
            this.inBuffer.write(tmp, result.length, this.outputBlockSize);
        }
        return result;
    }

    byte[] lastUpdateDelegate() throws TransformerException {
        byte[] result;
        if (this.wired == Direction.FORWARD) {
            result = this.inBuffer.toByteArray();
            byte[] padding = this.delegate.pad(result, 0, result.length);
            this.inBuffer.write(padding, 0, padding.length);
        } else {
            byte[] tmp = this.inBuffer.toByteArray();
            this.inBuffer.reset();
            try {
                int realLength = tmp.length;
            }
            catch (WrongPaddingException x) {
                throw new TransformerException("lastUpdateDelegate()", x);
            }
            this.inBuffer.write(tmp, 0, realLength -= this.delegate.unpad(tmp, 0, tmp.length));
        }
        result = this.inBuffer.toByteArray();
        this.inBuffer.reset();
        return result;
    }

    private final /* synthetic */ void this() {
        this.outputBlockSize = 1;
    }

    PaddingTransformer(IPad padding) {
        this.this();
        this.delegate = padding;
    }
}


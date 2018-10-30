/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.Transformer;
import gnu.crypto.assembly.TransformerException;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
class DeflateTransformer
extends Transformer {
    private Deflater compressor;
    private Inflater decompressor;
    private int outputBlockSize;
    private byte[] zlibBuffer;

    void initDelegate(Map attributes) throws TransformerException {
        if (this.tail == null) {
            throw new TransformerException("initDelegate()", new IllegalStateException("Compression transformer missing its tail!"));
        }
        this.outputBlockSize = this.tail.currentBlockSize();
        this.zlibBuffer = new byte[this.outputBlockSize];
        Direction flow = (Direction)attributes.get("gnu.crypto.assembly.transformer.direction");
        if (flow == Direction.FORWARD) {
            this.compressor = new Deflater();
        } else {
            this.decompressor = new Inflater();
        }
    }

    int delegateBlockSize() {
        return 1;
    }

    void resetDelegate() {
        this.compressor = null;
        this.decompressor = null;
        this.outputBlockSize = 1;
        this.zlibBuffer = null;
    }

    byte[] updateDelegate(byte[] in, int offset, int length) throws TransformerException {
        if (this.wired == Direction.FORWARD) {
            this.compressor.setInput(in, offset, length);
            while (!this.compressor.needsInput()) {
                this.compress();
            }
        } else {
            this.decompress(in, offset, length);
        }
        byte[] result = this.inBuffer.toByteArray();
        this.inBuffer.reset();
        return result;
    }

    byte[] lastUpdateDelegate() throws TransformerException {
        if (this.wired == Direction.FORWARD) {
            if (!this.compressor.finished()) {
                this.compressor.finish();
                while (!this.compressor.finished()) {
                    this.compress();
                }
            }
        } else if (!this.decompressor.finished()) {
            throw new TransformerException("lastUpdateDelegate()", new IllegalStateException("Compression transformer, after last update, must be finished but isn't"));
        }
        byte[] result = this.inBuffer.toByteArray();
        this.inBuffer.reset();
        return result;
    }

    private final void compress() {
        int len = this.compressor.deflate(this.zlibBuffer);
        if (len > 0) {
            this.inBuffer.write(this.zlibBuffer, 0, len);
        }
    }

    private final void decompress(byte[] in, int offset, int length) throws TransformerException {
        this.decompressor.setInput(in, offset, length);
        int len = 1;
        while (len > 0) {
            try {
                len = this.decompressor.inflate(this.zlibBuffer);
            }
            catch (DataFormatException x) {
                throw new TransformerException("decompress()", x);
            }
            if (len <= 0) continue;
            this.inBuffer.write(this.zlibBuffer, 0, len);
        }
    }

    private final /* synthetic */ void this() {
        this.outputBlockSize = 512;
    }

    DeflateTransformer() {
        this.this();
    }
}


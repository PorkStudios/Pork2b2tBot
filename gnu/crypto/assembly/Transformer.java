/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Cascade;
import gnu.crypto.assembly.CascadeTransformer;
import gnu.crypto.assembly.DeflateTransformer;
import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.Operation;
import gnu.crypto.assembly.PaddingTransformer;
import gnu.crypto.assembly.TransformerException;
import gnu.crypto.pad.IPad;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public abstract class Transformer {
    public static final String DIRECTION = "gnu.crypto.assembly.transformer.direction";
    protected Direction wired;
    protected Operation mode;
    protected Transformer tail;
    protected ByteArrayOutputStream inBuffer;
    protected ByteArrayOutputStream outBuffer;

    public static final Transformer getCascadeTransformer(Cascade cascade) {
        return new CascadeTransformer(cascade);
    }

    public static final Transformer getPaddingTransformer(IPad padding) {
        return new PaddingTransformer(padding);
    }

    public static final Transformer getDeflateTransformer() {
        return new DeflateTransformer();
    }

    public void setMode(Operation mode) {
        if (this.mode != null) {
            throw new IllegalStateException();
        }
        this.mode = mode;
    }

    public boolean isPreProcessing() {
        if (this.mode == null) {
            throw new IllegalStateException();
        }
        boolean bl = false;
        if (this.mode == Operation.PRE_PROCESSING) {
            bl = true;
        }
        return bl;
    }

    public boolean isPostProcessing() {
        return this.isPreProcessing() ^ true;
    }

    public void init(Map attributes) throws TransformerException {
        if (this.wired != null) {
            throw new IllegalStateException();
        }
        Direction flow = (Direction)attributes.get(DIRECTION);
        if (flow == null) {
            flow = Direction.FORWARD;
        }
        this.wired = flow;
        this.inBuffer.reset();
        this.outBuffer.reset();
        this.tail.init(attributes);
        this.initDelegate(attributes);
    }

    public int currentBlockSize() {
        if (this.wired == null) {
            throw new IllegalStateException();
        }
        return this.delegateBlockSize();
    }

    public void reset() {
        this.resetDelegate();
        this.wired = null;
        this.inBuffer.reset();
        this.outBuffer.reset();
        this.tail.reset();
    }

    public byte[] update(byte b) throws TransformerException {
        return this.update(new byte[]{b}, 0, 1);
    }

    public byte[] update(byte[] in) throws TransformerException {
        return this.update(in, 0, in.length);
    }

    public byte[] update(byte[] in, int offset, int length) throws TransformerException {
        if (this.wired == null) {
            throw new IllegalStateException();
        }
        byte[] result = this.wired == Direction.FORWARD ? this.forwardUpdate(in, offset, length) : this.inverseUpdate(in, offset, length);
        return result;
    }

    public byte[] lastUpdate() throws TransformerException {
        byte[] result;
        byte[] arrby = result = this.wired == Direction.FORWARD ? this.lastForwardUpdate() : this.lastInverseUpdate();
        if (this.inBuffer.size() != 0) {
            throw new TransformerException("lastUpdate(): input buffer not empty");
        }
        return result;
    }

    public byte[] lastUpdate(byte b) throws TransformerException {
        return this.lastUpdate(new byte[]{b}, 0, 1);
    }

    public byte[] lastUpdate(byte[] in) throws TransformerException {
        return this.lastUpdate(in, 0, in.length);
    }

    public byte[] lastUpdate(byte[] in, int offset, int length) throws TransformerException {
        byte[] result = this.update(in, offset, length);
        byte[] rest = this.lastUpdate();
        if (rest.length > 0) {
            byte[] newResult = new byte[result.length + rest.length];
            System.arraycopy(result, 0, newResult, 0, result.length);
            System.arraycopy(rest, 0, newResult, result.length, rest.length);
            result = newResult;
        }
        return result;
    }

    private final byte[] forwardUpdate(byte[] in, int off, int len) throws TransformerException {
        return this.isPreProcessing() ? this.preTransform(in, off, len) : this.postTransform(in, off, len);
    }

    private final byte[] inverseUpdate(byte[] in, int off, int len) throws TransformerException {
        return this.isPreProcessing() ? this.postTransform(in, off, len) : this.preTransform(in, off, len);
    }

    private final byte[] preTransform(byte[] in, int off, int len) throws TransformerException {
        byte[] result = this.updateDelegate(in, off, len);
        result = this.tail.update(result);
        return result;
    }

    private final byte[] postTransform(byte[] in, int off, int len) throws TransformerException {
        byte[] result = this.tail.update(in, off, len);
        result = this.updateDelegate(result, 0, result.length);
        return result;
    }

    private final byte[] lastForwardUpdate() throws TransformerException {
        return this.isPreProcessing() ? this.preLastTransform() : this.postLastTransform();
    }

    private final byte[] lastInverseUpdate() throws TransformerException {
        return this.isPreProcessing() ? this.postLastTransform() : this.preLastTransform();
    }

    private final byte[] preLastTransform() throws TransformerException {
        byte[] result = this.lastUpdateDelegate();
        result = this.tail.lastUpdate(result);
        return result;
    }

    private final byte[] postLastTransform() throws TransformerException {
        byte[] result = this.tail.lastUpdate();
        result = this.updateDelegate(result, 0, result.length);
        byte[] rest = this.lastUpdateDelegate();
        if (rest.length > 0) {
            byte[] newResult = new byte[result.length + rest.length];
            System.arraycopy(result, 0, newResult, 0, result.length);
            System.arraycopy(rest, 0, newResult, result.length, rest.length);
            result = newResult;
        }
        return result;
    }

    abstract void initDelegate(Map var1) throws TransformerException;

    abstract int delegateBlockSize();

    abstract void resetDelegate();

    abstract byte[] updateDelegate(byte[] var1, int var2, int var3) throws TransformerException;

    abstract byte[] lastUpdateDelegate() throws TransformerException;

    private final /* synthetic */ void this() {
        this.tail = null;
        this.inBuffer = new ByteArrayOutputStream(2048);
        this.outBuffer = new ByteArrayOutputStream(2048);
    }

    protected Transformer() {
        this.this();
        this.wired = null;
    }
}


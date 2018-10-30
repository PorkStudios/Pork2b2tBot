/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.LoopbackTransformer;
import gnu.crypto.assembly.Operation;
import gnu.crypto.assembly.Transformer;
import gnu.crypto.assembly.TransformerException;
import java.util.Map;

public class Assembly {
    public static final String DIRECTION = "gnu.crypto.assembly.assembly.direction";
    private Direction wired = null;
    private Transformer head = new LoopbackTransformer();

    public void addPreTransformer(Transformer t) {
        this.wireTransformer(t, Operation.PRE_PROCESSING);
    }

    public void addPostTransformer(Transformer t) {
        this.wireTransformer(t, Operation.POST_PROCESSING);
    }

    public void init(Map attributes) throws TransformerException {
        if (this.wired != null) {
            throw new IllegalStateException();
        }
        Direction flow = (Direction)attributes.get(DIRECTION);
        if (flow == null) {
            flow = Direction.FORWARD;
        }
        attributes.put("gnu.crypto.assembly.transformer.direction", flow);
        this.head.init(attributes);
        this.wired = flow;
    }

    public void reset() {
        this.head.reset();
        this.wired = null;
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
        return this.head.update(in, offset, length);
    }

    public byte[] lastUpdate() throws TransformerException {
        return this.lastUpdate(new byte[0], 0, 0);
    }

    public byte[] lastUpdate(byte b) throws TransformerException {
        return this.lastUpdate(new byte[]{b}, 0, 1);
    }

    public byte[] lastUpdate(byte[] in) throws TransformerException {
        return this.lastUpdate(in, 0, in.length);
    }

    public byte[] lastUpdate(byte[] in, int offset, int length) throws TransformerException {
        if (this.wired == null) {
            throw new IllegalStateException();
        }
        byte[] result = this.head.lastUpdate(in, offset, length);
        this.reset();
        return result;
    }

    private final void wireTransformer(Transformer t, Operation mode) {
        if (t.tail != null) {
            throw new IllegalArgumentException();
        }
        t.setMode(mode);
        t.tail = this.head;
        this.head = t;
    }
}


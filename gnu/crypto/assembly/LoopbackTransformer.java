/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Transformer;
import gnu.crypto.assembly.TransformerException;
import java.util.Map;

final class LoopbackTransformer
extends Transformer {
    public final void init(Map attributes) throws TransformerException {
    }

    public final void reset() {
    }

    public final byte[] update(byte[] in, int offset, int length) throws TransformerException {
        return this.updateDelegate(in, offset, length);
    }

    public final byte[] lastUpdate() throws TransformerException {
        return this.lastUpdateDelegate();
    }

    final void initDelegate(Map attributes) throws TransformerException {
    }

    final int delegateBlockSize() {
        return 1;
    }

    final void resetDelegate() {
    }

    final byte[] updateDelegate(byte[] in, int offset, int length) throws TransformerException {
        byte[] result = new byte[length];
        System.arraycopy(in, offset, result, 0, length);
        return result;
    }

    final byte[] lastUpdateDelegate() throws TransformerException {
        return new byte[0];
    }

    LoopbackTransformer() {
    }
}


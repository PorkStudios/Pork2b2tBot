/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

import gnu.crypto.assembly.Cascade;
import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.Stage;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

class CascadeStage
extends Stage {
    private Cascade delegate;

    public Set blockSizes() {
        return Collections.unmodifiableSet(this.delegate.blockSizes());
    }

    void initDelegate(Map attributes) throws InvalidKeyException {
        Direction flow = (Direction)attributes.get("gnu.crypto.assembly.stage.direction");
        attributes.put("gnu.crypto.assembly.stage.direction", flow.equals(this.forward) ? this.forward : Direction.reverse(this.forward));
        this.delegate.init(attributes);
    }

    public int currentBlockSize() throws IllegalStateException {
        return this.delegate.currentBlockSize();
    }

    void resetDelegate() {
        this.delegate.reset();
    }

    void updateDelegate(byte[] in, int inOffset, byte[] out, int outOffset) {
        this.delegate.update(in, inOffset, out, outOffset);
    }

    public boolean selfTest() {
        return this.delegate.selfTest();
    }

    CascadeStage(Cascade cascade, Direction forwardDirection) {
        super(forwardDirection);
        this.delegate = cascade;
    }
}

